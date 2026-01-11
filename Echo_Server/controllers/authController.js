const emailService = require('../services/emailService');
const User = require('../models/user');

const sendCodeMap = new Map();
const SEND_INTERVAL = 60000;
const MAX_ATTEMPTS = 2;

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

exports.login = async (req, res) => {
  try {
    console.log('[接收请求] /api/auth/login', req.body);
    const { email, password } = req.body;
    
    // 参数验证
    if (!email || !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的邮箱地址',
        data: null
      });
    }
    
    if (!password) {
      return res.status(400).json({
        code: 400,
        message: '请输入密码',
        data: null
      });
    }
    
    // 查找用户
    const user = await User.findByEmail(email);
    if (!user) {
      return res.status(400).json({
        code: 400,
        message: '邮箱或密码错误',
        data: null
      });
    }
    
    // 验证密码
    const passwordMatch = await User.verifyPassword(user.password, password);
    if (!passwordMatch) {
      return res.status(400).json({
        code: 400,
        message: '邮箱或密码错误',
        data: null
      });
    }
    
    console.log(`[用户登录成功] 邮箱: ${email}, 用户ID: ${user.user_id}`);
    
    res.status(200).json({
      code: 0,
      message: '登录成功',
      data: {
        user_id: user.user_id,
        nickname: user.nickname,
        email: user.email,
        avatar: user.avatar
      }
    });
  } catch (error) {
    console.error('登录异常:', error);
    res.status(500).json({
      code: 500,
      message: '服务器内部错误',
      data: null
    });
  }
};