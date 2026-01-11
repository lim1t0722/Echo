package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView emailTextView;
    private TextView userIdTextView;
    private TextView birthDateTextView;
    private Button logoutButton;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailTextView = view.findViewById(R.id.profile_email);
        userIdTextView = view.findViewById(R.id.profile_user_id);
        birthDateTextView = view.findViewById(R.id.profile_birth_date);
        logoutButton = view.findViewById(R.id.logout_button);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);

        // 显示用户信息
        updateUserInfo();

        // 退出登录按钮点击事件
        logoutButton.setOnClickListener(v -> {
            performLogout();
        });

        return view;
    }

    // 更新用户信息显示
    private void updateUserInfo() {
        String email = sharedPreferences.getString("email", "");
        String userId = sharedPreferences.getString("user_id", "");
        String birthDate = sharedPreferences.getString("birth_date", "");

        emailTextView.setText("邮箱：" + email);
        userIdTextView.setText("用户ID：" + userId);
        birthDateTextView.setText("出生年月：" + (birthDate.isEmpty() ? "未设置" : birthDate));
    }

    // 执行退出登录操作
    private void performLogout() {
        // 清理本地登录态
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();

        // 回到登录页
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}