package com.example.xfj.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.xfj.model.GroupMember;

import java.util.List;

/**
 * 群组成员数据访问接口
 */
@Dao
public interface GroupMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroupMember member);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GroupMember> members);

    @Update
    void update(GroupMember member);

    @Delete
    void delete(GroupMember member);

    // 获取特定群组的所有成员
    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    List<GroupMember> getGroupMembers(String groupId);

    // 获取特定群组的特定成员
    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId")
    GroupMember getGroupMember(String groupId, String userId);

    // 获取特定群组的管理员和群主
    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND (role = 'owner' OR role = 'admin')")
    List<GroupMember> getGroupAdmins(String groupId);

    // 获取特定群组的普通成员
    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND role = 'member'")
    List<GroupMember> getGroupRegularMembers(String groupId);

    // 获取特定群组的在线成员
    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND isOnline = 1")
    List<GroupMember> getOnlineGroupMembers(String groupId);

    // 更新成员的在线状态
    @Query("UPDATE group_members SET isOnline = :isOnline WHERE groupId = :groupId AND userId = :userId")
    void updateMemberOnlineStatus(String groupId, String userId, boolean isOnline);

    // 更新成员的角色
    @Query("UPDATE group_members SET role = :role WHERE groupId = :groupId AND userId = :userId")
    void updateMemberRole(String groupId, String userId, String role);

    // 更新成员的昵称
    @Query("UPDATE group_members SET nickname = :nickname WHERE groupId = :groupId AND userId = :userId")
    void updateMemberNickname(String groupId, String userId, String nickname);

    // 从群组中移除成员
    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    void removeGroupMember(String groupId, String userId);

    // 删除特定群组的所有成员
    @Query("DELETE FROM group_members WHERE groupId = :groupId")
    void deleteAllGroupMembers(String groupId);

    // 获取用户加入的所有群组
    @Query("SELECT groupId FROM group_members WHERE userId = :userId")
    List<String> getUserGroups(String userId);

    // 获取群组的成员数量
    @Query("SELECT COUNT(*) FROM group_members WHERE groupId = :groupId")
    int getGroupMemberCount(String groupId);
}
