package com.example.xfj.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.ChatActivity;
import com.example.xfj.R;
import com.example.xfj.model.Conversation;
import com.example.xfj.model.Message;
import com.example.xfj.model.User;
import com.example.xfj.ui.chat.ChatListAdapter.ChatViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListFragment extends Fragment {
    private RecyclerView recyclerViewChatList;
    private ChatListAdapter chatListAdapter;
    private List<Conversation> conversations;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化模拟数据
        initMockData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerViewChatList = view.findViewById(R.id.recyclerViewChatList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置RecyclerView的布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewChatList.setLayoutManager(layoutManager);

        // 创建并设置适配器
        chatListAdapter = new ChatListAdapter(conversations);
        // 设置点击事件监听器
        chatListAdapter.setOnItemClickListener(conversation -> {
            // 跳转到 ChatActivity
            Intent intent = new Intent(getContext(), ChatActivity.class);
            // 传递会话信息
            intent.putExtra("chat_id", conversation.getConversationId());
            intent.putExtra("chat_name", conversation.getName());
            intent.putExtra("is_group_chat", conversation.getType() == Conversation.TYPE_GROUP);
            startActivity(intent);
        });
        recyclerViewChatList.setAdapter(chatListAdapter);
    }

    // 初始化模拟数据
    private void initMockData() {
        conversations = new ArrayList<>();

        // 创建模拟用户
        User user1 = new User();
        user1.setUserId("user_1234567890");
        user1.setPhone("12345678901");
        user1.setNickname("张三");

        User user2 = new User();
        user2.setUserId("user_0987654321");
        user2.setPhone("12345678902");
        user2.setNickname("李四");

        User user3 = new User();
        user3.setUserId("user_5555555555");
        user3.setPhone("12345678903");
        user3.setNickname("王五");

        // 创建模拟消息
        Message message1 = new Message();
        message1.setMessageId("msg_1");
        message1.setFromUserId(user1.getUserId());
        message1.setToId(user2.getUserId());
        message1.setType(Message.TYPE_TEXT);
        message1.setContent("你好，在吗？");
        message1.setTimestamp(System.currentTimeMillis());
        message1.setStatus(Message.STATUS_READ);
        message1.setGroupMessage(false);
        message1.setSenderNickname(user1.getNickname());

        Message message2 = new Message();
        message2.setMessageId("msg_2");
        message2.setFromUserId(user2.getUserId());
        message2.setToId(user1.getUserId());
        message2.setType(Message.TYPE_TEXT);
        message2.setContent("在的，有什么事吗？");
        message2.setTimestamp(System.currentTimeMillis() - 3600000); // 1小时前
        message2.setStatus(Message.STATUS_READ);
        message2.setGroupMessage(false);
        message2.setSenderNickname(user2.getNickname());

        Message message3 = new Message();
        message3.setMessageId("msg_3");
        message3.setFromUserId(user3.getUserId());
        message3.setToId(user1.getUserId());
        message3.setType(Message.TYPE_TEXT);
        message3.setContent("今天一起吃饭吗？");
        message3.setTimestamp(System.currentTimeMillis() - 7200000); // 2小时前
        message3.setStatus(Message.STATUS_SENT);
        message3.setGroupMessage(false);
        message3.setSenderNickname(user3.getNickname());

        // 创建模拟会话
        Conversation conversation1 = new Conversation();
        conversation1.setConversationId("conv_1");
        conversation1.setType(Conversation.TYPE_SINGLE);
        conversation1.setName(user2.getNickname());
        conversation1.setAvatarUrl(null);
        List<String> participants1 = new ArrayList<>();
        participants1.add(user1.getUserId());
        participants1.add(user2.getUserId());
        conversation1.setParticipantIds(participants1);
        conversation1.setLastMessage(message2);
        conversation1.setUpdateTime(message2.getTimestamp());
        conversation1.setUnreadCount(0);
        conversation1.setMuted(false);
        conversation1.setPinned(true);

        Conversation conversation2 = new Conversation();
        conversation2.setConversationId("conv_2");
        conversation2.setType(Conversation.TYPE_SINGLE);
        conversation2.setName(user3.getNickname());
        conversation2.setAvatarUrl(null);
        List<String> participants2 = new ArrayList<>();
        participants2.add(user1.getUserId());
        participants2.add(user3.getUserId());
        conversation2.setParticipantIds(participants2);
        conversation2.setLastMessage(message3);
        conversation2.setUpdateTime(message3.getTimestamp());
        conversation2.setUnreadCount(2);
        conversation2.setMuted(false);
        conversation2.setPinned(false);

        // 添加会话到列表
        conversations.add(conversation1);
        conversations.add(conversation2);
    }
}
