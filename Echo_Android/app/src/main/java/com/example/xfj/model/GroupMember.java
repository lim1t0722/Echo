package com.example.xfj.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_members", primaryKeys = {"groupId", "userId"})
public class GroupMember {
    public static final String ROLE_OWNER = "owner";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MEMBER = "member";

    @NonNull
    private String groupId;
    @NonNull
    private String userId;
    private String nickname;
    private String phone;
    private String role;
    private boolean isOnline;

    @Ignore
    public GroupMember() {
    }

    @Ignore
    public GroupMember(String userId, String nickname, String phone, String role) {
        this.userId = userId;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
    }

    public GroupMember(@NonNull String groupId, @NonNull String userId, String nickname, String phone, String role, boolean isOnline) {
        this.groupId = groupId;
        this.userId = userId;
        this.nickname = nickname;
        this.phone = phone;
        this.role = role;
        this.isOnline = isOnline;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOwner() {
        return ROLE_OWNER.equals(role);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isMember() {
        return ROLE_MEMBER.equals(role);
    }
}