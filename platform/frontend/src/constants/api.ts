export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  FALLBACK_URL: 'http://localhost:8000/api/v1',
  TIMEOUT: 30000,
} as const

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    VERIFY_EMAIL: '/auth/verify-email',
    REQUEST_PASSWORD_RESET: '/auth/request-password-reset',
    RESET_PASSWORD: '/auth/reset-password',
    REFRESH: '/auth/refresh',
  },
  USERS: {
    ME: '/users/me',
    BASE: '/users/',
  },
  ROLES: {
    BASE: '/roles/',
  },
  PERMISSIONS: {
    BASE: '/permissions/',
  },
  PROJECTS: {
    BASE: '/projects',
    SEARCH: '/projects/search',
    COUNT: '/projects/count',
  },
} as const