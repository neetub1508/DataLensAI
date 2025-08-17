'use client'

import { useState, useEffect } from 'react'
import { BLOG_POST_STATUS, HTTP_METHODS, API_CONFIG } from '@/constants'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/store/auth'
import DashboardSidebar from '@/components/layouts/dashboard-sidebar'

interface BlogPost {
  id: string
  title: string
  slug: string
  excerpt: string
  status: string
  createdAt: string
  updatedAt: string
  publishedAt?: string
}

export default function BlogManagementPage() {
  const router = useRouter()
  const { user, token, isAuthenticated, logout } = useAuthStore()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [posts, setPosts] = useState<BlogPost[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login')
      return
    }
  }, [isAuthenticated, router])

  useEffect(() => {
    if (token) {
      fetchUserPosts()
    }
  }, [token])

  // Check if user is admin for UI purposes
  const isAdmin = user?.roles ? (
    Array.isArray(user.roles) 
      ? user.roles.some(role => role.toLowerCase() === 'admin')
      : Object.values(user.roles as Record<string, string>).some(role => role.toLowerCase() === 'admin')
  ) : false

  const fetchUserPosts = async () => {
    try {
      setLoading(true)
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/my-posts`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        const data = await response.json()
        setPosts(data.content || [])
      } else {
        setError('Failed to fetch your blog posts')
      }
    } catch (err) {
      setError('Failed to fetch your blog posts')
      console.error('Error fetching posts:', err)
    } finally {
      setLoading(false)
    }
  }

  const submitForApproval = async (postId: string) => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/posts/${postId}/submit`, {
        method: HTTP_METHODS.POST,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        // Refresh the posts list
        fetchUserPosts()
        alert('Post submitted for approval!')
      } else {
        alert('Failed to submit post for approval')
      }
    } catch (err) {
      alert('Failed to submit post for approval')
      console.error('Error submitting post:', err)
    }
  }

  const deletePost = async (postId: string) => {
    if (!confirm('Are you sure you want to delete this post?')) {
      return
    }

    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/posts/${postId}`, {
        method: HTTP_METHODS.DELETE,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        // Refresh the posts list
        fetchUserPosts()
        alert('Post deleted successfully!')
      } else {
        alert('Failed to delete post')
      }
    } catch (err) {
      alert('Failed to delete post')
      console.error('Error deleting post:', err)
    }
  }

  const getStatusBadge = (status: string) => {
    const statusClasses = {
      DRAFT: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300',
      PENDING_APPROVAL: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
      PUBLISHED: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300',
      REJECTED: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300'
    }
    
    return (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${statusClasses[status as keyof typeof statusClasses]}`}>
        {status.replace('_', ' ')}
      </span>
    )
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
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
                  My Blog Posts
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
            <div className="max-w-7xl mx-auto">
              {/* Page Header */}
              <div className="flex items-center justify-between mb-8">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    Blog Dashboard
                  </h2>
                  <p className="text-gray-600 dark:text-gray-400">
                    Create and manage your blog posts
                  </p>
                </div>
                <Link
                  href="/dashboard/blog/new"
                  className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                >
                  New Post
                </Link>
              </div>

              {/* Loading State */}
              {loading && (
                <div className="text-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
                  <p className="mt-4 text-gray-600 dark:text-gray-400">Loading your blog posts...</p>
                </div>
              )}

              {/* Content - only show when not loading */}
              {!loading && (
                <>
                  {/* Error State */}
                  {error && (
                    <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                      <p className="text-red-600 dark:text-red-400">{error}</p>
                    </div>
                  )}

                  {/* Posts List */}
                  {posts.length === 0 ? (
                    <div className="text-center py-12">
                      <div className="mx-auto w-24 h-24 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
                        <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </div>
                      <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                        No blog posts yet
                      </h3>
                      <p className="text-gray-600 dark:text-gray-400 mb-6">
                        Start sharing your thoughts by creating your first blog post.
                      </p>
                      <Link
                        href="/dashboard/blog/new"
                        className="inline-flex items-center px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
                      >
                        Create Your First Post
                      </Link>
                    </div>
                  ) : (
                    <div className="bg-white dark:bg-gray-800 shadow rounded-lg overflow-hidden">
                      <div className="px-4 py-5 sm:p-6">
                        <div className="space-y-6">
                          {posts.map((post) => (
                            <div
                              key={post.id}
                              className="border-b border-gray-200 dark:border-gray-700 last:border-b-0 pb-6 last:pb-0"
                            >
                              <div className="flex items-start justify-between">
                                <div className="flex-1 min-w-0">
                                  <div className="flex items-center space-x-3 mb-2">
                                    <h3 className="text-lg font-medium text-gray-900 dark:text-white truncate">
                                      {post.title}
                                    </h3>
                                    {getStatusBadge(post.status)}
                                  </div>
                                  
                                  {post.excerpt && (
                                    <p className="text-gray-600 dark:text-gray-400 text-sm mb-3 line-clamp-2">
                                      {post.excerpt}
                                    </p>
                                  )}
                                  
                                  <div className="flex items-center text-xs text-gray-500 dark:text-gray-400 space-x-4">
                                    <span>Created: {formatDate(post.createdAt)}</span>
                                    <span>Updated: {formatDate(post.updatedAt)}</span>
                                    {post.publishedAt && (
                                      <span>Published: {formatDate(post.publishedAt)}</span>
                                    )}
                                  </div>
                                </div>
                                
                                <div className="flex items-center space-x-2 ml-4">
                                  {/* View Post (if published) */}
                                  {post.status === BLOG_POST_STATUS.PUBLISHED && (
                                    <Link
                                      href={`/blog/${post.slug}`}
                                      target="_blank"
                                      className="p-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                                      title="View Post"
                                    >
                                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                      </svg>
                                    </Link>
                                  )}
                                  
                                  {/* Edit Post */}
                                  <Link
                                    href={`/dashboard/blog/edit/${post.id}`}
                                    className="p-2 text-gray-400 hover:text-blue-600 dark:hover:text-blue-400"
                                    title="Edit Post"
                                  >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                    </svg>
                                  </Link>
                                  
                                  {/* Submit for Approval (if draft) */}
                                  {post.status === BLOG_POST_STATUS.DRAFT && (
                                    <button
                                      onClick={() => submitForApproval(post.id)}
                                      className="p-2 text-gray-400 hover:text-green-600 dark:hover:text-green-400"
                                      title="Submit for Approval"
                                    >
                                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                      </svg>
                                    </button>
                                  )}
                                  
                                  {/* Delete Post */}
                                  <button
                                    onClick={() => deletePost(post.id)}
                                    className="p-2 text-gray-400 hover:text-red-600 dark:hover:text-red-400"
                                    title="Delete Post"
                                  >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                  </button>
                            </div>
                          </div>
                        </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}