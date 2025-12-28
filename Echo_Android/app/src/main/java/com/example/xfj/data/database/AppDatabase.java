package com.example.xfj.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.xfj.data.dao.UserDao;
import com.example.xfj.data.dao.MessageDao;
import com.example.xfj.data.dao.ConversationDao;
import com.example.xfj.data.dao.GroupMemberDao;
import com.example.xfj.model.Conversation;
import com.example.xfj.model.GroupMember;
import com.example.xfj.model.Message;
import com.example.xfj.model.User;

@Database(entities = {User.class, Message.class, Conversation.class, GroupMember.class}, version = 1, exportSchema = false)
@TypeConverters(com.example.xfj.data.database.TypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "xfj.db";
    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract MessageDao messageDao();
    public abstract ConversationDao conversationDao();
    public abstract GroupMemberDao groupMemberDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() // 生产环境不建议使用，会在版本升级时删除所有数据
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}