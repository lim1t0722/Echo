const nodemailer = require('nodemailer');
require('dotenv').config();

// 验证码存储（实际项目中应使用数据库）
const verificationCodes = new Map();

class EmailService {
  constructor() {
    this.transporter = nodemailer.createTransport({
      host: process.env.EMAIL_SERVICE,
      port: process.env.EMAIL_PORT,
      secure: false, // true for 465, false for other ports
      auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
      }
    });
  }

  generateVerifyCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  async sendEmailVerifyCode(email) {
    try {
      const verifyCode = this.generateVerifyCode();
      
      // 存储验证码（有效期5分钟）
      verificationCodes.set(email, {
        code: verifyCode,
        timestamp: Date.now()
      });
      
      // 真实发送邮件
      const info = await this.transporter.sendMail({
        from: process.env.EMAIL_FROM,
        to: email,
        subject: '【Echo】验证码',
        text: `您的验证码是: ${verifyCode}，5分钟内有效。`,
        html: `<div><p>您的验证码是: <strong>${verifyCode}</strong></p><p>5分钟内有效。</p></div>`
      });
      
      console.log('[发送邮件] 成功:', info.messageId);
      
      return {
        success: true,
        message: '验证码已发送',
        messageId: info.messageId,
        verifyCode: verifyCode
      };
      
      /* 模拟发送邮件代码，已启用真实发送
      console.log(`[发送验证码] 邮箱: ${email}, 验证码: ${verifyCode}`);
      
      return {
        success: true,
        message: '验证码已发送',
        verifyCode: verifyCode
      };
      */
    } catch (error) {
      console.error('[发送邮件异常] 错误信息:', error);
      return {
        success: false,
        message: error.message || '发送验证码失败',
        code: error.code || 'EMAIL_SEND_ERROR'
      };
    }
  }

  async checkEmailVerifyCode(email, verifyCode) {
    try {
      const codeInfo = verificationCodes.get(email);
      
      if (!codeInfo) {
        return {
          success: false,
          message: '验证码不存在'
        };
      }
      
      const now = Date.now();
      const expiryTime = process.env.SMS_CODE_EXPIRY || 300;
      
      if (now - codeInfo.timestamp > expiryTime * 1000) {
        verificationCodes.delete(email);
        return {
          success: false,
          message: '验证码已过期'
        };
      }
      
      if (codeInfo.code !== verifyCode) {
        return {
          success: false,
          message: '验证码错误'
        };
      }
      
      // 验证成功后删除验证码
      verificationCodes.delete(email);
      
      return {
        success: true,
        message: '验证码验证成功'
      };
    } catch (error) {
      console.error('验证邮件验证码异常:', error);
      return {
        success: false,
        message: error.message || '验证验证码失败',
        code: error.code || 'EMAIL_VERIFY_ERROR'
      };
    }
  }
}

module.exports = new EmailService();
