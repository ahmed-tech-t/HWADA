package com.example.hwada.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LocationCustom implements Parcelable {

    double latitude ;
    double longitude ;

    public LocationCustom() {
    }
    public LocationCustom(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected LocationCustom(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<LocationCustom> CREATOR = new Creator<LocationCustom>() {
        @Override
        public LocationCustom createFromParcel(Parcel in) {
            return new LocationCustom(in);
        }

        @Override
        public LocationCustom[] newArray(int size) {
            return new LocationCustom[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
