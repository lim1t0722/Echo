package com.example.xfj.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")

public class User implements Parcelable {
    @PrimaryKey
    @NonNull
    private String userId;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private String status;
    private long createTime;
    private long lastLoginTime;

    public User() {
    }

    protected User(Parcel in) {
        userId = in.readString();
        phone = in.readString();
        nickname = in.readString();
        avatarUrl = in.readString();
        status = in.readString();
        createTime = in.readLong();
        lastLoginTime = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(phone);
        parcel.writeString(nickname);
        parcel.writeString(avatarUrl);
        parcel.writeString(status);
        parcel.writeLong(createTime);
        parcel.writeLong(lastLoginTime);
    }
}