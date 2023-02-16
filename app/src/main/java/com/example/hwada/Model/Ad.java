package com.example.hwada.Model;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Ad implements Parcelable {


    private String id;
    private String authorId ;
    private LocationCustom authorLocation ;
    private String authorAddress ;
    private String title;
    private String description;
    private Timestamp timeStamp;
    private float distance ;
    private float rating;
    private String category;
    private String subCategory;
    private String subSubCategory;
    private ArrayList<AdReview> adReviews;
    private double price;
    private DaysSchedule daysSchedule ;
    private List<Uri> imagesUri;
    private List<String> imagesUrl;
    private int views ;

    public Ad(){
        this.adReviews =new ArrayList<>();
        this.imagesUri = new ArrayList<>();
        this.daysSchedule =new DaysSchedule();
        this.distance = 0;
    }

    public Ad(String adId, String category, String subCategory, String subSubCategory) {
        this.id = adId;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }


    protected Ad(Parcel in) {
        id = in.readString();
        authorId = in.readString();
        authorLocation = in.readParcelable(LocationCustom.class.getClassLoader());
        authorAddress = in.readString();
        title = in.readString();
        description = in.readString();
        timeStamp = in.readParcelable(Timestamp.class.getClassLoader());
        distance = in.readFloat();
        rating = in.readFloat();
        category = in.readString();
        subCategory = in.readString();
        subSubCategory = in.readString();
        adReviews = in.createTypedArrayList(AdReview.CREATOR);
        price = in.readDouble();
        daysSchedule = in.readParcelable(DaysSchedule.class.getClassLoader());
        imagesUri = in.createTypedArrayList(Uri.CREATOR);
        imagesUrl = in.createStringArrayList();
        views = in.readInt();
    }

    public static final Creator<Ad> CREATOR = new Creator<Ad>() {
        @Override
        public Ad createFromParcel(Parcel in) {
            return new Ad(in);
        }

        @Override
        public Ad[] newArray(int size) {
            return new Ad[size];
        }
    };

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public int getViews() {
        return views;
    }


    public void setViews(int views) {
        this.views = views;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public ArrayList<AdReview> getAdReviews() {
        return adReviews;
    }

    public void setAdToReviewsList(AdReview adReview) {
        adReviews.add(adReview);
    }
    public String getSubSubCategory() {
        return subSubCategory;
    }

    public void setSubSubCategory(String subSubCategory) {
        this.subSubCategory = subSubCategory;
    }
    public void removeAdFromReviewsList(int pos) {
        adReviews.remove(pos);
    }
    public void initAdReviewsList(){
        this.adReviews =new ArrayList<>();
    }
    public void setAdReviews(ArrayList<AdReview> adReviews) {
        this.adReviews = adReviews;
    }

    public void removeImageFromImagesUri(int pos) {
        imagesUri.remove(pos);
    }
    public void setImageToImagesUri(Uri image) {
        imagesUri.add(image);
    }

    public List<Uri> getImagesUri() {
        return imagesUri;
    }

    public void setImagesUri(List<Uri> imagesUri) {
        this.imagesUri = imagesUri;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public DaysSchedule getDaysSchedule() {
        return daysSchedule;
    }

    public void setDaysSchedule(DaysSchedule daysSchedule) {
        this.daysSchedule = daysSchedule;
    }


    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public LocationCustom getAuthorLocation() {
        return authorLocation;
    }

    public void setAuthorLocation(LocationCustom authorLocation) {
        this.authorLocation = authorLocation;
    }

    public String getAuthorAddress() {
        return authorAddress;
    }

    public void setAuthorAddress(String authorAddress) {
        this.authorAddress = authorAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(authorId);
        dest.writeParcelable(authorLocation, flags);
        dest.writeString(authorAddress);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(timeStamp, flags);
        dest.writeFloat(distance);
        dest.writeFloat(rating);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(subSubCategory);
        dest.writeTypedList(adReviews);
        dest.writeDouble(price);
        dest.writeParcelable(daysSchedule, flags);
        dest.writeTypedList(imagesUri);
        dest.writeStringList(imagesUrl);
        dest.writeInt(views);
    }
}
