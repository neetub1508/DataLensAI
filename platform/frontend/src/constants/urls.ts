// Development URLs
export const DEV_URLS = {
  FRONTEND: 'http://localhost:3000',
  BACKEND_API: 'http://localhost:8000',
  BACKEND_API_LEGACY: 'http://localhost:8080/api',
  BACKEND_API_V1: 'http://localhost:8000/api/v1',
  DATABASE: 'localhost:5433',
  REDIS: 'localhost:6379',
} as const

// External URLs
export const EXTERNAL_URLS = {
  GOOGLE_FONTS: 'https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap',
  GOOGLE_CLOUD_CONSOLE: 'https://console.cloud.google.com/',
  FACEBOOK_DEVELOPERS: 'https://developers.facebook.com/',
  GITHUB: 'https://github.com',
} as const

// Domain-specific URLs
export const DOMAIN_URLS = {
  COMPANY_DOMAIN: 'datalens.ai',
  SUPPORT_EMAIL: 'support@datalens.ai',
  ADMIN_EMAIL: 'admin@datalens.ai',
  USER_EMAIL: 'user@datalens.ai',
} as const

// Example/Placeholder URLs
export const EXAMPLE_URLS = {
  IMAGE_PLACEHOLDER: 'https://example.com/image.jpg',
  USER_EMAIL_PLACEHOLDER: 'user@example.com',
  DOMAIN_PLACEHOLDER: 'yourdomain.com',
} as const

// OAuth Redirect URLs (Development)
export const OAUTH_DEV_URLS = {
  GOOGLE_CALLBACK: `${DEV_URLS.BACKEND_API_V1}/login/oauth2/code/google`,
  FACEBOOK_CALLBACK: `${DEV_URLS.BACKEND_API_V1}/login/oauth2/code/facebook`,
  GITHUB_CALLBACK: `${DEV_URLS.BACKEND_API_V1}/login/oauth2/code/github`,
} as const

// Auth Page URLs
export const AUTH_URLS = {
  REGISTER: '/register',
  LOGIN: '/login',
  VERIFY_EMAIL: '/verify-email',
  RESET_PASSWORD: '/reset-password',
  AUTH_CALLBACK: '/auth/callback',
} as const