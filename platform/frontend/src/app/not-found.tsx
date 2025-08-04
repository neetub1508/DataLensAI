import Link from 'next/link'

export default function NotFound() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
      <div className="max-w-md w-full text-center space-y-6">
        <div className="space-y-2">
          <h1 className="text-6xl font-bold text-gray-900 dark:text-white">
            404
          </h1>
          <h2 className="text-2xl font-semibold text-gray-900 dark:text-white">
            Page not found
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            Sorry, we couldn't find the page you're looking for.
          </p>
        </div>
        
        <div className="space-y-4">
          <Link
            href="/"
            className="block w-full bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 transition-colors"
          >
            Go back home
          </Link>
          
          <Link
            href="/register"
            className="block w-full bg-gray-100 text-gray-900 px-4 py-2 rounded-md hover:bg-gray-200 transition-colors dark:bg-gray-800 dark:text-gray-100 dark:hover:bg-gray-700"
          >
            Get started with Data Lens AI
          </Link>
        </div>
      </div>
    </div>
  )
}