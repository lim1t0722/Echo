package com.example.xfj.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static final String WS_URL = "ws://121.41.105.155:3000";
    
    // 重试机制参数
    private static final long INITIAL_RETRY_DELAY = 1000; // 初始重试延迟1秒
    private static final long MAX_RETRY_DELAY = 60000; // 最大重试延迟60秒
    private static final int MAX_RETRY_COUNT = 10; // 最大重试次数
    
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private WebSocketListener webSocketListener;
    private OkHttpClient client;
    private Handler handler = new Handler(Looper.getMainLooper());
    private long retryDelay = INITIAL_RETRY_DELAY;
    private int retryCount = 0;
    private boolean isConnecting = false;
    private boolean shouldReconnect = false;
    
    private WebSocketManager() {
        // 私有构造函数，实现单例模式
        client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS) // 设置30秒心跳
                .build();
    }
    
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    
    public void connect(WebSocketListener listener) {
        if (isConnected() || isConnecting) {
            Log.d(TAG, "WebSocket already connected or connecting");
            return;
        }
        
        isConnecting = true;
        shouldReconnect = true;
        this.webSocketListener = new CustomWebSocketListener(listener);
        
        Log.d(TAG, "Attempting to connect to WebSocket: " + WS_URL + " (Attempt " + (retryCount + 1) + ")");
        
        Request request = new Request.Builder()
                .url(WS_URL)
                .build();
        
        webSocket = client.newWebSocket(request, this.webSocketListener);
    }
    
    public void disconnect() {
        shouldReconnect = false;
        retryDelay = INITIAL_RETRY_DELAY;
        retryCount = 0;
        
        if (webSocket != null) {
            Log.d(TAG, "Disconnecting WebSocket");
            webSocket.close(1000, "Normal closure");
            webSocket = null;
        }
        isConnecting = false;
    }
    
    public boolean isConnected() {
        return webSocket != null;
    }
    
    public boolean isConnecting() {
        return isConnecting;
    }
    
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
            Log.d(TAG, "Sent message: " + message);
        } else {
            Log.e(TAG, "WebSocket not connected, cannot send message");
            // 如果连接断开，尝试重新连接
            if (!isConnecting) {
                connect(webSocketListener);
            }
        }
    }
    
    // 内部类，处理WebSocket事件并实现重试逻辑
    private class CustomWebSocketListener extends WebSocketListener {
        private WebSocketListener originalListener;
        
        public CustomWebSocketListener(WebSocketListener listener) {
            this.originalListener = listener;
        }
        
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            Log.d(TAG, "WebSocket connected successfully");
            isConnecting = false;
            retryDelay = INITIAL_RETRY_DELAY;
            retryCount = 0;
            
            if (originalListener != null) {
                originalListener.onOpen(webSocket, response);
            }
        }
        
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "WebSocket closing: " + reason + " (code: " + code + ")");
            
            if (originalListener != null) {
                originalListener.onClosing(webSocket, code, reason);
            }
        }
        
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(TAG, "WebSocket closed: " + reason + " (code: " + code + ")");
            
            if (originalListener != null) {
                originalListener.onClosed(webSocket, code, reason);
            }
            
            // 连接关闭后，根据shouldReconnect决定是否重试
            if (shouldReconnect && retryCount < MAX_RETRY_COUNT) {
                scheduleReconnect();
            } else {
                isConnecting = false;
            }
        }
        
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            Log.e(TAG, "WebSocket connection failed: " + t.getMessage());
            
            if (originalListener != null) {
                originalListener.onFailure(webSocket, t, response);
            }
            
            // 连接失败后，根据shouldReconnect决定是否重试
            if (shouldReconnect && retryCount < MAX_RETRY_COUNT) {
                scheduleReconnect();
            } else {
                isConnecting = false;
            }
        }
        
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d(TAG, "Received message: " + text);
            
            if (originalListener != null) {
                originalListener.onMessage(webSocket, text);
            }
        }
        
        // 安排重新连接
        private void scheduleReconnect() {
            retryCount++;
            
            // 指数退避算法
            retryDelay = Math.min(retryDelay * 2, MAX_RETRY_DELAY);
            
            Log.d(TAG, "Scheduling reconnect in " + retryDelay + "ms (Attempt " + retryCount + ")");
            
            handler.postDelayed(() -> {
                if (shouldReconnect) {
                    isConnecting = false;
                    connect(originalListener);
                }
            }, retryDelay);
        }
    }
}