package com.example.hwada.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.Chat;
import com.example.hwada.R;
import com.example.hwada.databinding.ImageHolderBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private List<String> list = new ArrayList();
    OnItemListener pOnItemListener;

    ImageHolderBinding binding ;
    String TAG ="ImagesAdapter";
    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ImageHolderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return  new ImagesViewHolder(binding.getRoot(),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        Picasso.get().load(list.get(position)).into(holder.imageView, new Callback() {
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

    public void setList(List<String> list, OnItemListener onItemListener) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        ImageView imageView;
        ProgressBar progressBar ;
        public ImagesViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            imageView = binding.adImageHolder;
            progressBar = binding.progressBarImageHolder;
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.getItemPosition(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void getItemPosition(int position);
    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void addItems(int position ,List<String> temp){
        list.addAll(position, temp);
        notifyItemRangeInserted(position, temp.size());
    }
    public void addItem(String uri){
        list.add(uri);
        notifyDataSetChanged();
    }
    public void clearList(){
        list.clear();
        notifyDataSetChanged();
    }
    public void moveToTop(int pos,String s) {
        list.remove(pos);
        list.add(0, s);
        notifyDataSetChanged();
    }

}
