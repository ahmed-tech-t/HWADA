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
    User receiver;
    Timestamp timeStamp;
    public Chat() {
        messages = new ArrayList<>();
        lastMessage = new Message();
        receiver = new User();
        ad = new Ad();
    }

    public Chat(Ad ad, Timestamp timeStamp, String receiverId) {
        this.ad = ad;
        this.timeStamp = timeStamp;
        this.receiver = new User();
        this.receiver.setUId(receiverId);
    }


    public Chat(String id, Ad ad, Message lastMessage, Timestamp timeStamp, String receiverId) {
        this.id = id;
        this.ad = ad;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.receiver = new User();
        this.receiver.setUId(receiverId);
    }


    protected Chat(Parcel in) {
        id = in.readString();
        ad = in.readParcelable(Ad.class.getClassLoader());
        lastMessage = in.readParcelable(Message.class.getClassLoader());
        messages = in.createTypedArrayList(Message.CREATOR);
        receiver = in.readParcelable(User.class.getClassLoader());
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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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
        dest.writeParcelable(receiver, flags);
        dest.writeParcelable(timeStamp, flags);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Chat) {
            Chat otherObj = (Chat) other;
            return this.id.equals(otherObj.id);
        }
        return false;
    }

}
