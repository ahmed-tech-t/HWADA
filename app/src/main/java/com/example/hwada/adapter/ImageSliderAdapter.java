package com.example.hwada.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.R;
import com.example.hwada.databinding.ImageSliderLayoutBinding;
import com.example.hwada.util.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {
    private List<String> list;
    OnItemListener pOnItemListener;
    private Context mContext;



    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ImageSliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        String url = list.get(position);
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.imageView,new ProgressBar(mContext)).load(url,options);
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

    public class ImageSliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;

        ImageView imageView ;
        public ImageSliderViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            imageView =v.findViewById(R.id.image_view_image_slider_layout);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.getImagePosition(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void getImagePosition(int position);
    }
}
