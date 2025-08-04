'use client'

import { useEffect } from 'react'
import Link from 'next/link'

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {
  useEffect(() => {
    console.error('Application error:', error)
  }, [error])

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
      <div className="max-w-md w-full text-center space-y-6">
        <div className="space-y-2">
          <h1 className="text-6xl font-bold text-gray-900 dark:text-white">
            500
          </h1>
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white">
            Something went wrong!
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            An unexpected error has occurred. We're working to fix this issue.
          </p>
        </div>
        
        <div className="space-y-4">
          <button
            onClick={reset}
            className="w-full bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
          >
            Try again
          </button>
          
          <Link
            href="/"
            className="block w-full bg-gray-100 text-gray-900 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700"
          >
            Go back home
          </Link>
        </div>
        
        {process.env.NODE_ENV === 'development' && (
          <div className="mt-8 p-4 bg-red-50 border border-red-200 rounded-md text-left">
            <h3 className="text-sm font-medium text-red-800 mb-2">
              Development Error Details:
            </h3>
            <pre className="text-xs text-red-600 whitespace-pre-wrap">
              {error.message}
            </pre>
          </div>
        )}
      </div>
    </div>
  )
}