DROP DATABASE IF EXISTS quack;
CREATE DATABASE quack;
USE quack;

CREATE TABLE User (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) UNIQUE,
  bio VARCHAR(255),
  password VARCHAR(255)
);

CREATE TABLE Picture (
  imagePath VARCHAR(255) PRIMARY KEY,
  caption VARCHAR(255),
  authorId INT,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (authorId) REFERENCES User(id)
);

CREATE TABLE PictureLike (
  id INT AUTO_INCREMENT PRIMARY KEY,
  likerId INT,
  imagePath VARCHAR(255),
  FOREIGN KEY (likerId) REFERENCES User(id),
  FOREIGN KEY (imagePath) REFERENCES Picture(imagePath)
);


CREATE TABLE Follow (
  id INT AUTO_INCREMENT PRIMARY KEY,
  followerId INT,
  targetId INT,
  FOREIGN KEY (followerId) REFERENCES User(id),
  FOREIGN KEY (targetId) REFERENCES User(id)
);

CREATE TABLE Notification (
  id INT AUTO_INCREMENT PRIMARY KEY,
  notifierId INT,
  targetId INT,
  imagePath VARCHAR(255),
  timestamp TIMESTAMP,
  FOREIGN KEY (notifierId) REFERENCES User(id),
  FOREIGN KEY (targetId) REFERENCES User(id),
  FOREIGN KEY (imagePath) REFERENCES Picture(imagePath)
);

CREATE TABLE PictureCount (
  userId INT,
  count INT DEFAULT 0,
  PRIMARY KEY (userId),
  FOREIGN KEY (userId) REFERENCES User(id)
);

CREATE TABLE FollowersCount (
  userId INT,
  count INT DEFAULT 0,
  PRIMARY KEY (userId),
  FOREIGN KEY (userId) REFERENCES User(id)
);

CREATE TABLE FollowingCount (
  userId INT,
  count INT DEFAULT 0,
  PRIMARY KEY (userId),
  FOREIGN KEY (userId) REFERENCES User(id)
);

CREATE TABLE NotificationType (
  typeId INT AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(255)
);

CREATE TABLE UserSession (
    sessionId INT PRIMARY KEY,
    userId INT,
    lastActive TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES User(id)
);
