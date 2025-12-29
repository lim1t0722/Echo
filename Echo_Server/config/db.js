const mysql = require('mysql2');
require('dotenv').config();

// 创建数据库连接池
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
  connectTimeout: 10000,
  authSwitchHandler: function ({ pluginName, pluginData }, callback) {
    callback(null, pluginData);
  }
});

// 导出 Promise 版本的连接池
module.exports = pool.promise();