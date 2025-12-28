package com.example.xfj.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.xfj.model.Conversation;

import java.util.List;

/**
 * 对话数据访问接口
 */
@Dao
public interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Conversation conversation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Conversation> conversations);

    @Update
    void update(Conversation conversation);

    @Delete
    void delete(Conversation conversation);

    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId")
    Conversation getConversationById(String conversationId);

    // 获取所有对话，按更新时间倒序排列
    @Query("SELECT * FROM conversations ORDER BY updateTime DESC")
    List<Conversation> getAllConversations();

    // 获取特定类型的对话（单聊或群聊）
    @Query("SELECT * FROM conversations WHERE type = :type ORDER BY updateTime DESC")
    List<Conversation> getConversationsByType(int type);

    // 获取置顶的对话
    @Query("SELECT * FROM conversations WHERE isPinned = 1 ORDER BY updateTime DESC")
    List<Conversation> getPinnedConversations();

    // 更新对话的未读消息数量
    @Query("UPDATE conversations SET unreadCount = :unreadCount WHERE conversationId = :conversationId")
    void updateUnreadCount(String conversationId, int unreadCount);

    // 增加未读消息数量
    @Query("UPDATE conversations SET unreadCount = unreadCount + 1 WHERE conversationId = :conversationId")
    void incrementUnreadCount(String conversationId);

    // 重置未读消息数量为0
    @Query("UPDATE conversations SET unreadCount = 0 WHERE conversationId = :conversationId")
    void resetUnreadCount(String conversationId);

    // 更新对话的置顶状态
    @Query("UPDATE conversations SET isPinned = :isPinned WHERE conversationId = :conversationId")
    void updatePinnedStatus(String conversationId, boolean isPinned);

    // 更新对话的静音状态
    @Query("UPDATE conversations SET isMuted = :isMuted WHERE conversationId = :conversationId")
    void updateMutedStatus(String conversationId, boolean isMuted);

    // 更新对话的最后一条消息
    @Query("UPDATE conversations SET lastMessage = :lastMessage, updateTime = :updateTime WHERE conversationId = :conversationId")
    void updateLastMessage(String conversationId, String lastMessage, long updateTime);

    // 删除特定对话
    @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
    void deleteConversationById(String conversationId);

    // 删除所有对话
    @Query("DELETE FROM conversations")
    void deleteAllConversations();
}
