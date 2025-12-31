const aliyunSmsService = require('../services/aliyunSmsService');
const User = require('../models/user');
const { successResponse, validationError, errorResponse } = require('../utils/response');

const sendCodeMap = new Map();
const SEND_INTERVAL = 60000;
const MAX_ATTEMPTS = 2;

exports.sendCode = async (req, res) => {
  try {
    console.log('[接收请求] /api/send_sms', req.body);
    const { phone } = req.body;
    
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的手机号',
        data: null
      });
    }
    
    const now = Date.now();
    const lastSend = sendCodeMap.get(phone);
    
    if (lastSend && now - lastSend.timestamp < SEND_INTERVAL) {
      return res.status(400).json({
        code: 400,
        message: '发送频率过高，请稍后再试',
        data: null
      });
    }
    
    if (lastSend && lastSend.count >= MAX_ATTEMPTS && now - lastSend.timestamp < SEND_INTERVAL * 2) {
      return res.status(400).json({
        code: 400,
        message: '发送次数过多，请稍后再试',
        data: null
      });
    }
    
    const result = await aliyunSmsService.sendSmsVerifyCode(phone);
    
    if (result.success) {
      sendCodeMap.set(phone, {
        timestamp: now,
        count: lastSend ? lastSend.count + 1 : 1
      });
      
      console.log(`[发送验证码成功] 手机号: ${phone}, 验证码: ${result.verifyCode}, 请求ID: ${result.requestId}`);
      
      res.status(200).json({
        code: 0,
        message: '验证码已发送',
        data: null
      });
    } else {
      console.error(`[发送验证码失败] 手机号: ${phone}, 错误: ${result.message}, 错误码: ${result.code}`);
      
      res.status(400).json({
        code: 400,
        message: result.message || '发送验证码失败',
        data: null
      });
    }
  } catch (error) {
    console.error('发送验证码异常:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误',
      data: null
    });
  }
};

exports.verifyCode = async (req, res) => {
  try {
    const { phone, code } = req.body;
    
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的手机号',
        data: null
      });
    }
    
    if (!code || !/^\d{4,6}$/.test(code)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的验证码',
        data: null
      });
    }
    
    const result = await aliyunSmsService.checkSmsVerifyCode(phone, code);
    
    if (result.success) {
      console.log(`[验证码验证成功] 手机号: ${phone}`);
      
      let user = await User.findByPhone(phone);
      
      if (!user) {
        user = await User.create(phone);
        console.log(`[新用户创建] 手机号: ${phone}, 用户ID: ${user.user_id}`);
      } else {
        console.log(`[用户登录] 手机号: ${phone}, 用户ID: ${user.user_id}`);
      }
      
      res.status(200).json({
        code: 0,
        message: '登录成功',
        data: {
          user_id: user.user_id
        }
      });
    } else {
      console.error(`[验证码验证失败] 手机号: ${phone}, 错误: ${result.message}`);
      
      res.status(400).json({
        code: 400,
        message: result.message || '验证码错误或已过期',
        data: null
      });
    }
  } catch (error) {
    console.error('验证验证码异常:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误',
      data: null
    });
  }
};