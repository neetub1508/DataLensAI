'use client'

import { useEffect } from 'react'

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {
  useEffect(() => {
    console.error('Global application error:', error)
  }, [error])

  return (
    <html>
      <body>
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="max-w-md w-full text-center space-y-6">
            <div className="space-y-2">
              <h1 className="text-6xl font-bold text-gray-900">
                500
              </h1>
              <h2 className="text-2xl font-semibold text-gray-900">
                Application Error
              </h2>
              <p className="text-gray-600">
                A critical error has occurred. Please refresh the page or try again later.
              </p>
            </div>
            
            <div className="space-y-4">
              <button
                onClick={reset}
                className="w-full bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
              >
                Try again
              </button>
              
              <button
                onClick={() => window.location.href = '/'}
                className="w-full bg-gray-100 text-gray-900 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors"
              >
                Go to homepage
              </button>
            </div>
            
            {process.env.NODE_ENV === 'development' && (
              <div className="mt-8 p-4 bg-red-50 border border-red-200 rounded-md text-left">
                <h3 className="text-sm font-medium text-red-800 mb-2">
                  Error Details:
                </h3>
                <pre className="text-xs text-red-600 whitespace-pre-wrap">
                  {error.message}
                </pre>
                {error.stack && (
                  <pre className="text-xs text-red-500 whitespace-pre-wrap mt-2">
                    {error.stack}
                  </pre>
                )}
              </div>
            )}
          </div>
        </div>
      </body>
    </html>
  )
}