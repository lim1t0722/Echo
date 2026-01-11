package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class BirthDateActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Button confirmButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth_date);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("设置出生年月");

        datePicker = findViewById(R.id.date_picker);
        confirmButton = findViewById(R.id.confirm_button);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 设置日期选择器的范围：1900年到当前年份
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(currentYear, currentMonth, currentDay, null);
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.setMinDate(getMinDate(1900, 1, 1));

        // 隐藏日期选择器的天部分，只保留年月选择
        hideDayPicker();

        // 设置确认按钮点击事件
        confirmButton.setOnClickListener(v -> {
            saveBirthDate();
        });
    }

    // 保存出生年月
    private void saveBirthDate() {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1; // DatePicker的月份是0-11，需要加1

        // 格式化日期为yyyy-MM格式
        String birthDate = String.format("%d-%02d", year, month);

        // 保存到SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("birth_date", birthDate);
        editor.putBoolean("is_first_login", false); // 标记首次登录完成
        editor.apply();

        Toast.makeText(this, "出生年月已设置", Toast.LENGTH_SHORT).show();

        // 跳转到主Activity
        Intent intent = new Intent(BirthDateActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // 隐藏日期选择器的天部分
    private void hideDayPicker() {
        try {
            // 通过反射隐藏天部分
            java.lang.reflect.Field[] fields = datePicker.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().equals("mDaySpinner") || field.getName().equals("mDayPicker")) {
                    field.setAccessible(true);
                    Object dayPicker = field.get(datePicker);
                    if (dayPicker instanceof View) {
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取最小日期的时间戳
    private long getMinDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }
}