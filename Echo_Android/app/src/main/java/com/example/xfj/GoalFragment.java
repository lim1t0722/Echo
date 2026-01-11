package com.example.xfj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GoalFragment extends Fragment {

    private ListView goalListView;
    private EditText goalTitleEditText;
    private EditText goalDescriptionEditText;
    private Button addGoalButton;
    private Button resetGoalButton;
    private Button filterGoalButton;
    private GoalAdapter goalAdapter;
    private ArrayList<Goal> goalList;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        goalListView = view.findViewById(R.id.goal_list_view);
        goalTitleEditText = view.findViewById(R.id.goal_title);
        goalDescriptionEditText = view.findViewById(R.id.goal_description);
        addGoalButton = view.findViewById(R.id.add_goal_button);
        resetGoalButton = view.findViewById(R.id.reset_goal_button);
        filterGoalButton = view.findViewById(R.id.filter_goal_button);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);

        // 加载目标列表
        goalList = loadGoals();

        // 初始化适配器
        goalAdapter = new GoalAdapter(getActivity(), goalList, goal -> {
            // 删除目标
            goalList.remove(goal);
            goalAdapter.notifyDataSetChanged();
            saveGoals();
            Toast.makeText(getActivity(), "目标已删除", Toast.LENGTH_SHORT).show();
        }, goal -> {
            // 切换目标完成状态
            goal.setCompleted(!goal.isCompleted());
            goalAdapter.notifyDataSetChanged();
            saveGoals();
            Toast.makeText(getActivity(), goal.isCompleted() ? "目标已完成" : "目标未完成", Toast.LENGTH_SHORT).show();
        });
        goalListView.setAdapter(goalAdapter);

        // 添加目标按钮点击事件
        addGoalButton.setOnClickListener(v -> {
            String title = goalTitleEditText.getText().toString().trim();
            String description = goalDescriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getActivity(), "请输入目标标题", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取当前时间作为开始时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // 创建新目标
            Goal newGoal = new Goal(title, description, currentDate, "", false);
            goalList.add(newGoal);
            goalAdapter.notifyDataSetChanged();
            saveGoals();

            // 清空输入框
            goalTitleEditText.setText("");
            goalDescriptionEditText.setText("");

            Toast.makeText(getActivity(), "目标已添加", Toast.LENGTH_SHORT).show();
        });

        // 重置按钮点击事件
        resetGoalButton.setOnClickListener(v -> {
            goalTitleEditText.setText("");
            goalDescriptionEditText.setText("");
        });

        // 筛选按钮点击事件
        filterGoalButton.setOnClickListener(v -> {
            // 简单实现：切换显示已完成/未完成目标
            boolean showCompleted = sharedPreferences.getBoolean("show_completed_goals", true);
            sharedPreferences.edit().putBoolean("show_completed_goals", !showCompleted).apply();
            filterGoalButton.setText(showCompleted ? "显示已完成" : "显示未完成");
            Toast.makeText(getActivity(), "已切换显示状态", Toast.LENGTH_SHORT).show();
        });

        // 设置筛选按钮初始状态
        boolean showCompleted = sharedPreferences.getBoolean("show_completed_goals", true);
        filterGoalButton.setText(showCompleted ? "显示已完成" : "显示未完成");

        return view;
    }

    // 保存目标列表到SharedPreferences
    private void saveGoals() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Goal goal : goalList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", goal.getTitle());
                jsonObject.put("description", goal.getDescription());
                jsonObject.put("startDate", goal.getStartDate());
                jsonObject.put("endDate", goal.getEndDate());
                jsonObject.put("completed", goal.isCompleted());
                jsonArray.put(jsonObject);
            }
            sharedPreferences.edit().putString("goal_list", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 从SharedPreferences加载目标列表
    private ArrayList<Goal> loadGoals() {
        ArrayList<Goal> goals = new ArrayList<>();
        String goalListJson = sharedPreferences.getString("goal_list", "[]");
        try {
            JSONArray jsonArray = new JSONArray(goalListJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                String startDate = jsonObject.getString("startDate");
                String endDate = jsonObject.getString("endDate");
                boolean completed = jsonObject.getBoolean("completed");
                goals.add(new Goal(title, description, startDate, endDate, completed));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return goals;
    }

    // 目标实体类
    public static class Goal {
        private String title;
        private String description;
        private String startDate;
        private String endDate;
        private boolean completed;

        public Goal(String title, String description, String startDate, String endDate, boolean completed) {
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.completed = completed;
        }

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // 目标适配器
    private static class GoalAdapter extends android.widget.BaseAdapter {
        private Context context;
        private ArrayList<Goal> goals;
        private OnDeleteGoalListener deleteListener;
        private OnToggleGoalListener toggleListener;

        public GoalAdapter(Context context, ArrayList<Goal> goals, OnDeleteGoalListener deleteListener, OnToggleGoalListener toggleListener) {
            this.context = context;
            this.goals = goals;
            this.deleteListener = deleteListener;
            this.toggleListener = toggleListener;
        }

        @Override
        public int getCount() { return goals.size(); }

        @Override
        public Object getItem(int position) { return goals.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.goal_item, parent, false);
            }

            Goal goal = goals.get(position);

            TextView titleTextView = convertView.findViewById(R.id.goal_item_title);
            TextView descriptionTextView = convertView.findViewById(R.id.goal_item_description);
            TextView dateTextView = convertView.findViewById(R.id.goal_item_date);
            Button deleteButton = convertView.findViewById(R.id.goal_item_delete);
            Button toggleButton = convertView.findViewById(R.id.goal_item_toggle);

            titleTextView.setText(goal.getTitle());
            descriptionTextView.setText(goal.getDescription());
            dateTextView.setText("开始: " + goal.getStartDate() + (goal.getEndDate().isEmpty() ? "" : " | 结束: " + goal.getEndDate()));

            // 设置完成状态样式
            if (goal.isCompleted()) {
                titleTextView.setAlpha(0.5f);
                descriptionTextView.setAlpha(0.5f);
                toggleButton.setText("未完成");
            } else {
                titleTextView.setAlpha(1.0f);
                descriptionTextView.setAlpha(1.0f);
                toggleButton.setText("已完成");
            }

            // 删除按钮点击事件
            deleteButton.setOnClickListener(v -> deleteListener.onDeleteGoal(goal));

            // 切换完成状态按钮点击事件
            toggleButton.setOnClickListener(v -> toggleListener.onToggleGoal(goal));

            return convertView;
        }

        // 接口定义
        public interface OnDeleteGoalListener {
            void onDeleteGoal(Goal goal);
        }

        public interface OnToggleGoalListener {
            void onToggleGoal(Goal goal);
        }
    }
}