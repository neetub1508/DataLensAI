export const PROJECT_STATUS = {
  ACTIVE: 'ACTIVE',
  ARCHIVED: 'ARCHIVED',
  SUSPENDED: 'SUSPENDED',
} as const

export const PROJECT_MESSAGES = {
  CREATE_SUCCESS: 'Project created successfully',
  UPDATE_SUCCESS: 'Project updated successfully',
  DELETE_SUCCESS: 'Project deleted successfully',
  ARCHIVE_SUCCESS: 'Project archived successfully',
  RESTORE_SUCCESS: 'Project restored successfully',
  NOT_FOUND: 'Project not found',
  PERMISSION_DENIED: 'You do not have permission to access this project',
} as const