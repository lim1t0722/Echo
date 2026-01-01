const mysql = require('mysql2');

console.log('开始测试数据库连接...');

// 测试1: 使用空密码
console.log('\n测试1: 使用空密码连接');
const connection1 = mysql.createConnection({
  host: '121.41.105.155',
  port: 3306,
  user: 'Echo_admin',
  password: 'admin123'
});

connection1.connect((err) => {
  if (err) {
    console.error('连接失败:', err.message);
  } else {
    console.log('连接成功!');
    connection1.query('SELECT 1 as test', (err, results) => {
      if (err) {
        console.error('查询失败:', err.message);
      } else {
        console.log('查询结果:', results);
      }
      connection1.end();
    });
  }
});

// 测试2: 使用localhost
setTimeout(() => {
  console.log('\n测试2: 使用localhost连接');
  const connection2 = mysql.createConnection({
    host: 'localhost',
    port: 3306,
    user: 'root',
    password: ''
  });

  connection2.connect((err) => {
    if (err) {
      console.error('连接失败:', err.message);
    } else {
      console.log('连接成功!');
      connection2.query('SELECT 1 as test', (err, results) => {
        if (err) {
          console.error('查询失败:', err.message);
        } else {
          console.log('查询结果:', results);
        }
        connection2.end();
      });
    }
  });
}, 2000);