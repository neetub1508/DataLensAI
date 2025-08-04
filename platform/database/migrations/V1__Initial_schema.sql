-- Initial schema for Data Lens AI platform
-- Version: 1.0.0
-- Date: 2025-08-04
-- Description: Create core tables for user management, roles, and permissions

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP,
    locale VARCHAR(10) DEFAULT 'en-US',
    theme VARCHAR(20) DEFAULT 'light',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Create roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Create permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles junction table
CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id),
    UNIQUE(user_id, role_id)
);

-- Create role_permissions junction table
CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    granted_by UUID,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES users(id),
    UNIQUE(role_id, permission_id)
);

-- Create app_version table for tracking database migrations
CREATE TABLE app_version (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version VARCHAR(20) NOT NULL,
    description TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    applied_by VARCHAR(100)
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- Insert default roles
INSERT INTO roles (name, description, is_system_role) VALUES
('ADMIN', 'System Administrator with full access', TRUE),
('USER', 'Standard user with basic access', TRUE),
('VIEWER', 'Read-only access to analytics', TRUE);

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action) VALUES
('USER_CREATE', 'Create new users', 'user', 'create'),
('USER_READ', 'View user information', 'user', 'read'),
('USER_UPDATE', 'Update user information', 'user', 'update'),
('USER_DELETE', 'Delete users', 'user', 'delete'),
('ROLE_CREATE', 'Create new roles', 'role', 'create'),
('ROLE_READ', 'View role information', 'role', 'read'),
('ROLE_UPDATE', 'Update role information', 'role', 'update'),
('ROLE_DELETE', 'Delete roles', 'role', 'delete'),
('PERMISSION_CREATE', 'Create new permissions', 'permission', 'create'),
('PERMISSION_READ', 'View permission information', 'permission', 'read'),
('PERMISSION_UPDATE', 'Update permission information', 'permission', 'update'),
('PERMISSION_DELETE', 'Delete permissions', 'permission', 'delete'),
('ANALYTICS_READ', 'View analytics data', 'analytics', 'read'),
('ANALYTICS_CREATE', 'Create analytics reports', 'analytics', 'create'),
('ANALYTICS_UPDATE', 'Update analytics reports', 'analytics', 'update'),
('ANALYTICS_DELETE', 'Delete analytics reports', 'analytics', 'delete');

-- Assign permissions to roles
-- ADMIN role gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- USER role gets basic permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name IN (
    'USER_READ', 'USER_UPDATE',
    'ANALYTICS_READ', 'ANALYTICS_CREATE', 'ANALYTICS_UPDATE'
);

-- VIEWER role gets read-only permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'VIEWER' AND p.name IN (
    'USER_READ', 'ANALYTICS_READ'
);

-- Record this migration
INSERT INTO app_version (version, description, applied_by) 
VALUES ('1.0.0', 'Initial schema with users, roles, and permissions', 'system');