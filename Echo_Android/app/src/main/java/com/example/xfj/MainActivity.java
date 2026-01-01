package com.example.xfj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import androidx.core.view.GravityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xfj.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chat_list, R.id.nav_contacts, R.id.nav_groups)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        
        // 设置导航菜单项点击监听
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                
                if (id == R.id.nav_chat_list || id == R.id.nav_contacts || id == R.id.nav_groups) {
                    // 默认导航处理
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    drawer.closeDrawer(GravityCompat.START);
                    return handled;
                } else if (id == R.id.nav_add_friend) {
                    // 处理添加好友菜单项
                    AddFriendActivity.start(this);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.nav_settings) {
                    // 处理设置菜单项
                    showSettingsDialog();
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                
                return false;
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showSettingsDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("设置")
                .setItems(new String[]{"退出登录"}, (dialog, which) -> {
                    if (which == 0) {
                        logout();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void logout() {
        // 清除用户登录信息
        getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply();
        
        // 跳转到登录页面
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}