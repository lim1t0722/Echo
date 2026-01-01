const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const dotenv = require('dotenv');
const initDatabase = require('./utils/init_db');

const authRoutes = require('./routes/authRoutes');
const authController = require('./controllers/authController');

dotenv.config();

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json());

app.use('/api/auth', authRoutes);

app.post('/api/send_sms', authController.sendCode);
app.post('/api/send_email', authController.sendEmailCode);

app.get('/', (req, res) => {
    res.send('Echo Server 已启动');
});

app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'ok',
    message: 'Server is running'
  });
});

app.use((req, res) => {
  res.status(404).json({
    code: 404,
    message: '接口不存在',
    data: null
  });
});

app.use((err, req, res, next) => {
  console.error('服务器错误:', err);
  res.status(500).json({
    code: 500,
    message: '服务器内部错误',
    data: null
  });
});

const PORT = process.env.PORT || 3000;

const startServer = async () => {
  try {
    await initDatabase();
    
    app.listen(PORT, () => {
      console.log(`服务器运行在 http://localhost:${PORT}`);
      console.log(`API 文档: http://localhost:${PORT}/api`);
    });
  } catch (error) {
    console.error('数据库初始化失败:', error.message);
    console.log('服务器将在无数据库模式下启动，部分功能可能不可用');
    
    app.listen(PORT, () => {
      console.log(`服务器运行在 http://localhost:${PORT} (无数据库模式)`);
      console.log(`API 文档: http://localhost:${PORT}/api`);
      console.log('警告: 数据库连接失败，部分功能不可用');
    });
  }
};

startServer();