-- Initial seed data for Data Lens AI platform
-- Version: 1.0.0
-- Date: 2025-08-04
-- Description: Insert default admin user and sample data

-- Insert default admin user (password: admin123)
-- Note: In production, this should be changed immediately
-- Updated 2025-08-04: Fixed BCrypt hash to ensure login works correctly
-- BCrypt hash generated for "admin123" with strength 10: $2a$10$uqJ53YavHAam/jRBvGx4zud2dtG8zEIefO3fcA5n45uChLQIdAQYK
INSERT INTO users (email, password_hash, is_verified, status) VALUES
('admin@datalens.ai', '$2a$10$uqJ53YavHAam/jRBvGx4zud2dtG8zEIefO3fcA5n45uChLQIdAQYK', TRUE, 'active');

-- Assign ADMIN role to the default admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@datalens.ai' AND r.name = 'ADMIN';

-- Insert sample regular user (password: admin123)
-- Updated 2025-08-04: Fixed BCrypt hash to ensure login works correctly
INSERT INTO users (email, password_hash, is_verified, status) VALUES
('user@datalens.ai', '$2a$10$uqJ53YavHAam/jRBvGx4zud2dtG8zEIefO3fcA5n45uChLQIdAQYK', TRUE, 'active');

-- Assign USER role to the sample user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'user@datalens.ai' AND r.name = 'USER';