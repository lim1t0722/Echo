const SMSCode = require('../models/sms_code');
const User = require('../models/user');
const { generateToken } = require('../middlewares/auth');
const { successResponse, validationError, errorResponse } = require('../utils/response');

// 发送验证码
exports.sendCode = async (req, res) => {
  try {
    const { phone } = req.body;
    
    // 验证手机号格式
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      return validationError(res, '请输入有效的手机号');
    }
    
    // 检查发送频率
    const canSend = await SMSCode.checkSendInterval(phone);
    if (!canSend) {
      return validationError(res, '发送频率过高，请稍后再试');
    }
    
    // 生成并发送验证码
    await SMSCode.create(phone);
    
    successResponse(res, null, '验证码发送成功');
  } catch (error) {
    console.error('发送验证码失败:', error);
    errorResponse(res, 500, '发送验证码失败，请稍后再试');
  }
};

// 验证验证码并登录
exports.verifyCode = async (req, res) => {
  try {
    const { phone, code } = req.body;
    
    // 验证手机号格式
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      return validationError(res, '请输入有效的手机号');
    }
    
    // 验证验证码
    const isValid = await SMSCode.verify(phone, code);
    if (!isValid) {
      return validationError(res, '验证码无效或已过期');
    }
    
    // 查找或创建用户
    let user = await User.findByPhone(phone);
    if (!user) {
      user = await User.create(phone);
    }
    
    // 生成 JWT token
    const token = generateToken(user.id);
    
    successResponse(res, { token }, '登录成功');
  } catch (error) {
    console.error('验证验证码失败:', error);
    errorResponse(res, 500, '验证验证码失败，请稍后再试');
  }
};

// 用户信息获取
exports.getUserInfo = async (req, res) => {
  try {
    // 用户信息已在 authMiddleware 中添加到 req.user
    const user = req.user;
    
    // 返回用户信息
    const userInfo = {
      id: user.id,
      phone: user.phone,
      created_at: user.created_at
    };
    
    successResponse(res, userInfo, '获取用户信息成功');
  } catch (error) {
    console.error('获取用户信息失败:', error);
    errorResponse(res, 500, '获取用户信息失败，请稍后再试');
  }
};