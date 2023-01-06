package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DebugModel implements Parcelable {
    String date ;
    String body;
    String massage ;
    boolean fixed;
    int androidSdk ;
    String tag;


    public DebugModel(String date,String massage, String body, String tag, int androidSdk, boolean fixed) {
        this.date = date;
        this.massage = massage ;
        this.body = body;
        this.androidSdk = androidSdk;
        this.fixed = fixed;
        this.tag = tag;
    }

    public DebugModel(){
        fixed =false;
    }


    protected DebugModel(Parcel in) {
        date = in.readString();
        body = in.readString();
        massage = in.readString();
        fixed = in.readByte() != 0;
        androidSdk = in.readInt();
        tag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(body);
        dest.writeString(massage);
        dest.writeByte((byte) (fixed ? 1 : 0));
        dest.writeInt(androidSdk);
        dest.writeString(tag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DebugModel> CREATOR = new Creator<DebugModel>() {
        @Override
        public DebugModel createFromParcel(Parcel in) {
            return new DebugModel(in);
        }

        @Override
        public DebugModel[] newArray(int size) {
            return new DebugModel[size];
        }
    };

    public int getAndroidSdk() {
        return androidSdk;
    }

    public void setAndroidSdk(int androidSdk) {
        this.androidSdk = androidSdk;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }


}
