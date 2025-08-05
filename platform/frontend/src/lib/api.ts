import axios, { AxiosInstance, AxiosResponse } from 'axios'
import { toast } from 'react-hot-toast'

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'

class ApiClient {
  private client: AxiosInstance

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
    // Request interceptor to add auth token and project context
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('access_token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        
        // Add current project ID to headers if available
        const currentProject = localStorage.getItem('project-store')
        if (currentProject) {
          try {
            const projectStore = JSON.parse(currentProject)
            if (projectStore.state?.currentProject?.id) {
              config.headers['X-Project-ID'] = projectStore.state.currentProject.id
            }
          } catch (error) {
            console.warn('Failed to parse project store:', error)
          }
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

          const refreshToken = localStorage.getItem('refresh_token')
          if (refreshToken) {
            try {
              const response = await this.client.post('/auth/refresh', {
                refresh_token: refreshToken,
              })
              
              const { access_token, refresh_token: newRefreshToken } = response.data
              localStorage.setItem('access_token', access_token)
              localStorage.setItem('refresh_token', newRefreshToken)
              
              // Retry original request
              originalRequest.headers.Authorization = `Bearer ${access_token}`
              return this.client(originalRequest)
            } catch (refreshError) {
              // Refresh failed, redirect to login
              localStorage.removeItem('access_token')
              localStorage.removeItem('refresh_token')
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
    const response = await this.client.post('/auth/login', { email, password })
    return response.data
  }

  async register(email: string, password: string, locale = 'en') {
    const response = await this.client.post('/auth/register', { 
      email, 
      password, 
      locale 
    })
    return response.data
  }

  async verifyEmail(token: string) {
    const response = await this.client.post('/auth/verify-email', null, {
      params: { token }
    })
    return response.data
  }

  async requestPasswordReset(email: string) {
    const response = await this.client.post('/auth/request-password-reset', null, {
      params: { email }
    })
    return response.data
  }

  async resetPassword(token: string, newPassword: string) {
    const response = await this.client.post('/auth/reset-password', null, {
      params: { token, new_password: newPassword }
    })
    return response.data
  }

  // User methods
  async getCurrentUser() {
    const response = await this.client.get('/users/me')
    return response.data
  }

  async updateCurrentUser(data: any) {
    const response = await this.client.put('/users/me', data)
    return response.data
  }

  async getAllUsers(skip = 0, limit = 100) {
    const response = await this.client.get('/users/', {
      params: { skip, limit }
    })
    return response.data
  }

  async getUserById(userId: string) {
    const response = await this.client.get(`/users/${userId}`)
    return response.data
  }

  async updateUser(userId: string, data: any) {
    const response = await this.client.put(`/users/${userId}`, data)
    return response.data
  }

  async deleteUser(userId: string) {
    const response = await this.client.delete(`/users/${userId}`)
    return response.data
  }

  // Role methods
  async getAllRoles(skip = 0, limit = 100) {
    const response = await this.client.get('/roles/', {
      params: { skip, limit }
    })
    return response.data
  }

  async createRole(data: any) {
    const response = await this.client.post('/roles/', data)
    return response.data
  }

  async updateRole(roleId: string, data: any) {
    const response = await this.client.put(`/roles/${roleId}`, data)
    return response.data
  }

  async deleteRole(roleId: string) {
    const response = await this.client.delete(`/roles/${roleId}`)
    return response.data
  }

  // Permission methods
  async getAllPermissions(skip = 0, limit = 100) {
    const response = await this.client.get('/permissions/', {
      params: { skip, limit }
    })
    return response.data
  }

  async createPermission(data: any) {
    const response = await this.client.post('/permissions/', data)
    return response.data
  }

  // Project methods
  async getUserProjects() {
    const response = await this.client.get('/projects')
    return response.data
  }

  async createProject(data: { name: string; description?: string; settings?: string }) {
    const response = await this.client.post('/projects', data)
    return response.data
  }

  async getProject(projectId: string) {
    const response = await this.client.get(`/projects/${projectId}`)
    return response.data
  }

  async updateProject(projectId: string, data: { name: string; description?: string; settings?: string }) {
    const response = await this.client.put(`/projects/${projectId}`, data)
    return response.data
  }

  async deleteProject(projectId: string) {
    const response = await this.client.delete(`/projects/${projectId}`)
    return response.data
  }

  async archiveProject(projectId: string) {
    const response = await this.client.patch(`/projects/${projectId}/archive`)
    return response.data
  }

  async restoreProject(projectId: string) {
    const response = await this.client.patch(`/projects/${projectId}/restore`)
    return response.data
  }

  async addProjectMember(projectId: string, memberId: string) {
    const response = await this.client.post(`/projects/${projectId}/members/${memberId}`)
    return response.data
  }

  async removeProjectMember(projectId: string, memberId: string) {
    const response = await this.client.delete(`/projects/${projectId}/members/${memberId}`)
    return response.data
  }

  async searchProjects(query: string) {
    const response = await this.client.get('/projects/search', {
      params: { q: query }
    })
    return response.data
  }

  async getUserActiveProjectCount() {
    const response = await this.client.get('/projects/count')
    return response.data
  }

  async checkProjectAccess(projectId: string) {
    const response = await this.client.get(`/projects/${projectId}/access`)
    return response.data
  }
}

export const apiClient = new ApiClient()