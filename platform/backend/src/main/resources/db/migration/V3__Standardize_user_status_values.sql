-- Migration: V3__Standardize_user_status_values.sql
-- Description: Standardize user status values to uppercase for consistency with application code
-- Date: 2025-08-04
-- Issue: AuthService uses uppercase status values but database has lowercase

-- Update existing users to have uppercase status values
UPDATE users 
SET status = UPPER(status),
    updated_at = CURRENT_TIMESTAMP
WHERE status IS NOT NULL;

-- Update the default value for new users to be uppercase
ALTER TABLE users ALTER COLUMN status SET DEFAULT 'ACTIVE';

-- Add a comment to document the expected status values
COMMENT ON COLUMN users.status IS 'User status: ACTIVE, PENDING_VERIFICATION, SUSPENDED, INACTIVE';

-- Verify the update was successful
-- Expected: All users should now have uppercase status values