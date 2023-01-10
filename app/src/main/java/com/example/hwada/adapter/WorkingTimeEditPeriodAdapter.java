package com.example.hwada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import java.util.ArrayList;

public class WorkingTimeEditPeriodAdapter extends RecyclerView.Adapter<WorkingTimeEditPeriodAdapter.WorkingTimeViewHolder> {
    private ArrayList<WorkingTime> list = new ArrayList();
    OnItemListener pOnItemListener;

    @NonNull
    @Override
    public WorkingTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkingTimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.working_time_edit_period_layout, parent, false), pOnItemListener);
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

    public void setList(ArrayList<WorkingTime> list, OnItemListener onItemListener ) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class WorkingTimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        ImageView remove;
        Button from, to;

        public WorkingTimeViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            remove = v.findViewById(R.id.button_remove_time);
            from = v.findViewById(R.id.from_working_time);
            to = v.findViewById(R.id.to_working_time);
            this.onItemListener = onItemListener;

            from.setOnClickListener(this);
            to.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == from.getId()) onItemListener.fromTimeListener(getAdapterPosition(),from);
            else if (v.getId() == to.getId()) onItemListener.toTimeListener(getAdapterPosition(),to);
            else if (v.getId() == remove.getId()) onItemListener.removeTimeListener(getAdapterPosition());
        }
    }
    public interface OnItemListener {
        void fromTimeListener(int pos,Button button);

        void toTimeListener(int pos ,Button button);

        void removeTimeListener(int pos);

    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void addItem(WorkingTime w){
        list.add(w);
        notifyItemInserted(list.size() - 1);
    }
    public void clearList(){
        list.clear();
        notifyDataSetChanged();
    }
}
