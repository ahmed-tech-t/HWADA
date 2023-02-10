package com.example.hwada.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;

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
    String userId;
    App app;
    private static final String TAG = "ChatAdapter";

    public ChatAdapter(Context context){
        this.mContext = context;
        if(mContext!=null){
            app = (App) mContext.getApplicationContext();
        }
        setHasStableIds(true);
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_holder_layout, parent, false), pOnItemListener);
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
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Message lastMessage = list.get(position).getLastMessage();

        Glide.with(mContext).load(list.get(position).getAd().getImagesUrl().get(0)).into(holder.adImage);
        holder.title.setText(list.get(position).getAd().getTitle());


        if (list.get(position).getLastMessage() == null) {
            holder.messageStatus.setVisibility(View.GONE);
            holder.date.setText(handleTime(list.get(position).getTimeStamp()));
            holder.lastMessage.setText(mContext.getString(R.string.noMessages));
        } else {
            if(lastMessage.getBody() != null){
                String body = lastMessage.getBody();
                if(body.length()>0){
                    holder.lastMessage.setText(lastMessage.getBody());
                }else {
                    holder.lastMessage.setText(mContext.getString(R.string.photo));
                    holder.imageIcon.setVisibility(View.VISIBLE);
                }
            }else {
                holder.lastMessage.setText(mContext.getString(R.string.photo));
                holder.imageIcon.setVisibility(View.VISIBLE);
            }



            //set status
            if (!lastMessage.getSenderId().equals(userId)) {
                holder.messageStatus.setVisibility(View.GONE);
            } else {
                setStatus(position, holder.messageStatus);
            }


            //set alert if their is new messages
            if(lastMessage.getReceiverId().equals(userId)){
                Log.e(TAG, "onBindViewHolder: "+lastMessage.getReceiverId());
                Log.e(TAG, "onBindViewHolder: "+userId);
                Log.e(TAG, "onBindViewHolder: "+ lastMessage.isSeen());
                if(!lastMessage.isSeen()){
                    holder.newMessageNotification.setVisibility(View.VISIBLE);
                    holder.date.setTextColor(ContextCompat.getColor(mContext,R.color.background));
                }
            }
        }

        holder.date.setText(handleTime(list.get(position).getLastMessage().getTimeStamp()));
    }
    private void setStatus(int pos , ImageView status_im){
        Message message =list.get(pos).getLastMessage();

        if(message.isSeen()){
            status_im.setImageResource(R.drawable.message_read);
        }else if(!message.isSeen()&&message.isDelivered()){
            status_im.setImageResource(R.drawable.message_un_read);
        }else if(!message.isDelivered() && message.isSent()){
            status_im.setImageResource(R.drawable.message_sent);
        }else if(!message.isSent()) {
            status_im.setImageResource(R.drawable.message_not_sent);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(String userId ,ArrayList<Chat> list, OnItemListener onItemListener) {
        this.userId=userId;
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        TextView date ,title, lastMessage;

        ImageView adImage, messageStatus ,imageIcon;
        ShapeableImageView newMessageNotification;
        public ChatViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            date = v.findViewById(R.id.date_last_message_chat_holder);
            title = v.findViewById(R.id.chat_title_chat_holder);
            lastMessage =v.findViewById(R.id.last_message_chat_holder);
            adImage =v.findViewById(R.id.chat_image_chat_holder);
            messageStatus = v.findViewById(R.id.message_status_chat_holder);
            imageIcon = v.findViewById(R.id.image_icon_chat_holder);
            newMessageNotification = v.findViewById(R.id.new_message_notification_chat_holder);

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

    public String handleTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        String dateString = app.getDateFromTimeStamp(timestamp);
        try {
            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return dateString.split(",")[1]+dateString.split(",")[3] ;
            }
            else {
                return dateString.split(",")[0] ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }
    public void updateLastMessage(int pos, Message lastMessage){
        list.get(pos).setLastMessage(lastMessage);
        moveToTop(list.get(pos));
    }
    private void moveToTop(Chat chat) {
        list.remove(chat);
        list.add(0, chat);
        notifyDataSetChanged();
    }
    public ArrayList<Chat>getList(){
        return list;
    }

}
