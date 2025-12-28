package com.example.xfj.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message implements Parcelable {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_FILE = 5;
    public static final int TYPE_SYSTEM = 0;

    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_DELIVERED = 2;
    public static final int STATUS_READ = 3;
    public static final int STATUS_FAILED = -1;

    @PrimaryKey
    @NonNull
    private String messageId;
    private String fromUserId;
    private String toId; // could be userId or groupId
    private int type; // 0: system, 1: text, 2: image, 3: video, 4: audio, 5: file
    private String content;
    private long timestamp;
    private int status; // 0: sending, 1: sent, 2: delivered, 3: read, -1: failed
    private boolean isGroupMessage;
    private String senderNickname;
    private String senderAvatar;
    private boolean isSent; // whether this message was sent by current user

    public Message() {
    }

    protected Message(Parcel in) {
        messageId = in.readString();
        fromUserId = in.readString();
        toId = in.readString();
        type = in.readInt();
        content = in.readString();
        timestamp = in.readLong();
        status = in.readInt();
        isGroupMessage = in.readByte() != 0;
        senderNickname = in.readString();
        senderAvatar = in.readString();
        isSent = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getFormattedTime() {
        // Simple formatting for demo, in real app use SimpleDateFormat or DateTimeFormatter
        long hours = (timestamp / 1000 / 60 / 60) % 24;
        long minutes = (timestamp / 1000 / 60) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(messageId);
        parcel.writeString(fromUserId);
        parcel.writeString(toId);
        parcel.writeInt(type);
        parcel.writeString(content);
        parcel.writeLong(timestamp);
        parcel.writeInt(status);
        parcel.writeByte((byte) (isGroupMessage ? 1 : 0));
        parcel.writeString(senderNickname);
        parcel.writeString(senderAvatar);
        parcel.writeByte((byte) (isSent ? 1 : 0));
    }
}