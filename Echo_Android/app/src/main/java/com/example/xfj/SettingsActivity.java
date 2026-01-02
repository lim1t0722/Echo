package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.xfj.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String THEME_PREF_KEY = "app_theme";
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置");

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 设置主题切换监听
        setupThemeListeners();
        // 加载保存的主题设置
        loadSavedTheme();
        // 设置退出登录点击事件
        binding.llLogout.setOnClickListener(v -> logout());
        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupThemeListeners() {
        // 为主题单选按钮设置点击事件
        binding.rbThemeSystem.setOnClickListener(v -> updateTheme(THEME_SYSTEM));
        binding.rbThemeLight.setOnClickListener(v -> updateTheme(THEME_LIGHT));
        binding.rbThemeDark.setOnClickListener(v -> updateTheme(THEME_DARK));
    }

    private void loadSavedTheme() {
        int savedTheme = sharedPreferences.getInt(THEME_PREF_KEY, THEME_SYSTEM);
        switch (savedTheme) {
            case THEME_SYSTEM:
                binding.rbThemeSystem.setChecked(true);
                break;
            case THEME_LIGHT:
                binding.rbThemeLight.setChecked(true);
                break;
            case THEME_DARK:
                binding.rbThemeDark.setChecked(true);
                break;
        }
    }

    private void updateTheme(int theme) {
        // 保存主题设置
        sharedPreferences.edit().putInt(THEME_PREF_KEY, theme).apply();
        
        // 应用主题
        switch (theme) {
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
        
        Toast.makeText(this, "主题已更新", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        // 清除用户登录信息
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 跳转到登录页面
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
    }
}