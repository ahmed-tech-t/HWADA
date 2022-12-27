package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class WorkingTime implements Parcelable {
    String from;
    String to ;

    public WorkingTime(){
        this.from="_______";
        this.to = "_______";
    }
    protected WorkingTime(Parcel in) {
        from = in.readString();
        to = in.readString();
    }

    public static final Creator<WorkingTime> CREATOR = new Creator<WorkingTime>() {
        @Override
        public WorkingTime createFromParcel(Parcel in) {
            return new WorkingTime(in);
        }

        @Override
        public WorkingTime[] newArray(int size) {
            return new WorkingTime[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public WorkingTime(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
    }
}
