package com.example.hwada.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hwada.Model.AdReview;
import com.example.hwada.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private ArrayList<AdReview> list = new ArrayList();
    OnItemListener pOnItemListener;

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.username.setText(list.get(position).getAuthorName());
        holder.body.setText(list.get(position).getBody());
        //TODO
       // holder.userImage.setImageResource(list.get(position).getAuthorImage());
        holder.ratingBar.setRating(list.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<AdReview> list, OnItemListener onItemListener) {
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        TextView username ,body;
        RatingBar ratingBar;
        CircleImageView userImage;
        LinearLayout userLayout;
        public ReviewViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            username = v.findViewById(R.id.username_comment);
            body =v.findViewById(R.id.comment_body);
            ratingBar=v.findViewById(R.id.user_rating_comment);
            userImage=v.findViewById(R.id.user_image_comment);
            userLayout =v.findViewById(R.id.user_comment_layout);

            userLayout.setOnClickListener(this);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == userLayout.getId()) {
                onItemListener.getClickedUserFromComments(getAdapterPosition());
            }
        }
    }

    public interface OnItemListener {
        void getClickedUserFromComments(int position);
    }
}
