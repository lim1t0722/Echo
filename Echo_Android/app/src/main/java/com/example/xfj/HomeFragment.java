package com.example.xfj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView survivalTimeTextView;
    private Button checkInButton;
    private TextView checkInStatusTextView;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Runnable updateTimeRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        survivalTimeTextView = view.findViewById(R.id.survival_time_text);
        checkInButton = view.findViewById(R.id.check_in_button);
        checkInStatusTextView = view.findViewById(R.id.check_in_status);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);

        // 初始化存活时间显示
        updateSurvivalTime();

        // 设置定时更新存活时间
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateSurvivalTime();
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };
        handler.postDelayed(updateTimeRunnable, 0);

        // 初始化签到按钮状态
        updateCheckInStatus();

        // 设置签到按钮点击事件
        checkInButton.setOnClickListener(v -> {
            if (canCheckIn()) {
                performCheckIn();
            } else {
                Toast.makeText(getActivity(), "今日已签到", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 更新存活时间显示
    private void updateSurvivalTime() {
        String birthDateStr = sharedPreferences.getString("birth_date", "");
        if (birthDateStr.isEmpty()) {
            survivalTimeTextView.setText("请先设置出生年月");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            Date birthDate = sdf.parse(birthDateStr);
            Date currentDate = new Date();

            // 计算存活时间
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentDate);

            int years = currentCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            int months = currentCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH);
            int days = currentCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH);
            int hours = currentCal.get(Calendar.HOUR_OF_DAY) - birthCal.get(Calendar.HOUR_OF_DAY);
            int minutes = currentCal.get(Calendar.MINUTE) - birthCal.get(Calendar.MINUTE);
            int seconds = currentCal.get(Calendar.SECOND) - birthCal.get(Calendar.SECOND);

            // 调整负数情况
            if (seconds < 0) {
                seconds += 60;
                minutes--;
            }
            if (minutes < 0) {
                minutes += 60;
                hours--;
            }
            if (hours < 0) {
                hours += 24;
                days--;
            }
            if (days < 0) {
                // 获取上月天数
                birthCal.add(Calendar.MONTH, 1);
                birthCal.add(Calendar.DAY_OF_MONTH, -1);
                days += birthCal.get(Calendar.DAY_OF_MONTH);
                birthCal.add(Calendar.MONTH, -1);
                birthCal.add(Calendar.DAY_OF_MONTH, 1);
                months--;
            }
            if (months < 0) {
                months += 12;
                years--;
            }

            // 显示存活时间
            String survivalTime = String.format("你已经在这个世界上活了 %d年%d月%d天%d小时%d分钟%d秒",
                    years, months, days, hours, minutes, seconds);
            survivalTimeTextView.setText(survivalTime);

        } catch (ParseException e) {
            e.printStackTrace();
            survivalTimeTextView.setText("日期格式错误");
        }
    }

    // 检查是否可以签到
    private boolean canCheckIn() {
        String lastCheckInDate = sharedPreferences.getString("last_check_in_date", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        return !currentDate.equals(lastCheckInDate);
    }

    // 执行签到操作
    private void performCheckIn() {
        // 更新签到状态
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_check_in_date", currentDate);

        // 增加存活天数
        int survivalDays = sharedPreferences.getInt("survival_days", 0);
        editor.putInt("survival_days", survivalDays + 1);
        editor.apply();

        // 更新UI
        updateCheckInStatus();

        // 显示签到成功动画
        checkInButton.setBackgroundColor(getResources().getColor(R.color.success_color));
        checkInButton.setText("签到成功");
        Toast.makeText(getActivity(), "签到成功！存活天数+1", Toast.LENGTH_SHORT).show();

        // 恢复按钮状态
        new Handler().postDelayed(() -> {
            checkInButton.setBackgroundResource(R.drawable.circular_button);
            checkInButton.setText("存活签到");
        }, 1000);
    }

    // 更新签到状态显示
    private void updateCheckInStatus() {
        if (canCheckIn()) {
            checkInButton.setEnabled(true);
            checkInButton.setText("存活签到");
            checkInStatusTextView.setText("今日未签到");
        } else {
            checkInButton.setEnabled(false);
            checkInButton.setText("今日已签到");
            checkInStatusTextView.setText("今日已签到");
        }

        // 显示存活天数
        int survivalDays = sharedPreferences.getInt("survival_days", 0);
        checkInStatusTextView.append(" | 累计存活天数: " + survivalDays);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 停止定时更新
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }
}