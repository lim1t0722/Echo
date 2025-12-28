package com.example.xfj.network;

import com.example.xfj.model.Conversation;
import com.example.xfj.model.Message;
import com.example.xfj.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // 用户相关接口
    @POST("/api/auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);

    @POST("/api/send_sms")
    Call<ApiResponse<Void>> sendVerificationCode(@Body VerificationCodeRequest request);

    @GET("/api/users/{userId}")
    Call<ApiResponse<User>> getUserInfo(@Path("userId") String userId);

    @GET("/api/users/search")
    Call<ApiResponse<List<User>>> searchUsers(@Query("keyword") String keyword);

    // 会话相关接口
    @GET("/api/conversations")
    Call<ApiResponse<List<Conversation>>> getConversations();

    @GET("/api/conversations/{conversationId}/messages")
    Call<ApiResponse<List<Message>>> getMessages(@Path("conversationId") String conversationId,
                                                 @Query("page") int page,
                                                 @Query("size") int size);

    @POST("/api/messages/send")
    Call<ApiResponse<Message>> sendMessage(@Body SendMessageRequest request);

    // 好友相关接口
    @POST("/api/friends/add")
    Call<ApiResponse<Void>> addFriend(@Body AddFriendRequest request);

    @GET("/api/friends")
    Call<ApiResponse<List<User>>> getFriends();

    // 请求体类
    class LoginRequest {
        private String phone;
        private String password;

        public LoginRequest(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    class RegisterRequest {
        private String phone;
        private String password;
        private String verificationCode;
        private String nickname;

        public RegisterRequest(String phone, String password, String verificationCode, String nickname) {
            this.phone = phone;
            this.password = password;
            this.verificationCode = verificationCode;
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    class VerificationCodeRequest {
        private String phone;

        public VerificationCodeRequest(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    class SendMessageRequest {
        private String toId;
        private int type;
        private String content;
        private boolean isGroupMessage;

        public SendMessageRequest(String toId, int type, String content, boolean isGroupMessage) {
            this.toId = toId;
            this.type = type;
            this.content = content;
            this.isGroupMessage = isGroupMessage;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isGroupMessage() {
            return isGroupMessage;
        }

        public void setGroupMessage(boolean groupMessage) {
            isGroupMessage = groupMessage;
        }
    }

    class AddFriendRequest {
        private String userId;
        private String message;

        public AddFriendRequest(String userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
