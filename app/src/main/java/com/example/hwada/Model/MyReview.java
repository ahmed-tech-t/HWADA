package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.PropertyName;

import java.util.Map;

public class MyReview implements Parcelable {
    String reviewId;
    String adId;
    String adAuthorId;
    String category;
    String subCategory;
    String subSubCategory;


    public MyReview() {
    }

    public MyReview(String adId, String category, String subCategory, String subSubCategory) {
        this.adId = adId;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }

    public MyReview(String reviewId, String adId, String adAuthorId, String category, String subCategory, String subSubCategory) {
        this.reviewId = reviewId;
        this.adId = adId;
        this.adAuthorId = adAuthorId;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }

    protected MyReview(Parcel in) {
        reviewId = in.readString();
        adId = in.readString();
        adAuthorId = in.readString();
        category = in.readString();
        subCategory = in.readString();
        subSubCategory = in.readString();
    }

    public static final Creator<MyReview> CREATOR = new Creator<MyReview>() {
        @Override
        public MyReview createFromParcel(Parcel in) {
            return new MyReview(in);
        }

        @Override
        public MyReview[] newArray(int size) {
            return new MyReview[size];
        }
    };

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdAuthorId() {
        return adAuthorId;
    }

    public void setAdAuthorId(String adAuthorId) {
        this.adAuthorId = adAuthorId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

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
    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        this.subSubCategory = subSubCategory;
    }

    @Override
    public String toString() {
        return "MyReview{" +
                "reviewId='" + reviewId + '\'' +
                ", adId='" + adId + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", subSubCategory='" + subSubCategory + '\'' +
                '}';
    }
    public MyReview(Map<String,Object>data){
        this.reviewId = data.get("reviewId").toString();
        this.adId = data.get("adId").toString();
        this.category = data.get("category").toString();
        this.subCategory = data.get("subCategory").toString();
        this.subSubCategory =data.get("subSubCategory").toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(reviewId);
        dest.writeString(adId);
        dest.writeString(adAuthorId);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(subSubCategory);
    }
}
