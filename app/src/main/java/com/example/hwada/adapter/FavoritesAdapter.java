package com.example.hwada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.Ad;
import com.example.hwada.R;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {
    private ArrayList<Ad> list = new ArrayList();
    OnItemListener pOnItemListener;

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        holder.jop.setText(list.get(position).getTitle());
        holder.fullName.setText(list.get(position).getAuthorName());
        holder.distance.setText((int) list.get(position).getDistance());
        holder.date.setText(list.get(position).getDate());
        holder.favImage.setImageResource(R.drawable.fav_checked_icon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Ad> list, OnItemListener onItemListener) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImage , favImage;
        TextView jop , fullName , date , distance;
        OnItemListener onItemListener;

        public FavoritesViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            favImage = v.findViewById(R.id.item_fav);
            userImage = v.findViewById(R.id.item_user_image);
            jop = v.findViewById(R.id.item_jop_title);
            fullName = v.findViewById(R.id.item_username);
            date = v.findViewById(R.id.item_date);
            distance = v.findViewById(R.id.item_user_distance);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
            favImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==favImage.getId()){
                onItemListener.getFavItemPosition(getAdapterPosition(), favImage);
            }
            else onItemListener.getItemPosition(getAdapterPosition());

        }
    }

    public interface OnItemListener {
        void getItemPosition(int position);
        void getFavItemPosition(int position , ImageView imageView);
    }

    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());
    }
}
