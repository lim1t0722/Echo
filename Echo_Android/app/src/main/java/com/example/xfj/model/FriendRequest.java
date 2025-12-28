package com.example.xfj.model;

public class FriendRequest {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final int STATUS_CANCELED = 3;

    private String requestId;
    private String fromUserId;
    private String toUserId;
    private String fromUserNickname;
    private String fromUserPhone;
    private long timestamp;
    private int status;
    private String message;

    public FriendRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUserNickname() {
        return fromUserNickname;
    }

    public void setFromUserNickname(String fromUserNickname) {
        this.fromUserNickname = fromUserNickname;
    }

    public String getFromUserPhone() {
        return fromUserPhone;
    }

    public void setFromUserPhone(String fromUserPhone) {
        this.fromUserPhone = fromUserPhone;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}