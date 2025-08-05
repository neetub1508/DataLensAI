-- Blog functionality schema
-- Version: 0.4.0  
-- Description: Add blog posts with admin approval system

-- Ensure UUID extension is available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Function to update updated_at timestamp (if it doesn't exist)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create blog_posts table
CREATE TABLE blog_posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    excerpt VARCHAR(500),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'draft', -- draft, pending_approval, published, rejected
    featured_image_url VARCHAR(500),
    tags TEXT[], -- Array of tags
    approved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    approved_at TIMESTAMP,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create blog_categories table
CREATE TABLE blog_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create blog_post_categories junction table
CREATE TABLE blog_post_categories (
    post_id UUID NOT NULL REFERENCES blog_posts(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES blog_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, category_id)
);

-- Create blog_post_tags table for @ElementCollection
CREATE TABLE blog_post_tags (
    post_id UUID NOT NULL REFERENCES blog_posts(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (post_id, tag)
);

-- Create indexes for better performance
CREATE INDEX idx_blog_posts_status ON blog_posts(status);
CREATE INDEX idx_blog_posts_author ON blog_posts(author_id);
CREATE INDEX idx_blog_posts_published_at ON blog_posts(published_at);
CREATE INDEX idx_blog_posts_slug ON blog_posts(slug);
CREATE INDEX idx_blog_categories_slug ON blog_categories(slug);
CREATE INDEX idx_blog_post_categories_post ON blog_post_categories(post_id);
CREATE INDEX idx_blog_post_categories_category ON blog_post_categories(category_id);

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_blog_posts_updated_at BEFORE UPDATE ON blog_posts 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_blog_categories_updated_at BEFORE UPDATE ON blog_categories 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add blog permissions
INSERT INTO permissions (name, description, resource, action) VALUES
    ('BLOG_READ', 'Read blog posts', 'blog', 'read'),
    ('BLOG_CREATE', 'Create blog posts', 'blog', 'create'),
    ('BLOG_UPDATE', 'Update blog posts', 'blog', 'update'),
    ('BLOG_DELETE', 'Delete blog posts', 'blog', 'delete'),
    ('BLOG_APPROVE', 'Approve/reject blog posts', 'blog', 'approve'),
    ('BLOG_PUBLISH', 'Publish blog posts', 'blog', 'publish'),
    ('BLOG_CATEGORY_MANAGE', 'Manage blog categories', 'blog_category', 'manage');

-- Assign blog permissions to admin role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'admin' AND p.name IN ('BLOG_READ', 'BLOG_CREATE', 'BLOG_UPDATE', 'BLOG_DELETE', 'BLOG_APPROVE', 'BLOG_PUBLISH', 'BLOG_CATEGORY_MANAGE');

-- Assign basic blog permissions to user role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'user' AND p.name IN ('BLOG_READ', 'BLOG_CREATE', 'BLOG_UPDATE');

-- Insert default blog categories
INSERT INTO blog_categories (name, description, slug) VALUES
    ('Technology', 'Posts about technology and innovation', 'technology'),
    ('Data Science', 'Posts about data analysis and insights', 'data-science'),
    ('Product Updates', 'Product announcements and updates', 'product-updates'),
    ('Tutorials', 'How-to guides and tutorials', 'tutorials');

-- Update app version
INSERT INTO app_version (version, description) VALUES
    ('0.4.0', 'Added blog functionality with admin approval system');