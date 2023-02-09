package com.example.hwada.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.util.GlideImageLoader;
import com.example.hwada.viewmodel.MessageViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
     private ArrayList<Message> list = new ArrayList();
     String userId ;
     private Picasso picassoInstance;

     MessageViewModel messageViewModel ;
     private static final String TAG = "MessageAdapter";
     Context mContext;
     String chatId ;
     private static final int SENDER = 1;
     private static final int RECEIVER = 2;

     App app;
    OnItemListener pOnItemListener;



    public MessageAdapter(Context context,String chatId,MessageViewModel messageViewModel){
        this.mContext =context;
        this.chatId =chatId;
        this.messageViewModel = messageViewModel;
        app = (App) mContext.getApplicationContext();
       setHasStableIds(true);
       picassoInstance = Picasso.get();
       picassoInstance.setLoggingEnabled(true);
    }
     @NonNull
     @Override
     public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(SENDER == viewType){
            return new SenderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender_holder, parent, false), pOnItemListener);
        }else if(RECEIVER == viewType){
            return new ReceiverViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recevier_holder, parent, false), pOnItemListener);
        }
        return null;
    }

     @Override
     public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SenderViewHolder ) {
            holder.setIsRecyclable(false);

            // set body text
            if(list.get(position).getBody() != null && list.get(position).getBody().length()>25){
                setLayoutToVertical(((SenderViewHolder) holder).llParent,((SenderViewHolder) holder).llChatBody);
            }

            setBodyText(position,((SenderViewHolder) holder).body_tv);

            // set body image
            if(list.get(position).getUrl()!=null || list.get(position).getUri()!= null){

                setLayoutToVertical(((SenderViewHolder) holder).llParent,((SenderViewHolder) holder).llChatBody);

                setImage(position , ((SenderViewHolder) holder).image_sim);
            }

            //set date
            setDate(position , ((SenderViewHolder) holder).date_tv);


            // set status
            setStatus(position , ((SenderViewHolder) holder).status_im);

        }
        else if (holder instanceof ReceiverViewHolder){
            holder.setIsRecyclable(false);

            // set body text
            if(list.get(position).getBody() != null && list.get(position).getBody().length()>25){
                setLayoutToVertical(((ReceiverViewHolder) holder).llParent,((ReceiverViewHolder) holder).llChatBody);
            }

            setBodyText(position,((ReceiverViewHolder) holder).body_tv);


            if(list.get(position).getUrl()!=null || list.get(position).getUri()!=null){
                setLayoutToVertical(((ReceiverViewHolder) holder).llParent,((ReceiverViewHolder) holder).llChatBody);

                setImage(position , ((ReceiverViewHolder) holder).image_sim);
            }

            setDate(position , ((ReceiverViewHolder) holder).date_tv);

            setMessageStatusToSeen(position);
        }
    }


     @Override
     public long getItemId(int position) {
        return position;
    }

     private void setBodyText(int pos , TextView body_tv){
        if(list.get(pos).getBody()!=null && list.get(pos).getBody().length()>0){
            String body = list.get(pos).getBody();
            body_tv.setText(body);

        }else {
            body_tv.setVisibility(View.GONE);
        }
    }

    private void setMessageStatusToSeen(int pos){
        Message message = list.get(pos);
        if(userId.equals(message.getReceiverId())){
            if(!message.isSeen()){
                messageViewModel.setMessagesStatusToSeen(message,chatId);
            }
        }
    }
     private void setLayoutToVertical( LinearLayout llParent , LinearLayout llBodyChild ){
       llParent.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llBodyChild.getLayoutParams();
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        llBodyChild.setLayoutParams(params);
    }

     private void setImage(int pos , ShapeableImageView image){
       image.setVisibility(View.VISIBLE);
       double imageWidth = image.getWidth();
       double imageHeight = image.getHeight();
       if(imageHeight>imageWidth*1.5){
           imageHeight = imageWidth * 1.5;
           LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) image.getLayoutParams();
           params.height = (int) imageHeight;
           image.setLayoutParams(params);
       }

       if(list.get(pos).getUri() != null){
          Uri uri = list.get(pos).getUri();
       Picasso.get()
                   .load(uri)
                   .placeholder(R.color.forest_Green)
                   .into(image);

       }else {

           String url = list.get(pos).getUrl();
           Picasso.get()
                   .load(url)
                   .placeholder(R.color.forest_Green)
                   .into(image);

       }
     }

     private void setDate(int pos , TextView date_tv){
        String date = app.getDateFromTimeStamp(list.get(pos).getTimeStamp()) ;
        Log.w(TAG, "setDate: "+date );
        String time = date.split(",")[1]+date.split(",")[3];
        date_tv.setText(time);
    }

    private void setStatus(int pos , ImageView status_im){
        Message message =  list.get(pos);
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
    public int getItemViewType(int position) {
       Message message = list.get(position);
        if (message.getSenderId().equals(userId)) {

            return SENDER;
        } else{

            return RECEIVER;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Message> list , String userId , OnItemListener onItemListener) {
        this.list = list;
        this.mContext = mContext;
        this.userId = userId ;
        this.pOnItemListener = onItemListener;

        notifyDataSetChanged();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView body_tv , date_tv;
        ShapeableImageView image_sim;
        ImageView status_im;
        LinearLayout llParent ,llChatBody,llBackground;

        OnItemListener onItemListener;
        public SenderViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            body_tv = v.findViewById(R.id.tv_body_chat_sender_holder);
            date_tv =v.findViewById(R.id.tv_date_chat_sender_holder);
            image_sim =v.findViewById(R.id.sim_body_chat_sender_holder);
            status_im = v.findViewById(R.id.im_message_status_chat_sender_holder);
            llParent = v.findViewById(R.id.ll_parent_bubble_chat_sender);
            llChatBody =v.findViewById(R.id.ll_child_body_bubble_chat_sender);
            llBackground=v.findViewById(R.id.ll_background_bubble_chat_sender);

            this.onItemListener = onItemListener;
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListener.getImagePosition(getAdapterPosition());
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         TextView body_tv , date_tv;
         ShapeableImageView image_sim;
         LinearLayout llParent ,llChatBody,llBackground;

        OnItemListener onItemListener;
        public ReceiverViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            body_tv = v.findViewById(R.id.tv_body_chat_receiver_holder);
            date_tv =v.findViewById(R.id.tv_date_chat_receiver_holder);
            image_sim =v.findViewById(R.id.sim_body_chat_receiver_holder);
            llParent = v.findViewById(R.id.ll_parent_bubble_chat_receiver);
            llChatBody =v.findViewById(R.id.ll_child_body_bubble_chat_receiver);
            llBackground=v.findViewById(R.id.ll_background_bubble_chat_receiver);
            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.getImagePosition(getAdapterPosition());
        }
    }

    public void addItem(Message m){
        list.add(m);
        notifyItemInserted(list.size() - 1);
   }

    public interface OnItemListener {
        void getImagePosition(int position);
    }
    public ArrayList<Message> getList(){
        return list;
    }
    public void updateItem(int position, Message message) {
        list.set(position, message);
        notifyItemChanged(position);
    }

}
