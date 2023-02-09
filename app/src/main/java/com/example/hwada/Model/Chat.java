package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Chat implements Parcelable {
   String id;
    private Ad ad;
    Message lastMessage ;
    ArrayList<Message> messages ;
    String receiverId;
    Timestamp timeStamp;
    public Chat() {
        messages = new ArrayList<>();
        lastMessage = new Message();
        ad = new Ad();
    }

    public Chat(Ad ad, Timestamp timeStamp, String receiverId) {
        this.ad = ad;
        this.timeStamp = timeStamp;
        this.receiverId = receiverId;
    }


    public Chat(String id, Ad ad, Message lastMessage, Timestamp timeStamp, String receiverId) {
        this.id = id;
        this.ad = ad;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.receiverId = receiverId;
    }


    protected Chat(Parcel in) {
        id = in.readString();
        ad = in.readParcelable(Ad.class.getClassLoader());
        lastMessage = in.readParcelable(Message.class.getClassLoader());
        messages = in.createTypedArrayList(Message.CREATOR);
        receiverId = in.readString();
        timeStamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }





    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", ad=" + ad +
                ", lastMessage=" + lastMessage +
                ", timeStamp='" + timeStamp + '\'' +
                ", messages=" + messages +
                ", receiverId='" + receiverId + '\'' +
                '}'+"\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(ad, flags);
        dest.writeParcelable(lastMessage, flags);
        dest.writeTypedList(messages);
        dest.writeString(receiverId);
        dest.writeParcelable(timeStamp, flags);
    }
}
