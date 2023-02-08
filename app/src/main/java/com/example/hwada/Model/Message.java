package com.example.hwada.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class Message implements Parcelable {

    String id ;
    String date ;
    String body ;
    String url ;
    Uri uri ;
    boolean seen ;
    boolean delivered ;
    boolean sent ;
    String senderId;
    String receiverId;

    public Message() {
        seen = false ;
        delivered = false ;
        sent = false ;
    }

    public Message(String date, String body, String senderId, String receiverId) {
        this.date = date;
        this.body = body;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public Message(String date, String body,Uri uri, String senderId, String receiverId) {
        this.date = date;
        this.body = body;
        this.uri = uri;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public Message(String date, Uri uri, String senderId, String receiverId) {
        this.date = date;
        this.uri = uri;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Message(String date, String body, String url) {
        this.date = date;
        this.body = body;
        this.url = url;
        seen = false ;
        delivered = false ;
        sent = false ;
    }


    protected Message(Parcel in) {
        id = in.readString();
        date = in.readString();
        body = in.readString();
        url = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        seen = in.readByte() != 0;
        delivered = in.readByte() != 0;
        sent = in.readByte() != 0;
        senderId = in.readString();
        receiverId = in.readString();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
        dest.writeString(body);
        dest.writeString(url);
        dest.writeParcelable(uri, flags);
        dest.writeByte((byte) (seen ? 1 : 0));
        dest.writeByte((byte) (delivered ? 1 : 0));
        dest.writeByte((byte) (sent ? 1 : 0));
        dest.writeString(senderId);
        dest.writeString(receiverId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", uri=" + uri +
                ", seen=" + seen +
                ", delivered=" + delivered +
                ", sent=" + sent +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                '}';
    }
}
