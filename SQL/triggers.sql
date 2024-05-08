-- Remove existing objects if they exist
DROP FUNCTION IF EXISTS current_timestamp;
DROP PROCEDURE IF EXISTS UpdateFollowerCount;
DROP FUNCTION IF EXISTS CalculateTotalPictures;
DROP TRIGGER IF EXISTS after_follow_insert;
DROP TRIGGER IF EXISTS after_picture_post;

-- Function to get the current time
CREATE FUNCTION current_timestamp()
RETURNS TIMESTAMP
BEGIN
  RETURN NOW(); 
END;

-- Procedure to update how many followers a user has
CREATE PROCEDURE UpdateFollowerCount(IN userId INT)
BEGIN
    UPDATE User
    SET followersCount = (SELECT COUNT(*) FROM Follow WHERE targetId = userId)
    WHERE id = userId; -- Update the follower count in the User table
END;

-- Function to count how many pictures a user has posted
CREATE FUNCTION CalculateTotalPictures(userId INT) RETURNS INT
BEGIN
    DECLARE total_pictures INT;
    SELECT COUNT(*) INTO total_pictures FROM Picture WHERE authorId = userId; -- Get count of pictures
    RETURN total_pictures;
END;

-- Trigger to handle new followers
CREATE TRIGGER after_follow_insert AFTER INSERT ON Follow
FOR EACH ROW
BEGIN
    CALL UpdateFollowerCount(NEW.targetId); -- Call procedure to update followers count
END;

-- Trigger to update picture count after posting a new picture
CREATE TRIGGER after_picture_post AFTER INSERT ON Picture
FOR EACH ROW
BEGIN
    UPDATE User
    SET picturesCount = CalculateTotalPictures(NEW.authorId) -- Update picture count in User table
    WHERE id = NEW.authorId;
END;
