package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class AdReview implements Parcelable {


    private String id;
    private Timestamp timeStamp;
    private String authorId;
    private String authorName;
    private String authorImage;
    private float rating;
    private String body;

    public AdReview(){}

    public AdReview(Timestamp timeStamp, String authorId, String authorName, String authorImage, float rating, String body) {
        this.timeStamp = timeStamp;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.rating = rating;
        this.body = body;
    }


    public AdReview(String id, Timestamp timeStamp, String authorId, String authorName, String authorImage, float rating, String body) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.rating = rating;
        this.body = body;
    }


    protected AdReview(Parcel in) {
        id = in.readString();
        timeStamp = in.readParcelable(Timestamp.class.getClassLoader());
        authorId = in.readString();
        authorName = in.readString();
        authorImage = in.readString();
        rating = in.readFloat();
        body = in.readString();
    }

    public static final Creator<AdReview> CREATOR = new Creator<AdReview>() {
        @Override
        public AdReview createFromParcel(Parcel in) {
            return new AdReview(in);
        }

        @Override
        public AdReview[] newArray(int size) {
            return new AdReview[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public float getRating() {
        return rating;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(timeStamp, flags);
        dest.writeString(authorId);
        dest.writeString(authorName);
        dest.writeString(authorImage);
        dest.writeFloat(rating);
        dest.writeString(body);
    }
}
