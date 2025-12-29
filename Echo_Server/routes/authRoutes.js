const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

// 发送验证码
router.post('/send_code', authController.sendCode);

// 验证验证码并登录
router.post('/verify_code', authController.verifyCode);

module.exports = router;