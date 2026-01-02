package com.example.xfj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.adapter.ChatAdapter;
import com.example.xfj.model.Message;
import com.example.xfj.network.WebSocketManager;
import com.example.xfj.databinding.ActivityChatBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private List<Message> messages;
    private String chatName;
    private String targetUserId;
    private String currentUserId;
    private String currentUserNickname;
    private WebSocketManager webSocketManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查用户是否已登录
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            // 用户未登录，跳转到登录页面
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUserId = sharedPreferences.getString("user_id", "unknown_user");
        currentUserNickname = sharedPreferences.getString("nickname", "未知用户");
        
        // 初始化视图
        initViews();
        // 设置点击事件
        setupListeners();
        // 获取并设置聊天信息
        setupChatInfo();
        // 初始化WebSocket连接
        initWebSocket();
    }

    private void initViews() {
        // 初始化消息列表
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        binding.rvMessages.setAdapter(chatAdapter);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        binding.btnSend.setOnClickListener(v -> {
            String messageContent = binding.etMessage.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);
            } else {
                Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChatInfo() {
        // 从Intent中获取聊天信息
        chatName = getIntent().getStringExtra("chat_name");
        targetUserId = getIntent().getStringExtra("target_user_id");
        
        if (chatName != null) {
            binding.tvChatName.setText(chatName);
        } else {
            chatName = "聊天";
            binding.tvChatName.setText(chatName);
        }
        
        if (targetUserId == null) {
            targetUserId = "chat_room_id"; // 默认值，防止空指针
        }
    }

    private void initWebSocket() {
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect(new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connected");
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "WebSocket连接成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                handleReceivedMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "WebSocket连接已关闭", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket failure: " + t.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "WebSocket连接失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendMessage(String content) {
        try {
            // 创建消息对象
            String messageId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();
            
            // 创建本地消息（发送中状态）
            Message localMessage = new Message();
            localMessage.setMessageId(messageId);
            localMessage.setFromUserId(currentUserId);
            localMessage.setToId(targetUserId); // 使用真实的目标用户ID
            localMessage.setContent(content);
            localMessage.setType(Message.TYPE_TEXT);
            localMessage.setTimestamp(timestamp);
            localMessage.setStatus(Message.STATUS_SENDING);
            localMessage.setGroupMessage(false);
            localMessage.setSenderNickname(currentUserNickname);
            
            // 添加到本地消息列表
            runOnUiThread(() -> {
                chatAdapter.addMessage(localMessage);
                binding.rvMessages.scrollToPosition(messages.size() - 1);
            });
            
            // 创建JSON消息发送到服务器
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "text");
            jsonMessage.put("content", content);
            jsonMessage.put("from", currentUserId);
            jsonMessage.put("to", targetUserId);
            jsonMessage.put("messageId", messageId);
            jsonMessage.put("timestamp", timestamp);
            
            // 发送消息到服务器
            if (webSocketManager.isConnected()) {
                webSocketManager.sendMessage(jsonMessage.toString());
                
                // 更新消息状态为已发送
                localMessage.setStatus(Message.STATUS_SENT);
                runOnUiThread(() -> {
                    chatAdapter.notifyItemChanged(messages.size() - 1);
                });
            } else {
                // 连接断开，更新消息状态为发送失败
                localMessage.setStatus(Message.STATUS_FAILED);
                runOnUiThread(() -> {
                    chatAdapter.notifyItemChanged(messages.size() - 1);
                    Toast.makeText(ChatActivity.this, "发送失败，连接已断开", Toast.LENGTH_SHORT).show();
                });
            }
            
            // 清空输入框
            binding.etMessage.setText("");
            
        } catch (Exception e) {
            Log.e(TAG, "Error sending message: " + e.getMessage());
            Toast.makeText(this, "发送消息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleReceivedMessage(String text) {
        try {
            JSONObject json = new JSONObject(text);
            
            // 解析消息内容
            String content = json.optString("content");
            String fromUserId = json.optString("from");
            String messageId = json.optString("messageId");
            long timestamp = json.optLong("timestamp", System.currentTimeMillis());
            String senderNickname = json.optString("nickname", "未知用户");
            
            // 创建消息对象
            Message message = new Message();
            message.setMessageId(messageId);
            message.setFromUserId(fromUserId);
            message.setToId(targetUserId); // 使用真实的目标用户ID
            message.setContent(content);
            message.setType(Message.TYPE_TEXT);
            message.setTimestamp(timestamp);
            message.setStatus(Message.STATUS_READ);
            message.setGroupMessage(false);
            message.setSenderNickname(senderNickname);
            
            // 添加到消息列表
            runOnUiThread(() -> {
                chatAdapter.addMessage(message);
                binding.rvMessages.scrollToPosition(messages.size() - 1);
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing message: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开WebSocket连接
        webSocketManager.disconnect();
    }
}