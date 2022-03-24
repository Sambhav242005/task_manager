package com.surana.taskmanager;

import java.util.Comparator;

public class ItemListTask {
    public static Comparator<ItemListTask> Sort = new Comparator<ItemListTask>() {
        @Override
        public int compare(ItemListTask I1, ItemListTask I2) {
            return I1.getTime().compareTo(I2.getTime());
        }
    };
    String time;
    String Task;
    String date;

    public ItemListTask(String time, String task, String date) {
        this.time = time;
        Task = task;
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
