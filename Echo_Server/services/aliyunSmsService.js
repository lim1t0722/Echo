const Dysmsapi = require('@alicloud/dysmsapi20170525');
const OpenApi = require('@alicloud/openapi-client');
require('dotenv').config();

class AliyunSmsService {
  constructor() {
    const config = new OpenApi.Config({
      accessKeyId: process.env.ALIYUN_ACCESS_KEY_ID,
      accessKeySecret: process.env.ALIYUN_ACCESS_KEY_SECRET,
      regionId: process.env.ALIYUN_REGION || 'cn-hangzhou'
    });
    this.client = new Dysmsapi.default(config);
  }

  generateVerifyCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  async sendSmsVerifyCode(phoneNumber) {
    try {
      const verifyCode = this.generateVerifyCode();
      
      const request = new Dysmsapi.SendSmsRequest({
        phoneNumbers: phoneNumber,
        signName: process.env.ALIYUN_SMS_SIGN_NAME,
        templateCode: process.env.ALIYUN_SMS_TEMPLATE_CODE,
        templateParam: JSON.stringify({
          code: verifyCode,
          min: "5"
        })
      });

      console.log('[发送短信请求] 参数:', JSON.stringify({
        phoneNumbers: phoneNumber,
        signName: process.env.ALIYUN_SMS_SIGN_NAME,
        templateCode: process.env.ALIYUN_SMS_TEMPLATE_CODE,
        templateParam: {
          code: verifyCode,
          min: "5"
        }
      }));

      const response = await this.client.sendSms(request);
      
      console.log('[发送短信响应] 完整返回:', JSON.stringify(response.body));
      
      if (response.body.code === 'OK') {
        return {
          success: true,
          message: '验证码已发送',
          requestId: response.body.requestId,
          bizId: response.body.bizId,
          verifyCode: verifyCode
        };
      } else {
        return {
          success: false,
          message: response.body.message || '发送验证码失败',
          code: response.body.code
        };
      }
    } catch (error) {
      console.error('[发送短信异常] 错误信息:', error);
      return {
        success: false,
        message: error.message || '发送验证码失败',
        code: error.code || 'SMS_SEND_ERROR'
      };
    }
  }

  async checkSmsVerifyCode(phoneNumber, verifyCode) {
    try {
      const request = new Dysmsapi.QuerySendDetailsRequest({
        phoneNumber: phoneNumber,
        sendDate: this.getFormattedDate(),
        pageSize: 10,
        currentPage: 1
      });

      const response = await this.client.querySendDetails(request);
      
      console.log('查询短信验证码响应:', JSON.stringify(response.body));
      
      if (response.body.code !== 'OK') {
        return {
          success: false,
          message: response.body.message || '查询验证码失败',
          code: response.body.code
        };
      }

      const smsDetails = response.body.smsSendDetailDTOs.smsSendDetailDTO;
      
      if (!smsDetails || smsDetails.length === 0) {
        return {
          success: false,
          message: '未找到验证码记录',
          code: 'NO_SMS_RECORD'
        };
      }

      const latestSms = smsDetails[0];
      const content = latestSms.content;
      
      const codeMatch = content.match(/\d{4,6}/);
      if (!codeMatch) {
        return {
          success: false,
          message: '验证码格式错误',
          code: 'INVALID_CODE_FORMAT'
        };
      }

      const sentCode = codeMatch[0];
      
      if (sentCode === verifyCode) {
        return {
          success: true,
          message: '验证码验证成功'
        };
      } else {
        return {
          success: false,
          message: '验证码错误',
          code: 'INVALID_CODE'
        };
      }
    } catch (error) {
      console.error('验证短信验证码异常:', error);
      return {
        success: false,
        message: error.message || '验证验证码失败',
        code: error.code || 'SMS_VERIFY_ERROR'
      };
    }
  }

  getFormattedDate() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}${month}${day}`;
  }
}

module.exports = new AliyunSmsService();