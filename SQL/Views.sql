CREATE VIEW UserLikes AS
SELECT username AS username, COUNT(*) AS totalLikes
FROM PostLike
GROUP BY username
HAVING totalLikes > 0;

CREATE VIEW PopularPosts AS
SELECT postId AS postID, COUNT(*) AS numberOfLikes
FROM PostLike
GROUP BY postID
HAVING numberOfLikes > 0;