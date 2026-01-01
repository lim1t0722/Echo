const aliyunSmsService = require('../services/aliyunSmsService');
const emailService = require('../services/emailService');
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

exports.sendEmailCode = async (req, res) => {
  try {
    console.log('[接收请求] /api/send_email', req.body);
    const { email } = req.body;
    
    if (!email || !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的邮箱地址',
        data: null
      });
    }
    
    const now = Date.now();
    const lastSend = sendCodeMap.get(email);
    
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
    
    const result = await emailService.sendEmailVerifyCode(email);
    
    if (result.success) {
      sendCodeMap.set(email, {
        timestamp: now,
        count: lastSend ? lastSend.count + 1 : 1
      });
      
      console.log(`[发送验证码成功] 邮箱: ${email}, 验证码: ${result.verifyCode}`);
      
      res.status(200).json({
        code: 0,
        message: '验证码已发送',
        data: null
      });
    } else {
      console.error(`[发送验证码失败] 邮箱: ${email}, 错误: ${result.message}, 错误码: ${result.code}`);
      
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

exports.register = async (req, res) => {
  try {
    console.log('[接收请求] /api/auth/register', req.body);
    const { email, nickname, password, verificationCode } = req.body;
    
    // 参数验证
    if (!email || !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的邮箱地址',
        data: null
      });
    }
    
    if (!nickname || nickname.length < 2 || nickname.length > 20) {
      return res.status(400).json({
        code: 400,
        message: '昵称长度应在2-20个字符之间',
        data: null
      });
    }
    
    if (!password || password.length < 6) {
      return res.status(400).json({
        code: 400,
        message: '密码长度应至少6个字符',
        data: null
      });
    }
    
    if (!verificationCode || !/^\d{6}$/.test(verificationCode)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的验证码',
        data: null
      });
    }
    
    // 验证验证码
    const verifyResult = await emailService.checkEmailVerifyCode(email, verificationCode);
    
    if (!verifyResult.success) {
      console.error(`[注册失败] 邮箱: ${email}, 验证码错误: ${verifyResult.message}`);
      return res.status(400).json({
        code: 400,
        message: verifyResult.message || '验证码错误或已过期',
        data: null
      });
    }
    
    // 检查邮箱是否已注册
    const existingUser = await User.findByEmail(email);
    if (existingUser) {
      return res.status(400).json({
        code: 400,
        message: '该邮箱已注册',
        data: null
      });
    }
    
    // 创建新用户
    const user = await User.create(null, email, nickname, password);
    
    console.log(`[用户注册成功] 邮箱: ${email}, 昵称: ${nickname}, 用户ID: ${user.user_id}`);
    
    res.status(200).json({
      code: 0,
      message: '注册成功',
      data: {
        user_id: user.user_id
      }
    });
  } catch (error) {
    console.error('注册异常:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误',
      data: null
    });
  }
};

exports.verifyCode = async (req, res) => {
  try {
    const { phone, email, code } = req.body;
    
    if (!code || !/^\d{4,6}$/.test(code)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的验证码',
        data: null
      });
    }
    
    let result, user;
    
    if (email) {
      // 邮箱验证码验证
      if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
        return res.status(400).json({
          code: 400,
          message: '请输入有效的邮箱地址',
          data: null
        });
      }
      
      result = await emailService.checkEmailVerifyCode(email, code);
      
      if (result.success) {
        console.log(`[验证码验证成功] 邮箱: ${email}`);
        
        let user = await User.findByEmail(email);
        
        if (!user) {
          user = await User.create(null, email);
          console.log(`[新用户创建] 邮箱: ${email}, 用户ID: ${user.user_id}`);
        } else {
          console.log(`[用户登录] 邮箱: ${email}, 用户ID: ${user.user_id}`);
        }
        
        res.status(200).json({
          code: 0,
          message: '登录成功',
          data: {
            user_id: user.user_id
          }
        });
      } else {
        console.error(`[验证码验证失败] 邮箱: ${email}, 错误: ${result.message}`);
        
        res.status(400).json({
          code: 400,
          message: result.message || '验证码错误或已过期',
          data: null
        });
      }
    } else if (phone) {
      // 手机号验证码验证（保留原有功能）
      if (!/^1[3-9]\d{9}$/.test(phone)) {
        return res.status(400).json({
          code: 400,
          message: '请输入有效的手机号',
          data: null
        });
      }
      
      result = await aliyunSmsService.checkSmsVerifyCode(phone, code);
      
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
    } else {
      return res.status(400).json({
        code: 400,
        message: '请输入手机号或邮箱',
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