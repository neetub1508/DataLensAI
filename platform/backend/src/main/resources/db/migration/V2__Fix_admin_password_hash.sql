-- Migration: V2__Fix_admin_password_hash.sql
-- Description: Fix admin user password hash to ensure login works correctly
-- Date: 2025-08-04
-- Issue: Admin login was failing due to incorrect BCrypt hash

-- Update admin user password hash to working BCrypt hash for "admin123"
-- This ensures the admin user can login with password "admin123"
UPDATE users 
SET password_hash = '$2a$10$uqJ53YavHAam/jRBvGx4zud2dtG8zEIefO3fcA5n45uChLQIdAQYK',
    updated_at = CURRENT_TIMESTAMP
WHERE email = 'admin@datalens.ai';

-- Verify the update was successful
-- Expected: 1 row updated
-- This comment documents that exactly one admin user should be updated