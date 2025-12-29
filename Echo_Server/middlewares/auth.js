const jwt = require('jsonwebtoken');
const User = require('../models/user');
require('dotenv').config();

// 生成 JWT token
const generateToken = (userId) => {
  return jwt.sign(
    { userId },
    process.env.JWT_SECRET,
    { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
  );
};

// 验证 JWT token 中间件
const authMiddleware = async (req, res, next) => {
  try {
    // 获取 Authorization header
    const authHeader = req.header('Authorization');
    
    // 检查 Authorization header 格式
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        code: 401,
        message: '未授权，请提供有效的认证令牌',
        data: null
      });
    }
    
    // 提取 token
    const token = authHeader.replace('Bearer ', '');
    
    // 验证 token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // 查找用户
    const user = await User.findById(decoded.userId);
    if (!user) {
      return res.status(401).json({
        code: 401,
        message: '用户不存在',
        data: null
      });
    }
    
    // 将用户信息添加到请求对象
    req.user = user;
    next();
  } catch (error) {
    if (error.name === 'JsonWebTokenError') {
      return res.status(401).json({
        code: 401,
        message: '无效的认证令牌',
        data: null
      });
    } else if (error.name === 'TokenExpiredError') {
      return res.status(401).json({
        code: 401,
        message: '认证令牌已过期',
        data: null
      });
    }
    
    res.status(500).json({
      code: 500,
      message: '服务器内部错误',
      data: null
    });
  }
};

// WebSocket 认证中间件（预留）
const wsAuthMiddleware = (token) => {
  try {
    if (!token) return null;
    
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    return decoded.userId;
  } catch (error) {
    console.error('WebSocket 认证失败:', error);
    return null;
  }
};

module.exports = {
  generateToken,
  authMiddleware,
  wsAuthMiddleware
};