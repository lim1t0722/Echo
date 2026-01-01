const validatePhone = (req, res, next) => {
  const { phone } = req.body;
  
  if (!phone) {
    return res.status(400).json({
      code: 400,
      message: '手机号不能为空',
      data: null
    });
  }
  
  if (typeof phone !== 'string') {
    return res.status(400).json({
      code: 400,
      message: '手机号格式错误',
      data: null
    });
  }
  
  if (!/^1[3-9]\d{9}$/.test(phone)) {
    return res.status(400).json({
      code: 400,
      message: '请输入有效的手机号',
      data: null
    });
  }
  
  next();
};

const validateEmail = (req, res, next) => {
  const { email } = req.body;
  
  if (!email) {
    return res.status(400).json({
      code: 400,
      message: '邮箱不能为空',
      data: null
    });
  }
  
  if (typeof email !== 'string') {
    return res.status(400).json({
      code: 400,
      message: '邮箱格式错误',
      data: null
    });
  }
  
  if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
    return res.status(400).json({
      code: 400,
      message: '请输入有效的邮箱地址',
      data: null
    });
  }
  
  next();
};

const validateContact = (req, res, next) => {
  const { phone, email } = req.body;
  
  // 必须提供手机号或邮箱之一
  if (!phone && !email) {
    return res.status(400).json({
      code: 400,
      message: '请输入手机号或邮箱',
      data: null
    });
  }
  
  // 如果提供了手机号，验证手机号格式
  if (phone) {
    if (typeof phone !== 'string') {
      return res.status(400).json({
        code: 400,
        message: '手机号格式错误',
        data: null
      });
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的手机号',
        data: null
      });
    }
  }
  
  // 如果提供了邮箱，验证邮箱格式
  if (email) {
    if (typeof email !== 'string') {
      return res.status(400).json({
        code: 400,
        message: '邮箱格式错误',
        data: null
      });
    }
    
    if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)) {
      return res.status(400).json({
        code: 400,
        message: '请输入有效的邮箱地址',
        data: null
      });
    }
  }
  
  next();
};

const validateCode = (req, res, next) => {
  const { code } = req.body;
  
  if (!code) {
    return res.status(400).json({
      code: 400,
      message: '验证码不能为空',
      data: null
    });
  }
  
  if (typeof code !== 'string') {
    return res.status(400).json({
      code: 400,
      message: '验证码格式错误',
      data: null
    });
  }
  
  if (!/^\d{4,6}$/.test(code)) {
    return res.status(400).json({
      code: 400,
      message: '请输入有效的验证码',
      data: null
    });
  }
  
  next();
};

// 短信验证码发送验证
const validateSendSmsCode = [validatePhone];
// 邮箱验证码发送验证
const validateSendEmailCode = [validateEmail];
// 验证码验证（支持手机号或邮箱）
const validateVerifyCode = [validateContact, validateCode];

module.exports = {
  validatePhone,
  validateEmail,
  validateContact,
  validateCode,
  validateSendSmsCode,
  validateSendEmailCode,
  validateVerifyCode
};