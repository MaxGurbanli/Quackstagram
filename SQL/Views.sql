-- Shows the number of pictures each user has liked (user behavior)
CREATE VIEW UserLikes AS
SELECT username AS username, COUNT(*) AS totalLikes
FROM PictureLike
GROUP BY username
HAVING totalLikes > 0;

-- Shows the number of likes for each picture (content popularity)
CREATE VIEW PopularPictures AS
SELECT pictureId AS pictureID, COUNT(*) AS numberOfLikes
FROM PictureLike
GROUP BY pictureID
HAVING numberOfLikes > 0;

-- Shows the number of notifications for each user (system analytics)
CREATE VIEW SystemAnalytics AS
SELECT target AS username, COUNT(*) AS notifications_count
FROM Notification
GROUP BY target
HAVING COUNT(*) > 0;

CREATE INDEX ind_picturelike_username ON PictureLike(username);
CREATE INDEX ind_notification_target ON Notification(target);