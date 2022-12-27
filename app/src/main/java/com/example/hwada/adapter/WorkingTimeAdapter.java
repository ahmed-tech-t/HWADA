package com.example.hwada.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import java.util.ArrayList;
import java.util.List;

public class WorkingTimeAdapter extends RecyclerView.Adapter<WorkingTimeAdapter.WorkingTimeViewHolder> {
    private ArrayList<WorkingTime> list = new ArrayList();
    OnItemListener pOnItemListener;

    @NonNull
    @Override
    public WorkingTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkingTimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.working_time_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkingTimeViewHolder holder, int position) {
        holder.from.setText(list.get(position).getFrom());
        holder.to.setText(list.get(position).getTo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<WorkingTime> list, OnItemListener onItemListener) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class WorkingTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        ImageView add, remove;
        TextView from, to;

        public WorkingTimeViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            add = v.findViewById(R.id.button_add_time);
            remove = v.findViewById(R.id.button_remove_time);
            from = v.findViewById(R.id.from_working_time);
            to = v.findViewById(R.id.to_working_time);
            this.onItemListener = onItemListener;

            from.setOnClickListener(this);
            to.setOnClickListener(this);
            add.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == from.getId()) onItemListener.fromTimeListener(getAdapterPosition());
            else if (v.getId() == to.getId()) onItemListener.toTimeListener(getAdapterPosition());
            else if (v.getId() == add.getId()) onItemListener.addTimeListener(getAdapterPosition());
            else if (v.getId() == remove.getId())
                onItemListener.removeTimeListener(getAdapterPosition());
        }
    }
    public interface OnItemListener {
        void fromTimeListener(int pos);

        void toTimeListener(int pos);

        void addTimeListener(int pos);

        void removeTimeListener(int pos);

    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void addItem(WorkingTime w){
        list.add(w);
        notifyDataSetChanged();
    }
    public void clearList(){
        list.clear();
        notifyDataSetChanged();
    }
}
