package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkingTime implements Parcelable {


    String from;
    String to ;

    public WorkingTime(){
        this.from="";
        this.to = "";
    }
    protected WorkingTime(Parcel in) {
        from = in.readString();
        to = in.readString();
    }

    public boolean isWithinPeriod(String time) {
        try {
            // Convert from and to strings to Date objects
            DateFormat df = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            Date fromDate = df.parse(this.from);
            Date toDate = df.parse(this.to);

            // Get current time
            Date currentTime = df.parse(time);

            // Check if current time is within the period
            assert currentTime != null;
            return currentTime.after(fromDate) && currentTime.before(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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

    @Override
    public String toString() {
        return "WorkingTime{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
