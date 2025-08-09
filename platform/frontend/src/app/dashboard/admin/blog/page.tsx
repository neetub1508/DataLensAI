'use client'

import { useState, useEffect } from 'react'
import { HTTP_METHODS, API_CONFIG } from '@/constants'
import Link from 'next/link'
import { useAuthStore } from '@/store/auth'

interface BlogPost {
  id: string
  title: string
  slug: string
  excerpt: string
  status: string
  author: {
    id: string
    email: string
  }
  createdAt: string
  updatedAt: string
  categories: Array<{
    id: string
    name: string
  }>
  tags: string[]
}

export default function AdminBlogManagementPage() {
  const { token } = useAuthStore()
  const [pendingPosts, setPendingPosts] = useState<BlogPost[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (token) {
      fetchPendingPosts()
    }
  }, [token])

  const fetchPendingPosts = async () => {
    try {
      setLoading(true)
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/pending-posts`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        const data = await response.json()
        setPendingPosts(data.content || [])
      } else {
        setError('Failed to fetch pending blog posts')
      }
    } catch (err) {
      setError('Failed to fetch pending blog posts')
      console.error('Error fetching pending posts:', err)
    } finally {
      setLoading(false)
    }
  }

  const approvePost = async (postId: string) => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}/approve`, {
        method: HTTP_METHODS.POST,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        // Refresh the pending posts list
        fetchPendingPosts()
        alert('Post approved and published!')
      } else {
        alert('Failed to approve post')
      }
    } catch (err) {
      alert('Failed to approve post')
      console.error('Error approving post:', err)
    }
  }

  const rejectPost = async (postId: string) => {
    if (!confirm('Are you sure you want to reject this post?')) {
      return
    }

    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}/reject`, {
        method: HTTP_METHODS.POST,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        // Refresh the pending posts list
        fetchPendingPosts()
        alert('Post rejected')
      } else {
        alert('Failed to reject post')
      }
    } catch (err) {
      alert('Failed to reject post')
      console.error('Error rejecting post:', err)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
          <p className="mt-4 text-gray-600 dark:text-gray-400">Loading pending blog posts...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Blog Post Approval
        </h1>
        <p className="mt-2 text-gray-600 dark:text-gray-400">
          Review and approve blog posts from users
        </p>
      </div>

      {/* Error State */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
          <p className="text-red-600 dark:text-red-400">{error}</p>
        </div>
      )}

      {/* Pending Posts */}
      {pendingPosts.length === 0 ? (
        <div className="text-center py-12">
          <div className="mx-auto w-24 h-24 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
            <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
            No pending blog posts
          </h3>
          <p className="text-gray-600 dark:text-gray-400">
            All blog posts have been reviewed.
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {pendingPosts.map((post) => (
            <div
              key={post.id}
              className="bg-white dark:bg-gray-800 shadow rounded-lg p-6"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                    {post.title}
                  </h3>
                  
                  <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 space-x-4 mb-3">
                    <span>By {post.author.email}</span>
                    <span>•</span>
                    <span>Submitted: {formatDate(post.createdAt)}</span>
                  </div>

                  {/* Categories */}
                  {post.categories.length > 0 && (
                    <div className="flex flex-wrap gap-2 mb-3">
                      {post.categories.map((category) => (
                        <span
                          key={category.id}
                          className="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 rounded-full"
                        >
                          {category.name}
                        </span>
                      ))}
                    </div>
                  )}

                  {/* Excerpt */}
                  {post.excerpt && (
                    <p className="text-gray-600 dark:text-gray-400 mb-4">
                      {post.excerpt}
                    </p>
                  )}

                  {/* Tags */}
                  {post.tags && post.tags.length > 0 && (
                    <div className="flex flex-wrap gap-1 mb-4">
                      {post.tags.map((tag) => (
                        <span
                          key={tag}
                          className="px-2 py-1 text-xs bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded"
                        >
                          #{tag}
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                <div className="flex items-center space-x-2 ml-4">
                  {/* Preview/View Full Post */}
                  <button
                    onClick={() => {
                      // TODO: Implement post preview modal
                      alert('Preview functionality to be implemented')
                    }}
                    className="px-3 py-1 text-sm text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 border border-gray-300 dark:border-gray-600 rounded"
                  >
                    Preview
                  </button>

                  {/* Approve */}
                  <button
                    onClick={() => approvePost(post.id)}
                    className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded transition-colors"
                  >
                    Approve & Publish
                  </button>

                  {/* Reject */}
                  <button
                    onClick={() => rejectPost(post.id)}
                    className="px-4 py-2 text-sm bg-red-600 hover:bg-red-700 text-white rounded transition-colors"
                  >
                    Reject
                  </button>
                </div>
              </div>

              {/* Post Content Preview */}
              <div className="mt-4 p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Content Preview:
                </h4>
                <div
                  className="text-sm text-gray-600 dark:text-gray-400 line-clamp-3"
                  dangerouslySetInnerHTML={{ 
                    __html: post.excerpt || (post.title ? 'No excerpt provided' : 'No content preview available')
                  }}
                />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}