-- Remove existing objects if they exist
DROP FUNCTION IF EXISTS current_timestamp;

-- Function to get the current time
CREATE FUNCTION current_timestamp()
RETURNS TIMESTAMP
BEGIN
  RETURN NOW(); 
END;

DROP TRIGGER IF EXISTS NotificationTrigger;
-- Trigger to update the timestamp of the notification when a new notification is added
CREATE TRIGGER NotificationTrigger
AFTER INSERT
ON Notification
FOR EACH ROW
BEGIN
  UPDATE Notification
  SET timestamp = current_timestamp()
  WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS FollowTrigger;
-- Trigger to update the timestamp of the follow when a new follow is added
CREATE TRIGGER FollowTrigger
AFTER INSERT
ON Follow
FOR EACH ROW
BEGIN
  UPDATE Follow
  SET timestamp = current_timestamp()
  WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS UserSessionTrigger;
-- Trigger to update the lastActive timestamp of the user session when a new session is created
CREATE TRIGGER UserSessionTrigger
AFTER INSERT
ON UserSession
FOR EACH ROW
BEGIN
  UPDATE UserSession
  SET lastActive = current_timestamp()
  WHERE sessionId = NEW.sessionId;
END;

DROP TRIGGER IF EXISTS PictureTrigger;
-- Trigger to update the timestamp of the picture when a new picture is uploaded
CREATE TRIGGER PictureTrigger
AFTER INSERT
ON Picture
FOR EACH ROW
BEGIN
  UPDATE Picture
  SET timestamp = current_timestamp()
  WHERE imagePath = NEW.imagePath;
END;