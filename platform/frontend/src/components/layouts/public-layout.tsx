'use client'

import { ReactNode } from 'react'
import Link from 'next/link'
import { useTheme } from 'next-themes'
import { SunIcon, MoonIcon } from '@heroicons/react/24/outline'

interface PublicLayoutProps {
  children: ReactNode
}

export function PublicLayout({ children }: PublicLayoutProps) {
  const { theme, setTheme } = useTheme()

  return (
    <div className="min-h-screen bg-white dark:bg-gray-900">
      {/* Navigation */}
      <nav className="bg-white dark:bg-gray-900 shadow-sm border-b border-gray-200 dark:border-gray-700">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            {/* Logo */}
            <div className="flex items-center">
              <Link href="/" className="flex items-center space-x-2">
                <div className="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-sm">DL</span>
                </div>
                <span className="text-xl font-semibold text-gray-900 dark:text-white">
                  Data Lens AI
                </span>
              </Link>
            </div>

            {/* Navigation Links */}
            <div className="hidden md:flex items-center space-x-8">
              <Link 
                href="/" 
                className="text-gray-700 dark:text-gray-300 hover:text-green-600 dark:hover:text-green-400 px-3 py-2 text-sm font-medium"
              >
                Home
              </Link>
              <Link 
                href="/pricing" 
                className="text-gray-700 dark:text-gray-300 hover:text-green-600 dark:hover:text-green-400 px-3 py-2 text-sm font-medium"
              >
                Pricing
              </Link>
              <Link 
                href="/about" 
                className="text-gray-700 dark:text-gray-300 hover:text-green-600 dark:hover:text-green-400 px-3 py-2 text-sm font-medium"
              >
                About Us
              </Link>
              <Link 
                href="/blog" 
                className="text-gray-700 dark:text-gray-300 hover:text-green-600 dark:hover:text-green-400 px-3 py-2 text-sm font-medium"
              >
                Blog
              </Link>
            </div>

            {/* Right side */}
            <div className="flex items-center space-x-4">
              {/* Theme toggle */}
              <button
                onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
                className="p-2 text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                suppressHydrationWarning
              >
                {theme === 'dark' ? (
                  <SunIcon className="h-5 w-5" />
                ) : (
                  <MoonIcon className="h-5 w-5" />
                )}
              </button>

              {/* Auth buttons */}
              <Link
                href="/login"
                className="text-gray-700 dark:text-gray-300 hover:text-green-600 dark:hover:text-green-400 px-3 py-2 text-sm font-medium"
              >
                Sign In
              </Link>
              <Link
                href="/register"
                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
              >
                Get Data Lens AI Free
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Main content */}
      <main>{children}</main>

      {/* Footer */}
      <footer className="bg-gray-50 dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="col-span-1 md:col-span-2">
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-sm">DL</span>
                </div>
                <span className="text-xl font-semibold text-gray-900 dark:text-white">
                  Data Lens AI
                </span>
              </div>
              <p className="text-gray-600 dark:text-gray-400 max-w-md">
                Powerful analytics platform with AI-driven insights. Transform your data into actionable intelligence.
              </p>
            </div>
            
            <div>
              <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-4">Product</h3>
              <ul className="space-y-2">
                <li><Link href="/features" className="text-gray-600 dark:text-gray-400 hover:text-green-600">Features</Link></li>
                <li><Link href="/pricing" className="text-gray-600 dark:text-gray-400 hover:text-green-600">Pricing</Link></li>
                <li><Link href="/docs" className="text-gray-600 dark:text-gray-400 hover:text-green-600">Documentation</Link></li>
              </ul>
            </div>
            
            <div>
              <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-4">Company</h3>
              <ul className="space-y-2">
                <li><Link href="/about" className="text-gray-600 dark:text-gray-400 hover:text-green-600">About</Link></li>
                <li><Link href="/blog" className="text-gray-600 dark:text-gray-400 hover:text-green-600">Blog</Link></li>
                <li><Link href="/contact" className="text-gray-600 dark:text-gray-400 hover:text-green-600">Contact</Link></li>
              </ul>
            </div>
          </div>
          
          <div className="border-t border-gray-200 dark:border-gray-700 mt-8 pt-8">
            <p className="text-center text-gray-600 dark:text-gray-400 text-sm">
              © 2025 Data Lens AI. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}