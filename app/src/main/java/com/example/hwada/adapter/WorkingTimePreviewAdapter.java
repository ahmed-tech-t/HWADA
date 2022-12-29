package com.example.hwada.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import java.util.ArrayList;
import java.util.List;

public class WorkingTimePreviewAdapter extends RecyclerView.Adapter<WorkingTimePreviewAdapter.WorkingTimePreviewViewHolder> {
    private ArrayList<WorkingTime> list = new ArrayList();
    String tag ;
    @NonNull
    @Override
    public WorkingTimePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkingTimePreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.working_time_show_layout, parent, false));
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

    public class WorkingTimePreviewViewHolder extends RecyclerView.ViewHolder {
        TextView from ,to;
        public WorkingTimePreviewViewHolder(@NonNull View v) {
            super(v);
            from =v.findViewById(R.id.from_working_time_preview);
            to =v.findViewById(R.id.to_working_time_preview);
        }
    }

    public void clearList(){
            list.clear();
            notifyDataSetChanged();
    }

}
