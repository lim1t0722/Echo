const db = require('../config/db');

class User {
  constructor(user_id, phone, email, nickname, password, created_at) {
    this.user_id = user_id;
    this.phone = phone;
    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.created_at = created_at;
  }

  static generateUserId() {
    const timestamp = Date.now().toString().slice(-6);
    const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    const userId = (timestamp + random).slice(0, 10);
    return userId;
  }

  static async findByPhone(phone) {
    try {
      const [rows] = await db.execute('SELECT * FROM users WHERE phone = ? LIMIT 1', [phone]);
      if (rows.length === 0) return null;
      return new User(rows[0].user_id, rows[0].phone, rows[0].email || null, rows[0].nickname || null, rows[0].password || null, rows[0].created_at);
    } catch (error) {
      throw error;
    }
  }

  static async findByEmail(email) {
    try {
      const [rows] = await db.execute('SELECT * FROM users WHERE email = ? LIMIT 1', [email]);
      if (rows.length === 0) return null;
      return new User(rows[0].user_id, rows[0].phone || null, rows[0].email, rows[0].nickname || null, rows[0].password || null, rows[0].created_at);
    } catch (error) {
      throw error;
    }
  }

  static async findByUserId(user_id) {
    try {
      const [rows] = await db.execute('SELECT * FROM users WHERE user_id = ? LIMIT 1', [user_id]);
      if (rows.length === 0) return null;
      return new User(rows[0].user_id, rows[0].phone || null, rows[0].email || null, rows[0].nickname || null, rows[0].password || null, rows[0].created_at);
    } catch (error) {
      throw error;
    }
  }

  static async create(phone = null, email = null, nickname = null, password = null) {
    try {
      const user_id = User.generateUserId();
      await db.execute('INSERT INTO users (user_id, phone, email, nickname, password, created_at) VALUES (?, ?, ?, ?, ?, NOW())', [user_id, phone, email, nickname, password]);
      return new User(user_id, phone, email, nickname, password, new Date());
    } catch (error) {
      console.error('用户创建失败:', error);
      throw error;
    }
  }

  getBasicInfo() {
    return {
      user_id: this.user_id,
      phone: this.phone,
      email: this.email,
      nickname: this.nickname,
      created_at: this.created_at
    };
  }

  getMaskedPhone() {
    return this.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  }
}

module.exports = User;