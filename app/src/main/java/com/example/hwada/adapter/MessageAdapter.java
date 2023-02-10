package com.example.hwada.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.util.GlideImageLoader;
import com.example.hwada.viewmodel.MessageViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
            Message message = list.get(position);
            // set body text
            if(message.getBody() != null && message.getBody().length()>25){
                handelLayouts(message,((SenderViewHolder) holder).body_tv,((SenderViewHolder) holder).llDate,((SenderViewHolder) holder).rlImage,((SenderViewHolder) holder).clBody);
            }

            setBodyText(position,((SenderViewHolder) holder).body_tv);

            // set body image
            if(message.getUrl()!= null ||message.getUri() != null){
                handelLayouts(message,((SenderViewHolder) holder).body_tv,((SenderViewHolder) holder).llDate,((SenderViewHolder) holder).rlImage,((SenderViewHolder) holder).clBody);
                ((SenderViewHolder) holder).rlImage.setVisibility(View.VISIBLE);

                setImage(position , ((SenderViewHolder) holder).image_sim,((SenderViewHolder) holder).progressBar);
            }

            //set date
            setDate(position , ((SenderViewHolder) holder).date_tv);

            if(message.getBody()!= null && message.getBody().length()==0 || message.getBody()== null){
                ((SenderViewHolder) holder).date_tv.setTextColor(ContextCompat.getColor(mContext,R.color.whiteCoffee));
            }


            // set status
            setStatus(position , ((SenderViewHolder) holder).status_im);

        }
        else if (holder instanceof ReceiverViewHolder){
            holder.setIsRecyclable(false);

            Message message =  list.get(position);
            // set body text
            if(message.getBody() != null && message.getBody().length()>25){
                handelLayouts(message,((ReceiverViewHolder) holder).body_tv,((ReceiverViewHolder) holder).llDate,((ReceiverViewHolder) holder).rlImage,((ReceiverViewHolder) holder).clBody);
            }

            setBodyText(position,((ReceiverViewHolder) holder).body_tv);


            if(message.getUrl()!= null || message.getUri()!= null){
                handelLayouts(message,((ReceiverViewHolder) holder).body_tv,((ReceiverViewHolder) holder).llDate,((ReceiverViewHolder) holder).rlImage,((ReceiverViewHolder) holder).clBody);

                ((ReceiverViewHolder) holder).rlImage.setVisibility(View.VISIBLE);
                setImage(position , ((ReceiverViewHolder) holder).image_sim,((ReceiverViewHolder) holder).progressBar);
            }

            setDate(position , ((ReceiverViewHolder) holder).date_tv);

            if(message.getBody()!= null && message.getBody().length()==0||(message.getBody()== null)) {
                ((ReceiverViewHolder) holder).date_tv.setTextColor(ContextCompat.getColor(mContext, R.color.whiteCoffee));
            }

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

     private void setImage(int pos , ShapeableImageView image,ProgressBar progressBar){
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
           progressBar.setVisibility(View.VISIBLE);
           Picasso.get().load(uri).placeholder(R.color.forest_Green).into(image, new Callback() {
                       @Override
                       public void onSuccess() {
                           progressBar.setVisibility(View.GONE);
                       }

                       @Override
                       public void onError(Exception e) {
                           progressBar.setVisibility(View.GONE);
                       }
                   });


       }else {
           String url = list.get(pos).getUrl();
           String fileName = "Title_" + list.get(pos).getId() + ".jpg";
           File imageFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
           if (imageFile.exists()) {
               Log.e(TAG, "setImage: exist" );
               // Load image from the device storage
               progressBar.setVisibility(View.GONE);
               Uri imageUri = Uri.fromFile(imageFile);
               Picasso.get().load(imageUri).into(image);
               list.get(pos).setUri(imageUri);
           } else {
               Log.e(TAG, "setImage: not Exist" );
               // Make API call to download image
               progressBar.setVisibility(View.VISIBLE);
               Picasso.get().load(url).placeholder(R.color.forest_Green).into(image, new Callback() {
                   @Override
                   public void onSuccess() {
                       progressBar.setVisibility(View.GONE);
                       Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                       Uri imageUri = getImageUri(list.get(pos), mContext, bitmap);
                       list.get(pos).setUri(imageUri);
                   }

                   @Override
                   public void onError(Exception e) {
                       progressBar.setVisibility(View.GONE);
                       e.getMessage();
                   }
               });
           }
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


    private void handelLayouts(Message message,TextView textView , LinearLayout linearLayout , RelativeLayout relativeLayout , ConstraintLayout constraintLayout){

        ConstraintLayout.LayoutParams tvParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
        tvParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        tvParams.endToEnd  = ConstraintLayout.LayoutParams.PARENT_ID;
        tvParams.endToStart =  ConstraintLayout.LayoutParams.UNSET;
        textView.setLayoutParams(tvParams);

        ConstraintLayout.LayoutParams llParams = (ConstraintLayout.LayoutParams) linearLayout.getLayoutParams();
        llParams.topToBottom = textView.getId();
        linearLayout.setLayoutParams(llParams);


        //if their is text
            ConstraintLayout.LayoutParams clParams = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
            //clParams.topToBottom = relativeLayout.getId();

            if(message.getUrl()!= null || message.getUri()!= null){
                clParams.width = 0;
            }
           if(message.getBody()!=null && message.getBody().length()>0){
              clParams.topToBottom = relativeLayout.getId();
            }
        constraintLayout.setLayoutParams(clParams);

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
        LinearLayout llParent ,llChatBody,llBackground,llDate;
        RelativeLayout rlImage;
        ProgressBar progressBar;
        ConstraintLayout clBody;
        OnItemListener onItemListener;
        public SenderViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            body_tv = v.findViewById(R.id.tv_body_chat_sender_holder);
            date_tv =v.findViewById(R.id.tv_date_chat_sender_holder);
            image_sim =v.findViewById(R.id.sim_body_chat_sender_holder);
            status_im = v.findViewById(R.id.im_message_status_chat_sender_holder);

            llDate = v.findViewById(R.id.ll_date_chat_sender_holder);
            rlImage =v.findViewById(R.id.rl_image_chat_sender_holder);
            clBody =v.findViewById(R.id.cl_body_image_chat_sender_holder);
            progressBar = v.findViewById(R.id.progressBar_image_chat_sender_holder);

            this.onItemListener = onItemListener;
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(list.get(getAdapterPosition()).getUri()==null){
                onItemListener.getImagePosition(list.get(getAdapterPosition()).getUrl());
            }else onItemListener.getImagePosition(list.get(getAdapterPosition()).getUri().toString());
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
         TextView body_tv , date_tv;
         ShapeableImageView image_sim;
         LinearLayout llParent ,llChatBody,llBackground , llDate;
        ProgressBar progressBar;
        RelativeLayout rlImage;
        ConstraintLayout clBody;

        OnItemListener onItemListener;
        public ReceiverViewHolder(@NonNull View v, OnItemListener onItemListener) {
            super(v);
            body_tv = v.findViewById(R.id.tv_body_chat_receiver_holder);
            date_tv =v.findViewById(R.id.tv_date_chat_receiver_holder);
            image_sim =v.findViewById(R.id.sim_body_chat_receiver_holder);

            llDate = v.findViewById(R.id.ll_date_chat_receiver_holder);
            rlImage =v.findViewById(R.id.rl_image_chat_receiver_holder);
            clBody =v.findViewById(R.id.cl_body_image_chat_receiver_holder);

            progressBar = v.findViewById(R.id.progressBar_image_chat_receiver_holder);

            this.onItemListener = onItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(list.get(getAdapterPosition()).getUri()==null){
                onItemListener.getImagePosition(list.get(getAdapterPosition()).getUrl());
            }else onItemListener.getImagePosition(list.get(getAdapterPosition()).getUri().toString());
        }
    }

    public void addItem(Message m){
        list.add(m);
        notifyItemInserted(list.size() - 1);
   }

    public interface OnItemListener {
        void getImagePosition(String uri);
    }
    public ArrayList<Message> getList(){
        return list;
    }
    public void updateItem(int position, Message message) {
        list.set(position, message);
        notifyItemChanged(position);
    }
    public Uri getImageUri(Message message , Context inContext, Bitmap inImage) {
        String fileName = "Title_" + message.getId() + ".jpg";
        File file = new File(inContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

            if (app.checkStoragePermissions()) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Uri.fromFile(file);
            } else {
                app.requestStoragePermissions(mContext);
                return null;
            }

    }

}
