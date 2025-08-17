'use client'

import { useAuthStore } from '@/store/auth'
import { PublicLayout } from '@/components/layouts/public-layout'
import { HomePage } from '@/components/pages/home'
import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'

export default function Page() {
  const router = useRouter()
  const { isAuthenticated, user, isLoading } = useAuthStore()
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (mounted && isAuthenticated && user) {
      // Redirect authenticated users to dashboard
      router.push('/dashboard')
    }
  }, [mounted, isAuthenticated, user, router])

  // Show loading spinner during initial mount and auth loading
  if (!mounted || isLoading) {
    return (
      <div className="min-h-screen bg-white dark:bg-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    )
  }

  // Redirect authenticated users (this should not render due to useEffect redirect)
  if (isAuthenticated && user) {
    return (
      <div className="min-h-screen bg-white dark:bg-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    )
  }

  // Show public home page for non-authenticated users
  return (
    <PublicLayout>
      <HomePage />
    </PublicLayout>
  )
}