/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : springboot-vue

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 18/04/2022 17:29:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 用户表
-- ========================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    role        VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：admin-管理员，user-普通用户',
    phone       VARCHAR(11)           DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(50)           DEFAULT NULL COMMENT '邮箱',
    address     VARCHAR(200)          DEFAULT NULL COMMENT '地址',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200)          DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- ========================================
-- 图书分类表
-- ========================================
DROP TABLE IF EXISTS sys_category;
CREATE TABLE sys_category
(
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200)         DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='图书分类表';

-- ========================================
-- 图书表
-- ========================================
DROP TABLE IF EXISTS sys_book;
CREATE TABLE sys_book
(
    id           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    name         VARCHAR(100)   NOT NULL COMMENT '图书名称',
    author       VARCHAR(50)    NOT NULL COMMENT '作者',
    publisher    VARCHAR(100)   NOT NULL COMMENT '出版社',
    publish_time DATETIME       NOT NULL COMMENT '出版时间',
    price        DECIMAL(10, 2) NOT NULL COMMENT '价格',
    category_id  BIGINT         NOT NULL COMMENT '分类ID',
    stock        INT            NOT NULL DEFAULT 0 COMMENT '库存数量',
    description  TEXT                    DEFAULT NULL COMMENT '图书描述',
    create_time  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME                DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark       VARCHAR(200)            DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_name (name),
    KEY idx_author (author),
    KEY idx_publisher (publisher),
    KEY idx_category_id (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='图书表';

-- ========================================
-- 借阅记录表
-- ========================================
DROP TABLE IF EXISTS sys_borrow;
CREATE TABLE sys_borrow
(
    id              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '借阅ID',
    user_id         BIGINT      NOT NULL COMMENT '用户ID',
    book_id         BIGINT      NOT NULL COMMENT '图书ID',
    borrow_time     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_deadline DATETIME    NOT NULL COMMENT '归还截止时间',
    return_time     DATETIME             DEFAULT NULL COMMENT '归还时间',
    status          VARCHAR(20) NOT NULL DEFAULT 'unreturned' COMMENT '借阅状态：unreturned-未归还，returned-已归还，overdue-逾期',
    overdue_days    INT                  DEFAULT 0 COMMENT '逾期天数',
    remark          VARCHAR(200)         DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_book_id (book_id),
    KEY idx_status (status),
    KEY idx_user_book (user_id, book_id),
    KEY idx_user_status (user_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='借阅记录表';

-- ========================================
-- 初始化数据
-- ========================================

-- 管理员（密码: admin123，BCrypt加密）
INSERT INTO sys_user (username, password, role, phone, email, address, remark)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', '13800000001',
        'admin@library.com', '北京市海淀区', '超级管理员'),
       ('admin2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin', '13800000002',
        'admin2@library.com', '北京市朝阳区', '管理员2');

-- 普通用户（密码: 123456，BCrypt加密）
INSERT INTO sys_user (username, password, role, phone, email, address)
VALUES ('user01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user', '13900000001',
        'user01@test.com', '北京市西城区'),
       ('user02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user', '13900000002',
        'user02@test.com', '上海市浦东新区'),
       ('user03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user', '13900000003',
        'user03@test.com', '广州市天河区'),
       ('user04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user', '13900000004',
        'user04@test.com', '深圳市南山区'),
       ('user05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user', '13900000005',
        'user05@test.com', '杭州市西湖区');

-- 图书分类
INSERT INTO sys_category (name, remark)
VALUES ('文学', '包含小说、散文、诗歌等'),
       ('科技', '包含计算机、物理、化学等'),
       ('教育', '包含教材、教辅、考试等'),
       ('历史', '包含中国历史、世界历史等'),
       ('儿童', '包含绘本、童话、科普等'),
       ('经济', '包含金融、管理、投资等'),
       ('艺术', '包含音乐、美术、设计等'),
       ('生活', '包含美食、旅游、健康等');

-- 图书数据
INSERT INTO sys_book (name, author, publisher, publish_time, price, category_id, stock, description)
VALUES ('红楼梦', '曹雪芹', '人民文学出版社', '2018-01-01', 59.80, 1, 10, '中国古典四大名著之一'),
       ('三体', '刘慈欣', '重庆出版社', '2008-01-01', 68.00, 2, 8, '科幻小说代表作'),
       ('活着', '余华', '作家出版社', '2012-08-01', 29.00, 1, 15, '余华经典作品'),
       ('百年孤独', '加西亚·马尔克斯', '南海出版公司', '2017-08-01', 55.00, 1, 6, '魔幻现实主义文学代表作'),
       ('Java编程思想', 'Bruce Eckel', '机械工业出版社', '2007-06-01', 108.00, 2, 5, 'Java经典教材'),
       ('深入理解Java虚拟机', '周志明', '机械工业出版社', '2021-05-01', 129.00, 2, 12, 'JVM深入学习必读'),
       ('数据库系统概论', '王珊', '高等教育出版社', '2019-09-01', 49.00, 3, 20, '数据库经典教材'),
       ('Spring Boot实战', '丁雪丰', '人民邮电出版社', '2020-03-01', 79.00, 2, 7, 'Spring Boot入门实战'),
       ('Vue.js实战', '陈刚', '电子工业出版社', '2019-07-01', 89.00, 2, 9, 'Vue前端开发指南'),
       ('中国通史', '吕思勉', '上海古籍出版社', '2016-01-01', 45.00, 4, 11, '中国历史经典著作'),
       ('小王子', '圣埃克苏佩里', '人民文学出版社', '2015-01-01', 32.00, 5, 18, '世界经典童话'),
       ('经济学原理', '曼昆', '北京大学出版社', '2020-01-01', 128.00, 6, 4, '经济学入门教材'),
       ('设计模式', 'Gang of Four', '机械工业出版社', '2019-01-01', 69.00, 2, 6, '软件设计经典'),
       ('围城', '钱钟书', '人民文学出版社', '2017-11-01', 36.00, 1, 13, '中国现代文学经典'),
       ('时间简史', '霍金', '湖南科学技术出版社', '2018-06-01', 45.00, 2, 8, '科普经典著作'),
       ('西游记', '吴承恩', '人民文学出版社', '2018-01-01', 49.80, 1, 10, '中国古典四大名著之一'),
       ('水浒传', '施耐庵', '人民文学出版社', '2018-01-01', 55.00, 1, 9, '中国古典四大名著之一'),
       ('三国演义', '罗贯中', '人民文学出版社', '2018-01-01', 52.00, 1, 11, '中国古典四大名著之一'),
       ('算法导论', 'Thomas H. Cormen', '机械工业出版社', '2013-01-01', 128.00, 2, 3, '算法经典教材'),
       ('人类简史', '尤瓦尔·赫拉利', '中信出版社', '2017-01-01', 68.00, 4, 7, '人类历史全景');

-- 借阅记录
INSERT INTO sys_borrow (user_id, book_id, borrow_time, return_deadline, return_time, status, overdue_days)
VALUES (3, 1, '2026-03-01 10:00:00', '2026-03-31 10:00:00', '2026-03-20 14:00:00', 'returned', 0),
       (3, 2, '2026-03-15 09:00:00', '2026-04-14 09:00:00', NULL, 'unreturned', 0),
       (4, 3, '2026-02-01 11:00:00', '2026-03-03 11:00:00', '2026-03-10 16:00:00', 'overdue', 7),
       (5, 5, '2026-03-20 08:00:00', '2026-04-19 08:00:00', NULL, 'unreturned', 0),
       (6, 7, '2026-03-10 10:00:00', '2026-04-09 10:00:00', '2026-04-05 11:00:00', 'returned', 0),
       (7, 9, '2026-01-15 14:00:00', '2026-02-14 14:00:00', '2026-02-20 09:00:00', 'overdue', 6),
       (3, 10, '2026-03-25 16:00:00', '2026-04-24 16:00:00', NULL, 'unreturned', 0),
       (4, 12, '2026-03-28 09:00:00', '2026-04-27 09:00:00', NULL, 'unreturned', 0);