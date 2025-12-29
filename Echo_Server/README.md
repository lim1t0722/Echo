# Echo Server

Echo 即时通讯应用后端服务器，提供用户认证、短信验证码、用户信息管理等核心功能。

## 技术栈

- Node.js
- Express.js
- MySQL
- JWT (JSON Web Token)
- mysql2 (MySQL 驱动)

## 功能特性

- 基于手机号和短信验证码的用户认证系统
- JWT 身份认证
- 用户基本信息管理（user_id、手机号、创建时间）
- 短信验证码管理（有效期和发送频率限制）
- RESTful API 接口
- 数据库自动初始化
- 过期验证码自动清理

## 安装

### 前置要求

- Node.js (v14 或更高版本)
- MySQL (v5.7 或更高版本)

### 安装步骤

1. 克隆项目到本地

2. 安装依赖

```bash
npm install
```

3. 配置环境变量

复制 `.env.example` 为 `.env`，并根据实际情况修改配置：

```bash
cp .env.example .env
```

编辑 `.env` 文件：

```env
# 服务器配置
PORT=3000

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=echo_db

# JWT 配置
JWT_SECRET=your_secret_key
JWT_EXPIRES_IN=7d

# 阿里云短信服务配置
ALIBABA_CLOUD_ACCESS_KEY_ID=your_access_key_id
ALIBABA_CLOUD_ACCESS_KEY_SECRET=your_access_key_secret
ALIBABA_CLOUD_SMS_SIGN_NAME=your_sign_name
ALIBABA_CLOUD_SMS_TEMPLATE_CODE=your_template_code

# 验证码配置
SMS_CODE_EXPIRY=300  # 验证码有效期（秒）
SMS_CODE_SEND_INTERVAL=60  # 同一手机号发送间隔（秒）
```

4. 创建数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS echo_db;"
```

5. 启动服务器

```bash
npm run start
```

服务器启动成功后，会显示以下信息：

```
服务器运行在 http://localhost:3000
API 文档: http://localhost:3000/api
```

## API 接口文档

### 基础信息

- 基础 URL: `http://localhost:3000`
- 数据格式: JSON
- 字符编码: UTF-8

### 响应格式

所有接口返回统一的响应格式：

#### 成功响应

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {}
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

### 接口列表

#### 1. 健康检查

**接口地址**: `GET /health`

**请求参数**: 无

**响应示例**:

```json
{
  "status": "ok",
  "message": "Server is running"
}
```

#### 2. 发送短信验证码

**接口地址**: `POST /api/auth/send_code`

**请求头**:

```
Content-Type: application/json
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号（11位数字，1开头） |

**请求示例**:

```json
{
  "phone": "13800138000"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": null
}
```

**错误响应**:

```json
{
  "code": 400,
  "message": "请输入有效的手机号",
  "data": null
}
```

```json
{
  "code": 400,
  "message": "发送频率过高，请稍后再试",
  "data": null
}
```

#### 3. 验证验证码并登录/注册

**接口地址**: `POST /api/auth/verify_code`

**请求头**:

```
Content-Type: application/json
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 是 | 手机号 |
| code | string | 是 | 验证码（6位数字） |

**请求示例**:

```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**成功响应**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**错误响应**:

```json
{
  "code": 400,
  "message": "验证码无效或已过期",
  "data": null
}
```

#### 4. 获取用户信息

**接口地址**: `GET /api/user/me`

**请求头**:

```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求参数**: 无

**成功响应**:

```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "phone": "13800138000",
    "created_at": "2025-12-30T00:00:00.000Z"
  }
}
```

**错误响应**:

```json
{
  "code": 401,
  "message": "未授权，请提供有效的认证令牌",
  "data": null
}
```

```json
{
  "code": 401,
  "message": "认证令牌已过期",
  "data": null
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权或令牌无效 |
| 404 | 接口不存在 |
| 500 | 服务器内部错误 |

## 数据库结构

### users 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | VARCHAR(36) | 用户ID（UUID） |
| phone | VARCHAR(20) | 手机号 |
| created_at | TIMESTAMP | 创建时间 |

### sms_codes 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INT | 主键，自增 |
| phone | VARCHAR(20) | 手机号 |
| code | VARCHAR(6) | 验证码 |
| created_at | TIMESTAMP | 创建时间 |
| expires_at | TIMESTAMP | 过期时间 |

## 项目结构

```
Echo_Server/
├── config/           # 配置文件
│   └── db.js        # 数据库连接配置
├── controllers/     # 控制器
│   └── authController.js  # 认证控制器
├── middlewares/     # 中间件
│   └── auth.js      # 认证中间件
├── models/          # 数据模型
│   ├── user.js      # 用户模型
│   └── sms_code.js  # 短信验证码模型
├── routes/          # 路由
│   ├── authRoutes.js  # 认证路由
│   └── userRoutes.js  # 用户路由
├── utils/           # 工具函数
│   ├── init_db.js   # 数据库初始化
│   └── response.js  # 响应格式化
├── .env             # 环境变量配置
├── .env.example     # 环境变量示例
├── app.js           # 应用入口文件
└── package.json     # 项目配置文件
```

## 开发说明

### 修改端口

修改 `.env` 文件中的 `PORT` 配置项：

```env
PORT=8080
```

### 修改数据库连接

修改 `.env` 文件中的数据库配置项：

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=echo_db
```

### 修改 JWT 密钥

修改 `.env` 文件中的 `JWT_SECRET` 配置项：

```env
JWT_SECRET=your_secret_key
```

### 修改验证码有效期

修改 `.env` 文件中的 `SMS_CODE_EXPIRY` 配置项（单位：秒）：

```env
SMS_CODE_EXPIRY=300
```

### 修改验证码发送间隔

修改 `.env` 文件中的 `SMS_CODE_SEND_INTERVAL` 配置项（单位：秒）：

```env
SMS_CODE_SEND_INTERVAL=60
```

## 注意事项

1. 请确保 MySQL 服务已启动并可访问
2. 请确保 `.env` 文件中的数据库配置正确
3. JWT_SECRET 应该使用强随机字符串，不要使用默认值
4. 短信验证码功能需要配置阿里云短信服务，否则只能使用测试模式
5. 服务器启动时会自动创建数据库表，无需手动创建

## 许可证

ISC