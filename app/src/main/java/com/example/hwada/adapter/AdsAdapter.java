package com.example.hwada.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;
import com.example.hwada.R;

import java.util.ArrayList;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.HomeViewHolder> {
    private ArrayList<Ads> list = new ArrayList();
    private User user ;
    OnItemListener pOnItemListener;
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.jop.setText(list.get(position).getJop());
        holder.fullName.setText(list.get(position).getFullName());
        holder.distance.setText(list.get(position).getDistance());
        holder.date.setText(list.get(position).getDate());
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

    public void setList(User user,ArrayList<Ads> list,OnItemListener onItemListener) {
        this.user = user;
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImage , favImage;
        TextView jop , fullName , date , distance;
        OnItemListener onItemListener;
        public HomeViewHolder(@NonNull View v,OnItemListener onItemListener) {
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

    public interface OnItemListener{
        void getItemPosition(int position);
        void getFavItemPosition(int position , ImageView imageView);
    }
    private boolean adIsInFavList(String id){
        return user.getFavoriteAds().contains(id);
    }
}
