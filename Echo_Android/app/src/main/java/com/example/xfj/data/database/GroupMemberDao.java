package com.example.xfj.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.xfj.model.GroupMember;

import java.util.List;

@Dao
public interface GroupMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroupMember groupMember);

    @Update
    void update(GroupMember groupMember);

    @Delete
    void delete(GroupMember groupMember);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    LiveData<List<GroupMember>> getMembersByGroupId(String groupId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId")
    GroupMember getMemberByGroupIdAndUserId(String groupId, String userId);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND role = :role")
    List<GroupMember> getMembersByRole(String groupId, String role);

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND isOnline = 1")
    List<GroupMember> getOnlineGroupMembers(String groupId);

    @Query("UPDATE group_members SET isOnline = :isOnline WHERE groupId = :groupId AND userId = :userId")
    void updateOnlineStatus(String groupId, String userId, boolean isOnline);

    @Query("UPDATE group_members SET role = :role WHERE groupId = :groupId AND userId = :userId")
    void updateMemberRole(String groupId, String userId, String role);

    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    void deleteAllMembersByGroupId(String groupId);

    @Query("SELECT COUNT(*) FROM group_members WHERE groupId = :groupId")
    int getGroupMemberCount(String groupId);
}