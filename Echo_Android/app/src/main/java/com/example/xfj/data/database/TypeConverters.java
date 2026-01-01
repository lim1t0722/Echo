package com.example.xfj.data.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Room类型转换器，用于处理不直接支持的数据类型
 */
public class TypeConverters {

    private static Gson gson = new Gson();

    /**
     * 将List<String>转换为JSON字符串
     */
    @TypeConverter
    public static String fromStringList(List<String> strings) {
        if (strings == null) {
            return null;
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(strings, type);
    }

    /**
     * 将JSON字符串转换为List<String>
     */
    @TypeConverter
    public static List<String> toStringList(String json) {
        if (json == null) {
            return null;
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * 将Message对象转换为JSON字符串
     */
    @TypeConverter
    public static String fromMessage(com.example.xfj.model.Message message) {
        if (message == null) {
            return null;
        }
        return gson.toJson(message);
    }

    /**
     * 将JSON字符串转换为Message对象
     */
    @TypeConverter
    public static com.example.xfj.model.Message toMessage(String json) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, com.example.xfj.model.Message.class);
    }

    /**
     * 将User对象转换为JSON字符串
     */
    @TypeConverter
    public static String fromUser(com.example.xfj.model.User user) {
        if (user == null) {
            return null;
        }
        return gson.toJson(user);
    }

    /**
     * 将JSON字符串转换为User对象
     */
    @TypeConverter
    public static com.example.xfj.model.User toUser(String json) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, com.example.xfj.model.User.class);
    }

    /**
     * 将Date转换为Long类型的时间戳
     */
    @TypeConverter
    public static Long fromDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    /**
     * 将Long类型的时间戳转换为Date
     */
    @TypeConverter
    public static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }
}
