package com.example.xfj.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "conversations")
public class Conversation implements Parcelable {
    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_GROUP = 2;

    @PrimaryKey
    @NonNull
    private String conversationId;
    private int type; // 1: single, 2: group
    private String name;
    private String avatarUrl;
    private List<String> participantIds;
    private Message lastMessage;
    private long updateTime;
    private int unreadCount;
    private boolean isMuted;
    private boolean isPinned;

    public Conversation() {
    }

    protected Conversation(Parcel in) {
        conversationId = in.readString();
        type = in.readInt();
        name = in.readString();
        avatarUrl = in.readString();
        participantIds = in.createStringArrayList();
        lastMessage = in.readParcelable(Message.class.getClassLoader());
        updateTime = in.readLong();
        unreadCount = in.readInt();
        isMuted = in.readByte() != 0;
        isPinned = in.readByte() != 0;
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
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

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(conversationId);
        parcel.writeInt(type);
        parcel.writeString(name);
        parcel.writeString(avatarUrl);
        parcel.writeStringList(participantIds);
        parcel.writeParcelable(lastMessage, i);
        parcel.writeLong(updateTime);
        parcel.writeInt(unreadCount);
        parcel.writeByte((byte) (isMuted ? 1 : 0));
        parcel.writeByte((byte) (isPinned ? 1 : 0));
    }
}