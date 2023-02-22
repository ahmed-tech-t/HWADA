package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.databinding.WorkingTimeEditDaysLayoutBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class WorkingTimeEditDaysAdapter extends RecyclerView.Adapter<WorkingTimeEditDaysAdapter.MainWorkingTimeViewHolder> {
    DaysSchedule days ;
    OnItemListener pOnItemListener;
    String DAY_TAG;
    Context context ;
    String TAG ="MainWorkingTimeAdapter";

    WorkingTimeEditDaysLayoutBinding binding ;
    @NonNull
    @Override
    public MainWorkingTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = WorkingTimeEditDaysLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new MainWorkingTimeViewHolder(binding.getRoot(),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainWorkingTimeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String dayTitle = days.getDayTitleFromPosition(position);
        String dayVal = days.getDayValFromPosition(position);
        holder.button.setText(context.getString(R.string.add));
        holder.title.setText(dayTitle);
        WorkingTimePreviewPeriodAdapter innerAdapter = new WorkingTimePreviewPeriodAdapter();

        String day = days.getDayValFromPosition(position);
        ArrayList<WorkingTime> workingTimes = (ArrayList<WorkingTime>) days.getDays().get(day);
        if(workingTimes.size()>0) {
            holder.button.setVisibility(View.VISIBLE);
            holder.switch_.setChecked(true);
            holder.button.setText(R.string.edit);
            innerAdapter.setList(workingTimes,dayVal);
        }
        holder.innerRecycler.setAdapter(innerAdapter);
        holder.innerRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.switch_.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (pOnItemListener != null) {
                    pOnItemListener.switchClicked(isChecked,position);
                }

                if(isChecked){
                    holder.button.setVisibility(View.VISIBLE);
                    holder.innerRecycler.setVisibility(View.VISIBLE);
                }else {
                    holder.innerRecycler.setAdapter(new WorkingTimePreviewPeriodAdapter());
                    holder.button.setText(context.getString(R.string.add));
                    holder.button.setVisibility(View.GONE);
                    holder.innerRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.getDays().size();
    }

    public void setList(DaysSchedule days,String dayTag,Context context, OnItemListener onItemListener) {
        this.days = days;
        this.context =context;
        this.DAY_TAG =dayTag;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class MainWorkingTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        RecyclerView innerRecycler ;
        Button button;
        Switch switch_ ;
        TextView title ;
        public MainWorkingTimeViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            innerRecycler = binding.recyclerMainWorkTime;
            button = binding.buttonRightMainWorkTime;
            title = binding.tvTitleMainWorkTime;
            switch_ = binding.switchDayPreview;

            this.onItemListener = onItemListener;
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.rightButtonClicked(getAdapterPosition());
        }
    }

    public void updateRecycler(ArrayList<WorkingTime> workingTimes,int pos){
        Log.e(TAG, "updateRecycler: "+workingTimes);
        String day = days.getDayValFromPosition(pos);
        days.getDays().put(day,workingTimes);
        notifyItemChanged(pos);
    }

    public interface OnItemListener {
        void rightButtonClicked(int pos);
        void switchClicked(boolean isChecked,int pos);
    }

}
