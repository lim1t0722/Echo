package com.example.xfj.network;

import com.example.xfj.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // 用户相关接口
    @POST("/api/auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);

    @POST("/api/send_email")
    Call<ApiResponse<Void>> sendVerificationCode(@Body VerificationCodeRequest request);

    // 请求体类
    class LoginRequest {
        private String email;
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    class RegisterRequest {
        private String email;
        private String password;
        private String verificationCode;
        private String nickname;

        public RegisterRequest(String email, String password, String verificationCode, String nickname) {
            this.email = email;
            this.password = password;
            this.verificationCode = verificationCode;
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
        private String email;

        public VerificationCodeRequest(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
