'use client'

import { useAuthStore } from '@/store/auth'
import { PublicLayout } from '@/components/layouts/public-layout'
import { DashboardLayout } from '@/components/layouts/dashboard-layout'
import { HomePage } from '@/components/pages/home'
import { Dashboard } from '@/components/pages/dashboard'
import { useEffect, useState } from 'react'

export default function Page() {
  const { isAuthenticated, user, isLoading } = useAuthStore()
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  useEffect(() => {
    if (mounted) {
      // Initialize auth state on page load
      const token = localStorage.getItem('access_token')
      if (token && !isAuthenticated) {
        useAuthStore.getState().refreshUser()
      }
    }
  }, [mounted, isAuthenticated])

  // Show loading spinner during initial mount and auth loading
  if (!mounted || isLoading) {
    return (
      <div className="min-h-screen bg-white dark:bg-gray-900 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
      </div>
    )
  }

  // Show dashboard for authenticated users
  if (isAuthenticated && user) {
    return (
      <DashboardLayout>
        <Dashboard />
      </DashboardLayout>
    )
  }

  // Show public home page for non-authenticated users
  return (
    <PublicLayout>
      <HomePage />
    </PublicLayout>
  )
}