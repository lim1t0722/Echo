package com.example.xfj.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.xfj.data.dao.UserDao;
import com.example.xfj.data.database.AppDatabase;
import com.example.xfj.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;
    private MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // 获取当前登录用户
    public LiveData<User> getCurrentUser() {
        return currentUserLiveData;
    }

    // 设置当前登录用户
    public void setCurrentUser(User user) {
        currentUserLiveData.setValue(user);
        // 保存到数据库
        executorService.execute(() -> {
            userDao.insert(user);
        });
    }

    // 从数据库获取用户
    public void getUserById(String userId, OnUserLoadedCallback callback) {
        executorService.execute(() -> {
            User user = userDao.getUserById(userId);
            if (callback != null) {
                callback.onUserLoaded(user);
            }
        });
    }

    // 从数据库获取用户（通过手机号）
    public void getUserByPhone(String phone, OnUserLoadedCallback callback) {
        executorService.execute(() -> {
            User user = userDao.getUserByPhone(phone);
            if (callback != null) {
                callback.onUserLoaded(user);
            }
        });
    }

    // 保存用户到数据库
    public void saveUser(User user) {
        executorService.execute(() -> {
            userDao.insert(user);
        });
    }

    // 保存多个用户到数据库
    public void saveAllUsers(List<User> users) {
        executorService.execute(() -> {
            userDao.insertAll(users);
        });
    }

    // 更新用户信息
    public void updateUser(User user) {
        executorService.execute(() -> {
            userDao.update(user);
        });
    }

    // 删除用户
    public void deleteUser(User user) {
        executorService.execute(() -> {
            userDao.delete(user);
        });
    }

    // 用户加载回调接口
    public interface OnUserLoadedCallback {
        void onUserLoaded(User user);
    }
}