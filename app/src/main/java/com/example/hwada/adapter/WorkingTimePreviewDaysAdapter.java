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
import com.example.hwada.databinding.WorkingTimePreviewDaysLayoutBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class WorkingTimePreviewDaysAdapter extends RecyclerView.Adapter<WorkingTimePreviewDaysAdapter.WorkingTimePreviewDaysViewHolder> {
    DaysSchedule days ;
    Context context ;

    WorkingTimePreviewDaysLayoutBinding binding ;

    private static final String TAG = "WorkingTimePreviewDaysAdapter";
    @NonNull
    @Override
    public WorkingTimePreviewDaysViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = WorkingTimePreviewDaysLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new WorkingTimePreviewDaysViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull WorkingTimePreviewDaysViewHolder holder, int position) {

        WorkingTimePreviewPeriodAdapter adapter =new WorkingTimePreviewPeriodAdapter();

        int i = 0;
        for (Map.Entry<String, ArrayList<WorkingTime>> entry : days.getDays().entrySet()) {
            if (i == position) {
                holder.day.setText(days.getDayTitleFromDay(entry.getKey()));
                adapter.setList(entry.getValue(),entry.getKey());
                break;
            }
            i++;
        }

            holder.recyclerView.setAdapter(adapter);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return days.getDays().size();
    }
    public void setDaysSchedule(DaysSchedule days,Context context) {
        this.days = days;
        this.context =context;
        notifyDataSetChanged();
    }

    public class WorkingTimePreviewDaysViewHolder extends RecyclerView.ViewHolder {
        TextView day ;
        RecyclerView recyclerView ;
        public WorkingTimePreviewDaysViewHolder(@NonNull View v) {
            super(v);
            day =binding.tvTitleWorkingTimePreviewDays;
            recyclerView =binding.recyclerMainWorkingTimePreviewDays;
        }
    }

}
