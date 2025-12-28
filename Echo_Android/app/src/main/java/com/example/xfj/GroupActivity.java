package com.example.xfj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.adapter.GroupMemberAdapter;
import com.example.xfj.model.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity implements GroupMemberAdapter.OnMemberClickListener {

    private RecyclerView rvMembers;
    private GroupMemberAdapter memberAdapter;
    private List<GroupMember> groupMembers;
    private TextView tvGroupName;
    private TextView tvMemberCount;
    private Button btnInviteMember;
    private Button btnLeaveGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // 获取群组信息（实际应用中应该从Intent或API获取）
        String groupId = getIntent().getStringExtra("group_id");
        String groupName = getIntent().getStringExtra("group_name");
        int memberCount = getIntent().getIntExtra("member_count", 0);

        initViews();
        initData(groupId, groupName, memberCount);
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        setTitle("群聊管理");
        rvMembers = findViewById(R.id.rv_group_members);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvMemberCount = findViewById(R.id.tv_group_member_count);
        btnInviteMember = findViewById(R.id.btn_invite_member);
        btnLeaveGroup = findViewById(R.id.btn_leave_group);
    }

    private void initData(String groupId, String groupName, int memberCount) {
        // 设置群组信息
        tvGroupName.setText(groupName != null ? groupName : "我的群组");
        tvMemberCount.setText("成员数量: " + (memberCount > 0 ? memberCount : 3));

        // 初始化模拟群成员数据
        groupMembers = new ArrayList<>();
        groupMembers.add(new GroupMember("1", "张三", "13800138000", GroupMember.ROLE_OWNER));
        groupMembers.add(new GroupMember("2", "李四", "13900139000", GroupMember.ROLE_ADMIN));
        groupMembers.add(new GroupMember("3", "王五", "13700137000", GroupMember.ROLE_MEMBER));
    }

    private void setupRecyclerView() {
        memberAdapter = new GroupMemberAdapter(groupMembers, this);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));
        rvMembers.setAdapter(memberAdapter);
    }

    private void setupListeners() {
        btnInviteMember.setOnClickListener(v -> {
            // 跳转到添加好友/邀请成员界面
            Intent intent = new Intent(GroupActivity.this, AddFriendActivity.class);
            intent.putExtra("from_group", true);
            startActivity(intent);
        });

        btnLeaveGroup.setOnClickListener(v -> {
            // 模拟退出群组
            Toast.makeText(this, "已退出群组", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onRemoveMember(GroupMember member) {
        // 模拟移除群成员
        groupMembers.remove(member);
        memberAdapter.notifyDataSetChanged();
        tvMemberCount.setText("成员数量: " + groupMembers.size());
        Toast.makeText(this, "已移除成员: " + member.getNickname(), Toast.LENGTH_SHORT).show();
    }

    // 静态方法用于启动该Activity
    public static void start(AppCompatActivity activity, String groupId, String groupName, int memberCount) {
        Intent intent = new Intent(activity, GroupActivity.class);
        intent.putExtra("group_id", groupId);
        intent.putExtra("group_name", groupName);
        intent.putExtra("member_count", memberCount);
        activity.startActivity(intent);
    }
    
    // 简化的静态启动方法
    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity, GroupActivity.class);
        // 默认群组信息（实际应用中应该传递真实群组ID）
        intent.putExtra("group_id", "group_1");
        intent.putExtra("group_name", "默认群组");
        intent.putExtra("member_count", 3);
        activity.startActivity(intent);
    }
}
