// 成功响应
const successResponse = (res, data = null, message = '请求成功') => {
  res.status(200).json({
    code: 200,
    message,
    data
  });
};

// 错误响应
const errorResponse = (res, statusCode, message = '请求失败') => {
  res.status(statusCode).json({
    code: statusCode,
    message,
    data: null
  });
};

// 参数验证错误
const validationError = (res, message = '参数验证失败') => {
  res.status(400).json({
    code: 400,
    message,
    data: null
  });
};

// 未授权错误
const unauthorizedError = (res, message = '未授权，请登录') => {
  res.status(401).json({
    code: 401,
    message,
    data: null
  });
};

// 禁止访问错误
const forbiddenError = (res, message = '禁止访问') => {
  res.status(403).json({
    code: 403,
    message,
    data: null
  });
};

// 资源不存在错误
const notFoundError = (res, message = '资源不存在') => {
  res.status(404).json({
    code: 404,
    message,
    data: null
  });
};

// 服务器内部错误
const serverError = (res, message = '服务器内部错误') => {
  res.status(500).json({
    code: 500,
    message,
    data: null
  });
};

module.exports = {
  successResponse,
  errorResponse,
  validationError,
  unauthorizedError,
  forbiddenError,
  notFoundError,
  serverError
};