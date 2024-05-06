-- Function to get the current timestamp
CREATE FUNCTION current_timestamp()
RETURNS TIMESTAMP
BEGIN
  RETURN NOW();
END;