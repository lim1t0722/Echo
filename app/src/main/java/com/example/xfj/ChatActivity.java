package com.example.xfj;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    private TextView tvChatName;
    private EditText etMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupListeners();
        // 获取并设置聊天信息
        setupChatInfo();
    }

    private void initViews() {
        tvChatName = findViewById(R.id.tv_chat_name);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    // 模拟发送消息
                    sendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupChatInfo() {
        // 从Intent中获取聊天信息
        String chatName = getIntent().getStringExtra("chat_name");
        if (chatName != null) {
            tvChatName.setText(chatName);
        } else {
            tvChatName.setText("聊天");
        }
    }

    private void sendMessage(String message) {
        // 模拟发送消息
        Toast.makeText(this, "发送消息: " + message, Toast.LENGTH_SHORT).show();
        // 清空输入框
        etMessage.setText("");
    }
}