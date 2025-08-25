export interface Project {
  id: string
  name: string
  description?: string
  is_active: boolean
  update_date: string
  update_by: string
  user_id: string
  user_email: string
  created_at: string
  updated_at: string
}

export interface ProjectRequest {
  name: string
  description?: string
  isActive: boolean
}

export interface ProjectStats {
  totalProjects: number
  activeProjects: number
  inProgressProjects: number
  completedProjects: number
}

export interface ProjectState {
  projects: Project[]
  currentProject: Project | null
  stats: ProjectStats | null
  isLoading: boolean
  error: string | null
}

export interface SnowflakeStage {
  stage_name: string
  stage_schema: string
  stage_database: string
  stage_type: string
  stage_location: string
  file_format?: string
  copy_options?: string
  comment?: string
  owner: string
  created: string
}