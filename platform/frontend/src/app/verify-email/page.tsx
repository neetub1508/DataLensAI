'use client'

import { useEffect, useState } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import Link from 'next/link'
import { useAuthStore } from '@/store/auth'
import { CheckCircleIcon, ExclamationCircleIcon } from '@heroicons/react/24/outline'

export default function VerifyEmailPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { verifyEmail } = useAuthStore()
  
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading')
  const [message, setMessage] = useState('')

  useEffect(() => {
    const token = searchParams.get('token')
    
    if (!token) {
      setStatus('error')
      setMessage('No verification token provided')
      return
    }

    const handleVerification = async () => {
      try {
        await verifyEmail(token)
        setStatus('success')
        setMessage('Your email has been successfully verified! You can now sign in to your account.')
      } catch (error: any) {
        setStatus('error')
        setMessage(error.response?.data?.detail || 'Email verification failed. The token may be invalid or expired.')
      }
    }

    handleVerification()
  }, [searchParams, verifyEmail])

  const LoadingState = () => (
    <div className="flex flex-col items-center space-y-4">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      <p className="text-lg text-gray-600 dark:text-gray-400">Verifying your email...</p>
    </div>
  )

  const SuccessState = () => (
    <div className="flex flex-col items-center space-y-6">
      <CheckCircleIcon className="h-16 w-16 text-green-600" />
      <div className="text-center space-y-2">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Email Verified!</h1>
        <p className="text-gray-600 dark:text-gray-400 max-w-md">{message}</p>
      </div>
      <div className="space-y-3 w-full max-w-sm">
        <Link
          href="/login"
          className="block w-full bg-green-600 text-white text-center px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
        >
          Sign In Now
        </Link>
        <Link
          href="/"
          className="block w-full bg-gray-100 text-gray-900 text-center px-4 py-2 rounded-md hover:bg-gray-200 transition-colors dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700"
        >
          Go to Home
        </Link>
      </div>
    </div>
  )

  const ErrorState = () => (
    <div className="flex flex-col items-center space-y-6">
      <ExclamationCircleIcon className="h-16 w-16 text-red-600" />
      <div className="text-center space-y-2">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Verification Failed</h1>
        <p className="text-gray-600 dark:text-gray-400 max-w-md">{message}</p>
      </div>
      <div className="space-y-3 w-full max-w-sm">
        <Link
          href="/register"
          className="block w-full bg-green-600 text-white text-center px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
        >
          Create New Account
        </Link>
        <Link
          href="/login"
          className="block w-full bg-gray-100 text-gray-900 text-center px-4 py-2 rounded-md hover:bg-gray-200 transition-colors dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700"
        >
          Try to Sign In
        </Link>
      </div>
    </div>
  )

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full text-center space-y-8">
        {status === 'loading' && <LoadingState />}
        {status === 'success' && <SuccessState />}
        {status === 'error' && <ErrorState />}
      </div>
    </div>
  )
}