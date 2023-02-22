package com.example.hwada.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.Chat;
import com.example.hwada.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private List<String> list = new ArrayList();
    OnItemListener pOnItemListener;

    String TAG ="ImagesAdapter";
    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_holder, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        Picasso.get().load(list.get(position)).into(holder.imageView);
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
        public ImagesViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            imageView =v.findViewById(R.id.ad_image_holder);
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
