package com.example.hwada.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.AdReview;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<AdReview> list = new ArrayList();
    OnItemListener pOnItemListener;
    Context mContext;
    private static final String TAG = "ReviewAdapter";

    App app ;

    public ReviewAdapter (Context mContext){
        this.mContext = mContext;
        app = (App) mContext.getApplicationContext();
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false), pOnItemListener);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.username.setText(list.get(position).getAuthorName());
        holder.body.setText(list.get(position).getBody());
        holder.date.setText(handleTime(list.get(position).getTimeStamp()));
        //TODO

        Glide.with(mContext).load(list.get(position).getAuthorImage()).into(holder.userImage);
        holder.ratingBar.setRating(list.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<AdReview> list ,OnItemListener onItemListener) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        ImageView menu ;
        TextView username ,body , date;
        RatingBar ratingBar;
        ShapeableImageView userImage;
        LinearLayout userLayout;
        public ReviewViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);

            menu = v.findViewById(R.id.review_item_menu);
            date = v.findViewById(R.id.date_review);
            username = v.findViewById(R.id.username_comment);
            body =v.findViewById(R.id.comment_body);
            ratingBar=v.findViewById(R.id.user_rating_comment);
            userImage=v.findViewById(R.id.user_image_comment);
            userLayout =v.findViewById(R.id.user_comment_layout);

            menu.setOnClickListener(this);
            userLayout.setOnClickListener(this);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == userLayout.getId()) {
                onItemListener.getClickedUserFromComments(getAdapterPosition());
            }else if(v.getId() == menu.getId()){
                onItemListener.menuFromComments(getAdapterPosition(),menu);
            }
        }
    }

    public interface OnItemListener {
        void getClickedUserFromComments(int position);
        void menuFromComments(int position,ImageView imageView);

    }
    public void removeOneItem(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(AdReview review) {
        Log.e(TAG, "addItem: " + review );
        list.add(0,review);
        notifyDataSetChanged();
    }

    public ArrayList<AdReview> getList(){
        return list ;
    }
    public void updateItem(AdReview review , int pos){
        list.set(pos, review);
        notifyDataSetChanged();
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
                return mContext.getString(R.string.today)+"  " + dateString.split(",")[1] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return mContext.getString(R.string.yesterday)+" "+ dateString.split(",")[1];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

}
