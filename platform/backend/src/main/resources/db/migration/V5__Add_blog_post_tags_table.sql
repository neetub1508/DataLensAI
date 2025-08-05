-- Version: 0.5.0  
-- Description: Migration placeholder - blog_post_tags table already exists in V4

-- The blog_post_tags table is already created in V4 migration
-- This migration is kept for version tracking purposes

-- Update app version
INSERT INTO app_version (version, description) VALUES
    ('0.5.0', 'Blog post tags table verified (already exists in V4)');