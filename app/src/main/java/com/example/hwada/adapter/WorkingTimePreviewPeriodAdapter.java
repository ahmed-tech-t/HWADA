package com.example.hwada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.databinding.WorkingTimePreviewPeriodLayoutBinding;

import java.util.ArrayList;

public class WorkingTimePreviewPeriodAdapter extends RecyclerView.Adapter<WorkingTimePreviewPeriodAdapter.WorkingTimePreviewViewHolder> {
    private ArrayList<WorkingTime> list = new ArrayList();

    WorkingTimePreviewPeriodLayoutBinding binding;
    @NonNull
    @Override
    public WorkingTimePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding =  WorkingTimePreviewPeriodLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return  new WorkingTimePreviewViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull WorkingTimePreviewViewHolder holder, int position) {
        holder.from.setText(list.get(position).getFrom());
        holder.to.setText(list.get(position).getTo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<WorkingTime> list ,String tag) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void clearList(){
            list.clear();
            notifyDataSetChanged();

    }
    public class WorkingTimePreviewViewHolder extends RecyclerView.ViewHolder {
        TextView from ,to;
        public WorkingTimePreviewViewHolder(@NonNull View v) {
            super(v);
            from = binding.fromWorkingTimePreview;
            to = binding.toWorkingTimePreview;
        }
    }



}
