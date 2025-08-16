import axios, { AxiosInstance, AxiosResponse } from 'axios'
import { toast } from 'react-hot-toast'
import { API_CONFIG, API_ENDPOINTS, STORAGE_KEYS, DEFAULT_LOCALE } from '../constants'

const API_URL = API_CONFIG.BASE_URL

class ApiClient {
  private client: AxiosInstance
  private refreshTokenPromise: Promise<any> | null = null

  constructor() {
    this.client = axios.create({
      baseURL: API_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    this.setupInterceptors()
  }

  private setupInterceptors() {
    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN)
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        
        
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response: AxiosResponse) => response,
      async (error) => {
        const originalRequest = error.config

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true

          const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN)
          if (refreshToken) {
            try {
              // Use existing promise if refresh is already in progress
              if (!this.refreshTokenPromise) {
                this.refreshTokenPromise = this.client.post(API_ENDPOINTS.AUTH.REFRESH, null, {
                  params: { refresh_token: refreshToken }
                })
              }
              
              const response = await this.refreshTokenPromise
              
              const { access_token, refresh_token: newRefreshToken } = response.data
              localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, access_token)
              localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, newRefreshToken)
              
              // Clear the refresh promise
              this.refreshTokenPromise = null
              
              // Retry original request
              originalRequest.headers.Authorization = `Bearer ${access_token}`
              return this.client(originalRequest)
            } catch (refreshError) {
              // Clear the refresh promise on error
              this.refreshTokenPromise = null
              
              // Refresh failed, redirect to login
              localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN)
              localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN)
              window.location.href = '/login'
              return Promise.reject(refreshError)
            }
          } else {
            // No refresh token, redirect to login
            window.location.href = '/login'
          }
        }

        // Handle other errors
        const errorMessage = error.response?.data?.detail || 'An error occurred'
        toast.error(errorMessage)
        
        return Promise.reject(error)
      }
    )
  }

  // Auth methods
  async login(email: string, password: string) {
    const response = await this.client.post(API_ENDPOINTS.AUTH.LOGIN, { email, password })
    return response.data
  }

  async register(email: string, password: string, locale: string = DEFAULT_LOCALE) {
    const response = await this.client.post(API_ENDPOINTS.AUTH.REGISTER, { 
      email, 
      password, 
      locale 
    })
    return response.data
  }

  async verifyEmail(token: string) {
    const response = await this.client.post(API_ENDPOINTS.AUTH.VERIFY_EMAIL, null, {
      params: { token }
    })
    return response.data
  }

  async requestPasswordReset(email: string) {
    const response = await this.client.post(API_ENDPOINTS.AUTH.REQUEST_PASSWORD_RESET, null, {
      params: { email }
    })
    return response.data
  }

  async resetPassword(token: string, newPassword: string) {
    const response = await this.client.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, null, {
      params: { token, new_password: newPassword }
    })
    return response.data
  }

  // User methods
  async getCurrentUser() {
    const response = await this.client.get(API_ENDPOINTS.USERS.ME)
    return response.data
  }

  async updateCurrentUser(data: any) {
    const response = await this.client.put(API_ENDPOINTS.USERS.ME, data)
    return response.data
  }

  async getAllUsers(skip = 0, limit = 100) {
    const response = await this.client.get(API_ENDPOINTS.USERS.BASE, {
      params: { skip, limit }
    })
    return response.data
  }

  async getUserById(userId: string) {
    const response = await this.client.get(`${API_ENDPOINTS.USERS.BASE}${userId}`)
    return response.data
  }

  async updateUser(userId: string, data: any) {
    const response = await this.client.put(`${API_ENDPOINTS.USERS.BASE}${userId}`, data)
    return response.data
  }

  async deleteUser(userId: string) {
    const response = await this.client.delete(`${API_ENDPOINTS.USERS.BASE}${userId}`)
    return response.data
  }

  // Role methods
  async getAllRoles(skip = 0, limit = 100) {
    const response = await this.client.get(API_ENDPOINTS.ROLES.BASE, {
      params: { skip, limit }
    })
    return response.data
  }

  async createRole(data: any) {
    const response = await this.client.post(API_ENDPOINTS.ROLES.BASE, data)
    return response.data
  }

  async updateRole(roleId: string, data: any) {
    const response = await this.client.put(`${API_ENDPOINTS.ROLES.BASE}${roleId}`, data)
    return response.data
  }

  async deleteRole(roleId: string) {
    const response = await this.client.delete(`${API_ENDPOINTS.ROLES.BASE}${roleId}`)
    return response.data
  }

  // Permission methods
  async getAllPermissions(skip = 0, limit = 100) {
    const response = await this.client.get(API_ENDPOINTS.PERMISSIONS.BASE, {
      params: { skip, limit }
    })
    return response.data
  }

  async createPermission(data: any) {
    const response = await this.client.post(API_ENDPOINTS.PERMISSIONS.BASE, data)
    return response.data
  }

  // Project methods
  async getAllProjects() {
    const response = await this.client.get(API_ENDPOINTS.PROJECTS.BASE)
    return response.data
  }

  async getActiveProjects() {
    const response = await this.client.get(API_ENDPOINTS.PROJECTS.ACTIVE)
    return response.data
  }

  async getProjectById(projectId: string) {
    const response = await this.client.get(`${API_ENDPOINTS.PROJECTS.BASE}/${projectId}`)
    return response.data
  }

  async createProject(data: { name: string; description?: string; isActive: boolean }) {
    const response = await this.client.post(API_ENDPOINTS.PROJECTS.BASE, data)
    return response.data
  }

  async updateProject(projectId: string, data: { name: string; description?: string; isActive: boolean }) {
    const response = await this.client.put(`${API_ENDPOINTS.PROJECTS.BASE}/${projectId}`, data)
    return response.data
  }

  async deleteProject(projectId: string) {
    const response = await this.client.delete(`${API_ENDPOINTS.PROJECTS.BASE}/${projectId}`)
    return response.data
  }

  async getRecentProjects(limit = 10) {
    const response = await this.client.get(API_ENDPOINTS.PROJECTS.RECENT, {
      params: { limit }
    })
    return response.data
  }

  async getProjectStats() {
    const response = await this.client.get(API_ENDPOINTS.PROJECTS.STATS)
    return response.data
  }

  async searchProjects(query: string) {
    const response = await this.client.get(API_ENDPOINTS.PROJECTS.SEARCH, {
      params: { q: query }
    })
    return response.data
  }

}

export const apiClient = new ApiClient()