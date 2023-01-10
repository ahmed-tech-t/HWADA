package com.example.hwada.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class WorkingTimePreviewDaysAdapter extends RecyclerView.Adapter<WorkingTimePreviewDaysAdapter.WorkingTimePreviewDaysViewHolder> {
    ArrayList<String> days ;
    ArrayList<ArrayList<WorkingTime>> schedule ;
    Context context ;


    private static final String TAG = "WorkingTimePreviewDaysAdapter";
    @NonNull
    @Override
    public WorkingTimePreviewDaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkingTimePreviewDaysViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.working_time_preview_days_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkingTimePreviewDaysViewHolder holder, int position) {
            holder.day.setText(days.get(position));
            WorkingTimePreviewPeriodAdapter adapter =new WorkingTimePreviewPeriodAdapter();
            adapter.setList( schedule.get(position),days.get(position).toLowerCase(Locale.ROOT));
            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return schedule.size();
    }
    public void setDaysSchedule( ArrayList<ArrayList<WorkingTime>> schedule,ArrayList<String> days  ,Context context) {
        this.schedule = schedule;
        this.days =days;
        this.context =context;
        notifyDataSetChanged();
    }

    public class WorkingTimePreviewDaysViewHolder extends RecyclerView.ViewHolder {
        TextView day ;
        RecyclerView recyclerView ;
        public WorkingTimePreviewDaysViewHolder(@NonNull View v) {
            super(v);
            day =v.findViewById(R.id.tv_title_working_time_preview_days);
            recyclerView =v.findViewById(R.id.recycler_main_working_time_preview_days);
        }
    }

}
