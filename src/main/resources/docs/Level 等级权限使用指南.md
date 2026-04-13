# 基于 Level 等级的动态权限系统

## 一、快速开始

### 1. 执行数据库初始化
```bash
source d:\项目\test\src\main\resources\sql\init_level_tables.sql
```

### 2. 初始账号
- **admin** / 密码需要加密后设置 - level 1（超级管理员）
- **user1** / 密码需要加密后设置 - level 999（新用户）

## 二、核心概念

### 等级制度
| 等级 | 名称 | 权限说明 |
|------|------|---------|
| level 1 | 超级管理员 | 最高权限，可以访问所有接口 |
| level 2 | 管理员 | 可以修改密码和管理低等级用户 |
| level 3 | 普通管理员 | 可以查询用户信息 |
| level 4-998 | 自定义等级 | 根据需求分配 |
| level 999 | 新用户 (unknown) | 只能登录和访问基础接口 |

**规则：数字越小，权限越大**

### 权限验证规则
```
用户等级 <= 接口要求等级 → 允许访问
用户等级 > 接口要求等级 → 拒绝访问
```

## 三、使用方式

### 1. Controller 中使用注解

```java
@RequireLevel(1)  // 需要 level 1 的用户
@PostMapping("/level/modify")
public Result modifyLevel(@RequestParam String username,
                          @RequestParam Integer level) {
    // ...
}

@RequireLevel(2)  // 需要 level 2 或更小的用户
@PostMapping("/password/modify")
public Result modifyPassword(@RequestParam String username,
                             @RequestParam String password) {
    // ...
}
```

### 2. 动态配置接口等级

```bash
# 查询所有接口等级配置
GET /api/level/interfaces

# 创建接口等级配置（需要 level 1）
POST /api/level/interface
Content-Type: application/json
{
  "interfacePath": "/api/user/delete",
  "interfaceMethod": "DELETE",
  "requiredLevel": 2,
  "description": "删除用户接口"
}

# 更新接口等级（需要 level 1）
PUT /api/level/interface
Content-Type: application/json
{
  "id": 1,
  "requiredLevel": 1
}

# 删除接口等级配置（需要 level 1）
DELETE /api/level/interface/1
```

### 3. 修改用户等级

```bash
# level 1 用户可以修改低等级用户（需要 level 1）
POST /auth/level/modify?username=zhangsan&level=3

# 规则：
# - 只能修改等级 > 自己的用户
# - 不能将用户等级修改为 <= 自己的等级
```

## 四、API 接口说明

### 等级管理接口（需要 level 1）

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/level/interfaces | GET | 查询所有接口等级配置 |
| /api/level/interface/{id} | GET | 查询单个接口等级配置 |
| /api/level/interface | POST | 创建接口等级配置 |
| /api/level/interface | PUT | 更新接口等级配置 |
| /api/level/interface/{id} | DELETE | 删除接口等级配置 |

### 用户管理接口

| 接口 | 方法 | 等级要求 | 说明 |
|------|------|---------|------|
| /auth/level/modify | POST | level 1 | 修改用户等级 |
| /auth/password/modify | POST | level 2 | 修改用户密码 |
| /auth/login | POST | level 999 | 用户登录 |
| /auth/register | POST | level 999 | 用户注册 |

## 五、等级分配规则

### 基本原则
1. **高等级可以分配低等级**：level 1 可以创建/修改 level 2,3,4...
2. **不能分配同级或更高等级**：level 2 不能修改 level 1 或 level 2
3. **新用户默认为 unknown**：注册时 level = 999

### 示例场景

✅ **正确示例：**
- level 1 → 修改 level 2 用户 ✓
- level 1 → 修改 level 999 用户 ✓
- level 2 → 修改 level 3 用户 ✓

❌ **错误示例：**
- level 2 → 修改 level 1 用户 ✗（等级不足）
- level 2 → 修改 level 2 用户 ✗（不能修改同级）
- level 2 → 将用户改为 level 1 ✗（不能改为更高等级）

## 六、数据库表结构

### user 表
```sql
CREATE TABLE `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `level` INT DEFAULT 999,
    `deleteFlag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### interface_level 表
```sql
CREATE TABLE `interface_level` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `interface_path` VARCHAR(200) NOT NULL,
    `interface_method` VARCHAR(10) NOT NULL,
    `required_level` INT NOT NULL,
    `description` VARCHAR(500),
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_path_method` (`interface_path`, `interface_method`)
);
```

## 七、优势

✅ **完全动态** - 接口等级存储在数据库，可随时修改  
✅ **灵活扩展** - 可以创建任意多个等级（1-999）  
✅ **向下兼容** - 高等级自动拥有低等级权限  
✅ **集中管理** - 所有接口等级配置在一个表中  
✅ **简单直观** - 数字等级，易于理解和管理  
✅ **纯后端 API** - 专注于后端接口权限控制  

## 八、注意事项

1. **level 数字越小权限越大**（1 是最高权限）
2. **首次使用需要设置至少一个 level 1 用户**
3. **注册的新用户 level 默认为 999**
4. **接口路径支持通配符**（如 `/api/**`）
5. **密码加密**：使用 salt + MD5 加密
6. **等级限制**：不能修改等级 >= 自己的用户
