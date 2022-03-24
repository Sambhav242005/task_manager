package com.surana.taskmanager;

public class ItemListTask {
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
