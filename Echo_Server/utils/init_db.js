const db = require('../config/db');

async function initDatabase() {
  try {
    // 创建用户表
    await db.execute(`
      CREATE TABLE IF NOT EXISTS users (
        user_id VARCHAR(10) PRIMARY KEY COMMENT '用户ID（6-10位数字）',
        phone VARCHAR(20) UNIQUE COMMENT '用户手机号',
        email VARCHAR(50) UNIQUE COMMENT '用户邮箱',
        nickname VARCHAR(20) COMMENT '用户昵称',
        password VARCHAR(100) COMMENT '用户密码（加密存储）',
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
        INDEX idx_phone (phone),
        INDEX idx_email (email)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表'
    `);

    console.log('数据库表创建成功');
  } catch (error) {
    console.error('数据库表创建失败:', error);
    throw error;
  }
}

module.exports = initDatabase;