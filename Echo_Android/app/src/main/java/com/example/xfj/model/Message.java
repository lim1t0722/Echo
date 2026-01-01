package com.example.xfj.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "messages")
public class Message {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VOICE = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_FILE = 5;

    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_READ = 3;
    public static final int STATUS_FAILED = 4;

    @NonNull
    @PrimaryKey
    private String messageId;
    private String fromUserId;
    private String toId;
    private String content;
    private int type;
    private long timestamp;
    private boolean isRead;
    private int status;
    private boolean isGroupMessage;
    private String senderNickname;

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isGroupMessage() {
        return isGroupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        isGroupMessage = groupMessage;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    // DataBinding需要的isSent方法
    public boolean isSent() {
        // 这里可以根据实际情况实现，比如比较fromUserId和当前用户ID
        // 暂时返回true作为示例
        return true;
    }

    // DataBinding需要的formattedTime属性
    @Ignore
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }
}