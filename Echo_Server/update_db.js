const mysql = require('mysql2/promise');
require('dotenv').config();

async function updateDatabase() {
  try {
    // 使用用户提供的数据库地址
    const connection = await mysql.createConnection({
      host: '121.41.105.155',
      port: 3306,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME,
      connectTimeout: 10000
    });

    console.log('数据库连接成功');

    // 为users表添加缺少的字段
    await connection.execute('ALTER TABLE users ADD COLUMN nickname VARCHAR(20) NULL COMMENT "用户昵称"');
    console.log('✅ 添加nickname字段成功');

    await connection.execute('ALTER TABLE users ADD COLUMN password VARCHAR(100) NULL COMMENT "用户密码（加密存储）"');
    console.log('✅ 添加password字段成功');

    // 查看更新后的表结构
    const [rows] = await connection.execute('DESCRIBE users');
    console.log('\n更新后的Users表结构:');
    console.table(rows);

    await connection.end();
    return rows;
  } catch (error) {
    if (error.code === 'ER_DUP_FIELDNAME') {
      console.log('⚠️  字段已存在，跳过添加');
    } else {
      console.error('数据库更新失败:', error);
    }
    return null;
  }
}

updateDatabase();
