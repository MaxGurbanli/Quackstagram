DROP DATABASE IF EXISTS quack;
CREATE DATABASE quack;
USE quack;

CREATE TABLE User (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) UNIQUE,
  bio VARCHAR(255),
  password VARCHAR(255),
  picturesCount INT DEFAULT 0,
  followersCount INT DEFAULT 0,
  followingCount INT DEFAULT 0
);

CREATE TABLE Picture (
  id INT AUTO_INCREMENT PRIMARY KEY,
  imagePath VARCHAR(255),
  caption VARCHAR(255),
  likesCount INT DEFAULT 0,
  authorId INT,
  FOREIGN KEY (authorId) REFERENCES User(id)
);

CREATE TABLE PictureLike (
  id INT AUTO_INCREMENT PRIMARY KEY,
  likerId INT,
  pictureId INT,
  FOREIGN KEY (likerId) REFERENCES User(id),
  FOREIGN KEY (pictureId) REFERENCES Picture(id)
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
  type VARCHAR(255),
  pictureId INT,
  FOREIGN KEY (notifierId) REFERENCES User(id),
  FOREIGN KEY (pictureId) REFERENCES Picture(id)
);
