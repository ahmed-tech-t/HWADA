package com.example.hwada.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;

import java.util.ArrayList;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.HomeViewHolder> {
    private ArrayList<Ad> list = new ArrayList();
    private User user ;
    OnItemListener pOnItemListener;
    Context mContext;
    private static final String TAG = "AdsAdapter";

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Glide.with(mContext).load(list.get(position).getImagesUrl().get(0)).into(holder.userImage);
        holder.title.setText(list.get(position).getTitle());
        if(list.get(position).getDescription().length()>237){
            holder.description.setText(list.get(position).getDescription().substring(0, Math.min(list.get(position).getDescription().length(), 237))+"...");
        }else{
            holder.description.setText(list.get(position).getDescription());
        }
        holder.rating.setText(list.get(position).getRating()+"");
        holder.distance.setText(list.get(position).getDistance()+"");
        holder.date.setText(list.get(position).getDate());
        holder.price.setText(mContext.getString(R.string.from) + "  " + list.get(position).getPrice() + "");
        if(user!=null){
            //TODO list.get(position).getId();
           String  adId = String.valueOf(position);
            if(adIsInFavList(adId)){
             holder.favImage.setImageResource(R.drawable.fav_checked_icon);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(User user,ArrayList<Ad> list,Context mContext,OnItemListener onItemListener) {
        this.user = user;
        this.list = list;
        this.mContext =mContext ;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImage , favImage ;
        TextView title , description , date , distance , price , rating;
        OnItemListener onItemListener;
        public HomeViewHolder(@NonNull View v,OnItemListener onItemListener) {
            super(v);
            favImage = v.findViewById(R.id.item_fav);
            userImage = v.findViewById(R.id.item_user_image);
            title = v.findViewById(R.id.item_jop_title);
            description = v.findViewById(R.id.item_description);
            date = v.findViewById(R.id.item_date);
            price =v.findViewById(R.id.price_item);
            rating = v.findViewById(R.id.item_user_rating);
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

    public interface OnItemListener{
        void getItemPosition(int position);
        void getFavItemPosition(int position , ImageView imageView);
    }
    private boolean adIsInFavList(String id){
        return false;
    }

    public String handleTime(String date){

    return "";
    }
}
