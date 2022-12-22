package com.example.hwada.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Ad implements Parcelable {


    private String id;
    private String authorId;
    private String authorName;
    private Location authorLocation;
    private String title;
    private String description;
    private String date ;
    private String distance ;
    private double rating;
    private String category;
    private String subCategory;
    private ArrayList<AdReview> adReviews;


    public Ad(){
        this.adReviews =new ArrayList<>();
    }

    public Ad(String authorId, String authorName, Location authorLocation, String title, String description, String date, String category, String subCategory) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorLocation = authorLocation;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.subCategory = subCategory;
        this.adReviews =new ArrayList<>();

    }

    protected Ad(Parcel in) {
        id = in.readString();
        authorId = in.readString();
        authorName = in.readString();
        authorLocation = in.readParcelable(Location.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        date = in.readString();
        distance = in.readString();
        rating = in.readDouble();
        category = in.readString();
        subCategory = in.readString();
        adReviews = in.createTypedArrayList(AdReview.CREATOR);
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

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

    public Location getAuthorLocation() {
        return authorLocation;
    }

    public void setAuthorLocation(Location authorLocation) {
        this.authorLocation = authorLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<AdReview> getAdReviews() {
        return adReviews;
    }

    public void setAdToReviewsList(AdReview adReview) {
        adReviews.add(adReview);
    }

    public void removeAdFromReviewsList(int pos) {
        adReviews.remove(pos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(authorId);
        dest.writeString(authorName);
        dest.writeParcelable(authorLocation, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(distance);
        dest.writeDouble(rating);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeTypedList(adReviews);
    }
}
