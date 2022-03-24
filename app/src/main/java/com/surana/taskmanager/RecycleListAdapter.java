package com.surana.taskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleListAdapter extends RecyclerView.Adapter<RecycleListAdapter.ViewHolder>{

    private ArrayList<ItemListTask> itemListTasks = new ArrayList<>();
    private Context context;

    public RecycleListAdapter(ArrayList<ItemListTask> itemListTasks, Context context) {
        this.itemListTasks = itemListTasks;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycle_task_view,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        holder.date.setText(itemListTasks.get(position).getDate());
        holder.time.setText(itemListTasks.get(position).getTime());
        holder.task.setText(itemListTasks.get(position).getTask());
    }

    @Override
    public int getItemCount() {
        return itemListTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView date,time,task;
        public ViewHolder( View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_listItem);
            time = itemView.findViewById(R.id.time_listItem);
            task = itemView.findViewById(R.id.task_listItem);

        }
    }

}