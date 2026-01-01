package com.example.xfj.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.List;

@Entity(tableName = "conversations")
@TypeConverters(com.example.xfj.data.database.TypeConverters.class)
public class Conversation {
    public static final int TYPE_SINGLE = 0;
    public static final int TYPE_GROUP = 1;

    @NonNull
    @PrimaryKey
    private String conversationId;
    private int type;
    private User otherUser;
    private Message lastMessage;
    private int unreadCount;
    private Date updateTime;
    private boolean isGroup;
    private boolean isPinned;
    private boolean isMuted;
    private String name;
    private String avatarUrl;
    private List<String> participantIds;

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    // 用于显示对话名称
    public String getName() {
        if (otherUser != null) {
            return otherUser.getNickname();
        }
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public void setUpdateTime(long timestamp) {
        this.updateTime = new Date(timestamp);
    }
}