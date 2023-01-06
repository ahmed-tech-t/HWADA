package com.example.hwada.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.R;
import com.example.hwada.databinding.ImageSliderLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {
    private List<String> list;
    OnItemListener pOnItemListener;
    private Context mContext;

    ImageSliderLayoutBinding binding;
    public ImageSliderAdapter(Context context, List<String> imageUrls) {
        this.mContext = context;
        this.list = imageUrls;
    }


    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ImageSliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        Glide.with(mContext).load(list.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<String> list, OnItemListener onItemListener) {
        this.list = list;
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
           // onItemListener.getItemPosition(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void getItemPosition(int position);
    }
}
