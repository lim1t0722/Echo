const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { validateSendCode, validateVerifyCode } = require('../middlewares/validationMiddleware');

router.post('/sendCode', validateSendCode, authController.sendCode);
router.post('/verifyCode', validateVerifyCode, authController.verifyCode);

module.exports = router;