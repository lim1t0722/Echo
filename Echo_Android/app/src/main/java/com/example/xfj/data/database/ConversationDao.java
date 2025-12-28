package com.example.xfj.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.xfj.model.Conversation;

import java.util.List;

@Dao
public interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Conversation conversation);

    @Update
    void update(Conversation conversation);

    @Delete
    void delete(Conversation conversation);

    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, updateTime DESC")
    LiveData<List<Conversation>> getAllConversations();

    @Query("SELECT * FROM conversations WHERE conversationId = :conversationId")
    Conversation getConversationById(String conversationId);

    @Query("UPDATE conversations SET unreadCount = unreadCount + 1 WHERE conversationId = :conversationId")
    void incrementUnreadCount(String conversationId);

    @Query("UPDATE conversations SET unreadCount = 0 WHERE conversationId = :conversationId")
    void clearUnreadCount(String conversationId);

    @Query("UPDATE conversations SET lastMessage = :lastMessage, updateTime = :updateTime WHERE conversationId = :conversationId")
    void updateLastMessage(String conversationId, Object lastMessage, long updateTime);

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE conversationId = :conversationId")
    void updatePinnedStatus(String conversationId, boolean isPinned);

    @Query("UPDATE conversations SET isMuted = :isMuted WHERE conversationId = :conversationId")
    void updateMuteStatus(String conversationId, boolean isMuted);
}