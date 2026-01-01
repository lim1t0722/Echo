package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.xfj.databinding.ActivityLoginBinding;

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

        // Check if user is already logged in
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
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

        // Simulate login API call
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simulate network delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // Hide loading
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setVisibility(View.VISIBLE);

                // Store token in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", "sample_token_" + email);
                editor.putString("email", email);
                editor.putString("user_id", "user_" + email.replace("@", "_"));
                editor.putString("nickname", "用户" + email.split("@")[0]);
                editor.apply();

                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                navigateToMain();
            });
        }).start();
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}