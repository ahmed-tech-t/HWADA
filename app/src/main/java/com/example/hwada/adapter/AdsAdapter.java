package com.example.hwada.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ItemViewBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.HomeViewHolder> {
    private ArrayList<Ad> list ;
    private User user ;
    OnItemListener pOnItemListener;
    Context mContext;
    private static final String TAG = "AdsAdapter";
    ItemViewBinding binding ;

    App app ;
    public AdsAdapter (Context context){
      list = new ArrayList<>();
      this.mContext =context ;
      app = (App) mContext.getApplicationContext();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= ItemViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new HomeViewHolder(binding.getRoot(),pOnItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        //post image
        String url = list.get(position).getMainImage();
        Glide.with(mContext).load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.userImage);


        //fav image
        if(adIsInFavList(list.get(position).getId())){
            holder.favImage.setImageResource(R.drawable.fav_checked_icon);
        }else holder.favImage.setImageResource(R.drawable.fav_uncheck_icon);

        //title
        holder.title.setText(list.get(position).getTitle());

        //description

        holder.description.setText(list.get(position).getDescription());

        //rating
        holder.rating.setText(list.get(position).getRating()+"");

        holder.distance.setText(list.get(position).getDistance()+"");

        holder.date.setText(handleTime(list.get(position).getTimeStamp()));

        //price
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String formattedValue = decimalFormat.format(list.get(position).getPrice());
        holder.price.setText(mContext.getString(R.string.from) + "  " + formattedValue);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(User user, ArrayList<Ad> list, OnItemListener onItemListener) {
        this.user = user;
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ShapeableImageView userImage ;
        ImageView favImage ;
        TextView title , description , date , distance , price , rating;
        ProgressBar progressBar;
        OnItemListener onItemListener;
        public HomeViewHolder(@NonNull View v,OnItemListener onItemListener) {
            super(v);
            favImage = binding.itemFav;
            userImage = binding.itemUserImage;
            title = binding.itemJopTitle;
            description = binding.itemDescription;
            date = binding.itemDate;
            price = binding.priceItem;
            rating = binding.itemUserRating;
            distance = binding.itemUserDistance;
            progressBar = binding.progressBarItemView;
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
            favImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==favImage.getId()){
                onItemListener.getFavItemPosition(getBindingAdapterPosition(), favImage);
            }
            else onItemListener.getItemPosition(getBindingAdapterPosition());
        }
    }

    public interface OnItemListener{
        void getItemPosition(int position);
        void getFavItemPosition(int position , ImageView imageView);
    }
    private boolean adIsInFavList(String id) {
        for (int i =  0 ; i < user.getFavAds().size(); i++) {
            if(user.getFavAds().get(i).getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    public String handleTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        String dateString = app.getDateFromTimeStamp(timestamp);
        try {
            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return mContext.getString(R.string.today)+"  " + dateString.split(",")[1]+dateString.split(",")[3] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return mContext.getString(R.string.yesterday);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0];
    }

    public ArrayList<Ad> getList(){
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Ad ad) {
        list.add(ad);
        notifyDataSetChanged();
    }
}
