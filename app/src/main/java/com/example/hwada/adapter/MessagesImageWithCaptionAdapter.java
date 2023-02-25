package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.R;
import com.example.hwada.databinding.ImageLayoutBinding;
import com.example.hwada.util.GlideImageLoader;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MessagesImageWithCaptionAdapter extends RecyclerView.Adapter<MessagesImageWithCaptionAdapter.MessagesImageWithCaptionViewHolder> {
    private ArrayList<String> list = new ArrayList();
    Context mContext;
    OnItemListener pOnItemListener;
    ImageLayoutBinding binding;
    @NonNull
    @Override
    public MessagesImageWithCaptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ImageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MessagesImageWithCaptionViewHolder(binding.getRoot(),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesImageWithCaptionViewHolder holder, int position) {
        String url = list.get(position);
        Picasso.get().load(url).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
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


    public class MessagesImageWithCaptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        PhotoView image ;
        ProgressBar progressBar;
        public MessagesImageWithCaptionViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            image = binding.imImageLayout;
            progressBar = binding.progressBarImageLayout;
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemListener {
        void getDeletedItemPosition(int pos);
    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());
    }

}
