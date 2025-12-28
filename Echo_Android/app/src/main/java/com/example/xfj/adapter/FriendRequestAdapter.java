package com.example.xfj.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xfj.R;
import com.example.xfj.model.FriendRequest;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(FriendRequest request);
        void onReject(FriendRequest request);
    }

    private List<FriendRequest> requests;
    private OnRequestActionListener listener;

    public FriendRequestAdapter(List<FriendRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.bind(request, listener);
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvPhone;
        private Button btnAccept;
        private Button btnReject;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }

        public void bind(FriendRequest request, OnRequestActionListener listener) {
            tvNickname.setText(request.getFromUserNickname());
            tvPhone.setText(request.getFromUserPhone());

            btnAccept.setOnClickListener(v -> listener.onAccept(request));
            btnReject.setOnClickListener(v -> listener.onReject(request));
        }
    }
}