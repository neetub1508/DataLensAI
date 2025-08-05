export interface Project {
  id: string
  name: string
  description?: string
  status: 'ACTIVE' | 'ARCHIVED' | 'SUSPENDED'
  ownerId: string
  ownerEmail: string
  memberCount: number
  settings?: string
  lastAccessedAt?: string
  createdAt: string
  updatedAt: string
  isOwner: boolean
}

export interface CreateProjectRequest {
  name: string
  description?: string
  settings?: string
}

export interface UpdateProjectRequest {
  name: string
  description?: string
  settings?: string
}