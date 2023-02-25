package com.example.hwada.adapter;

import android.annotation.SuppressLint;
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
import com.example.hwada.databinding.ChatHolderLayoutBinding;
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
    ChatHolderLayoutBinding binding;
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
        binding = ChatHolderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ChatViewHolder(binding.getRoot(),pOnItemListener);
    }

    @Override
    public long getItemId(int position) {
        return  list.get(position).getId().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.setIsRecyclable(false);

       //add title and image
        Glide.with(mContext).load(list.get(position).getAd().getImagesUrl().get(0)).into(holder.adImage);
        holder.title.setText(list.get(position).getAd().getTitle());

        // add author name and image
        Glide.with(mContext).load(list.get(position).getReceiver().getImage()).into(holder.receiverImage);
        holder.receiverName.setText(list.get(position).getReceiver().getUsername());

        handelLastMessage(holder,position);

        holder.date.setText(handleTime(list.get(position).getLastMessage().getTimeStamp()));
    }


    private void handelLastMessage(ChatViewHolder holder ,int position){
        Message lastMessage = list.get(position).getLastMessage();

        if (lastMessage == null) {
            holder.messageStatus.setVisibility(View.GONE);
            holder.date.setText(handleTime(list.get(position).getTimeStamp()));
            holder.lastMessage.setText(mContext.getString(R.string.noMessages));
        }else {
            //set body
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
          handelMessageStatus(holder,position);

          handelMessagesAlert(holder,position);
        }
    }

    private void handelMessagesAlert(ChatViewHolder holder ,int position){
        Message lastMessage = list.get(position).getLastMessage();
        //set alert if their is new messages
        if(lastMessage.getReceiverId().equals(userId)){
            if(!lastMessage.isSeen()){
                holder.newMessageNotification.setVisibility(View.VISIBLE);
                holder.date.setTextColor(ContextCompat.getColor(mContext,R.color.background));
            }else{
                holder.newMessageNotification.setVisibility(View.GONE);
                holder.date.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
            }
        }else {
            holder.newMessageNotification.setVisibility(View.GONE);
            holder.date.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
        }
    }
    private void handelMessageStatus(ChatViewHolder holder,int position){
        Message lastMessage = list.get(position).getLastMessage();
        
        if (lastMessage.getSenderId().equals(userId)) {
            holder.messageStatus.setVisibility(View.VISIBLE);
            setStatus(position, holder.messageStatus);
        }else holder.messageStatus.setVisibility(View.GONE);
    }
    

    private void setStatus(int pos , ImageView status_im){
        Message message = list.get(pos).getLastMessage();

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

    @SuppressLint("NotifyDataSetChanged")
    public void setList(String userId , ArrayList<Chat> list, OnItemListener onItemListener) {
        this.userId=userId;
        this.list = list;
        this.pOnItemListener = onItemListener;
        notifyDataSetChanged();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        TextView date ,title, lastMessage , receiverName;
        ImageView adImage, messageStatus ,imageIcon;
        ShapeableImageView newMessageNotification,receiverImage;
        public ChatViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            date =binding.dateLastMessageChatHolder;
            receiverName = binding.receiverNameChatHolder;
            receiverImage = binding.simReceiverImageChatHolder;
            title = binding.chatTitleChatHolder;
            lastMessage =binding.lastMessageChatHolder;
            adImage =binding.chatImageChatHolder;
            messageStatus = binding.messageStatusChatHolder;
            imageIcon = binding.imageIconChatHolder;
            newMessageNotification = binding.newMessageNotificationChatHolder;

            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
            adImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==adImage.getId()){
                onItemListener.pressedImagePosition(getBindingAdapterPosition());
            }else onItemListener.getItemPosition(getBindingAdapterPosition());
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

    @SuppressLint("NotifyDataSetChanged")
    private void moveToTop(Chat chat,int pos) {
        list.remove(chat);
        notifyItemRemoved(pos);
        list.add(0, chat);
        notifyItemInserted(0);
    }
    public ArrayList<Chat>getList(){
        return list;
    }
    public void addItem(Chat c) {
        list.add(0,c);
        notifyItemInserted(0);
    }
    public void updateChatWithNewMessage(Chat chat ,int pos){
        list.set(pos,chat);
        moveToTop(chat,pos);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateChat(Chat chat , int pos){
        list.set(pos,chat);
        notifyItemChanged(pos,chat);
    }
}
