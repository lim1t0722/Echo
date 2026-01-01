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

  static async verifyPassword(storedPassword, inputPassword) {
    try {
      // 由于当前没有加密，直接比较
      // 实际项目中应该使用bcrypt等加密算法
      return storedPassword === inputPassword;
    } catch (error) {
      throw error;
    }
  }

  static async updateUserInfo(userId, nickname, avatar) {
    try {
      const updateFields = [];
      const updateValues = [];

      if (nickname !== undefined) {
        updateFields.push('nickname = ?');
        updateValues.push(nickname);
      }
      if (avatar !== undefined) {
        updateFields.push('avatar = ?');
        updateValues.push(avatar);
      }

      if (updateFields.length === 0) {
        // 没有需要更新的字段
        const user = await this.findByUserId(userId);
        return user ? user.getBasicInfo() : null;
      }

      updateValues.push(userId);
      const sql = `UPDATE users SET ${updateFields.join(', ')} WHERE user_id = ?`;
      
      await db.execute(sql, updateValues);
      
      // 返回更新后的用户信息
      const updatedUser = await this.findByUserId(userId);
      return updatedUser ? updatedUser.getBasicInfo() : null;
    } catch (error) {
      console.error('更新用户信息失败:', error);
      throw error;
    }
  }
}

module.exports = User;