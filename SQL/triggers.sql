-- Remove existing objects if they exist
DROP FUNCTION IF EXISTS current_timestamp;

-- Function to get the current time
CREATE FUNCTION current_timestamp()
RETURNS TIMESTAMP
BEGIN
  RETURN NOW(); 
END;