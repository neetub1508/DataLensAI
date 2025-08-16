'use client'

import { useState } from 'react'
import { Project } from '@/types/project'
import { useProjectStore } from '@/store/project'
import EditProjectModal from './edit-project-modal'
import { formatDistanceToNow } from 'date-fns'

interface ProjectListItemProps {
  project: Project
}

export default function ProjectListItem({ project }: ProjectListItemProps) {
  const { deleteProject } = useProjectStore()
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
      setIsDeleting(true)
      try {
        await deleteProject(project.id)
      } catch (error) {
        // Error handling is done in the store
      } finally {
        setIsDeleting(false)
      }
    }
  }

  const formatDate = (dateString: string) => {
    try {
      // Parse the date string and ensure it's valid
      const date = new Date(dateString)
      
      // Check if the date is valid
      if (isNaN(date.getTime())) {
        return 'Unknown'
      }
      
      // Use formatDistanceToNow for relative time
      return formatDistanceToNow(date, { addSuffix: true })
    } catch (error) {
      console.error('Error formatting date:', dateString, error)
      return 'Unknown'
    }
  }

  return (
    <>
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow hover:shadow-md transition-shadow p-6 border border-gray-200 dark:border-gray-700">
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 mb-2">
              <h3 className="text-lg font-medium text-gray-900 dark:text-white truncate">
                {project.name}
              </h3>
              <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                project.is_active 
                  ? 'bg-green-100 text-green-800 dark:bg-green-800 dark:text-green-100' 
                  : 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-100'
              }`}>
                {project.is_active ? 'Active' : 'Inactive'}
              </span>
            </div>
            
            {project.description && (
              <p className="text-sm text-gray-600 dark:text-gray-400 mb-3 line-clamp-2">
                {project.description}
              </p>
            )}
            
            <div className="flex items-center text-xs text-gray-500 dark:text-gray-400 space-x-4">
              <span>Created {formatDate(project.created_at)}</span>
              {(() => {
                if (!project.update_date) return null;
                
                // Parse both dates to compare them
                const createdDate = new Date(project.created_at);
                const updatedDate = new Date(project.update_date);
                
                // Only show update date if it's significantly different (more than 1 minute apart)
                const timeDifference = Math.abs(updatedDate.getTime() - createdDate.getTime());
                const oneMinute = 60 * 1000; // 1 minute in milliseconds
                
                if (timeDifference > oneMinute) {
                  return <span>Last updated {formatDate(project.update_date)}</span>;
                }
                
                return null;
              })()}
            </div>
          </div>
          
          <div className="flex items-center space-x-2 ml-4">
            <button
              onClick={() => setIsEditModalOpen(true)}
              className="p-2 text-gray-400 hover:text-green-600 dark:hover:text-green-400 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              title="Edit project"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>
            
            <button
              onClick={handleDelete}
              disabled={isDeleting}
              className="p-2 text-gray-400 hover:text-red-600 dark:hover:text-red-400 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              title="Delete project"
            >
              {isDeleting ? (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-red-600"></div>
              ) : (
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              )}
            </button>
          </div>
        </div>
      </div>

      <EditProjectModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        project={project}
      />
    </>
  )
}