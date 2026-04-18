# 基于 Level 等级的动态权限系统（多等级授权版）

## 一、快速开始

### 1. 执行数据库初始化
```bash
source d:\项目\test\src\main\resources\sql\init_level_tables.sql
```

### 2. 初始账号
- **admin** / 密码：123456 - level 1（超级管理员）
- **user1** / 密码：123456 - level 999（新用户）

## 二、核心概念

### 等级制度
| 等级 | 名称 | 权限说明 |
|------|------|---------|
| level 1 | 超级管理员 | 最高权限，可以访问所有接口（特殊权限） |
| level 2 | 管理员 | 可以修改密码和管理低等级用户 |
| level 3 | 普通管理员 | 可以查询用户信息 |
| level 4-998 | 自定义等级 | 根据需求分配 |
| level 999 | 新用户 (unknown) | 只能登录和访问基础接口 |

**规则：数字越小，权限越大**

### 权限验证规则（重要）

**新版规则：**
1. **level 1 用户（超级管理员）**：可以访问所有接口，无需检查 `allowed_levels`
2. **其他用户（level 2+）**：等级没有高低之分，仅通过 `allowed_levels` 决定是否可访问
3. **开放接口**：登录、注册、CORS 预检请求不需要配置，任何人都可以访问

**接口配置格式：**
```
allowed_levels = "1,2,3"  → 允许 level 1、2、3 的用户访问
allowed_levels = "1"      → 只允许 level 1 访问
allowed_levels = "2,999"  → 允许 level 2、999 访问
```

**示例：**
- `allowed_levels = "1"` → 只有超级管理员可以访问
- `allowed_levels = "1,2"` → 超级管理员和 level 2 可以访问
- `allowed_levels = "2,999"` → level 2 和 level 999 可以访问（level 1 也可以）
- **level 1 用户特殊**：即使接口配置是 `allowed_levels = "2,999"`，level 1 用户仍然可以访问
- **没有配置的接口** → 所有人都可以访问（默认开放）

**等级关系说明：**
- level 1 是超级管理员，拥有最高权限
- level 2, 3, 4, ..., 999 之间**没有高低之分**，只有是否在 `allowed_levels` 列表中的区别
- 例如：allowed_levels = "999" 时，level 999 可以访问，但 level 2 不能访问

## 三、使用方式

### 1. 动态配置接口等级

```bash
# 查询所有接口等级配置
GET /api/level/interfaces

# 创建接口等级配置（需要 level 1）
POST /api/level/interface
Content-Type: application/json
{
  "interfacePath": "/api/user/delete",
  "interfaceMethod": "DELETE",
  "allowedLevels": "1,2",
  "description": "删除用户接口，允许 level 1 和 level 2 访问",
  "status": 1
}

# 更新接口等级（需要 level 1）
PUT /api/level/interface
Content-Type: application/json
{
  "id": 1,
  "allowedLevels": "1,2,3"
}

# 删除接口等级配置（需要 level 1）
DELETE /api/level/interface/1
```

### 2. 修改用户等级

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
| /auth/password/modify | POST | level 1,2 | 修改用户密码 |
| /auth/login | POST | 开放接口 | 用户登录（不需要配置） |
| /auth/register | POST | 开放接口 | 用户注册（不需要配置） |

## 五、等级分配规则

### 基本原则
1. **高等级可以分配低等级**：level 1 可以创建/修改 level 2,3,4...
2. **不能分配同级或更高等级**：level 2 不能修改 level 1
3. **新用户默认为 unknown**：注册时 level = 999
4. **level 1 特殊权限**：可以访问所有接口，无需检查 allowed_levels
5. **开放接口**：登录、注册、CORS 预检不需要配置，任何人都可访问
6. **等级平等**：level 2-999 之间没有高低之分，仅通过 allowed_levels 授权

### 示例场景

✅ **正确示例：**
- level 1 → 修改 level 2 用户 ✓
- level 1 → 修改 level 999 用户 ✓
- level 2 → 修改 level 999 用户 ✓（如果 allowed_levels 包含 2）
- level 1 → 访问 allowed_levels="2,999" 的接口 ✓（超级管理员特权）
- 任何人 → 访问 /auth/login 接口 ✓（开放接口）

❌ **错误示例：**
- level 2 → 修改 level 1 用户 ✗（不能修改超级管理员）
- level 999 → 访问 allowed_levels="1,2,3" 的接口 ✗（不在允许列表中）

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

### interface_level 表（重要变更）
```sql
CREATE TABLE `interface_level` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `interface_path` VARCHAR(200) NOT NULL,
    `interface_method` VARCHAR(10) NOT NULL,
    `allowed_levels` VARCHAR(500) NOT NULL COMMENT '允许的等级列表，逗号分隔',
    `description` VARCHAR(500),
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_path_method` (`interface_path`, `interface_method`)
);
```

## 七、与旧版本的区别

### 旧版本（required_level）
```
权限验证：用户等级 <= 接口要求等级
示例：required_level = 3 → level 1,2,3 都可以访问
```

### 新版本（allowed_levels）
```
权限验证：
  - level 1 用户：可以访问所有接口（超级管理员特权）
  - 其他用户：用户等级在 allowed_levels 列表中才能访问
  - 开放接口：登录、注册、CORS 预检不需要配置
  - 等级平等：level 2-999 之间没有高低之分

示例：
  allowed_levels = "2,999" → level 2 和 999 可以访问（level 1 也可以）
  没有配置的接口 → 所有人都可以访问

优势：可以精确控制每个等级，等级之间独立平等
```

## 八、优势

✅ **完全动态** - 接口等级存储在数据库，可随时修改  
✅ **灵活授权** - 可以精确指定哪些等级可以访问  
✅ **超级管理员特权** - level 1 可以访问所有接口  
✅ **支持多等级** - 一个接口可以授权给多个等级  
✅ **开放接口** - 登录、注册等接口无需配置即可访问  
✅ **集中管理** - 所有接口等级配置在一个表中  
✅ **简单直观** - 逗号分隔的等级列表，易于理解  
✅ **纯后端 API** - 专注于后端接口权限控制  

## 九、注意事项

1. **level 数字越小权限越大**（1 是最高权限）
2. **首次使用需要设置至少一个 level 1 用户**
3. **注册的新用户 level 默认为 999**
4. **接口路径支持通配符**（如 `/api/**`）
5. **密码加密**：使用 salt + MD5 加密
6. **等级限制**：不能修改等级 >= 自己的用户
7. **allowed_levels 格式**：逗号分隔的字符串，如 "1,2,3,999"
8. **level 1 特殊权限**：可以访问所有接口，无需检查 allowed_levels
9. **开放接口**：登录、注册、CORS 预检不需要配置，任何人都可访问
10. **等级平等**：level 2-999 之间没有高低之分，仅通过 allowed_levels 授权

## 十、常见问题

### Q1: 为什么 level 1 可以访问所有接口？
A: level 1 是超级管理员，拥有最高权限，可以访问系统中的所有接口，无需检查 allowed_levels。

### Q2: 如何限制某个接口只允许特定等级访问？
A: 在创建接口配置时，设置 `allowedLevels` 为你想要的等级，如 "2,999"。

### Q3: level 999 用户可以访问哪些接口？
A: 只能访问 `allowed_levels` 包含 "999" 的接口，以及没有配置的开放接口（如登录、注册）。

### Q4: 如何查看所有接口的权限配置？
A: 使用 GET `/api/level/interfaces` 接口查询所有配置。

### Q5: 开放接口有哪些？
A: 登录（/auth/login）、注册（/auth/register）、CORS 预检请求（OPTIONS）是开放接口，不需要配置任何人都可以访问。

### Q6: 如果接口没有配置会怎样？
A: 如果接口没有在 interface_level 表中配置，则默认开放，所有人都可以访问。

### Q7: level 2 和 level 999 哪个权限大？
A: level 2 和 level 999 之间**没有高低之分**，它们是完全平等的。能否访问接口只取决于 allowed_levels 是否包含该等级。
例如：allowed_levels="999" 时，level 999 可以访问，但 level 2 不能访问。

### Q8: level 2 用户可以修改 level 999 用户吗？
A: 可以，只要 level 2 用户的 allowed_levels 配置中包含访问修改接口的权限，就可以修改 level 999 用户（但不能修改 level 1）。
