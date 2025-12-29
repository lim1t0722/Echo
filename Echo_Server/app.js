const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const dotenv = require('dotenv');
const initDatabase = require('./utils/init_db');
const SMSCode = require('./models/sms_code');

// 路由导入
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');

// 加载环境变量
dotenv.config();

// 创建 Express 应用
const app = express();

// 中间件配置
app.use(helmet());
app.use(cors());
app.use(express.json());

// 路由配置
app.use('/api/auth', authRoutes);
app.use('/api/user', userRoutes);

// 健康检查接口
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'ok',
    message: 'Server is running'
  });
});

// 404 处理
app.use((req, res) => {
  res.status(404).json({
    code: 404,
    message: '接口不存在',
    data: null
  });
});

// 错误处理中间件
app.use((err, req, res, next) => {
  console.error('服务器错误:', err);
  res.status(500).json({
    code: 500,
    message: '服务器内部错误',
    data: null
  });
});

// 启动服务器
const PORT = process.env.PORT || 3000;

const startServer = async () => {
  try {
    // 初始化数据库
    await initDatabase();
    
    // 清理过期验证码
    await SMSCode.cleanupExpired();
    
    // 启动服务器
    app.listen(PORT, () => {
      console.log(`服务器运行在 http://localhost:${PORT}`);
      console.log(`API 文档: http://localhost:${PORT}/api`);
    });
  } catch (error) {
    console.error('数据库初始化失败:', error.message);
    console.log('服务器将在无数据库模式下启动，部分功能可能不可用');
    
    // 即使数据库连接失败，也启动服务器
    app.listen(PORT, () => {
      console.log(`服务器运行在 http://localhost:${PORT} (无数据库模式)`);
      console.log(`API 文档: http://localhost:${PORT}/api`);
      console.log('警告: 数据库连接失败，部分功能不可用');
    });
  }
};

// 启动服务器
startServer();