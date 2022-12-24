package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyReview implements Parcelable {
    String id;
    String addId;
    String category;
    String subCategory;
    String subSubCategory;



    public MyReview(String addId, String category, String subCategory, String subSubCategory) {
        this.addId = addId;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory =subSubCategory;
    }


    protected MyReview(Parcel in) {
        id = in.readString();
        addId = in.readString();
        category = in.readString();
        subCategory = in.readString();
        subSubCategory = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(addId);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(subSubCategory);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddId() {
        return addId;
    }

    public void setAddId(String addId) {
        this.addId = addId;
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
        subCategory = subCategory;
    }
    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        this.subSubCategory = subSubCategory;
    }

}
