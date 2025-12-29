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

const validateSendCode = [validatePhone];
const validateVerifyCode = [validatePhone, validateCode];

module.exports = {
  validatePhone,
  validateCode,
  validateSendCode,
  validateVerifyCode
};