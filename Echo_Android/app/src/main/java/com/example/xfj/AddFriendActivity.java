package com.example.xfj;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.xfj.network.ApiResponse;
import com.example.xfj.network.ApiService;
import com.example.xfj.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private LinearLayout layoutSearchResult;
    private ImageView ivAvatar;
    private TextView tvNickname;
    private Button btnAddFriend;
    private RecyclerView rvFriendRequests;

    private List<FriendRequest> friendRequests;
    private FriendRequestAdapter friendRequestAdapter;
    private User searchResultUser;
    private boolean isFromGroup;
    private String groupId;
    
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // 获取参数
        isFromGroup = getIntent().getBooleanExtra("from_group", false);
        groupId = getIntent().getStringExtra("group_id");

        // 初始化API服务和SharedPreferences
        apiService = RetrofitClient.getInstance(this).getApiService();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

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
        request1.setTimestamp(System.currentTimeMillis() - 3600000);
        request1.setStatus(FriendRequest.STATUS_PENDING);
        request1.setMessage("你好，我是赵六");
        friendRequests.add(request1);

        FriendRequest request2 = new FriendRequest();
        request2.setRequestId("req_2");
        request2.setFromUserId("user_456");
        request2.setToUserId("user_123");
        request2.setFromUserNickname("孙七");
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
            // 真实搜索用户
            searchUser(keyword);
        });

        // 添加好友按钮点击事件
        btnAddFriend.setOnClickListener(v -> {
            if (searchResultUser != null) {
                // 真实发送好友请求
                sendFriendRequest(searchResultUser);
            }
        });
    }

    private void searchUser(String keyword) {
        // 调用API搜索用户
        Call<ApiResponse<List<User>>> call = apiService.searchUsers(keyword);
        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<User> users = response.body().getData();
                    if (users != null && !users.isEmpty()) {
                        // 显示第一个搜索结果
                        searchResultUser = users.get(0);
                        tvNickname.setText(searchResultUser.getNickname());
                        layoutSearchResult.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(AddFriendActivity.this, "未找到用户", Toast.LENGTH_SHORT).show();
                        layoutSearchResult.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(AddFriendActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                    layoutSearchResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(AddFriendActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                layoutSearchResult.setVisibility(View.GONE);
            }
        });
    }

    private void sendFriendRequest(User user) {
        if (user == null) return;
        
        // 获取当前用户ID
        String currentUserId = sharedPreferences.getString("user_id", "");
        
        // 前端验证规则
        if (currentUserId.equals(user.getUserId())) {
            Toast.makeText(this, "不能添加自己为好友", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFromGroup) {
            // 模拟邀请成员入群（实际项目中应该调用API）
            Toast.makeText(this, "已邀请" + user.getNickname() + "加入群组", Toast.LENGTH_SHORT).show();
        } else {
            // 发送好友请求API
            Call<ApiResponse<Void>> call = apiService.addFriend(new ApiService.AddFriendRequest(user.getUserId(), "你好，我想添加你为好友"));
            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            Toast.makeText(AddFriendActivity.this, "好友请求已发送", Toast.LENGTH_SHORT).show();
                        } else {
                            // 服务器返回错误信息
                            Toast.makeText(AddFriendActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddFriendActivity.this, "发送失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(AddFriendActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }
            });
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