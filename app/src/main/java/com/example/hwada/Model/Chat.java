package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Chat implements Parcelable {
    private Ad ad;
    Message lastMessage ;
    String date ;
    ArrayList<Message> messages ;
    String receiverId;

    public Chat() {
        messages = new ArrayList<>();
        lastMessage = new Message();
        ad = new Ad();
    }

    public Chat( Ad ad, ArrayList<Message> messages) {
        this.ad = ad;
        this.messages = messages;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Chat(Ad ad, String date, Message lastMessage) {
        this.ad = ad;
        this.date = date;
        this.lastMessage = lastMessage;
    }

    public Chat(Ad ad ,String date) {
        this.ad = ad;
        this.date = date;
    }


    protected Chat(Parcel in) {
        ad = in.readParcelable(Ad.class.getClassLoader());
        lastMessage = in.readParcelable(Message.class.getClassLoader());
        date = in.readString();
        messages = in.createTypedArrayList(Message.CREATOR);
        receiverId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(ad, flags);
        dest.writeParcelable(lastMessage, flags);
        dest.writeString(date);
        dest.writeTypedList(messages);
        dest.writeString(receiverId);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }


}
