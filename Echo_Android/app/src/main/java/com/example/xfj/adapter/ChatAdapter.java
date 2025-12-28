package com.example.xfj.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.xfj.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.databinding.ItemMessageBinding;
import com.example.xfj.model.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
        
        // 为新添加的消息应用淡入动画
        if (position == messages.size() - 1) {
            holder.itemView.clearAnimation();
            android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(
                    holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.startAnimation(animation);
        }
    }
    
    @Override
    public void onViewRecycled(@NonNull MessageViewHolder holder) {
        super.onViewRecycled(holder);
        // 清除动画，避免复用视图时出现问题
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void addMessages(List<Message> newMessages) {
        int startPosition = messages.size();
        messages.addAll(newMessages);
        notifyItemRangeInserted(startPosition, newMessages.size());
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemMessageBinding binding;

        MessageViewHolder(@NonNull ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
        }
    }
}
