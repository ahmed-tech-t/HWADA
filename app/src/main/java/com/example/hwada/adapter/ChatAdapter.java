package com.example.hwada.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Chat;
import com.example.hwada.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<Chat> list = new ArrayList();
    OnItemListener pOnItemListener;
    Context mContext;

    private static final String TAG = "ChatAdapter";
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_holder_layout, parent, false), pOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Glide.with(mContext).load(list.get(position).getAd().getImagesUrl().get(0)).into(holder.adImage);
        holder.title.setText(list.get(position).getAd().getTitle());
        if(list.get(position).getLastMessage() == null){
            holder.messageStatus.setVisibility(View.GONE);
            holder.date.setText(handleTime(list.get(position).getDate()));
            holder.lastMessage.setText(mContext.getString(R.string.noMessages));
        }else {
            holder.date.setText(handleTime(list.get(position).getLastMessage().getDate()));
            if(list.get(position).getLastMessage().isSeen())
                holder.messageStatus.setImageResource(R.drawable.message_read);
            else holder.messageStatus.setImageResource(R.drawable.message_un_read);
            holder.lastMessage.setText(list.get(position).getLastMessage().getBody());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Chat> list,Context mContext, OnItemListener onItemListener) {
        this.list = list;
        this.mContext = mContext;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        TextView date ,title, lastMessage;

        ImageView adImage, messageStatus;
        public ChatViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            date = v.findViewById(R.id.date_last_message_chat_holder);
            title = v.findViewById(R.id.chat_title_chat_holder);
            lastMessage =v.findViewById(R.id.last_message_chat_holder);
            adImage =v.findViewById(R.id.chat_image_chat_holder);
            messageStatus = v.findViewById(R.id.message_status_chat_holder);


            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
            adImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==adImage.getId()){
                onItemListener.pressedImagePosition(getAdapterPosition());
            }else onItemListener.getItemPosition(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void getItemPosition(int position);
        void pressedImagePosition(int pos);
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
                return dateString.split(",")[1] ;
            }
            else {
                return dateString.split(",")[0] ;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
