package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;

import java.util.ArrayList;
import java.util.Locale;


public class WorkingTimeEditDaysAdapter extends RecyclerView.Adapter<WorkingTimeEditDaysAdapter.MainWorkingTimeViewHolder> {
    private ArrayList<String> list = new ArrayList();
    OnItemListener pOnItemListener;
    String DAY_TAG;
    Context context ;

    ArrayList<WorkingTime>workingTimes;
    String TAG ="MainWorkingTimeAdapter";
    @NonNull
    @Override
    public MainWorkingTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainWorkingTimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.working_time_edit_days_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainWorkingTimeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.button.setText("add");
        holder.title.setText(list.get(position));
        WorkingTimePreviewPeriodAdapter innerAdapter = new WorkingTimePreviewPeriodAdapter();

        if(workingTimes!=null  ) {
                holder.button.setVisibility(View.VISIBLE);
                holder.switch_.setChecked(true);
                holder.button.setText(R.string.edit);
                innerAdapter.setList(workingTimes,list.get(position).toLowerCase(Locale.ROOT));
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
                    holder.button.setVisibility(View.GONE);
                    holder.innerRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<String> list,String dayTag,Context context, OnItemListener onItemListener) {
        this.list = list;
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
            innerRecycler = v.findViewById(R.id.recycler_main_work_time);
            button = v.findViewById(R.id.button_right_main_work_time);
            title =  v.findViewById(R.id.tv_title_main_work_time);
            switch_ = v.findViewById(R.id.switch_day_preview);
            this.onItemListener = onItemListener;
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.rightButtonClicked(getAdapterPosition(),list.get(getAdapterPosition()).toLowerCase(Locale.ROOT));
        }
    }

    public void updateRecycler(ArrayList<WorkingTime> workingTimes,int pos){
        this.workingTimes = workingTimes;
        notifyItemChanged(pos);
    }

    public interface OnItemListener {
        void rightButtonClicked(int pos,String day);
        void switchClicked(boolean isChecked,int pos);
    }

}
