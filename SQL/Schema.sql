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
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (followerId) REFERENCES User(id),
  FOREIGN KEY (targetId) REFERENCES User(id)
);

CREATE TABLE Notification (
  id INT AUTO_INCREMENT PRIMARY KEY,
  notifierId INT,
  targetId INT,
  imagePath VARCHAR(255),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (notifierId) REFERENCES User(id),
  FOREIGN KEY (targetId) REFERENCES User(id),
  FOREIGN KEY (imagePath) REFERENCES Picture(imagePath)
);

CREATE TABLE UserSession (
  sessionId INT PRIMARY KEY,
  userId INT UNIQUE,
  lastActive TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES User(id)
);

-- Dummy data for testing. Contains 24 users, 11 pictures, 11 picture likes, 19 notifications, and 101 follows. Includes data originally provided from OOM assignment.

INSERT INTO `user` VALUES (1,'Lorin','null','Password'),(2,'Xylo','Fierce warrior, not solo','Password'),(3,'Zara','Humanoid robot much like the rest','Password'),(4,'Mystar','Xylo and I are not the same!','Password'),(5,'max','Meow','121212'),(6,'max2','null','hahaha'),(10,'max3','meowzers','121212'),(11,'max4','mewozers','121212'),(12,'max5','meow','121212'),(13,'Jack','I\'m jack lol','121212'),(14,'John','my password is 121212','121212'),(15,'alice','Loves to travel and take pictures of sunsets.','password123'),(16,'bob','Avid mountain climber and coffee enthusiast.','password123'),(17,'carol','Bookworm with a passion for historical fiction.','password123'),(18,'dave','Gamer and aspiring game developer.','password123'),(19,'eve','Photographer with a love for nature.','password123'),(20,'frank','Tech geek and gadget lover.','password123'),(21,'grace','Artist and painter of surreal landscapes.','password123'),(22,'heidi','Chef in training, sharing my culinary adventures.','password123'),(23,'ivan','Fitness enthusiast and personal trainer.','password123'),(24,'judy','Baker and dessert lover.','password123');

INSERT INTO `follow` VALUES (1,2,1),(2,3,1),(3,4,1),(4,4,3),(5,1,4),(6,1,3),(7,5,4),(8,5,3),(10,6,5),(20,5,1),(28,15,16),(29,15,17),(30,15,18),(31,15,19),(32,15,20),(33,15,21),(34,15,22),(35,15,23),(36,15,24),(37,16,15),(38,16,17),(39,16,18),(40,16,19),(41,16,20),(42,16,21),(43,16,22),(44,16,23),(45,16,24),(46,17,15),(47,17,16),(48,17,18),(49,17,19),(50,17,20),(51,17,21),(52,17,22),(53,17,23),(54,17,24),(55,18,15),(56,18,16),(57,18,17),(58,18,19),(59,18,20),(60,18,21),(61,18,22),(62,18,23),(63,18,24),(64,19,15),(65,19,16),(66,19,17),(67,19,18),(68,19,20),(69,19,21),(70,19,22),(71,19,23),(72,19,24),(73,20,15),(74,20,16),(75,20,17),(76,20,18),(77,20,19),(78,20,21),(79,20,22),(80,20,23),(81,20,24),(82,21,15),(83,21,16),(84,21,17),(85,21,18),(86,21,19),(87,21,20),(88,21,22),(89,21,23),(90,21,24),(91,22,15),(92,22,16),(93,22,17),(94,22,18),(95,22,19),(96,22,20),(97,22,21),(98,22,23),(99,22,24),(100,23,15),(101,23,16),(102,23,17),(103,23,18),(104,23,19),(105,23,20),(106,23,21),(107,23,22),(108,23,24),(109,24,15),(110,24,16),(111,24,17),(112,24,18),(113,24,19),(114,24,20),(115,24,21),(116,24,22),(117,24,23),(118,3,5);

INSERT INTO `picture` VALUES ('Lorin_1.png','In the cookie jar my hand was not.',1,'2023-12-17 18:07:43'),('Lorin_2.png','Meditate I must.',1,'2023-12-17 18:09:35'),('max_1.png','Enter a caption',5,'2024-02-22 10:38:22'),('max_2.png','Enter a caption',5,'2024-02-29 10:13:56'),('max2_1.png','Enter a caption',6,'2024-03-28 20:57:37'),('Mystar_1.png','Cookies gone?',4,'2023-12-17 18:26:50'),('Mystar_2.png','In my soup a fly is.',4,'2023-12-17 18:27:24'),('Xylo_1.png','My tea strong as Force is.',2,'2023-12-17 18:22:40'),('Xylo_2.png','Jedi mind trick failed.',2,'2023-12-17 18:23:14'),('Zara_1.png','Lost my map I have. Oops.',3,'2023-12-17 18:24:31'),('Zara_2.png','Yoga with Yoda',3,'2023-12-17 18:25:03');

INSERT INTO `notification` VALUES (96,13,1,'Lorin_1.png','2024-02-28 13:15:24'),(97,3,1,'Lorin_2.png','2024-03-15 08:13:11'),(98,16,1,'Lorin_1.png','2024-01-22 07:22:45'),(99,19,1,'Lorin_2.png','2024-02-05 16:45:16'),(100,2,1,'Lorin_1.png','2024-03-01 11:30:00'),(101,14,1,'Lorin_2.png','2024-03-10 06:05:33'),(102,18,4,'Mystar_1.png','2024-01-18 18:34:56'),(103,15,4,'Mystar_2.png','2024-03-02 14:20:44'),(104,20,4,'Mystar_1.png','2024-02-11 12:11:58'),(105,5,3,'Zara_1.png','2024-02-18 09:14:09'),(106,17,3,'Zara_2.png','2024-01-25 22:12:15'),(107,13,3,'Zara_1.png','2024-03-07 21:09:31'),(108,15,3,'Zara_2.png','2024-02-27 05:15:12'),(109,4,5,'max_1.png','2024-01-30 10:13:00'),(110,16,5,'max_2.png','2024-02-23 17:44:29'),(111,20,5,'max_1.png','2024-03-14 15:20:47'),(112,18,5,'max_2.png','2024-01-17 13:33:50'),(113,1,5,'max_2.png','2024-02-12 11:10:05'),(114,3,5,'max_1.png','2024-03-13 09:22:40');

INSERT INTO `picturelike` VALUES (2,1,'Zara_1.png'),(4,1,'Zara_2.png'),(23,5,'max2_1.png'),(25,5,'Mystar_1.png'),(27,5,'Lorin_2.png'),(54,3,'max_1.png'),(55,3,'max_2.png'),(56,5,'Lorin_1.png'),(57,5,'Mystar_2.png'),(60,5,'Zara_2.png'),(61,5,'Zara_1.png');

INSERT INTO `usersession` VALUES (1,5,'2024-05-20 18:32:24');