'use client'

import { useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { useAuthStore } from '@/store/auth'

export default function AuthCallbackPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { setToken } = useAuthStore()

  useEffect(() => {
    const token = searchParams.get('token')
    const userId = searchParams.get('userId')
    const error = searchParams.get('error')

    if (error) {
      console.error('OAuth authentication failed:', error)
      router.push('/register?error=oauth-failed')
      return
    }

    if (token && userId) {
      // Store the token
      setToken(token)
      
      // Redirect to dashboard
      router.push('/dashboard')
    } else {
      // No token received, redirect to register
      router.push('/register?error=oauth-token-missing')
    }
  }, [searchParams, router, setToken])

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-green-600 mx-auto"></div>
        <p className="mt-4 text-lg text-gray-600 dark:text-gray-400">
          Completing your sign up...
        </p>
      </div>
    </div>
  )
}