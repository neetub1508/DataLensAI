import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { AuthState, User, LoginCredentials, RegisterCredentials } from '@/types/auth'
import { apiClient } from '@/lib/api'
import { toast } from 'react-hot-toast'
import { STORAGE_KEYS } from '@/constants'

interface AuthStore extends AuthState {
  login: (credentials: LoginCredentials) => Promise<void>
  register: (credentials: RegisterCredentials) => Promise<void>
  logout: () => void
  refreshUser: () => Promise<void>
  refreshUserPromise: Promise<void> | null
  verifyEmail: (token: string) => Promise<void>
  requestPasswordReset: (email: string) => Promise<void>
  resetPassword: (token: string, newPassword: string) => Promise<void>
  setLoading: (loading: boolean) => void
  setToken: (token: string) => void
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,
      refreshUserPromise: null,

      login: async (credentials: LoginCredentials) => {
        try {
          set({ isLoading: true })
          const response = await apiClient.login(credentials.email, credentials.password)
          
          const { access_token, refresh_token, user } = response
          
          // Store tokens
          localStorage.setItem('access_token', access_token)
          localStorage.setItem('refresh_token', refresh_token)
          
          set({
            user,
            token: access_token,
            refreshToken: refresh_token,
            isAuthenticated: true,
            isLoading: false,
          })
          
          toast.success('Logged in successfully!')
        } catch (error: any) {
          set({ isLoading: false })
          const message = error.response?.data?.detail || 'Login failed'
          toast.error(message)
          throw error
        }
      },

      register: async (credentials: RegisterCredentials) => {
        try {
          set({ isLoading: true })
          const response = await apiClient.register(
            credentials.email,
            credentials.password,
            credentials.locale
          )
          
          set({ isLoading: false })
          toast.success('Registration successful! Please check your email for verification.')
          return response
        } catch (error: any) {
          set({ isLoading: false })
          const message = error.response?.data?.detail || 'Registration failed'
          toast.error(message)
          throw error
        }
      },

      logout: () => {
        localStorage.removeItem('access_token')
        localStorage.removeItem('refresh_token')
        
        set({
          user: null,
          token: null,
          refreshToken: null,
          isAuthenticated: false,
          isLoading: false,
        })
        
        toast.success('Logged out successfully!')
      },

      refreshUser: async () => {
        const state = get()
        
        // If a refresh is already in progress, wait for it
        if (state.refreshUserPromise) {
          return state.refreshUserPromise
        }
        
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
        if (!token) return Promise.resolve()
        
        // Create the refresh promise
        const refreshPromise = (async () => {
          try {
            const user = await apiClient.getCurrentUser()
            set({ user, refreshUserPromise: null })
          } catch (error) {
            set({ refreshUserPromise: null })
            // If user fetch fails, logout
            get().logout()
          }
        })()
        
        // Store the promise to prevent concurrent calls
        set({ refreshUserPromise: refreshPromise })
        
        return refreshPromise
      },

      verifyEmail: async (token: string) => {
        try {
          set({ isLoading: true })
          await apiClient.verifyEmail(token)
          set({ isLoading: false })
          toast.success('Email verified successfully!')
        } catch (error: any) {
          set({ isLoading: false })
          const message = error.response?.data?.detail || 'Email verification failed'
          toast.error(message)
          throw error
        }
      },

      requestPasswordReset: async (email: string) => {
        try {
          set({ isLoading: true })
          await apiClient.requestPasswordReset(email)
          set({ isLoading: false })
          toast.success('Password reset email sent!')
        } catch (error: any) {
          set({ isLoading: false })
          const message = error.response?.data?.detail || 'Failed to send reset email'
          toast.error(message)
          throw error
        }
      },

      resetPassword: async (token: string, newPassword: string) => {
        try {
          set({ isLoading: true })
          await apiClient.resetPassword(token, newPassword)
          set({ isLoading: false })
          toast.success('Password reset successfully!')
        } catch (error: any) {
          set({ isLoading: false })
          const message = error.response?.data?.detail || 'Password reset failed'
          toast.error(message)
          throw error
        }
      },

      setLoading: (loading: boolean) => {
        set({ isLoading: loading })
      },

      setToken: (token: string) => {
        localStorage.setItem('access_token', token)
        set({
          token,
          isAuthenticated: true,
        })
        // Refresh user data with new token
        get().refreshUser()
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)

// Initialize auth state on app load
if (typeof window !== 'undefined') {
  const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
  if (token) {
    useAuthStore.getState().refreshUser()
  }
}