package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.widget.LinearLayout.LayoutParams;
import android.text.InputType;
import android.app.AlertDialog;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.UUID;
import com.example.xfj.network.WebSocketManager;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
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

        // 检查用户登录状态（三态模型）
        checkUserLoginStatus();
        
        // 如果未返回，则继续创建Activity

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuickMessageDialog();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        // 从SharedPreferences获取用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname", "未知用户");
        String userId = sharedPreferences.getString("user_id", "");
        
        // 更新侧边栏用户信息
        View headerView = navigationView.getHeaderView(0);
        if (headerView instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) headerView;
            // 获取标题TextView（索引1：ImageView是索引0）
            if (linearLayout.getChildCount() > 1 && linearLayout.getChildAt(1) instanceof TextView) {
                TextView navHeaderTitle = (TextView) linearLayout.getChildAt(1);
                navHeaderTitle.setText(nickname);
            }
            // 获取副标题TextView（索引2）
            if (linearLayout.getChildCount() > 2 && linearLayout.getChildAt(2) instanceof TextView) {
                TextView navHeaderSubtitle = (TextView) linearLayout.getChildAt(2);
                navHeaderSubtitle.setText("ID: " + userId);
            }
        }
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
                Intent intent = new Intent(this, AddFriendActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_settings) {
                // 处理设置菜单项
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 不再加载右上角菜单，设置入口已移至侧边栏
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    
    /**
     * 检查用户登录状态（三态模型）
     * UNLOGIN: user_id为空
     * REGISTERING: user_id不为空，但is_register_completed为false
     * LOGINED: user_id不为空，且is_register_completed为true
     */
    private void checkUserLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");
        boolean isRegisterCompleted = sharedPreferences.getBoolean("is_register_completed", false);
        
        // 调试日志
        Log.d("MainActivity", "当前用户ID: " + userId);
        Log.d("MainActivity", "注册完成状态: " + isRegisterCompleted);
        
        if (userId.isEmpty()) {
            // UNLOGIN: 用户未登录，跳转到登录页面
            Log.d("MainActivity", "用户ID为空，跳转至登录页");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (!isRegisterCompleted) {
            // REGISTERING: 用户已注册但未完成用户名设置，跳转到设置用户名页面
            Log.d("MainActivity", "用户已注册但未设置用户名，跳转至设置用户名页");
            Intent intent = new Intent(this, SetUsernameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // LOGINED: 用户已完成注册并登录，继续进入主界面
            Log.d("MainActivity", "用户已完成注册并登录，进入主界面");
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // 检查用户登录状态（三态模型）
        checkUserLoginStatus();
    }

    private void showQuickMessageDialog() {
        // 创建对话框布局
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("快速发送消息");

        // 创建表单布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // 添加目标用户ID输入框
        EditText targetUserIdEditText = new EditText(this);
        targetUserIdEditText.setHint("目标用户ID");
        targetUserIdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 16);
        targetUserIdEditText.setLayoutParams(params1);
        layout.addView(targetUserIdEditText);

        // 添加消息内容输入框
        EditText messageEditText = new EditText(this);
        messageEditText.setHint("消息内容");
        messageEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        messageEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(messageEditText);

        builder.setView(layout);

        // 设置发送按钮
        builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String targetUserId = targetUserIdEditText.getText().toString().trim();
                String messageContent = messageEditText.getText().toString().trim();

                if (targetUserId.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入目标用户ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (messageContent.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入消息内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendQuickMessage(targetUserId, messageContent);
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void sendQuickMessage(String targetUserId, String messageContent) {
        // 获取当前用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("user_id", "unknown_user");
        String currentUserNickname = sharedPreferences.getString("nickname", "未知用户");

        try {
            // 创建JSON消息
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "text");
            jsonMessage.put("content", messageContent);
            jsonMessage.put("from", currentUserId);
            jsonMessage.put("to", targetUserId);
            jsonMessage.put("messageId", UUID.randomUUID().toString());
            jsonMessage.put("timestamp", System.currentTimeMillis());

            // 获取WebSocketManager实例并发送消息
            WebSocketManager webSocketManager = WebSocketManager.getInstance();
            if (webSocketManager.isConnected()) {
                webSocketManager.sendMessage(jsonMessage.toString());
                Toast.makeText(this, "消息发送成功", Toast.LENGTH_SHORT).show();
            } else {
                // 如果未连接，尝试连接
                webSocketManager.connect(new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        webSocketManager.sendMessage(jsonMessage.toString());
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "消息发送成功", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "发送失败，连接服务器失败", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
        }
    }

}