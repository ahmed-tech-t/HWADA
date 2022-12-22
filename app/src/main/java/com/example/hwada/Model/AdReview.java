package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class AdReview implements Parcelable {


    private String id;
    private String date;
    private String authorId;
    private String authorName;
    private String authorImage;
    private double rating;
    private String body;

    public AdReview(){}

    public AdReview(String date, String authorId, String authorName, String authorImage, double rating, String body) {
        this.date = date;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImage = authorImage;
        this.rating = rating;
        this.body = body;
    }


    protected AdReview(Parcel in) {
        id = in.readString();
        date = in.readString();
        authorId = in.readString();
        authorName = in.readString();
        authorImage = in.readString();
        rating = in.readDouble();
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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
        dest.writeString(date);
        dest.writeString(authorId);
        dest.writeString(authorName);
        dest.writeString(authorImage);
        dest.writeDouble(rating);
        dest.writeString(body);
    }
}
