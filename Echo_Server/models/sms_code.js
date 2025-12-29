const db = require('../config/db');
require('dotenv').config();

class SMSCode {
  constructor(id, phone, code, expired_at, used, created_at) {
    this.id = id;
    this.phone = phone;
    this.code = code;
    this.expired_at = expired_at;
    this.used = used;
    this.created_at = created_at;
  }

  // 生成6位数字验证码
  static generateCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  // 发送验证码（模拟阿里云短信服务）
  static async send(phone, code) {
    try {
      // 这里是模拟发送短信的逻辑
      // 实际项目中需要集成阿里云短信服务
      console.log(`向手机号 ${phone} 发送验证码：${code}`);
      return true;
    } catch (error) {
      console.error('发送短信失败:', error);
      throw error;
    }
  }

  // 创建并存储验证码
  static async create(phone) {
    try {
      const code = this.generateCode();
      const expiry = parseInt(process.env.SMS_CODE_EXPIRY || 300);
      const expired_at = new Date(Date.now() + expiry * 1000);
      
      await db.execute(
        'INSERT INTO sms_codes (phone, code, expired_at) VALUES (?, ?, ?)',
        [phone, code, expired_at]
      );
      
      // 发送验证码
      await this.send(phone, code);
      
      return code;
    } catch (error) {
      throw error;
    }
  }

  // 检查同一手机号是否在发送间隔内
  static async checkSendInterval(phone) {
    try {
      const interval = parseInt(process.env.SMS_CODE_SEND_INTERVAL || 60);
      const [rows] = await db.execute(
        'SELECT created_at FROM sms_codes WHERE phone = ? ORDER BY created_at DESC LIMIT 1',
        [phone]
      );
      
      if (rows.length === 0) return true;
      
      const lastSendTime = new Date(rows[0].created_at);
      const now = new Date();
      
      return (now - lastSendTime) / 1000 >= interval;
    } catch (error) {
      throw error;
    }
  }

  // 验证验证码
  static async verify(phone, code) {
    try {
      // 查询未使用且未过期的验证码
      const [rows] = await db.execute(
        'SELECT * FROM sms_codes WHERE phone = ? AND code = ? AND used = false AND expired_at > NOW() ORDER BY created_at DESC LIMIT 1',
        [phone, code]
      );
      
      if (rows.length === 0) return false;
      
      // 标记验证码为已使用
      await db.execute('UPDATE sms_codes SET used = true WHERE id = ?', [rows[0].id]);
      
      return true;
    } catch (error) {
      throw error;
    }
  }

  // 清理过期的验证码
  static async cleanupExpired() {
    try {
      await db.execute('DELETE FROM sms_codes WHERE expired_at <= NOW()');
      console.log('已清理过期的验证码');
    } catch (error) {
      console.error('清理过期验证码失败:', error);
    }
  }
}

module.exports = SMSCode;