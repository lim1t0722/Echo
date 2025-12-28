package com.example.xfj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.adapter.FriendRequestAdapter;
import com.example.xfj.model.FriendRequest;
import com.example.xfj.model.User;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private LinearLayout layoutSearchResult;
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvPhone;
    private Button btnAddFriend;
    private RecyclerView rvFriendRequests;

    private List<FriendRequest> friendRequests;
    private FriendRequestAdapter friendRequestAdapter;
    private User searchResultUser;
    private boolean isFromGroup;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // 获取参数
        isFromGroup = getIntent().getBooleanExtra("from_group", false);
        groupId = getIntent().getStringExtra("group_id");

        initViews();
        initFriendRequests();
        setupListeners();
    }

    private void initViews() {
        // 根据是否来自群组设置不同标题
        if (isFromGroup) {
            setTitle("邀请成员");
        } else {
            setTitle("添加好友");
        }
        
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        layoutSearchResult = findViewById(R.id.layout_search_result);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvPhone = findViewById(R.id.tv_phone);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        rvFriendRequests = findViewById(R.id.rv_friend_requests);
        
        // 根据是否来自群组设置不同按钮文本
        if (isFromGroup) {
            btnAddFriend.setText("邀请加入");
        }
    }

    private void initFriendRequests() {
        // 如果来自群组，隐藏好友请求列表
        if (isFromGroup) {
            rvFriendRequests.setVisibility(View.GONE);
            return;
        }
        
        // 初始化模拟的好友请求数据
        friendRequests = new ArrayList<>();

        FriendRequest request1 = new FriendRequest();
        request1.setRequestId("req_1");
        request1.setFromUserId("user_789");
        request1.setToUserId("user_123");
        request1.setFromUserNickname("赵六");
        request1.setFromUserPhone("12345678907");
        request1.setTimestamp(System.currentTimeMillis() - 3600000);
        request1.setStatus(FriendRequest.STATUS_PENDING);
        request1.setMessage("你好，我是赵六");
        friendRequests.add(request1);

        FriendRequest request2 = new FriendRequest();
        request2.setRequestId("req_2");
        request2.setFromUserId("user_456");
        request2.setToUserId("user_123");
        request2.setFromUserNickname("孙七");
        request2.setFromUserPhone("12345678908");
        request2.setTimestamp(System.currentTimeMillis() - 7200000);
        request2.setStatus(FriendRequest.STATUS_PENDING);
        request2.setMessage("很高兴认识你");
        friendRequests.add(request2);

        // 设置好友请求列表
        friendRequestAdapter = new FriendRequestAdapter(friendRequests, new FriendRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(FriendRequest request) {
                handleAcceptRequest(request);
            }

            @Override
            public void onReject(FriendRequest request) {
                handleRejectRequest(request);
            }
        });

        rvFriendRequests.setLayoutManager(new LinearLayoutManager(this));
        rvFriendRequests.setAdapter(friendRequestAdapter);
    }

    private void setupListeners() {
        // 搜索按钮点击事件
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                return;
            }
            // 模拟搜索用户
            simulateSearchUser(keyword);
        });

        // 添加好友按钮点击事件
        btnAddFriend.setOnClickListener(v -> {
            if (searchResultUser != null) {
                // 模拟发送好友请求
                simulateSendFriendRequest(searchResultUser);
            }
        });
    }

    private void simulateSearchUser(String keyword) {
        // 模拟搜索结果（实际项目中应该调用API）
        searchResultUser = new User();
        searchResultUser.setUserId("user_111");
        searchResultUser.setPhone("12345678904");
        searchResultUser.setNickname("测试用户");

        // 显示搜索结果
        tvNickname.setText(searchResultUser.getNickname());
        tvPhone.setText(searchResultUser.getPhone());
        layoutSearchResult.setVisibility(View.VISIBLE);
    }

    private void simulateSendFriendRequest(User user) {
        // 根据是否来自群组执行不同操作
        if (isFromGroup) {
            // 模拟邀请成员入群
            Toast.makeText(this, "已邀请" + user.getNickname() + "加入群组", Toast.LENGTH_SHORT).show();
        } else {
            // 模拟发送好友请求
            Toast.makeText(this, "好友请求已发送", Toast.LENGTH_SHORT).show();
        }

        // 隐藏搜索结果
        layoutSearchResult.setVisibility(View.GONE);
        etSearch.setText("");
    }

    private void handleAcceptRequest(FriendRequest request) {
        // 模拟接受好友请求（实际项目中应该调用API）
        request.setStatus(FriendRequest.STATUS_ACCEPTED);
        friendRequestAdapter.notifyDataSetChanged();
        Toast.makeText(this, "已接受好友请求", Toast.LENGTH_SHORT).show();
    }

    private void handleRejectRequest(FriendRequest request) {
        // 模拟拒绝好友请求（实际项目中应该调用API）
        request.setStatus(FriendRequest.STATUS_REJECTED);
        friendRequestAdapter.notifyDataSetChanged();
        Toast.makeText(this, "已拒绝好友请求", Toast.LENGTH_SHORT).show();
    }

    public static void start(android.content.Context context) {
        Intent intent = new Intent(context, AddFriendActivity.class);
        context.startActivity(intent);
    }
}