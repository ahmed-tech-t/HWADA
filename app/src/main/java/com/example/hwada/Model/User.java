package com.example.hwada.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class User implements Parcelable {
    private String uId ;
    private  String username;
    private LocationCustom location;
    private ArrayList<Ad>favAds;
    private ArrayList<Ad>ads;

    private float rating;
    private ArrayList<MyReview> myReviews;

    private  String email;
    private String phone ;
    private  String aboutYou;
    private String image;
    private  String gender;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    boolean isNew , isCreated;

    public User(String uId, String username, LocationCustom location, float rating, String email, String phone, String aboutYou, String image, String gender) {
        this.uId = uId;
        this.username = username;
        this.location = location;
        this.rating = rating;
        this.email = email;
        this.phone = phone;
        this.aboutYou = aboutYou;
        this.image = image;
        this.gender = gender;
    }

    public User(String uId, String username, LocationCustom location, ArrayList<Ad> favAds, ArrayList<Ad> ads, float rating, ArrayList<MyReview> myReviews, String email, String phone, String aboutYou, String image, String gender) {
        this.uId = uId;
        this.username = username;
        this.location = location;
        this.favAds = favAds;
        this.ads = ads;
        this.rating = rating;
        this.myReviews = myReviews;
        this.email = email;
        this.phone = phone;
        this.aboutYou = aboutYou;
        this.image = image;
        this.gender = gender;
    }

    public User(){
        this.favAds =new ArrayList<>();
        this.ads =new ArrayList<>();
        this.myReviews=new ArrayList<>();
    }
    public User(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.favAds =new ArrayList<>();
        this.ads =new ArrayList<>();
        this.myReviews=new ArrayList<>();
    }

    public User(String uId, String email) {
        this.uId = uId;
        this.email = email;
        this.favAds =new ArrayList<>();
        this.ads =new ArrayList<>();
        this.myReviews=new ArrayList<>();
    }
    public User(String uId, String username, String email, String phone) {
        this.uId = uId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.favAds =new ArrayList<>();
        this.ads =new ArrayList<>();
        this.myReviews=new ArrayList<>();
    }


    protected User(Parcel in) {
        uId = in.readString();
        username = in.readString();
        location = in.readParcelable(LocationCustom.class.getClassLoader());
        favAds = in.createTypedArrayList(Ad.CREATOR);
        ads = in.createTypedArrayList(Ad.CREATOR);
        rating = in.readFloat();
        myReviews = in.createTypedArrayList(MyReview.CREATOR);
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

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
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

    public LocationCustom getLocation() {
        return location;
    }

    public void setLocation(LocationCustom location) {
        this.location = location;
    }

    public void setAdToFavAdsList(Ad ad){
        this.favAds.add(ad);
    }
    public void setAdToAdsList(Ad ad){
        this.ads.add(ad);
    }
    public void setReviewToMyReviewsList(MyReview review){
        this.myReviews.add(review);
    }
    public void removeAdFromAdsFavList(int pos){
        this.favAds.remove(pos);
    }
    public void removeAdFromAdsList(int pos){
        this.ads.remove(pos);
    }

    public ArrayList<Ad> getFavAds() {
        return favAds;
    }

    public void setFavAds(ArrayList<Ad> favAds) {
        this.favAds = favAds;
    }

    public ArrayList<Ad> getAds() {
        return ads;
    }

    public void setAds(ArrayList<Ad> ads) {
        this.ads = ads;
    }

    public ArrayList<MyReview> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(ArrayList<MyReview> myReviews) {
        this.myReviews = myReviews;
    }

    public void removeAdFromMyReviewList(int pos){
        this.myReviews.remove(pos);
    }

    public void initFavAdsList(){ favAds=new ArrayList<>();}

    @Override
    public String toString() {
        return "User{" +
                "uId='" + uId + '\'' +
                ", username='" + username + '\'' +
                ", location=" + location +
                ", favAds=" + favAds +
                ", ads=" + ads +
                ", rating=" + rating +
                ", myReviews=" + myReviews +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", aboutYou='" + aboutYou + '\'' +
                ", image='" + image + '\'' +
                ", gender='" + gender + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                ", isNew=" + isNew +
                ", isCreated=" + isCreated +
                '}';
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
        dest.writeTypedList(favAds);
        dest.writeTypedList(ads);
        dest.writeFloat(rating);
        dest.writeTypedList(myReviews);
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
