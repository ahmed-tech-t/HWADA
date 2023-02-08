package com.example.hwada.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.util.GlideImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        //post image
        String url = list.get(position).getImagesUrl().get(0);
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.userImage,new ProgressBar(mContext)).load(url,options);

      //  Picasso.get().load(list.get(position).getImagesUrl().get(0)).into(holder.userImage);

        //fav image
        if(adIsInFavList(list.get(position).getId())){
            holder.favImage.setImageResource(R.drawable.fav_checked_icon);
        }else holder.favImage.setImageResource(R.drawable.fav_uncheck_icon);

        //title
        holder.title.setText(list.get(position).getTitle());

        //description
        if(list.get(position).getDescription().length()>237){
            holder.description.setText(list.get(position).getDescription().substring(0, Math.min(list.get(position).getDescription().length(), 237))+"...");
        }else{
            holder.description.setText(list.get(position).getDescription());
        }

        //rating
        holder.rating.setText(list.get(position).getRating()+"");


        list.get(position).setDistance(Float.valueOf(getDistance(position)));

        holder.distance.setText(list.get(position).getDistance()+"");
        holder.date.setText(handleTime(list.get(position).getDate()));

        //price
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String formattedValue = decimalFormat.format(list.get(position).getPrice());
        holder.price.setText(mContext.getString(R.string.from) + "  " + formattedValue);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(User user,ArrayList<Ad> list,Context mContext,OnItemListener onItemListener) {
        this.user = user;
        this.list = list;
        this.mContext = mContext ;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ShapeableImageView userImage ;
        ImageView favImage ;
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
    private boolean adIsInFavList(String id) {
        for (int i =  0 ; i < user.getFavAds().size(); i++) {
            if(user.getFavAds().get(i).getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    public String handleTime(String dateString){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy , h:mm a", Locale.ENGLISH);
            Date date = dateFormat.parse(dateString);

            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return mContext.getString(R.string.today)+"  " + dateString.split(",")[1] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return mContext.getString(R.string.yesterday)+"  "+ dateString.split(",")[1];
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0];
    }

    public String getDistance(int pos){
        Location location1 = new Location("user");
        if(user.getLocation()!=null){
            location1.setLatitude(user.getLocation().getLatitude());
            location1.setLongitude(user.getLocation().getLongitude());

            Location location2 = new Location("ad");
            location2.setLatitude(list.get(pos).getAuthorLocation().getLatitude());
            location2.setLongitude(list.get(pos).getAuthorLocation().getLongitude());


            float distanceInMeters = location1.distanceTo(location2)/1000;

            return String.format(Locale.US, "%.2f", distanceInMeters);
        }
        return "-1";
    }

    public void addItem(Ad ad) {
        list.add(ad);
        notifyDataSetChanged();
    }
}
