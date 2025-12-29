const db = require('../config/db');
const { v4: uuidv4 } = require('uuid');

class User {
  constructor(id, phone, created_at) {
    this.id = id;
    this.phone = phone;
    this.created_at = created_at;
  }

  // 根据手机号查找用户
  static async findByPhone(phone) {
    try {
      const [rows] = await db.execute('SELECT * FROM users WHERE phone = ?', [phone]);
      if (rows.length === 0) return null;
      return new User(rows[0].id, rows[0].phone, rows[0].created_at);
    } catch (error) {
      throw error;
    }
  }

  // 根据ID查找用户
  static async findById(id) {
    try {
      const [rows] = await db.execute('SELECT * FROM users WHERE id = ?', [id]);
      if (rows.length === 0) return null;
      return new User(rows[0].id, rows[0].phone, rows[0].created_at);
    } catch (error) {
      throw error;
    }
  }

  // 创建新用户
  static async create(phone) {
    try {
      const id = uuidv4();
      await db.execute('INSERT INTO users (id, phone) VALUES (?, ?)', [id, phone]);
      return new User(id, phone, new Date());
    } catch (error) {
      throw error;
    }
  }

  // 获取用户基本信息（用于返回给客户端）
  getBasicInfo() {
    return {
      id: this.id,
      phone: this.phone,
      created_at: this.created_at
    };
  }

  // 脱敏手机号
  getMaskedPhone() {
    return this.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  }
}

module.exports = User;