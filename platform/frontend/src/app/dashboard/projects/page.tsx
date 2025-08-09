'use client'

import { useState, useEffect } from 'react'
import { PROJECT_STATUS } from '@/constants/project'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/store/auth'
import { useProjectStore } from '@/store/project'
import DashboardSidebar from '@/components/layouts/dashboard-sidebar'
import ProjectSetup from '@/components/project/project-setup'

export default function ProjectsPage() {
  const router = useRouter()
  const { user, isAuthenticated } = useAuthStore()
  const { projects, currentProject, fetchProjects, deleteProject, archiveProject, restoreProject } = useProjectStore()
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [showProjectSetup, setShowProjectSetup] = useState(false)
  const [selectedProject, setSelectedProject] = useState<string | null>(null)

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login')
      return
    }
  }, [isAuthenticated, router])

  useEffect(() => {
    if (isAuthenticated) {
      fetchProjects()
    }
  }, [isAuthenticated, fetchProjects])

  const handleDeleteProject = async (projectId: string) => {
    if (confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
      try {
        await deleteProject(projectId)
      } catch (error) {
        console.error('Failed to delete project:', error)
      }
    }
  }

  const handleArchiveProject = async (projectId: string) => {
    try {
      await archiveProject(projectId)
    } catch (error) {
      console.error('Failed to archive project:', error)
    }
  }

  const handleRestoreProject = async (projectId: string) => {
    try {
      await restoreProject(projectId)
    } catch (error) {
      console.error('Failed to restore project:', error)
    }
  }

  if (!isAuthenticated || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-green-600"></div>
      </div>
    )
  }

  if (showProjectSetup) {
    return (
      <ProjectSetup
        onProjectSelected={(project) => {
          setShowProjectSetup(false)
          fetchProjects()
        }}
      />
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
                  Project Management
                </h1>
              </div>
              <button
                onClick={() => setShowProjectSetup(true)}
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
              >
                New Project
              </button>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto">
          <div className="p-6">
            {projects.length === 0 ? (
              <div className="text-center py-12">
                <div className="w-24 h-24 mx-auto bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
                  <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 9a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V9a2 2 0 00-2-2H5z" />
                  </svg>
                </div>
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                  No projects yet
                </h3>
                <p className="text-gray-600 dark:text-gray-400 mb-6">
                  Get started by creating your first project to organize your analytics work.
                </p>
                <button
                  onClick={() => setShowProjectSetup(true)}
                  className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                >
                  Create First Project
                </button>
              </div>
            ) : (
              <div className="space-y-6">
                {/* Active Projects */}
                <div>
                  <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                    Active Projects ({projects.filter(p => p.status === PROJECT_STATUS.ACTIVE).length})
                  </h2>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {projects.filter(p => p.status === PROJECT_STATUS.ACTIVE).map((project) => (
                      <div
                        key={project.id}
                        className={`bg-white dark:bg-gray-800 shadow rounded-lg p-6 hover:shadow-lg transition-shadow ${
                          currentProject?.id === project.id ? 'ring-2 ring-green-500' : ''
                        }`}
                      >
                        <div className="flex items-start justify-between mb-4">
                          <div className="flex-1 min-w-0">
                            <h3 className="text-lg font-medium text-gray-900 dark:text-white truncate">
                              {project.name}
                            </h3>
                            {project.description && (
                              <p className="mt-1 text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                                {project.description}
                              </p>
                            )}
                          </div>
                          <div className="flex items-center space-x-1 ml-4">
                            {project.isOwner && (
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-800 dark:text-blue-100">
                                Owner
                              </span>
                            )}
                            {currentProject?.id === project.id && (
                              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100">
                                Current
                              </span>
                            )}
                          </div>
                        </div>
                        
                        <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400 mb-4">
                          <div className="flex items-center">
                            <svg className="mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                            </svg>
                            {project.memberCount + 1} members
                          </div>
                          <div>
                            {project.lastAccessedAt 
                              ? `Last used ${new Date(project.lastAccessedAt).toLocaleDateString()}`
                              : `Created ${new Date(project.createdAt).toLocaleDateString()}`
                            }
                          </div>
                        </div>

                        <div className="flex items-center justify-between">
                          <button
                            onClick={() => router.push('/dashboard')}
                            className="text-green-600 hover:text-green-500 text-sm font-medium"
                          >
                            Open Project
                          </button>
                          
                          <div className="relative">
                            <button
                              onClick={() => setSelectedProject(selectedProject === project.id ? null : project.id)}
                              className="p-1 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-700"
                            >
                              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                              </svg>
                            </button>
                            
                            {selectedProject === project.id && (
                              <div className="absolute right-0 top-8 mt-1 w-48 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-md shadow-lg z-10">
                                <div className="py-1">
                                  <button
                                    onClick={() => {
                                      // Edit functionality would go here
                                      setSelectedProject(null)
                                    }}
                                    className="block w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                                  >
                                    Edit Project
                                  </button>
                                  {project.isOwner && (
                                    <>
                                      <button
                                        onClick={() => {
                                          handleArchiveProject(project.id)
                                          setSelectedProject(null)
                                        }}
                                        className="block w-full text-left px-4 py-2 text-sm text-yellow-700 dark:text-yellow-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                                      >
                                        Archive Project
                                      </button>
                                      <button
                                        onClick={() => {
                                          handleDeleteProject(project.id)
                                          setSelectedProject(null)
                                        }}
                                        className="block w-full text-left px-4 py-2 text-sm text-red-700 dark:text-red-300 hover:bg-gray-100 dark:hover:bg-gray-700"
                                      >
                                        Delete Project
                                      </button>
                                    </>
                                  )}
                                </div>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Archived Projects */}
                {projects.filter(p => p.status === PROJECT_STATUS.ARCHIVED).length > 0 && (
                  <div>
                    <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                      Archived Projects ({projects.filter(p => p.status === PROJECT_STATUS.ARCHIVED).length})
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                      {projects.filter(p => p.status === PROJECT_STATUS.ARCHIVED).map((project) => (
                        <div
                          key={project.id}
                          className="bg-white dark:bg-gray-800 shadow rounded-lg p-6 opacity-75"
                        >
                          <div className="flex items-start justify-between mb-4">
                            <div className="flex-1 min-w-0">
                              <h3 className="text-lg font-medium text-gray-900 dark:text-white truncate">
                                {project.name}
                              </h3>
                              {project.description && (
                                <p className="mt-1 text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                                  {project.description}
                                </p>
                              )}
                            </div>
                            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-100">
                              Archived
                            </span>
                          </div>
                          
                          <div className="flex items-center justify-between text-sm text-gray-500 dark:text-gray-400 mb-4">
                            <div className="flex items-center">
                              <svg className="mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                              </svg>
                              {project.memberCount + 1} members
                            </div>
                            <div>
                              Archived {new Date(project.updatedAt).toLocaleDateString()}
                            </div>
                          </div>

                          <div className="flex items-center justify-between">
                            {project.isOwner && (
                              <button
                                onClick={() => handleRestoreProject(project.id)}
                                className="text-green-600 hover:text-green-500 text-sm font-medium"
                              >
                                Restore Project
                              </button>
                            )}
                            
                            {project.isOwner && (
                              <button
                                onClick={() => handleDeleteProject(project.id)}
                                className="text-red-600 hover:text-red-500 text-sm font-medium"
                              >
                                Delete Permanently
                              </button>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}