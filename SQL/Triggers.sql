DROP TRIGGER IF EXISTS UserSessionTrigger;
DROP TRIGGER IF EXISTS PictureLikeTrigger;

DELIMITER //

-- Trigger to update the lastActive timestamp of the user session when a new session is created
CREATE TRIGGER UserSessionTrigger
BEFORE INSERT ON UserSession
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

DELIMITER ;
