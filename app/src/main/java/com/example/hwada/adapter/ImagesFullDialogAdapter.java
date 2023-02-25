package com.example.hwada.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.hwada.R;
import com.example.hwada.databinding.ImageLayoutBinding;
import com.example.hwada.util.GlideImageLoader;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagesFullDialogAdapter extends RecyclerView.Adapter<ImagesFullDialogAdapter.ImagesFullDialogViewHolder> {
    private ArrayList<String> list = new ArrayList();
    Context mContext;
    private static final String TAG = "ImagesFullDialogAdapter";

    ImageLayoutBinding binding;

    @NonNull
    @Override
    public ImagesFullDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ImageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ImagesFullDialogViewHolder(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull ImagesFullDialogViewHolder holder, int position) {
        String url = list.get(position);
        Log.d(TAG, "onBindViewHolder: onSuccess out");
        Picasso.get().load(url).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: in");
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
    public void setList(ArrayList<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        notifyDataSetChanged();
    }
    public class ImagesFullDialogViewHolder extends RecyclerView.ViewHolder {

        PhotoView image ;
        ProgressBar progressBar;
        public ImagesFullDialogViewHolder(@NonNull View v) {
            super(v);
            image = binding.imImageLayout;
            progressBar = binding.progressBarImageLayout;
        }
    }

}
