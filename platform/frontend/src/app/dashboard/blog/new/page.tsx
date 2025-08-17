'use client'

import { useState, useEffect } from 'react'
import { HTTP_METHODS, API_CONFIG, EXAMPLE_URLS } from '@/constants'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { useAuthStore } from '@/store/auth'
import DashboardSidebar from '@/components/layouts/dashboard-sidebar'

interface BlogCategory {
  id: string
  name: string
  slug: string
}

export default function NewBlogPostPage() {
  const router = useRouter()
  const { user, token, isAuthenticated, logout } = useAuthStore()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    excerpt: '',
    featuredImageUrl: '',
    tags: '',
    categoryIds: [] as string[]
  })
  
  const [publishImmediately, setPublishImmediately] = useState(false)
  
  const [categories, setCategories] = useState<BlogCategory[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login')
      return
    }
  }, [isAuthenticated, router])

  useEffect(() => {
    if (token) {
      fetchCategories()
    }
  }, [token])

  // Check if user is admin
  const isAdmin = user?.roles ? (
    Array.isArray(user.roles) 
      ? user.roles.some(role => role.toLowerCase() === 'admin')
      : Object.values(user.roles as Record<string, string>).some(role => role.toLowerCase() === 'admin')
  ) : false

  const fetchCategories = async () => {
    try {
      // Try admin endpoint first, fall back to public if not admin
      const endpoint = isAdmin 
        ? `${API_CONFIG.BASE_URL}/blog/admin/categories`
        : `${API_CONFIG.BASE_URL}/blog/categories`
        
      const response = await fetch(endpoint, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })
      
      if (response.ok) {
        const data = await response.json()
        setCategories(data)
      }
    } catch (err) {
      console.error('Error fetching categories:', err)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleCategoryChange = (categoryId: string, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      categoryIds: checked
        ? [...prev.categoryIds, categoryId]
        : prev.categoryIds.filter(id => id !== categoryId)
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      const postData = {
        ...formData,
        tags: formData.tags ? formData.tags.split(',').map(tag => tag.trim()).filter(tag => tag) : []
      }

      // Create the post first
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/posts`, {
        method: HTTP_METHODS.POST,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(postData)
      })

      if (response.ok) {
        const createdPost = await response.json()
        
        // If admin chose to publish immediately, approve the post
        if (isAdmin && publishImmediately) {
          try {
            await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${createdPost.id}/approve`, {
              method: HTTP_METHODS.POST,
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
              }
            })
          } catch (approveError) {
            console.error('Error auto-approving post:', approveError)
            // Continue anyway - the post was created successfully
          }
        }
        
        router.push('/dashboard/blog')
      } else {
        const errorData = await response.json()
        setError(errorData.message || 'Failed to create blog post')
      }
    } catch (err) {
      setError('Failed to create blog post')
      console.error('Error creating post:', err)
    } finally {
      setLoading(false)
    }
  }

  if (!isAuthenticated || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-green-600"></div>
      </div>
    )
  }

  return (
    <div className="flex h-screen bg-gray-50 dark:bg-gray-900">
      <DashboardSidebar isOpen={sidebarOpen} onToggle={() => setSidebarOpen(!sidebarOpen)} />
      
      <div className="flex-1 flex flex-col overflow-hidden lg:ml-64">
        {/* Header */}
        <header className="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700">
          <div className="px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center py-4">
              <div className="flex items-center">
                <button
                  onClick={() => setSidebarOpen(true)}
                  className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-green-500"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                  </svg>
                </button>
                <h1 className="ml-2 lg:ml-0 text-2xl font-bold text-gray-900 dark:text-white">
                  Create New Blog Post
                </h1>
              </div>
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2">
                  <div className="text-sm text-gray-700 dark:text-gray-300">
                    Welcome, <span className="font-medium">{user.email}</span>
                  </div>
                  {isAdmin && (
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800 dark:text-red-100">
                      Admin
                    </span>
                  )}
                </div>
                <button
                  onClick={logout}
                  className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Sign Out
                </button>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto">
          <div className="p-6">
            <div className="max-w-4xl mx-auto">
              {/* Page Header */}
              <div className="flex items-center justify-between mb-8">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Create New Blog Post
                  </h2>
                  <p className="text-gray-600 dark:text-gray-400">
                    Write and publish your thoughts
                  </p>
                </div>
                <Link
                  href="/dashboard/blog"
                  className="px-4 py-2 text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 border border-gray-300 dark:border-gray-600 rounded-lg transition-colors"
                >
                  Cancel
                </Link>
              </div>

              {/* Error Message */}
              {error && (
                <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                  <p className="text-red-600 dark:text-red-400">{error}</p>
                </div>
              )}

              {/* Form */}
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Title */}
                <div>
                  <label htmlFor="title" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Title *
                  </label>
                  <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    placeholder="Enter your blog post title"
                  />
                </div>

                {/* Excerpt */}
                <div>
                  <label htmlFor="excerpt" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Excerpt
                  </label>
                  <textarea
                    id="excerpt"
                    name="excerpt"
                    value={formData.excerpt}
                    onChange={handleInputChange}
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    placeholder="Brief summary of your post (optional)"
                  />
                </div>

                {/* Content */}
                <div>
                  <label htmlFor="content" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Content *
                  </label>
                  <textarea
                    id="content"
                    name="content"
                    value={formData.content}
                    onChange={handleInputChange}
                    required
                    rows={15}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    placeholder="Write your blog post content here... (You can use HTML)"
                  />
                  <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                    You can use HTML formatting in your content.
                  </p>
                </div>

                {/* Featured Image URL */}
                <div>
                  <label htmlFor="featuredImageUrl" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Featured Image URL
                  </label>
                  <input
                    type="url"
                    id="featuredImageUrl"
                    name="featuredImageUrl"
                    value={formData.featuredImageUrl}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    placeholder={EXAMPLE_URLS.IMAGE_PLACEHOLDER}
                  />
                </div>

                {/* Tags */}
                <div>
                  <label htmlFor="tags" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                    Tags
                  </label>
                  <input
                    type="text"
                    id="tags"
                    name="tags"
                    value={formData.tags}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    placeholder="tech, data science, tutorial (comma-separated)"
                  />
                  <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                    Separate tags with commas
                  </p>
                </div>

                {/* Categories */}
                {categories.length > 0 && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Categories
                    </label>
                    <div className="space-y-2">
                      {categories.map((category) => (
                        <label key={category.id} className="flex items-center">
                          <input
                            type="checkbox"
                            checked={formData.categoryIds.includes(category.id)}
                            onChange={(e) => handleCategoryChange(category.id, e.target.checked)}
                            className="rounded border-gray-300 text-green-600 shadow-sm focus:border-green-300 focus:ring focus:ring-green-200 focus:ring-opacity-50"
                          />
                          <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
                            {category.name}
                          </span>
                        </label>
                      ))}
                    </div>
                  </div>
                )}

                {/* Admin Options */}
                {isAdmin && (
                  <div className="border-t pt-6">
                    <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                      Admin Options
                    </h3>
                    <div className="space-y-3">
                      <label className="flex items-center">
                        <input
                          type="checkbox"
                          checked={publishImmediately}
                          onChange={(e) => setPublishImmediately(e.target.checked)}
                          className="rounded border-gray-300 text-green-600 shadow-sm focus:border-green-300 focus:ring focus:ring-green-200 focus:ring-opacity-50"
                        />
                        <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
                          Publish immediately (skip approval process)
                        </span>
                      </label>
                      <p className="text-xs text-gray-500 dark:text-gray-400 ml-5">
                        When checked, this post will be published immediately without requiring approval.
                      </p>
                    </div>
                  </div>
                )}

                {/* Submit Button */}
                <div className="flex items-center justify-end space-x-4 pt-6">
                  <Link
                    href="/dashboard/blog"
                    className="px-4 py-2 text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200"
                  >
                    Cancel
                  </Link>
                  <button
                    type="submit"
                    disabled={loading}
                    className="px-6 py-2 bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white rounded-lg transition-colors"
                  >
                    {loading ? 'Creating...' : (isAdmin && publishImmediately) ? 'Create & Publish' : 'Create Post'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}