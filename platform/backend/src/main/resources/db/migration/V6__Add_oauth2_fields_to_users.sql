-- Add OAuth2 provider fields to users table
ALTER TABLE users 
ADD COLUMN provider VARCHAR(50) DEFAULT 'LOCAL',
ADD COLUMN provider_id VARCHAR(255);

-- Create indexes for OAuth2 fields
CREATE INDEX idx_users_provider ON users(provider);
CREATE INDEX idx_users_provider_id ON users(provider_id);

-- Make password_hash nullable for OAuth2 users
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;