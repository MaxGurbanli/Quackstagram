-- Remove existing objects if they exist
DROP TRIGGER IF EXISTS NotificationTrigger;
DROP TRIGGER IF EXISTS FollowTrigger;
DROP TRIGGER IF EXISTS UserSessionTrigger;
DROP TRIGGER IF EXISTS PictureTrigger;

-- Trigger to update the lastActive timestamp of the user session when a new session is created
CREATE TRIGGER UserSessionTrigger
BEFORE INSERT ON UserSession
FOR EACH ROW
BEGIN
  SET NEW.lastActive = NOW();
END;
