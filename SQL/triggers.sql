-- Procedure to increment the post count for a user
CREATE PROCEDURE incr_post_count(IN user_id INT)
BEGIN
  UPDATE users SET postsCount = postsCount + 1 WHERE id = user_id;
END;

-- Function to get the current timestamp
CREATE FUNCTION current_timestamp()
RETURNS TIMESTAMP
BEGIN
  RETURN NOW();
END;

-- Trigger to call the incr_post_count procedure after a new post is inserted
CREATE TRIGGER after_post_insert
AFTER INSERT ON posts
FOR EACH ROW
CALL incr_post_count(NEW.user_id);