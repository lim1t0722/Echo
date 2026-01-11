package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.xfj.databinding.ActivityLoginBinding;
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

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Check if user is already logged in and auto-login is enabled
        boolean autoLogin = sharedPreferences.getBoolean("auto_login", false);
        String userId = sharedPreferences.getString("user_id", null);
        String email = sharedPreferences.getString("email", null);
        if (autoLogin && userId != null && email != null) {
            navigateToMain();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入邮箱和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "请输入有效的邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setVisibility(View.GONE);

        // 创建ApiService实例
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();
        
        // 调用登录API
        Call<ApiResponse<User>> call = apiService.login(new ApiService.LoginRequest(email, password));
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setVisibility(View.VISIBLE);
                
                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 0) {
                        // Login success
                        User user = apiResponse.getData();
                        
                        // 确保user和userId都有效
                        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "登录失败，用户信息无效", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Store user info in SharedPreferences (同步保存)
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String userId = user.getUserId();
                        String nickname = user.getNickname() != null ? user.getNickname() : "用户" + email.split("@")[0];
                        
                        editor.putString("email", email);
                        editor.putString("user_id", userId);
                        editor.putString("nickname", nickname);
                        
                        // 只保留后端实际返回并使用token的相关代码
                        if (user.getToken() != null) {
                            editor.putString("token", user.getToken());
                        }
                        
                        if (user.getAvatar() != null) {
                            editor.putString("avatar", user.getAvatar());
                        }
                        
                        // 设置注册状态为已完成（登录用户默认已完成注册）
                        editor.putBoolean("is_register_completed", true);
                        editor.putBoolean("auto_login", binding.cbAutoLogin.isChecked());
                        editor.commit(); // 使用commit()确保同步保存
                        
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        // Login failed
                        String errorMessage = apiResponse != null ? apiResponse.getMessage() : "登录失败";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Network error or server error
                    Toast.makeText(LoginActivity.this, "登录失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setVisibility(View.VISIBLE);
                
                // Network error
                String errorMessage = "网络异常";
                if (t instanceof SocketTimeoutException) {
                    errorMessage = "连接超时，请重试";
                } else if (t instanceof ConnectException) {
                    errorMessage = "连接失败，请检查网络";
                } else if (t instanceof UnknownHostException) {
                    errorMessage = "无法连接服务器";
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isFirstLogin = sharedPreferences.getBoolean("is_first_login", true);
        String birthDate = sharedPreferences.getString("birth_date", "");

        // 如果是首次登录或未设置出生年月，跳转到出生年月设置页面
        if (isFirstLogin || birthDate.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, BirthDateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // 否则直接跳转到主页面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }
}