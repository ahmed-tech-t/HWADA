package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.util.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImagesIndicatorAdapter extends RecyclerView.Adapter<ImagesIndicatorAdapter.ImagesIndicatorViewHolder> {
    private List<String> list;
    OnItemListener pOnItemListener;
    private Context mContext;
    private int selectedPosition = -1;


    @NonNull
    @Override
    public ImagesIndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagesIndicatorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_indicator_slider_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesIndicatorViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String url = list.get(position);
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.image,new ProgressBar(mContext)).load(url,options);

        if (selectedPosition == position) {
            holder.linearLayout.setSelected(true);
        } else {
            holder.linearLayout.setSelected(false);
        }



        holder.linearLayout.setOnClickListener(v -> {
            // Set the current item as selected
            selectedPosition = position;
            // Notify the adapter that the data has changed
            notifyDataSetChanged();
            pOnItemListener.getItemPosition(position);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<String> list,Context mContext, OnItemListener onItemListener) {
        this.list = list;
        this.mContext = mContext;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }
    public class ImagesIndicatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemListener onItemListener;
        ImageView image ;
        LinearLayout linearLayout;
      
        public ImagesIndicatorViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            linearLayout = v.findViewById(R.id.ll_images_indicator_slider_layout);
            image = v.findViewById(R.id.im_image_indicator_slider_layout);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemListener {
        void getItemPosition(int position);
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());
    }
}
