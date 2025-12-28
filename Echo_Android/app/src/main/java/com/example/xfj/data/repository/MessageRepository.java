package com.example.xfj.data.repository;

import android.content.Context;

import com.example.xfj.data.dao.MessageDao;
import com.example.xfj.data.database.AppDatabase;
import com.example.xfj.model.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageRepository {
    private MessageDao messageDao;
    private ExecutorService executorService;

    public MessageRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        messageDao = db.messageDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // 保存消息到数据库
    public void saveMessage(Message message) {
        executorService.execute(() -> {
            messageDao.insert(message);
        });
    }

    // 保存多条消息到数据库
    public void saveAllMessages(List<Message> messages) {
        executorService.execute(() -> {
            messageDao.insertAll(messages);
        });
    }

    // 更新消息
    public void updateMessage(Message message) {
        executorService.execute(() -> {
            messageDao.update(message);
        });
    }

    // 更新消息状态
    public void updateMessageStatus(String messageId, int status) {
        executorService.execute(() -> {
            messageDao.updateMessageStatus(messageId, status);
        });
    }

    // 获取与特定用户的聊天记录
    public void getChatMessages(String currentUserId, String otherUserId, OnMessagesLoadedCallback callback) {
        executorService.execute(() -> {
            List<Message> messages = messageDao.getChatMessages(currentUserId, otherUserId);
            if (callback != null) {
                callback.onMessagesLoaded(messages);
            }
        });
    }

    // 获取特定群组的聊天记录
    public void getGroupMessages(String groupId, OnMessagesLoadedCallback callback) {
        executorService.execute(() -> {
            List<Message> messages = messageDao.getGroupMessages(groupId);
            if (callback != null) {
                callback.onMessagesLoaded(messages);
            }
        });
    }

    // 获取最新的消息
    public void getLatestMessages(int limit, OnMessagesLoadedCallback callback) {
        executorService.execute(() -> {
            List<Message> messages = messageDao.getLatestMessages(limit);
            if (callback != null) {
                callback.onMessagesLoaded(messages);
            }
        });
    }

    // 删除特定对话的所有消息
    public void deleteChatMessages(String currentUserId, String otherUserId) {
        executorService.execute(() -> {
            messageDao.deleteChatMessages(currentUserId, otherUserId);
        });
    }

    // 删除特定群组的所有消息
    public void deleteGroupMessages(String groupId) {
        executorService.execute(() -> {
            messageDao.deleteGroupMessages(groupId);
        });
    }

    // 删除所有消息
    public void deleteAllMessages() {
        executorService.execute(() -> {
            messageDao.deleteAllMessages();
        });
    }

    // 删除单条消息
    public void deleteMessage(Message message) {
        executorService.execute(() -> {
            messageDao.delete(message);
        });
    }

    // 消息加载回调接口
    public interface OnMessagesLoadedCallback {
        void onMessagesLoaded(List<Message> messages);
    }
}