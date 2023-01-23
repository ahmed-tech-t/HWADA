package com.example.hwada.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {
    private ArrayList<Ad> list = new ArrayList();
    OnItemListener pOnItemListener;
    Context mContext ;

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        //fav image
        holder.favImage.setImageResource(R.drawable.fav_checked_icon);

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
        //price
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String formattedValue = decimalFormat.format(list.get(position).getPrice());
        holder.price.setText(mContext.getString(R.string.from) + "  " + formattedValue);    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Ad> list,Context context, OnItemListener onItemListener) {
        this.list = list;
        this.mContext =context ;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView userImage , favImage ;
        TextView title , description , date , distance , price , rating;
        OnItemListener onItemListener;

        public FavoritesViewHolder(@NonNull View v, OnItemListener onItemListener) {
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
