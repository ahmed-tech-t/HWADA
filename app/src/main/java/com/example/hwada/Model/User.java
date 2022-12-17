package com.example.hwada.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class User implements Parcelable {
    private String uId ;
    private  String username;
    private Location location;

    private ArrayList <String> favoriteAds;
    @SuppressWarnings("WeakerAccess")
    private  String email;
    private String phone ;
    private  String aboutYou;


    private String image;
    private  String gender;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    boolean isNew , isCreated;

    public User() {
        this.favoriteAds = new ArrayList<>();
    }

    public User(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.favoriteAds = new ArrayList<>();
    }

    public User(String uId, String email) {
        this.uId = uId;
        this.email = email;
        this.favoriteAds = new ArrayList<>();
    }
    public User(String uId, String username, String email, String phone) {
        this.uId = uId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.favoriteAds = new ArrayList<>();
    }


    protected User(Parcel in) {
        uId = in.readString();
        username = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        favoriteAds = in.createStringArrayList();
        email = in.readString();
        phone = in.readString();
        aboutYou = in.readString();
        image = in.readString();
        gender = in.readString();
        isAuthenticated = in.readByte() != 0;
        isNew = in.readByte() != 0;
        isCreated = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAboutYou() {
        return aboutYou;
    }

    public void setAboutYou(String aboutYou) {
        this.aboutYou = aboutYou;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<String> getFavoriteAds() {
        return favoriteAds;
    }
    public void addOneAdToFavorite(String ad) {
        this.favoriteAds.add(ad);
    }
    public void removeOneAdFromFavorite(String ad) {
        this.favoriteAds.remove(ad);
    }
    public void setFavoriteAds(ArrayList<String> favoriteAds) {
        this.favoriteAds = favoriteAds;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(uId);
        dest.writeString(username);
        dest.writeParcelable(location, flags);
        dest.writeStringList(favoriteAds);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(aboutYou);
        dest.writeString(image);
        dest.writeString(gender);
        dest.writeByte((byte) (isAuthenticated ? 1 : 0));
        dest.writeByte((byte) (isNew ? 1 : 0));
        dest.writeByte((byte) (isCreated ? 1 : 0));
    }
}
