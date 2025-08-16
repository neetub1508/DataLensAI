import { create } from 'zustand'
import { Project, ProjectRequest, ProjectStats, ProjectState } from '@/types/project'
import { apiClient } from '@/lib/api'
import { toast } from 'react-hot-toast'

interface ProjectStore extends ProjectState {
  // Actions
  fetchProjects: () => Promise<void>
  fetchActiveProjects: () => Promise<void>
  fetchAllProjects: () => Promise<void>
  fetchProjectStats: () => Promise<void>
  fetchRecentProjects: (limit?: number) => Promise<void>
  createProject: (data: ProjectRequest) => Promise<Project>
  updateProject: (id: string, data: ProjectRequest) => Promise<Project>
  deleteProject: (id: string) => Promise<void>
  searchProjects: (query: string) => Promise<Project[]>
  setCurrentProject: (project: Project | null) => void
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
  clearError: () => void
}

export const useProjectStore = create<ProjectStore>((set, get) => ({
  // Initial state
  projects: [],
  currentProject: null,
  stats: null,
  isLoading: false,
  error: null,

  // Actions
  fetchProjects: async () => {
    // By default, fetch only active projects for the main project view
    try {
      set({ isLoading: true, error: null })
      const projects = await apiClient.getActiveProjects()
      set({ projects, isLoading: false })
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to fetch projects'
      set({ error: message, isLoading: false })
      toast.error(message)
    }
  },

  fetchActiveProjects: async () => {
    try {
      set({ isLoading: true, error: null })
      const projects = await apiClient.getActiveProjects()
      set({ projects, isLoading: false })
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to fetch active projects'
      set({ error: message, isLoading: false })
      toast.error(message)
    }
  },

  fetchAllProjects: async () => {
    try {
      set({ isLoading: true, error: null })
      const projects = await apiClient.getAllProjects()
      set({ projects, isLoading: false })
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to fetch all projects'
      set({ error: message, isLoading: false })
      toast.error(message)
    }
  },

  fetchProjectStats: async () => {
    try {
      const stats = await apiClient.getProjectStats()
      set({ stats })
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to fetch project statistics'
      set({ error: message })
      toast.error(message)
    }
  },

  fetchRecentProjects: async (limit = 10) => {
    try {
      set({ isLoading: true, error: null })
      const projects = await apiClient.getRecentProjects(limit)
      set({ projects, isLoading: false })
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to fetch recent projects'
      set({ error: message, isLoading: false })
      toast.error(message)
    }
  },

  createProject: async (data: ProjectRequest) => {
    try {
      set({ isLoading: true, error: null })
      const newProject = await apiClient.createProject(data)
      
      // Only add to the current list if the project is active (since we're showing active projects)
      if (newProject.is_active) {
        const currentProjects = get().projects
        set({ 
          projects: [newProject, ...currentProjects],
          isLoading: false 
        })
      } else {
        set({ isLoading: false })
      }
      
      toast.success('Project created successfully!')
      
      // Refresh stats
      get().fetchProjectStats()
      
      return newProject
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to create project'
      set({ error: message, isLoading: false })
      toast.error(message)
      throw error
    }
  },

  updateProject: async (id: string, data: ProjectRequest) => {
    try {
      set({ isLoading: true, error: null })
      const updatedProject = await apiClient.updateProject(id, data)
      
      // Handle the project list based on active status
      const currentProjects = get().projects
      
      if (updatedProject.is_active) {
        // If project is now active, update it in the list or add it if it wasn't there
        const existingProjectIndex = currentProjects.findIndex(project => project.id === id)
        let updatedProjects
        
        if (existingProjectIndex >= 0) {
          // Update existing project
          updatedProjects = currentProjects.map(project =>
            project.id === id ? updatedProject : project
          )
        } else {
          // Add the project to the list (it was previously inactive)
          updatedProjects = [updatedProject, ...currentProjects]
        }
        
        set({ 
          projects: updatedProjects,
          currentProject: get().currentProject?.id === id ? updatedProject : get().currentProject,
          isLoading: false 
        })
      } else {
        // If project is now inactive, remove it from the active projects list
        const filteredProjects = currentProjects.filter(project => project.id !== id)
        set({ 
          projects: filteredProjects,
          currentProject: get().currentProject?.id === id ? null : get().currentProject,
          isLoading: false 
        })
      }
      
      toast.success('Project updated successfully!')
      
      // Refresh stats
      get().fetchProjectStats()
      
      return updatedProject
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to update project'
      set({ error: message, isLoading: false })
      toast.error(message)
      throw error
    }
  },

  deleteProject: async (id: string) => {
    try {
      set({ isLoading: true, error: null })
      await apiClient.deleteProject(id)
      
      // Remove the project from the projects list
      const currentProjects = get().projects
      const filteredProjects = currentProjects.filter(project => project.id !== id)
      
      set({ 
        projects: filteredProjects,
        currentProject: get().currentProject?.id === id ? null : get().currentProject,
        isLoading: false 
      })
      
      toast.success('Project deleted successfully!')
      
      // Refresh stats
      get().fetchProjectStats()
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to delete project'
      set({ error: message, isLoading: false })
      toast.error(message)
      throw error
    }
  },

  searchProjects: async (query: string) => {
    try {
      const projects = await apiClient.searchProjects(query)
      return projects
    } catch (error: any) {
      const message = error.response?.data?.error || 'Failed to search projects'
      toast.error(message)
      throw error
    }
  },

  setCurrentProject: (project: Project | null) => {
    set({ currentProject: project })
  },

  setLoading: (loading: boolean) => {
    set({ isLoading: loading })
  },

  setError: (error: string | null) => {
    set({ error })
  },

  clearError: () => {
    set({ error: null })
  },
}))