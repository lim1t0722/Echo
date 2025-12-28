package com.example.xfj.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.R;
import com.example.xfj.model.GroupMember;

import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private List<GroupMember> groupMembers;
    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onRemoveMember(GroupMember member);
    }

    public GroupMemberAdapter(List<GroupMember> groupMembers, OnMemberClickListener listener) {
        this.groupMembers = groupMembers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_member, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberViewHolder holder, int position) {
        GroupMember member = groupMembers.get(position);
        holder.bind(member, listener);
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    public static class GroupMemberViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNickname;
        private TextView tvRole;
        private TextView tvPhone;
        private Button btnRemove;

        public GroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(GroupMember member, OnMemberClickListener listener) {
            tvNickname.setText(member.getNickname());
            tvPhone.setText(member.getPhone());

            // 设置角色显示
            if (member.isOwner()) {
                tvRole.setText("群主");
                tvRole.setVisibility(View.VISIBLE);
            } else if (member.isAdmin()) {
                tvRole.setText("管理员");
                tvRole.setVisibility(View.VISIBLE);
            } else {
                tvRole.setVisibility(View.GONE);
            }

            // 根据当前用户角色决定是否显示移除按钮
            if (member.isOwner() || member.isAdmin()) {
                btnRemove.setVisibility(View.VISIBLE);
                btnRemove.setOnClickListener(v -> listener.onRemoveMember(member));
            } else {
                btnRemove.setVisibility(View.GONE);
            }
        }
    }
}
