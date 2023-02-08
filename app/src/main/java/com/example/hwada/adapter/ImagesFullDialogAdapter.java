package com.example.hwada.adapter;

import android.content.Context;
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

public class ImagesFullDialogAdapter extends RecyclerView.Adapter<ImagesFullDialogAdapter.ImagesFullDialogViewHolder> {
    private ArrayList<String> list = new ArrayList();
    Context mContext;
    private static final String TAG = "ImagesFullDialogAdapter";
    @NonNull
    @Override
    public ImagesFullDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagesFullDialogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesFullDialogViewHolder holder, int position) {
        String url = list.get(position);

        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.image,new ProgressBar(mContext)).load(url,options);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(ArrayList<String> list,Context mContext) {
        this.list = list;
        this.mContext = mContext;
        notifyDataSetChanged();
    }
    public class ImagesFullDialogViewHolder extends RecyclerView.ViewHolder {

        PhotoView image ;
        public ImagesFullDialogViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.im_image_layout);
        }
    }

}
