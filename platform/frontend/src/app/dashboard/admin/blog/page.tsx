'use client'

import { useState, useEffect } from 'react'
import { HTTP_METHODS, API_CONFIG } from '@/constants'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/store/auth'
import DashboardSidebar from '@/components/layouts/dashboard-sidebar'

interface BlogPost {
  id: string
  title: string
  slug: string
  excerpt: string
  content: string
  status: string
  author: {
    id: string
    email: string
  }
  createdAt: string
  updatedAt: string
  featuredImageUrl?: string
  categories: Array<{
    id: string
    name: string
  }>
  tags: string[]
}

interface BlogPostPreviewModalProps {
  post: BlogPost | null
  isOpen: boolean
  onClose: () => void
  onApprove: (postId: string) => void
  onReject: (postId: string) => void
}

// Blog Post Preview Modal Component
function BlogPostPreviewModal({ post, isOpen, onClose, onApprove, onReject }: BlogPostPreviewModalProps) {
  if (!isOpen || !post) return null

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const handleApprove = () => {
    onApprove(post.id)
    onClose()
  }

  const handleReject = () => {
    onReject(post.id)
    onClose()
  }

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        {/* Background overlay */}
        <div 
          className="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75"
          onClick={onClose}
        />

        {/* Modal panel */}
        <div className="inline-block w-full max-w-4xl p-6 my-8 overflow-hidden text-left align-middle transition-all transform bg-white dark:bg-gray-800 shadow-xl rounded-lg">
          {/* Header */}
          <div className="flex items-center justify-between mb-6 pb-4 border-b border-gray-200 dark:border-gray-700">
            <div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
                {post.title}
              </h2>
              <div className="flex items-center text-sm text-gray-500 dark:text-gray-400 space-x-4 mt-2">
                <span>By {post.author.email}</span>
                <span>•</span>
                <span>Submitted: {formatDate(post.createdAt)}</span>
              </div>
            </div>
            <button
              onClick={onClose}
              className="p-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          {/* Featured Image */}
          {post.featuredImageUrl && (
            <div className="mb-6">
              <img
                src={post.featuredImageUrl}
                alt={post.title}
                className="w-full h-64 object-cover rounded-lg"
              />
            </div>
          )}

          {/* Categories and Tags */}
          <div className="mb-6">
            {post.categories.length > 0 && (
              <div className="mb-3">
                <span className="text-sm font-medium text-gray-700 dark:text-gray-300 mr-2">Categories:</span>
                <div className="inline-flex flex-wrap gap-2">
                  {post.categories.map((category) => (
                    <span
                      key={category.id}
                      className="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 rounded-full"
                    >
                      {category.name}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {post.tags && post.tags.length > 0 && (
              <div>
                <span className="text-sm font-medium text-gray-700 dark:text-gray-300 mr-2">Tags:</span>
                <div className="inline-flex flex-wrap gap-1">
                  {post.tags.map((tag) => (
                    <span
                      key={tag}
                      className="px-2 py-1 text-xs bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 rounded"
                    >
                      #{tag}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Excerpt */}
          {post.excerpt && (
            <div className="mb-6">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">Excerpt</h3>
              <p className="text-gray-600 dark:text-gray-400 italic">
                {post.excerpt}
              </p>
            </div>
          )}

          {/* Content */}
          <div className="mb-8">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Content</h3>
            <div className="max-h-96 overflow-y-auto">
              <div
                className="prose dark:prose-invert max-w-none text-gray-700 dark:text-gray-300"
                dangerouslySetInnerHTML={{ __html: post.content }}
              />
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center justify-end space-x-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <button
              onClick={onClose}
              className="px-4 py-2 text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 border border-gray-300 dark:border-gray-600 rounded-lg transition-colors"
            >
              Close
            </button>
            <button
              onClick={handleReject}
              className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors"
            >
              Reject
            </button>
            <button
              onClick={handleApprove}
              className="px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
            >
              Approve & Publish
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default function AdminBlogManagementPage() {
  const router = useRouter()
  const { user, token, isAuthenticated, logout } = useAuthStore()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [pendingPosts, setPendingPosts] = useState<BlogPost[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [previewPost, setPreviewPost] = useState<BlogPost | null>(null)
  const [isPreviewOpen, setIsPreviewOpen] = useState(false)
  const [selectedPosts, setSelectedPosts] = useState<Set<string>>(new Set())
  const [bulkActionLoading, setBulkActionLoading] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login')
      return
    }
  }, [isAuthenticated, router])

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

  const fetchPostDetails = async (postId: string): Promise<BlogPost | null> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (response.ok) {
        const post = await response.json()
        return post
      } else {
        console.error('Failed to fetch post details')
        return null
      }
    } catch (err) {
      console.error('Error fetching post details:', err)
      return null
    }
  }

  const openPreview = async (post: BlogPost) => {
    // If the post already has content, use it directly
    if (post.content) {
      setPreviewPost(post)
      setIsPreviewOpen(true)
      return
    }

    // Otherwise, fetch the full post details
    const fullPost = await fetchPostDetails(post.id)
    if (fullPost) {
      setPreviewPost(fullPost)
      setIsPreviewOpen(true)
    } else {
      alert('Failed to load post details for preview')
    }
  }

  const closePreview = () => {
    setIsPreviewOpen(false)
    setPreviewPost(null)
  }

  const approvePost = async (postId: string) => {
    if (!confirm('Are you sure you want to approve and publish this post?')) {
      return
    }

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
        // Close preview modal if open
        if (isPreviewOpen) closePreview()
        alert('Post approved and published!')
      } else {
        const errorData = await response.json().catch(() => ({ message: 'Unknown error' }))
        alert(`Failed to approve post: ${errorData.message || 'Unknown error'}`)
      }
    } catch (err) {
      alert('Failed to approve post: Network error')
      console.error('Error approving post:', err)
    }
  }

  const rejectPost = async (postId: string) => {
    const reason = prompt('Please provide a reason for rejecting this post (optional):')
    if (reason === null) return // User cancelled

    if (!confirm('Are you sure you want to reject this post?')) {
      return
    }

    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}/reject`, {
        method: HTTP_METHODS.POST,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ reason: reason || 'No reason provided' })
      })

      if (response.ok) {
        // Refresh the pending posts list
        fetchPendingPosts()
        // Close preview modal if open
        if (isPreviewOpen) closePreview()
        alert('Post rejected')
      } else {
        const errorData = await response.json().catch(() => ({ message: 'Unknown error' }))
        alert(`Failed to reject post: ${errorData.message || 'Unknown error'}`)
      }
    } catch (err) {
      alert('Failed to reject post: Network error')
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

  const togglePostSelection = (postId: string) => {
    const newSelection = new Set(selectedPosts)
    if (newSelection.has(postId)) {
      newSelection.delete(postId)
    } else {
      newSelection.add(postId)
    }
    setSelectedPosts(newSelection)
  }

  const toggleSelectAll = () => {
    if (selectedPosts.size === pendingPosts.length) {
      setSelectedPosts(new Set())
    } else {
      setSelectedPosts(new Set(pendingPosts.map(post => post.id)))
    }
  }

  const bulkApprove = async () => {
    if (selectedPosts.size === 0) return
    
    if (!confirm(`Are you sure you want to approve and publish ${selectedPosts.size} post(s)?`)) {
      return
    }

    setBulkActionLoading(true)
    let successCount = 0
    let failureCount = 0

    for (const postId of Array.from(selectedPosts)) {
      try {
        const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}/approve`, {
          method: HTTP_METHODS.POST,
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })
        
        if (response.ok) {
          successCount++
        } else {
          failureCount++
        }
      } catch (err) {
        failureCount++
        console.error('Error approving post:', err)
      }
    }

    setBulkActionLoading(false)
    setSelectedPosts(new Set())
    fetchPendingPosts()
    
    alert(`Bulk approval completed: ${successCount} approved, ${failureCount} failed`)
  }

  const bulkReject = async () => {
    if (selectedPosts.size === 0) return
    
    const reason = prompt('Please provide a reason for rejecting these posts (optional):')
    if (reason === null) return

    if (!confirm(`Are you sure you want to reject ${selectedPosts.size} post(s)?`)) {
      return
    }

    setBulkActionLoading(true)
    let successCount = 0
    let failureCount = 0

    for (const postId of Array.from(selectedPosts)) {
      try {
        const response = await fetch(`${API_CONFIG.BASE_URL}/blog/admin/posts/${postId}/reject`, {
          method: HTTP_METHODS.POST,
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ reason: reason || 'No reason provided' })
        })
        
        if (response.ok) {
          successCount++
        } else {
          failureCount++
        }
      } catch (err) {
        failureCount++
        console.error('Error rejecting post:', err)
      }
    }

    setBulkActionLoading(false)
    setSelectedPosts(new Set())
    fetchPendingPosts()
    
    alert(`Bulk rejection completed: ${successCount} rejected, ${failureCount} failed`)
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
                  Blog Post Approval
                </h1>
              </div>
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-2">
                  <div className="text-sm text-gray-700 dark:text-gray-300">
                    Welcome, <span className="font-medium">{user.email}</span>
                  </div>
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 dark:bg-red-800 dark:text-red-100">
                    Admin
                  </span>
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
              <div className="mb-6">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                      Blog Management Dashboard
                    </h2>
                    <p className="text-gray-600 dark:text-gray-400">
                      Review and approve blog posts from users
                    </p>
                  </div>
                  <div className="text-right">
                    <div className="text-2xl font-bold text-green-600 dark:text-green-400">
                      {pendingPosts.length}
                    </div>
                    <div className="text-sm text-gray-500 dark:text-gray-400">
                      Pending Review
                    </div>
                  </div>
                </div>
              </div>

              {/* Loading State */}
              {loading && (
                <div className="text-center py-12">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
                  <p className="mt-4 text-gray-600 dark:text-gray-400">Loading pending blog posts...</p>
                </div>
              )}

              {/* Content - only show when not loading */}
              {!loading && (
                <>
                  {/* Bulk Actions */}
                  {pendingPosts.length > 0 && (
                    <div className="mb-6 p-4 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={selectedPosts.size === pendingPosts.length && pendingPosts.length > 0}
                  onChange={toggleSelectAll}
                  className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
                />
                <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
                  Select All ({pendingPosts.length})
                </span>
              </label>
              {selectedPosts.size > 0 && (
                <span className="text-sm text-blue-600 dark:text-blue-400">
                  {selectedPosts.size} selected
                </span>
              )}
            </div>
            
            {selectedPosts.size > 0 && (
              <div className="flex items-center space-x-2">
                <button
                  onClick={bulkApprove}
                  disabled={bulkActionLoading}
                  className="px-4 py-2 bg-green-600 hover:bg-green-700 disabled:bg-gray-400 text-white text-sm rounded transition-colors"
                >
                  {bulkActionLoading ? 'Processing...' : `Approve ${selectedPosts.size}`}
                </button>
                <button
                  onClick={bulkReject}
                  disabled={bulkActionLoading}
                  className="px-4 py-2 bg-red-600 hover:bg-red-700 disabled:bg-gray-400 text-white text-sm rounded transition-colors"
                >
                  {bulkActionLoading ? 'Processing...' : `Reject ${selectedPosts.size}`}
                </button>
              </div>
                    )}
                      </div>
                    </div>
                  )}

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
                <div className="flex items-start space-x-4 flex-1">
                  {/* Checkbox */}
                  <div className="pt-1">
                    <input
                      type="checkbox"
                      checked={selectedPosts.has(post.id)}
                      onChange={() => togglePostSelection(post.id)}
                      className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
                    />
                  </div>
                  
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
                </div>

                <div className="flex items-center space-x-2 ml-4">
                  {/* Preview/View Full Post */}
                  <button
                    onClick={() => openPreview(post)}
                    className="px-3 py-1 text-sm text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-200 border border-blue-300 dark:border-blue-600 rounded transition-colors"
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
                </>
              )}

              {/* Preview Modal */}
              <BlogPostPreviewModal
                post={previewPost}
                isOpen={isPreviewOpen}
                onClose={closePreview}
                onApprove={approvePost}
                onReject={rejectPost}
              />
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}