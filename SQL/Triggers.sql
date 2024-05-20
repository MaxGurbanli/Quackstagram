DELIMITER //

DROP TRIGGER IF EXISTS UserSessionTrigger;
DROP TRIGGER IF EXISTS PictureLikeTrigger;
DROP FUNCTION IF EXISTS getLikesCount;
DROP PROCEDURE IF EXISTS deleteUser;

-- Trigger to update the lastActive timestamp when a new session is created
CREATE TRIGGER UserSessionBeforeInsertTrigger
BEFORE INSERT ON UserSession
FOR EACH ROW
BEGIN
  SET NEW.lastActive = NOW();
END;
//

-- Trigger to update the lastActive timestamp when a session is updated
CREATE TRIGGER UserSessionBeforeUpdateTrigger
BEFORE UPDATE ON UserSession
FOR EACH ROW
BEGIN
  SET NEW.lastActive = NOW();
END;
//

-- Trigger to create a notification when a picture is liked
CREATE TRIGGER PictureLikeTrigger
AFTER INSERT ON PictureLike
FOR EACH ROW
BEGIN
  DECLARE authorId INT DEFAULT NULL;
  SELECT authorId INTO authorId FROM Picture WHERE imagePath = NEW.imagePath LIMIT 1;
  IF authorId IS NOT NULL THEN
    INSERT INTO Notification (notifierId, targetId, imagePath, timestamp)
    VALUES (NEW.likerId, authorId, NEW.imagePath, NOW());
  END IF;
END;
//

-- Function to get the number of likes for a picture
CREATE FUNCTION getLikesCount(imagePath VARCHAR(255)) 
RETURNS INT 
DETERMINISTIC
BEGIN
  DECLARE likesCount INT;
  SELECT COUNT(*) INTO likesCount
  FROM PictureLike
  WHERE PictureLike.imagePath = imagePath;
  RETURN likesCount;
END;
//

-- This procedure deletes a user and all related data
CREATE PROCEDURE deleteUser(IN userId INT)
BEGIN
    -- Delete PictureLikes associated with user's pictures
    DELETE FROM PictureLike WHERE imagePath IN (SELECT imagePath FROM Picture WHERE authorId = userId);

    -- Delete Notifications related to user's pictures
    DELETE FROM Notification WHERE imagePath IN (SELECT imagePath FROM Picture WHERE authorId = userId);

    -- Delete the user's sessions
    DELETE FROM UserSession WHERE userId = userId;

    -- Delete likes made by the user
    DELETE FROM PictureLike WHERE likerId = userId;

    -- Delete notifications where the user is the notifier or target
    DELETE FROM Notification WHERE notifierId = userId OR targetId = userId;

    -- Delete the user's pictures
    DELETE FROM Picture WHERE authorId = userId;

    -- Delete the user
    DELETE FROM User WHERE id = userId;
END;
//

DELIMITER ;