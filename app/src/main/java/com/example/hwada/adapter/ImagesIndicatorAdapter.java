package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.databinding.ImageIndicatorSliderLayoutBinding;
import com.example.hwada.util.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImagesIndicatorAdapter extends RecyclerView.Adapter<ImagesIndicatorAdapter.ImagesIndicatorViewHolder> {
    private List<String> list;
    OnItemListener pOnItemListener;
    private Context mContext;
    private int selectedPosition = -1;

    ImageIndicatorSliderLayoutBinding binding;


    @NonNull
    @Override
    public ImagesIndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ImageIndicatorSliderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return new ImagesIndicatorViewHolder(binding.getRoot(),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesIndicatorViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String url = list.get(position);
        Glide.with(mContext).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.image);

        if (selectedPosition == position) {
            holder.linearLayout.setSelected(true);
        } else {
            holder.linearLayout.setSelected(false);
        }

        holder.linearLayout.setOnClickListener(v -> {
            // Set the current item as selected
            selectedPosition = position;
            // Notify the adapter that the data has changed
            notifyItemChanged(position,list.get(position));
            pOnItemListener.getItemPosition(position);
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<String> list, Context mContext, OnItemListener onItemListener) {
        this.list = list;
        this.mContext = mContext;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }
    public class ImagesIndicatorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemListener onItemListener;
        ImageView image ;
        LinearLayout linearLayout;
        ProgressBar progressBar ;
      
        public ImagesIndicatorViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            linearLayout =binding.llImagesIndicatorSliderLayout;
            image = binding.imImageIndicatorSliderLayout;
            progressBar =binding.progressBarImageIndicatorSliderLayout;

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

    @SuppressLint("NotifyDataSetChanged")
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
