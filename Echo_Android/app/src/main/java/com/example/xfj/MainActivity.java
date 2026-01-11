package com.example.xfj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private GoalFragment goalFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 初始化Fragment
        homeFragment = new HomeFragment();
        goalFragment = new GoalFragment();
        profileFragment = new ProfileFragment();

        // 默认显示首页
        setFragment(homeFragment);

        // 设置底部导航栏点击事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                setFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_goal) {
                setFragment(goalFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                setFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    // Fragment切换方法
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}