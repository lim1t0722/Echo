const axios = require('axios');

async function testRegister() {
  try {
    // 生成测试数据
    const testEmail = `test_${Date.now()}@example.com`;
    const testNickname = `测试用户${Date.now().toString().slice(-4)}`;
    const testPassword = '123456';
    const testCode = '123456'; // 注意：实际测试需要先发送验证码获取真实的code

    console.log('测试数据:');
    console.log('邮箱:', testEmail);
    console.log('昵称:', testNickname);
    console.log('密码:', testPassword);
    console.log('验证码:', testCode);

    // 测试注册接口
    const response = await axios.post('http://localhost:3000/api/auth/register', {
      email: testEmail,
      nickname: testNickname,
      password: testPassword,
      verificationCode: testCode
    });

    console.log('\n注册成功响应:');
    console.log(response.data);
    return response.data;
  } catch (error) {
    console.error('\n注册失败:');
    if (error.response) {
      console.log('状态码:', error.response.status);
      console.log('响应数据:', error.response.data);
    } else if (error.request) {
      console.log('没有收到响应:', error.request);
    } else {
      console.log('错误信息:', error.message);
    }
    return null;
  }
}

testRegister();
