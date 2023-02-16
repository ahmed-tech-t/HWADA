package com.example.hwada.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;


public class Message implements Parcelable {

    String id ;
    String body ;
    String url ;
    Uri uri ;
    boolean seen ;
    boolean delivered ;
    boolean sent ;
    String senderId;
    String receiverId;
    Timestamp timeStamp;
    public Message() {
        seen = false ;
        delivered = false ;
        sent = false ;
    }

    public Message(String id, Timestamp timeStamp, String body, String url ,boolean seen, boolean delivered, boolean sent, String senderId, String receiverId) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.body = body;
        this.url = url;
        this.seen = seen;
        this.delivered = delivered;
        this.sent = sent;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message(Timestamp timeStamp, String body, String senderId, String receiverId) {
        this.timeStamp = timeStamp;
        this.body = body;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public Message(Timestamp timeStamp, String body,Uri uri, String senderId, String receiverId) {
        this.timeStamp = timeStamp;
        this.body = body;
        this.uri = uri;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public Message(Timestamp timeStamp, Uri uri, String senderId, String receiverId) {
        this.timeStamp = timeStamp;
        this.uri = uri;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message(Timestamp timeStamp, String body, String url) {
        this.timeStamp = timeStamp;
        this.body = body;
        this.url = url;
        seen = false ;
        delivered = false ;
        sent = false ;
    }


    protected Message(Parcel in) {
        id = in.readString();
        body = in.readString();
        url = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        seen = in.readByte() != 0;
        delivered = in.readByte() != 0;
        sent = in.readByte() != 0;
        senderId = in.readString();
        receiverId = in.readString();
        timeStamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(body);
        dest.writeString(url);
        dest.writeParcelable(uri, flags);
        dest.writeByte((byte) (seen ? 1 : 0));
        dest.writeByte((byte) (delivered ? 1 : 0));
        dest.writeByte((byte) (sent ? 1 : 0));
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeParcelable(timeStamp, flags);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", uri=" + uri +
                ", seen=" + seen +
                ", delivered=" + delivered +
                ", sent=" + sent +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                '}'+"\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            return this.getId().equals(message.getId());
        }
        return false;
    }

    public boolean isStatusChanged(Message m){
        if(this.sent!= m.sent)return true;
        if (this.delivered!= m.delivered)return true;
        if(this.seen != m.seen) return  true;
        return false;
    }

    public String printStatus(){
        return  ", seen=" + seen +
                ", delivered=" + delivered +
                ", sent=" + sent ;
    }


}
