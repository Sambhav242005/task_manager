package com.surana.taskmanager;

import java.util.Comparator;

public class ItemListTask {
    public static Comparator<ItemListTask> Sort = new Comparator<ItemListTask>() {


        @Override
        public int compare(ItemListTask one, ItemListTask another) {

            int comparetionOfDate = one.getDate().compareTo(another.getDate());

            if (comparetionOfDate == 0) {
                return one.getTime().compareTo(another.getTime());
            }else {
                return  comparetionOfDate;
            }
        }
    };
    String time;
    String Task;
    String date;
    String type;

    public ItemListTask(String time, String task, String date, String type) {
        this.time = time;
        Task = task;
        this.date = date;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
