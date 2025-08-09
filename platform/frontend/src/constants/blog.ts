export const BLOG_POST_STATUS = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
  ARCHIVED: 'ARCHIVED',
} as const

export const BLOG_MESSAGES = {
  POST_CREATED: 'Blog post created successfully',
  POST_UPDATED: 'Blog post updated successfully',
  POST_DELETED: 'Blog post deleted successfully',
  POST_PUBLISHED: 'Blog post published successfully',
  POST_ARCHIVED: 'Blog post archived successfully',
  CATEGORY_CREATED: 'Category created successfully',
  CATEGORY_UPDATED: 'Category updated successfully',
} as const