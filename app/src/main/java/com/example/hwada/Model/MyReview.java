package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyReview implements Parcelable {
    String id;
    String addId;
    String category;
    String SubCategory;

    public MyReview(String addId, String category, String subCategory) {
        this.addId = addId;
        this.category = category;
        SubCategory = subCategory;
    }

    protected MyReview(Parcel in) {
        id = in.readString();
        addId = in.readString();
        category = in.readString();
        SubCategory = in.readString();
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
        return SubCategory;
    }

    public void setSubCategory(String subCategory) {
        SubCategory = subCategory;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(addId);
        dest.writeString(category);
        dest.writeString(SubCategory);
    }
}
