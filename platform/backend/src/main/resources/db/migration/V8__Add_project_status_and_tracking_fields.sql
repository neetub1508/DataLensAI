-- Add project status and tracking fields
-- Version: 0.1.1
-- Description: Add is_active, update_date, and update_by fields to projects table

-- Add new columns to projects table
ALTER TABLE projects 
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN update_by UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';

-- Update existing projects to set update_by to the user_id (assuming user created and updated their own projects)
UPDATE projects SET update_by = user_id WHERE update_by = '00000000-0000-0000-0000-000000000000';

-- Ensure all existing projects are set to active (in case the DEFAULT didn't apply properly)
UPDATE projects SET is_active = TRUE WHERE is_active IS NULL;

-- Create indexes for better performance
CREATE INDEX idx_project_active ON projects(is_active);
CREATE INDEX idx_project_update_date ON projects(update_date);

-- Create trigger to automatically update update_date when project is modified
CREATE OR REPLACE FUNCTION update_project_update_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_projects_update_date BEFORE UPDATE ON projects 
    FOR EACH ROW EXECUTE FUNCTION update_project_update_date();