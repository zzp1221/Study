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
    `required_level` INT NOT NULL COMMENT '需要的等级',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_path_method` (`interface_path`, `interface_method`),
    INDEX `idx_required_level` (`required_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接口等级配置表';

-- 4. 初始化用户数据
-- admin 用户密码：123456
-- 加密方式：salt + MD5(password + salt)
-- salt: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
-- MD5(123456 + salt): e8c05ea54b90e424f1eae8e5437ebc97
INSERT INTO `user` (`username`, `password`, `level`, `deleteFlag`) VALUES
('admin', 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6e8c05ea54b90e424f1eae8e5437ebc97', 1),
('user1', 'b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7f9d16fb65c01f535g2fbf9f6548fcd08', 999);

-- 说明：
-- admin 用户：level=1（超级管理员），密码：123456
-- user1 用户：level=999（新用户，unknown），密码：123456
-- 密码格式：salt(32 位) + MD5(password+salt)(32 位)
-- 实际使用时请通过注册接口创建用户，密码会自动加密

-- 5. 初始化接口等级配置数据
INSERT INTO `interface_level` (`interface_path`, `interface_method`, `required_level`, `description`, `status`) VALUES
-- ============================================
-- Level 1 接口（超级管理员专用）
-- ============================================
('/api/level/interfaces', 'GET', 1, '查询所有接口等级配置', 1),
('/api/level/interface/**', 'GET', 1, '查询单个接口等级配置', 1),
('/api/level/interface', 'POST', 1, '创建接口等级配置', 1),
('/api/level/interface', 'PUT', 1, '更新接口等级配置', 1),
('/api/level/interface/**', 'DELETE', 1, '删除接口等级配置', 1),
('/auth/level/modify', 'POST', 1, '修改用户等级', 1),

-- ============================================
-- Level 2 接口（管理员可用）
-- ============================================
('/auth/password/modify', 'POST', 2, '修改用户密码', 1),

-- ============================================
-- Level 3 接口（普通管理员可用）
-- ============================================
('/api/user/**', 'GET', 3, '用户查询接口', 1),

-- ============================================
-- Level 999 接口（所有登录用户可用）
-- ============================================
('/auth/login', 'POST', 999, '用户登录', 1),
('/auth/register', 'POST', 999, '用户注册', 1),
('/**', 'OPTIONS', 999, 'CORS 预检请求', 1);

-- 6. 等级说明
-- level 1: 超级管理员 - 可以访问所有接口，可以修改任何低等级用户
-- level 2: 管理员 - 可以修改密码和管理低等级用户
-- level 3: 普通管理员 - 可以查询用户信息
-- level 4-998: 自定义等级 - 根据需求分配
-- level 999: 新用户 (unknown) - 只能登录和访问基础接口

-- 7. 权限验证规则
-- 用户等级 <= 接口要求等级 → 允许访问
-- 用户等级 > 接口要求等级 → 拒绝访问
-- 示例：
--   level 1 用户可以访问 level >= 1 的所有接口
--   level 2 用户可以访问 level >= 2 的接口
--   level 999 用户只能访问 level >= 999 的接口

-- 8. 高等级用户可以分配低等级
-- level 1 可以创建/修改 level 2, 3, 4, ..., 999
-- level 2 可以创建/修改 level 3, 4, ..., 999
-- 不能修改等级 >= 自己的用户

-- ============================================
-- 初始化完成
-- ============================================
