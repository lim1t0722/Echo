package com.example.xfj.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.xfj.model.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Message> messages);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Query("SELECT * FROM messages WHERE messageId = :messageId")
    Message getMessageById(String messageId);

    // 获取与特定用户的聊天记录
    @Query("SELECT * FROM messages WHERE (fromUserId = :currentUserId AND toId = :otherUserId) OR (fromUserId = :otherUserId AND toId = :currentUserId) AND isGroupMessage = 0 ORDER BY timestamp ASC")
    List<Message> getChatMessages(String currentUserId, String otherUserId);

    // 获取特定群组的聊天记录
    @Query("SELECT * FROM messages WHERE toId = :groupId AND isGroupMessage = 1 ORDER BY timestamp ASC")
    List<Message> getGroupMessages(String groupId);

    // 获取最新的消息
    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    List<Message> getLatestMessages(int limit);

    // 删除特定对话的所有消息
    @Query("DELETE FROM messages WHERE ((fromUserId = :currentUserId AND toId = :otherUserId) OR (fromUserId = :otherUserId AND toId = :currentUserId)) AND isGroupMessage = 0")
    void deleteChatMessages(String currentUserId, String otherUserId);

    // 删除特定群组的所有消息
    @Query("DELETE FROM messages WHERE toId = :groupId AND isGroupMessage = 1")
    void deleteGroupMessages(String groupId);

    // 删除所有消息
    @Query("DELETE FROM messages")
    void deleteAllMessages();

    // 更新消息状态
    @Query("UPDATE messages SET status = :status WHERE messageId = :messageId")
    void updateMessageStatus(String messageId, int status);
}