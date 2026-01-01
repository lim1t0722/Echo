const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { authMiddleware } = require('../middlewares/auth');

// 获取用户信息（需要认证）
router.get('/me', authMiddleware, authController.getUserInfo);

// 更新用户信息（需要认证）
router.post('/update', authMiddleware, authController.updateUserInfo);

module.exports = router;