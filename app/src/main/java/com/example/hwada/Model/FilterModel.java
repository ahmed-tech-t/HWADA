package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FilterModel implements Parcelable {
    String Sort ;
    boolean open ;

    public FilterModel(String sort,boolean isOpen) {
        Sort = sort;
        this.open = isOpen;
    }

    protected FilterModel(Parcel in) {
        Sort = in.readString();
        open = in.readByte() != 0;
    }

    public static final Creator<FilterModel> CREATOR = new Creator<FilterModel>() {
        @Override
        public FilterModel createFromParcel(Parcel in) {
            return new FilterModel(in);
        }

        @Override
        public FilterModel[] newArray(int size) {
            return new FilterModel[size];
        }
    };

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Sort);
        dest.writeByte((byte) (open ? 1 : 0));
    }
}
