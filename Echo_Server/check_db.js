const mysql = require('mysql2/promise');
require('dotenv').config();

async function checkDatabase() {
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

    // 检查users表结构
    const [rows] = await connection.execute('DESCRIBE users');
    console.log('\nUsers表结构:');
    console.table(rows);

    // 检查是否存在email、nickname等字段
    const fieldNames = rows.map(row => row.Field);
    console.log('\n表中存在的字段:', fieldNames);

    await connection.end();
    return rows;
  } catch (error) {
    console.error('数据库连接或查询失败:', error);
    return null;
  }
}

checkDatabase();
