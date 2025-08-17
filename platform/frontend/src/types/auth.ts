export interface User {
  id: string
  email: string
  isVerified: boolean
  status: string
  locale: string
  createdAt: string
  updatedAt: string
  lastLoginAt?: string
  roles: string[]
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface RegisterCredentials {
  email: string
  password: string
  locale?: string
}

export interface AuthToken {
  access_token: string
  refresh_token: string
  token_type: string
}

export interface AuthState {
  user: User | null
  token: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
}