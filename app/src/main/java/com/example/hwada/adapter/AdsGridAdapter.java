package com.example.hwada.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.util.GlideImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdsGridAdapter extends RecyclerView.Adapter<AdsGridAdapter.HomeViewHolder> {
    private ArrayList<Ad> list = new ArrayList();
    private User user ;
    OnItemListener pOnItemListener;
    Context mContext;
    private static final String TAG = "AdsGridAdapter";
    App app;
    public AdsGridAdapter (Context context){
        this.mContext = context;
        if(mContext!=null) app = (App) mContext.getApplicationContext();
    }
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_grid_layout, parent, false),pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {

        if (list!= null) {

        String url = list.get(position).getImagesUrl().get(0);
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.userImage, new ProgressBar(mContext)).load(url, options);


        holder.title.setText(list.get(position).getTitle());

        //fav image
        if (adIsInFavList(list.get(position).getId())) {
            holder.favImage.setImageResource(R.drawable.fav_checked_icon);
        } else holder.favImage.setImageResource(R.drawable.fav_uncheck_icon);


        holder.rating.setText(list.get(position).getRating() + "");

        list.get(position).setDistance(Float.valueOf(getDistance(position)));
        holder.distance.setText(list.get(position).getDistance() + "");
        holder.date.setText(handleTime(list.get(position).getTimeStamp()));
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String formattedValue = decimalFormat.format(list.get(position).getPrice());

        holder.price.setText(mContext.getString(R.string.from) + "  " + formattedValue);
        if (user != null) {
            //TODO list.get(position).getId();
            String adId = String.valueOf(position);
            if (adIsInFavList(adId)) {
                holder.favImage.setImageResource(R.drawable.fav_checked_icon);
            }
        }
    }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(User user,ArrayList<Ad> list,OnItemListener onItemListener) {
        this.user = user;
        this.list = list;
        this.mContext =mContext ;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ShapeableImageView userImage ;
        ImageView favImage ;
        TextView title  , date , distance , price , rating;
        OnItemListener onItemListener;
        public HomeViewHolder(@NonNull View v,OnItemListener onItemListener) {
            super(v);
            favImage = v.findViewById(R.id.item_fav_grid_layout);
            userImage = v.findViewById(R.id.item_user_image_design_grid_layout);
            title = v.findViewById(R.id.item_title_design_grid_layout);
            date = v.findViewById(R.id.item_date_grid_layout);
            price =v.findViewById(R.id.item_price_grid_layout);
            rating = v.findViewById(R.id.item_user_rating_grid_layout);
            distance = v.findViewById(R.id.item_user_distance_grid_layout);
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
    public String handleTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        String dateString = app.getDateFromTimeStamp(timestamp);
        try {
            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return mContext.getString(R.string.today)+" " + dateString.split(",")[1] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return mContext.getString(R.string.yesterday)+" "+ dateString.split(",")[1];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0];
    }

    public String getDistance(int pos){
        Location location1 = new Location("user");
        location1.setLatitude(user.getLocation().getLatitude());
        location1.setLongitude(user.getLocation().getLongitude());

        Location location2 = new Location("ad");
        location2.setLatitude(list.get(pos).getAuthorLocation().getLatitude());
        location2.setLongitude(list.get(pos).getAuthorLocation().getLongitude());

        float distanceInMeters = location1.distanceTo(location2)/1000;

        return String.format(Locale.US, "%.2f", distanceInMeters);

    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,list.size());
    }
}
