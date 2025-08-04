export interface User {
  id: string
  email: string
  is_verified: boolean
  status: string
  locale: string
  created_at: string
  updated_at: string
  last_login_at?: string
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