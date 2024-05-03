CREATE DATABASE IF NOT EXISTS quack;

USE quack;

CREATE TABLE User (
  username VARCHAR(255) PRIMARY KEY,
  bio VARCHAR(255),
  password VARCHAR(255),
  postsCount INT,
  followersCount INT,
  followingCount INT
);

CREATE TABLE Post (
  id INT AUTO_INCREMENT PRIMARY KEY,
  imagePath VARCHAR(255),
  caption VARCHAR(255),
  likesCount INT,
  username VARCHAR(255),
  FOREIGN KEY (username) REFERENCES User(username)
);

CREATE TABLE PostLike (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255),
  postId INT,
  FOREIGN KEY (username) REFERENCES User(username),
  FOREIGN KEY (postId) REFERENCES Post(id)
);

CREATE TABLE Follow (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255),
  target VARCHAR(255),
  FOREIGN KEY (username) REFERENCES User(username),
  FOREIGN KEY (target) REFERENCES User(username)
);

CREATE TABLE Notification (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255),
  target VARCHAR(255),
  FOREIGN KEY (username) REFERENCES User(username),
  FOREIGN KEY (target) REFERENCES User(username)
);