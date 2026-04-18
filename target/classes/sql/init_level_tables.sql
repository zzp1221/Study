-- ============================================
-- 基于 Level 等级的动态权限系统数据库初始化脚本
-- ============================================

-- 1. 创建 database（如果不存在）
CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `test`;

-- 2. 创建 user 表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    `level` INT DEFAULT 999 COMMENT '用户等级，数字越小权限越大',
    `deleteFlag` TINYINT DEFAULT 0 COMMENT '删除标记：0-未删除，1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 3. 创建 interface_level 表（接口等级配置表）
DROP TABLE IF EXISTS `interface_level`;
CREATE TABLE `interface_level` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置 ID',
    `interface_path` VARCHAR(200) NOT NULL COMMENT '接口路径',
    `interface_method` VARCHAR(10) NOT NULL COMMENT 'HTTP 方法',
    `allowed_levels` VARCHAR(500) NOT NULL COMMENT '允许的等级列表，逗号分隔',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_path_method` (`interface_path`, `interface_method`),
    INDEX `idx_allowed_levels` (`allowed_levels`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口等级配置表';

-- 5. 初始化接口等级配置数据
-- allowed_levels 格式：逗号分隔的等级列表，如 "1,2,3" 表示允许 level 1、2、3 访问
-- 注意：登录、注册接口是开放接口，不需要配置在表中
INSERT INTO `interface_level` (`interface_path`, `interface_method`, `allowed_levels`, `description`, `status`) VALUES
-- ============================================
-- 初始接口
-- ============================================
('/api/level/interfaces', 'GET', '1', '查询所有接口等级配置', 1),
('/api/level/interface/**', 'GET', '1', '查询单个接口等级配置', 1),
('/api/level/interface', 'POST', '1', '创建接口等级配置', 1),
('/api/level/interface', 'PUT', '1', '更新接口等级配置', 1),
('/api/level/interface/**', 'DELETE', '1', '删除接口等级配置', 1),
('/auth/level/modify', 'POST', '1', '修改用户等级', 1),
('/auth/password/modify', 'POST', '1', '修改用户密码', 1),
('/api/user/**', 'GET', '1', '用户查询接口', 1);


