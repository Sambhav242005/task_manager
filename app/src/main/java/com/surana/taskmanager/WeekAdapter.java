package com.surana.taskmanager;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WeekAdapter extends ArrayAdapter<WeekItemList> {

    private List<WeekItemList> itemLists = new ArrayList<WeekItemList>();
    Context context;

    @Override
    public void add( WeekItemList object) {
        super.add(object);
    }

    public WeekAdapter(List<WeekItemList> itemLists, Context context) {
        super(context,R.layout.week_list_layout);
        this.itemLists = itemLists;
        this.context = context;
    }
    private class ViewHolder {
        TextView textView;
    }

    public int getCount() {
        return this.itemLists.size();
    }
    public WeekItemList getItem(int index) {
        return this.itemLists.get(index);
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.week_list_layout, parent, false);

            holder.textView = convertView.findViewById(R.id.week_item);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        WeekItemList itemList = getItem(position);
        holder.textView.setText(itemList.getItemName());

        return convertView;
    }
}
