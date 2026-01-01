package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.xfj.databinding.ActivityRegisterBinding;
import com.example.xfj.model.User;
import com.example.xfj.network.ApiResponse;
import com.example.xfj.network.ApiService;
import com.example.xfj.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer;
    private ApiService apiService;
    private Call<ApiResponse<Void>> verificationCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        // 初始化ApiService
        apiService = RetrofitClient.getInstance(this).getApiService();

        binding.btnGetVerification.setOnClickListener(v -> getVerificationCode());
        binding.btnRegister.setOnClickListener(v -> register());
        binding.tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getVerificationCode() {
        String email = binding.etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        // 禁用按钮防止重复提交
        binding.btnGetVerification.setEnabled(false);
        binding.btnGetVerification.setText("发送中");

        // 取消之前的请求（如果存在）
        if (verificationCall != null && !verificationCall.isCanceled()) {
            verificationCall.cancel();
        }

        // 创建请求体
        ApiService.VerificationCodeRequest request = new ApiService.VerificationCodeRequest(email);
        
        // 发送请求
        verificationCall = apiService.sendVerificationCode(request);
        verificationCall.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // 请求成功，启动倒计时
                        startCountdown();
                        Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 服务器返回错误信息
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // 恢复按钮状态
                        binding.btnGetVerification.setEnabled(true);
                        binding.btnGetVerification.setText("获取验证码");
                    }
                } else {
                    // 请求失败
                    Toast.makeText(RegisterActivity.this, "请求失败，请重试", Toast.LENGTH_SHORT).show();
                    // 恢复按钮状态
                    binding.btnGetVerification.setEnabled(true);
                    binding.btnGetVerification.setText("获取验证码");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // 网络异常或连接超时
                String errorMessage = "网络异常";
                if (t instanceof SocketTimeoutException) {
                    errorMessage = "连接超时，请重试";
                } else if (t instanceof ConnectException) {
                    errorMessage = "连接失败，请检查网络";
                } else if (t instanceof UnknownHostException) {
                    errorMessage = "无法连接服务器";
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                // 恢复按钮状态
                binding.btnGetVerification.setEnabled(true);
                binding.btnGetVerification.setText("获取验证码");
            }
        });
    }

    private void startCountdown() {
        binding.btnGetVerification.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.btnGetVerification.setText("重新获取(" + millisUntilFinished / 1000 + ")");
            }

            public void onFinish() {
                binding.btnGetVerification.setEnabled(true);
                binding.btnGetVerification.setText("获取验证码");
            }
        }.start();
    }

    private void register() {
        String email = binding.etEmail.getText().toString().trim();
        String verificationCode = binding.etVerification.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || verificationCode.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verificationCode.length() != 6) {
            Toast.makeText(this, "请输入6位验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "密码至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnRegister.setVisibility(View.GONE);

        // Create API service instance
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        
        // Use nickname as the part before @ in email
        String nickname = "用户" + email.substring(0, email.indexOf('@'));
        
        // Call register API
        Call<ApiResponse<User>> call = apiService.register(new ApiService.RegisterRequest(email, password, verificationCode, nickname));
        
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setVisibility(View.VISIBLE);
                
                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 0) {
                        // Register success
                        User user = apiResponse.getData();
                        
                        // Store user info in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", "sample_token_" + email); // TODO: Replace with actual token from API response
                        editor.putString("email", email);
                        editor.putString("user_id", user != null ? user.getUserId() : "user_" + email);
                        editor.putString("nickname", nickname);
                        editor.apply();
                        
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        // Register failed with error message
                        String errorMessage = apiResponse != null ? apiResponse.getMessage() : "注册失败";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Network error or server error
                    Toast.makeText(RegisterActivity.this, "注册失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setVisibility(View.VISIBLE);
                
                // Network error
                String errorMessage = "网络异常";
                if (t instanceof SocketTimeoutException) {
                    errorMessage = "连接超时，请重试";
                } else if (t instanceof ConnectException) {
                    errorMessage = "连接失败，请检查网络";
                } else if (t instanceof UnknownHostException) {
                    errorMessage = "无法连接服务器";
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 邮箱格式验证
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}