package com.example.xfj.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.R;
import com.example.xfj.model.Conversation;
import com.example.xfj.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final List<Conversation> conversations;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Conversation conversation);
    }

    public ChatListAdapter(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation, listener);
        
        // 为新添加的对话应用淡入动画
        if (position == conversations.size() - 1) {
            holder.itemView.clearAnimation();
            android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(
                    holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.startAnimation(animation);
        }
    }
    
    @Override
    public void onViewRecycled(@NonNull ChatViewHolder holder) {
        super.onViewRecycled(holder);
        // 清除动画，避免复用视图时出现问题
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvLastMessage;
        private final TextView tvTime;
        private final TextView tvUnreadCount;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.imageViewAvatar);
            tvName = itemView.findViewById(R.id.textViewName);
            tvLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            tvTime = itemView.findViewById(R.id.textViewTime);
            tvUnreadCount = itemView.findViewById(R.id.textViewUnreadCount);
        }

        public void bind(Conversation conversation, OnItemClickListener listener) {
            // 设置会话名称
            tvName.setText(conversation.getName());

            // 设置最后一条消息
            Message lastMessage = conversation.getLastMessage();
            if (lastMessage != null) {
                tvLastMessage.setText(lastMessage.getContent());

                // 设置消息时间
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                tvTime.setText(sdf.format(lastMessage.getTimestamp()));
            }

            // 设置未读消息数
            int unreadCount = conversation.getUnreadCount();
            if (unreadCount > 0) {
                tvUnreadCount.setVisibility(View.VISIBLE);
                tvUnreadCount.setText(String.valueOf(unreadCount));
            } else {
                tvUnreadCount.setVisibility(View.GONE);
            }

            // 设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(conversation);
                    }
                }
            });
        }
    }
}
