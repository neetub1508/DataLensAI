'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useProjectStore } from '@/store/project'
import { Project } from '@/types/project'
import { toast } from 'react-hot-toast'

interface ProjectSetupProps {
  onProjectSelected?: (project: Project) => void
}

export default function ProjectSetup({ onProjectSelected }: ProjectSetupProps) {
  const router = useRouter()
  const { 
    projects, 
    currentProject,
    isLoading, 
    error,
    fetchProjects, 
    createProject, 
    setCurrentProject,
    clearError 
  } = useProjectStore()

  const [showCreateForm, setShowCreateForm] = useState(false)
  const [formData, setFormData] = useState({
    name: '',
    description: ''
  })

  useEffect(() => {
    fetchProjects()
  }, [fetchProjects])

  useEffect(() => {
    if (error) {
      toast.error(error)
      clearError()
    }
  }, [error, clearError])

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.name.trim()) {
      toast.error('Project name is required')
      return
    }

    try {
      const newProject = await createProject({
        name: formData.name.trim(),
        description: formData.description.trim()
      })
      
      setFormData({ name: '', description: '' })
      setShowCreateForm(false)
      setCurrentProject(newProject)
      
      if (onProjectSelected) {
        onProjectSelected(newProject)
      }
      
      toast.success('Project created successfully!')
    } catch (error) {
      console.error('Failed to create project:', error)
    }
  }

  const handleSelectProject = (project: Project) => {
    setCurrentProject(project)
    if (onProjectSelected) {
      onProjectSelected(project)
    }
    toast.success(`Switched to ${project.name}`)
  }

  if (isLoading && projects.length === 0) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
          <p className="mt-4 text-gray-600 dark:text-gray-400">Loading projects...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
            Welcome to Data Lens AI
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-400">
            {projects.length === 0 
              ? "Let's get started by creating your first project"
              : "Select a project to continue or create a new one"
            }
          </p>
        </div>

        {projects.length === 0 ? (
          // No projects - show create form
          <div className="max-w-md mx-auto">
            <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
                Create Your First Project
              </h2>
              <form onSubmit={handleCreateProject} className="space-y-4">
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                    Project Name *
                  </label>
                  <input
                    type="text"
                    id="name"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-green-500 focus:border-green-500"
                    placeholder="My Analytics Project"
                    required
                  />
                </div>
                <div>
                  <label htmlFor="description" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                    Description
                  </label>
                  <textarea
                    id="description"
                    rows={3}
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-green-500 focus:border-green-500"
                    placeholder="Describe what this project is for..."
                  />
                </div>
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
                >
                  {isLoading ? 'Creating...' : 'Create Project'}
                </button>
              </form>
            </div>
          </div>
        ) : (
          // Show existing projects
          <div className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-semibold text-gray-900 dark:text-white">
                Your Projects
              </h2>
              <button
                onClick={() => setShowCreateForm(!showCreateForm)}
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
              >
                New Project
              </button>
            </div>

            {showCreateForm && (
              <div className="bg-white dark:bg-gray-800 shadow rounded-lg p-6">
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                  Create New Project
                </h3>
                <form onSubmit={handleCreateProject} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="new-name" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                      Project Name *
                    </label>
                    <input
                      type="text"
                      id="new-name"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-green-500 focus:border-green-500"
                      placeholder="My New Project"
                      required
                    />
                  </div>
                  <div>
                    <label htmlFor="new-description" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                      Description
                    </label>
                    <input
                      type="text"
                      id="new-description"
                      value={formData.description}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                      className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:outline-none focus:ring-green-500 focus:border-green-500"
                      placeholder="Project description..."
                    />
                  </div>
                  <div className="md:col-span-2 flex space-x-3">
                    <button
                      type="submit"
                      disabled={isLoading}
                      className="flex-1 py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
                    >
                      {isLoading ? 'Creating...' : 'Create'}
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setShowCreateForm(false)
                        setFormData({ name: '', description: '' })
                      }}
                      className="flex-1 py-2 px-4 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {projects.map((project) => (
                <div
                  key={project.id}
                  className={`bg-white dark:bg-gray-800 shadow rounded-lg p-6 cursor-pointer transition-all hover:shadow-lg ${
                    currentProject?.id === project.id ? 'ring-2 ring-green-500' : ''
                  }`}
                  onClick={() => handleSelectProject(project)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="text-lg font-medium text-gray-900 dark:text-white">
                        {project.name}
                      </h3>
                      {project.description && (
                        <p className="mt-1 text-sm text-gray-600 dark:text-gray-400">
                          {project.description}
                        </p>
                      )}
                    </div>
                    <div className="flex items-center space-x-2">
                      {project.isOwner && (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-800 dark:text-blue-100">
                          Owner
                        </span>
                      )}
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                        project.status === 'ACTIVE' 
                          ? 'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100'
                          : 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-100'
                      }`}>
                        {project.status}
                      </span>
                    </div>
                  </div>
                  
                  <div className="mt-4 flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
                    <div className="flex items-center">
                      <svg className="mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                      </svg>
                      {project.memberCount + 1} members
                    </div>
                    <div>
                      {project.lastAccessedAt && (
                        <>Last used {new Date(project.lastAccessedAt).toLocaleDateString()}</>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}