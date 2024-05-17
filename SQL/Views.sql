DROP VIEW IF EXISTS UserLikes;
-- Shows the number of pictures each user has liked (user behavior)
CREATE VIEW UserLikes AS
SELECT likerId AS likerId, COUNT(*) AS totalLikes
FROM PictureLike
GROUP BY likerId
HAVING totalLikes > 0;

DROP VIEW IF EXISTS PopularPictures;
-- Shows the number of likes for each picture (content popularity)
CREATE VIEW PopularPictures AS
SELECT imagePath AS imagePath, COUNT(*) AS numberOfLikes
FROM PictureLike
GROUP BY imagePath
HAVING numberOfLikes > 0;

DROP VIEW IF EXISTS SystemAnalytics;
-- Shows the number of notifications for each user (system analytics)
CREATE VIEW SystemAnalytics AS
SELECT targetId AS targetId, COUNT(*) AS notifications_count
FROM Notification
GROUP BY targetId
HAVING COUNT(*) > 0;

CREATE INDEX ind_picturelike_username ON PictureLike(likerId);
CREATE INDEX ind_notification_target ON Notification(targetId);