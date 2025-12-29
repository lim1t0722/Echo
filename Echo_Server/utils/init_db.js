const db = require('../config/db');

async function initDatabase() {
  try {
    // 创建用户表
    await db.execute(`
      CREATE TABLE IF NOT EXISTS users (
        id VARCHAR(36) PRIMARY KEY COMMENT '用户唯一标识',
        phone VARCHAR(20) NOT NULL UNIQUE COMMENT '用户手机号',
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
        INDEX idx_phone (phone)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表'
    `);

    // 创建短信验证码表
    await db.execute(`
      CREATE TABLE IF NOT EXISTS sms_codes (
        id INT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
        phone VARCHAR(20) NOT NULL COMMENT '手机号',
        code VARCHAR(6) NOT NULL COMMENT '验证码',
        expired_at DATETIME NOT NULL COMMENT '过期时间',
        used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已使用',
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        INDEX idx_phone (phone),
        INDEX idx_expired_at (expired_at)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表'
    `);

    console.log('数据库表创建成功');
  } catch (error) {
    console.error('数据库表创建失败:', error);
    throw error;
  }
}

module.exports = initDatabase;