-- MySQL dump 10.13  Distrib 8.0.45, for macos15 (arm64)
--
-- Host: localhost    Database: springboot-vue
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_book`
--

DROP TABLE IF EXISTS `sys_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `isbn` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '图书名称',
  `author` varchar(50) NOT NULL COMMENT '作者',
  `publisher` varchar(100) NOT NULL COMMENT '出版社',
  `publish_time` datetime NOT NULL COMMENT '出版时间',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `borrow_num` int DEFAULT '0',
  `description` text COMMENT '图书描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`,`isbn`),
  KEY `idx_name` (`name`),
  KEY `idx_author` (`author`),
  KEY `idx_publisher` (`publisher`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_book`
--

LOCK TABLES `sys_book` WRITE;
/*!40000 ALTER TABLE `sys_book` DISABLE KEYS */;
INSERT INTO `sys_book` VALUES (1,'978-7-537-18062-8','红楼梦','曹雪芹','人民文学出版社','2018-01-01 00:00:00',59.80,1,10,0,'中国古典四大名著之一','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(2,'978-7-697-42611-7','三体','刘慈欣','重庆出版社','2008-01-01 00:00:00',68.00,2,8,0,'科幻小说代表作','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(3,'978-7-104-61272-7','活着','余华','作家出版社','2012-08-01 00:00:00',29.00,1,15,0,'余华经典作品','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(4,'978-7-512-81328-5','百年孤独','加西亚·马尔克斯','南海出版公司','2017-08-01 00:00:00',55.00,1,6,0,'魔幻现实主义文学代表作','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(5,'978-7-600-12003-3','Java编程思想','Bruce Eckel','机械工业出版社','2007-06-01 00:00:00',108.00,2,5,0,'Java经典教材','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(6,'978-7-227-44412-4','深入理解Java虚拟机','周志明','机械工业出版社','2021-05-01 00:00:00',129.00,2,12,0,'JVM深入学习必读','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(7,'978-7-360-98916-0','数据库系统概论','王珊','高等教育出版社','2019-09-01 00:00:00',49.00,3,20,0,'数据库经典教材','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(8,'978-7-445-74246-3','Spring Boot实战','丁雪丰','人民邮电出版社','2020-03-01 00:00:00',79.00,2,7,0,'Spring Boot入门实战','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(9,'978-7-941-49019-3','Vue.js实战','陈刚','电子工业出版社','2019-07-01 00:00:00',89.00,2,9,0,'Vue前端开发指南','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(10,'978-7-547-46908-5','中国通史','吕思勉','上海古籍出版社','2016-01-01 00:00:00',45.00,4,11,0,'中国历史经典著作','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(11,'978-7-598-19177-7','小王子','圣埃克苏佩里','人民文学出版社','2015-01-01 00:00:00',32.00,5,18,0,'世界经典童话','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(12,'978-7-931-17703-5','经济学原理','曼昆','北京大学出版社','2020-01-01 00:00:00',128.00,6,4,0,'经济学入门教材','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(13,'978-7-109-18483-3','设计模式','Gang of Four','机械工业出版社','2019-01-01 00:00:00',69.00,2,6,0,'软件设计经典','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(14,'978-7-917-30678-3','围城','钱钟书','人民文学出版社','2017-11-01 00:00:00',36.00,1,13,0,'中国现代文学经典','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(15,'978-7-477-85269-8','时间简史','霍金','湖南科学技术出版社','2018-06-01 00:00:00',45.00,2,8,0,'科普经典著作','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(16,'978-7-183-73285-2','西游记','吴承恩','人民文学出版社','2018-01-01 00:00:00',49.80,1,10,0,'中国古典四大名著之一','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(17,'978-7-169-70625-1','水浒传','施耐庵','人民文学出版社','2018-01-01 00:00:00',55.00,1,9,0,'中国古典四大名著之一','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(18,'978-7-695-91347-4','三国演义','罗贯中','人民文学出版社','2018-01-01 00:00:00',52.00,1,11,0,'中国古典四大名著之一','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(19,'978-7-947-20987-7','算法导论','Thomas H. Cormen','机械工业出版社','2013-01-01 00:00:00',128.00,2,3,0,'算法经典教材','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL),(20,'978-7-586-42576-1','人类简史','尤瓦尔·赫拉利','中信出版社','2017-01-01 00:00:00',68.00,4,7,0,'人类历史全景','2026-04-13 10:43:32','2026-04-14 11:04:18',NULL);
/*!40000 ALTER TABLE `sys_book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_borrow`
--

DROP TABLE IF EXISTS `sys_borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_borrow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '借阅ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `borrow_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
  `return_deadline` datetime NOT NULL COMMENT '归还截止时间',
  `return_time` datetime DEFAULT NULL COMMENT '归还时间',
  `status` varchar(20) NOT NULL DEFAULT 'unreturned' COMMENT '借阅状态：unreturned-未归还，returned-已归还，overdue-逾期',
  `overdue_days` int DEFAULT '0' COMMENT '逾期天数',
  `prolong` int DEFAULT '3',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  KEY `idx_user_book` (`user_id`,`book_id`),
  KEY `idx_user_status` (`user_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='借阅记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_borrow`
--

LOCK TABLES `sys_borrow` WRITE;
/*!40000 ALTER TABLE `sys_borrow` DISABLE KEYS */;
INSERT INTO `sys_borrow` VALUES (1,3,1,'2026-03-01 10:00:00','2026-03-31 10:00:00','2026-03-20 14:00:00','returned',0,3,NULL),(2,3,2,'2026-03-15 09:00:00','2026-04-14 09:00:00',NULL,'unreturned',0,3,NULL),(3,4,3,'2026-02-01 11:00:00','2026-03-03 11:00:00','2026-03-10 16:00:00','overdue',7,3,NULL),(4,5,5,'2026-03-20 08:00:00','2026-04-19 08:00:00',NULL,'unreturned',0,3,NULL),(5,6,7,'2026-03-10 10:00:00','2026-04-09 10:00:00','2026-04-05 11:00:00','returned',0,3,NULL),(6,7,9,'2026-01-15 14:00:00','2026-02-14 14:00:00','2026-02-20 09:00:00','overdue',6,3,NULL),(7,3,10,'2026-03-25 16:00:00','2026-04-24 16:00:00',NULL,'unreturned',0,3,NULL),(8,4,12,'2026-03-28 09:00:00','2026-04-27 09:00:00',NULL,'unreturned',0,3,NULL),(9,3,20,'2026-04-16 18:38:52','2026-05-16 18:38:52',NULL,'unreturned',0,3,NULL),(10,3,19,'2026-04-16 18:44:35','2026-05-16 18:44:35',NULL,'unreturned',0,3,NULL),(11,3,18,'2026-04-16 18:46:06','2026-05-16 18:46:06',NULL,'unreturned',0,3,NULL);
/*!40000 ALTER TABLE `sys_borrow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_category`
--

DROP TABLE IF EXISTS `sys_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `code` varchar(45) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`,`code`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_category`
--

LOCK TABLES `sys_category` WRITE;
/*!40000 ALTER TABLE `sys_category` DISABLE KEYS */;
INSERT INTO `sys_category` VALUES (1,'AB152880','文学','2026-04-13 10:43:32','2026-04-15 10:13:18','包含小说、散文、诗歌等1'),(2,'AB15434C','科技','2026-04-13 10:43:32','2026-04-15 10:13:18','包含计算机、物理、化学等'),(3,'AB154608','教育','2026-04-13 10:43:32','2026-04-15 10:13:18','包含教材、教辅、考试等'),(4,'AB1547D4','历史','2026-04-13 10:43:32','2026-04-15 10:13:18','包含中国历史、世界历史等'),(5,'AB154982','儿童','2026-04-13 10:43:32','2026-04-15 10:13:18','包含绘本、童话、科普等'),(6,'AB154B1C','经济','2026-04-13 10:43:32','2026-04-15 10:13:18','包含金融、管理、投资等'),(7,'AB154CC0','艺术','2026-04-13 10:43:32','2026-04-15 10:13:18','包含音乐、美术、设计等'),(8,'AB154E5A','生活','2026-04-13 10:43:32','2026-04-15 10:13:18','包含美食、旅游、健康等');
/*!40000 ALTER TABLE `sys_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `time` bigint DEFAULT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
INSERT INTO `sys_log` VALUES (1,'admin','修改用户','com.library.controller.UserController.updateUser()','{\"id\":8,\"username\":\"zhangsan\",\"password\":\"$2a$10$IXkIEurrgbM/KlewPiARk.dG/ieUv9LxuXaVbuHo/RDU8RjgJLxmC\",\"nickname\":\"张三\",\"sex\":null,\"role\":\"user\",\"phone\":null,\"email\":null,\"address\":null,\"remark\":null}',32,'127.0.0.1','2026-04-16 17:04:15'),(2,'admin','修改用户','com.library.controller.UserController.updateUser()','{\"id\":8,\"username\":\"zhangsan\",\"password\":\"$2a$10$IXkIEurrgbM/KlewPiARk.dG/ieUv9LxuXaVbuHo/RDU8RjgJLxmC\",\"nickname\":\"张三\",\"sex\":1,\"role\":\"user\",\"phone\":\"13232323232\",\"email\":null,\"address\":\"地址地址地址地址地址\",\"remark\":null}',21,'127.0.0.1','2026-04-16 17:04:57'),(3,'user01','借阅图书','com.library.controller.BorrowController.borrowBook()','{\"bookId\":20,\"bookIsbn\":null,\"bookName\":null,\"userName\":null,\"status\":\"0\",\"userId\":3,\"pageNum\":1,\"pageSize\":10}',39,'127.0.0.1','2026-04-16 18:38:52'),(4,'user01','借阅图书','com.library.controller.BorrowController.borrowBook()','{\"bookId\":19,\"bookIsbn\":null,\"bookName\":null,\"userName\":null,\"status\":\"0\",\"userId\":3,\"pageNum\":1,\"pageSize\":10}',31,'127.0.0.1','2026-04-16 18:44:35'),(5,'user01','借阅图书','com.library.controller.BorrowController.borrowBook()','{\"bookId\":18,\"bookIsbn\":null,\"bookName\":null,\"userName\":null,\"status\":\"0\",\"userId\":3,\"pageNum\":1,\"pageSize\":10}',36,'127.0.0.1','2026-04-16 18:46:06'),(6,'user01','新增借阅记录','com.library.controller.BookWithUserController.insertNew()','{\"bookId\":null,\"bookIsbn\":null,\"bookName\":\"三国演义\",\"userName\":null,\"status\":null,\"userId\":3,\"pageNum\":1,\"pageSize\":10}',1,'127.0.0.1','2026-04-16 18:46:06');
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin-管理员，user-普通用户',
  `nickname` varchar(45) DEFAULT NULL,
  `sex` int DEFAULT NULL,
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','admin','昵称',1,'13800000001','admin@library.com','北京市海淀区','2026-04-13 10:43:32',NULL,'超级管理员'),(2,'admin2','$2a$10$lGa5myrsdce6YBS69mhY1.mRMOCXCDfiy8MPyqcMWB3sZkZ96/joW','admin',NULL,NULL,'13800000002','admin2@library.com','北京市朝阳区','2026-04-13 10:43:32',NULL,'管理员2'),(3,'user01','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','user',NULL,NULL,'13900000001','user01@test.com','北京市西城区','2026-04-13 10:43:32',NULL,NULL),(4,'user02','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','user',NULL,NULL,'13900000002','user02@test.com','上海市浦东新区','2026-04-13 10:43:32',NULL,NULL),(5,'user03','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','user',NULL,NULL,'13900000003','user03@test.com','广州市天河区','2026-04-13 10:43:32',NULL,NULL),(6,'user04','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','user',NULL,NULL,'13900000004','user04@test.com','深圳市南山区','2026-04-13 10:43:32',NULL,NULL),(7,'user05','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','user',NULL,NULL,'13900000005','user05@test.com','杭州市西湖区','2026-04-13 10:43:32',NULL,NULL),(8,'zhangsan','$2a$10$IXkIEurrgbM/KlewPiARk.dG/ieUv9LxuXaVbuHo/RDU8RjgJLxmC','user','张三',1,'13232323232',NULL,'地址地址地址地址地址','2026-04-16 17:01:58','2026-04-16 17:01:58',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'springboot-vue'
--

--
-- Dumping routines for database 'springboot-vue'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-17 17:28:58
