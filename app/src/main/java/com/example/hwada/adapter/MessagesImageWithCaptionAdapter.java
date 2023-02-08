package com.example.hwada.adapter;

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
import com.example.hwada.util.GlideImageLoader;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Collections;

public class MessagesImageWithCaptionAdapter extends RecyclerView.Adapter<MessagesImageWithCaptionAdapter.MessagesImageWithCaptionViewHolder> {
    private ArrayList<String> list = new ArrayList();
    Context mContext;
    OnItemListener pOnItemListener;

    @NonNull
    @Override
    public MessagesImageWithCaptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessagesImageWithCaptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesImageWithCaptionViewHolder holder, int position) {
        String url = list.get(position);
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.image,new ProgressBar(mContext)).load(url,options);
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


    public class MessagesImageWithCaptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        PhotoView image ;
        public MessagesImageWithCaptionViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            image = v.findViewById(R.id.im_image_layout);
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
