const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { validateSendSmsCode, validateVerifyCode } = require('../middlewares/validationMiddleware');

router.post('/sendCode', validateSendSmsCode, authController.sendCode);
router.post('/verifyCode', validateVerifyCode, authController.verifyCode);

module.exports = router;