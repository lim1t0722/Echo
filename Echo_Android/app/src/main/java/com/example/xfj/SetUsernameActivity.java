package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.xfj.databinding.ActivitySetUsernameBinding;
import com.example.xfj.model.User;
import com.example.xfj.network.ApiResponse;
import com.example.xfj.network.ApiService;
import com.example.xfj.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUsernameActivity extends AppCompatActivity {

    private ActivitySetUsernameBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        binding.btnSave.setOnClickListener(v -> saveUsername());
    }

    private void saveUsername() {
        String username = binding.etUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() < 2 || username.length() > 20) {
            Toast.makeText(this, "用户名长度应在2-20个字符之间", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setVisibility(View.GONE);

        // Get current user info
        String userId = sharedPreferences.getString("user_id", null);
        String email = sharedPreferences.getString("email", null);

        if (userId == null || email == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Create API service instance
        ApiService apiService = RetrofitClient.getInstance(this).getApiService();

        // Call update username API
        Call<ApiResponse<User>> call = apiService.updateUserInfo(new ApiService.UpdateUserInfoRequest(userId, username, null));

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 0) {
                        // Update success
                        User user = apiResponse.getData();

                        // Update user info in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nickname", user != null ? user.getNickname() : username);
                        editor.apply();

                        Toast.makeText(SetUsernameActivity.this, "用户名设置成功", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        // Update failed with error message
                        String errorMessage = apiResponse != null ? apiResponse.getMessage() : "设置失败";
                        Toast.makeText(SetUsernameActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Network error or server error
                    Toast.makeText(SetUsernameActivity.this, "设置失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);

                // Network error
                Toast.makeText(SetUsernameActivity.this, "网络异常，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(SetUsernameActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SetUsernameActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}