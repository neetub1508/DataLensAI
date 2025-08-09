import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { apiClient } from '@/lib/api'
import { Project, CreateProjectRequest, UpdateProjectRequest } from '@/types/project'
import { PROJECT_STATUS } from '@/constants/project'

interface ProjectState {
  projects: Project[]
  currentProject: Project | null
  isLoading: boolean
  error: string | null
  
  // Actions
  fetchProjects: () => Promise<void>
  createProject: (data: CreateProjectRequest) => Promise<Project>
  updateProject: (projectId: string, data: UpdateProjectRequest) => Promise<Project>
  deleteProject: (projectId: string) => Promise<void>
  setCurrentProject: (project: Project | null) => void
  selectProject: (projectId: string) => Promise<void>
  archiveProject: (projectId: string) => Promise<void>
  restoreProject: (projectId: string) => Promise<void>
  searchProjects: (query: string) => Promise<Project[]>
  clearError: () => void
}

export const useProjectStore = create<ProjectState>()(
  persist(
    (set, get) => ({
      projects: [],
      currentProject: null,
      isLoading: false,
      error: null,

      fetchProjects: async () => {
        set({ isLoading: true, error: null })
        try {
          const projects = await apiClient.getUserProjects()
          set({ projects, isLoading: false })
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to fetch projects',
            isLoading: false 
          })
          throw error
        }
      },

      createProject: async (data: CreateProjectRequest) => {
        set({ isLoading: true, error: null })
        try {
          const project = await apiClient.createProject(data)
          set(state => ({ 
            projects: [project, ...state.projects],
            isLoading: false 
          }))
          return project
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to create project',
            isLoading: false 
          })
          throw error
        }
      },

      updateProject: async (projectId: string, data: UpdateProjectRequest) => {
        set({ isLoading: true, error: null })
        try {
          const updatedProject = await apiClient.updateProject(projectId, data)
          set(state => ({
            projects: state.projects.map(p => 
              p.id === projectId ? updatedProject : p
            ),
            currentProject: state.currentProject?.id === projectId ? updatedProject : state.currentProject,
            isLoading: false
          }))
          return updatedProject
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to update project',
            isLoading: false 
          })
          throw error
        }
      },

      deleteProject: async (projectId: string) => {
        set({ isLoading: true, error: null })
        try {
          await apiClient.deleteProject(projectId)
          set(state => ({
            projects: state.projects.filter(p => p.id !== projectId),
            currentProject: state.currentProject?.id === projectId ? null : state.currentProject,
            isLoading: false
          }))
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to delete project',
            isLoading: false 
          })
          throw error
        }
      },

      setCurrentProject: (project: Project | null) => {
        set({ currentProject: project })
      },

      selectProject: async (projectId: string) => {
        set({ isLoading: true, error: null })
        try {
          const project = await apiClient.getProject(projectId)
          set({ currentProject: project, isLoading: false })
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to select project',
            isLoading: false 
          })
          throw error
        }
      },

      archiveProject: async (projectId: string) => {
        set({ isLoading: true, error: null })
        try {
          await apiClient.archiveProject(projectId)
          set(state => ({
            projects: state.projects.map(p => 
              p.id === projectId ? { ...p, status: PROJECT_STATUS.ARCHIVED } : p
            ),
            isLoading: false
          }))
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to archive project',
            isLoading: false 
          })
          throw error
        }
      },

      restoreProject: async (projectId: string) => {
        set({ isLoading: true, error: null })
        try {
          await apiClient.restoreProject(projectId)
          set(state => ({
            projects: state.projects.map(p => 
              p.id === projectId ? { ...p, status: PROJECT_STATUS.ACTIVE } : p
            ),
            isLoading: false
          }))
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to restore project',
            isLoading: false 
          })
          throw error
        }
      },

      searchProjects: async (query: string) => {
        try {
          const projects = await apiClient.searchProjects(query)
          return projects
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Failed to search projects'
          })
          throw error
        }
      },

      clearError: () => {
        set({ error: null })
      }
    }),
    {
      name: 'project-store',
      partialize: (state) => ({
        currentProject: state.currentProject
      })
    }
  )
)