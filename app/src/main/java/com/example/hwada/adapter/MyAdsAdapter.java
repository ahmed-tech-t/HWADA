package com.example.hwada.adapter;

import android.annotation.SuppressLint;
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
import com.example.hwada.databinding.ItemViewBinding;
import com.example.hwada.databinding.ItemViewMyAdsBinding;
import com.example.hwada.util.GlideImageLoader;
import com.google.firebase.Timestamp;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.MyAdsViewHolder> {
    private ArrayList<Ad> list;
    private User user ;

    ItemViewMyAdsBinding binding;

    OnItemListener pOnItemListener;
    Context mContext;
    private static final String TAG = "AdsAdapter";
    App app ;
    public MyAdsAdapter(Context context){
        this.mContext =context;
        list = new ArrayList<>();
      app = (App) mContext.getApplicationContext();
    }
    @NonNull
    @Override
    public MyAdsAdapter.MyAdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemViewMyAdsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
       return new MyAdsViewHolder(binding.getRoot(),pOnItemListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyAdsAdapter.MyAdsViewHolder holder, int position) {

        String url = list.get(position).getMainImage();
        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);
        new GlideImageLoader(holder.userImage,new ProgressBar(mContext)).load(url,options);


        holder.title.setText(list.get(position).getTitle());
        holder.description.setText(list.get(position).getDescription());
        holder.rating.setText(list.get(position).getRating()+"");

        holder.views.setText(list.get(position).getViews()+"");

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

    @SuppressLint("NotifyDataSetChanged")
    public void setList(User user, OnItemListener onItemListener) {
        this.user = user;
        this.list = user.getAds();
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class MyAdsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImage ,menu ;
        TextView title , description , date , views , price , rating;
        OnItemListener onItemListener;
        public MyAdsViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            menu = binding.menuItem ;
            userImage = binding.itemUserImage;
            title = binding.itemJopTitle;
            description = binding.itemDescription;
            date = binding.itemDate;
            price = binding.priceItem;
            rating = binding.itemUserRating;
            views = binding.tvViewItemViewMyAds;
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
            menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == binding.menuItem.getId()){
                onItemListener.getClickedItemMenu(getAdapterPosition(),menu);
            }else{
                onItemListener.getItemPosition(getAdapterPosition());
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface OnItemListener{
        void getItemPosition(int position);
        void getClickedItemMenu(int position,ImageView imageView);
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
                return mContext.getString(R.string.yesterday)+"  "+ dateString.split(",")[1]+dateString.split(",")[3];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0]+dateString.split(",")[3];
    }

}
