-- Shows the number of posts each user has liked (user behavior)
CREATE VIEW UserLikes AS
SELECT username AS username, COUNT(*) AS totalLikes
FROM PostLike
GROUP BY username
HAVING totalLikes > 0;

-- Shows the number of likes for each post (content popularity)
CREATE VIEW PopularPosts AS
SELECT postId AS postID, COUNT(*) AS numberOfLikes
FROM PostLike
GROUP BY postID
HAVING numberOfLikes > 0;

-- Shows the number of notifications for each user (system analytics)
CREATE VIEW SystemAnalytics AS
SELECT target AS username, COUNT(*) AS notifications_count
FROM Notification
GROUP BY target
HAVING COUNT(*) > 0;

CREATE INDEX ind_postlike_username ON PostLike(username);
CREATE INDEX ind_notification_target ON Notification(target);