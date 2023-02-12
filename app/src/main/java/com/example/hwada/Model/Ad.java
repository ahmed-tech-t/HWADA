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
    private String authorId;
    private String authorName;
    private LocationCustom authorLocation;
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

    public Ad(String id, String category, String subCategory, String subSubCategory) {
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }

    public Ad(String id, String authorId, String title, String category, String subCategory, String subSubCategory, List<String> imagesUrl) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.imagesUrl = imagesUrl;
    }

    public Ad(String id, String authorId, String authorName, LocationCustom authorLocation, String title, String description, Timestamp timeStamp, float distance, float rating, String category, String subCategory, String subSubCategory, ArrayList<AdReview> adReviews, double price, DaysSchedule daysSchedule, List<String> imagesUrl, int views) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorLocation = authorLocation;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.distance = distance;
        this.rating = rating;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.adReviews = adReviews;
        this.price = price;
        this.daysSchedule = daysSchedule;
        this.imagesUrl = imagesUrl;
        this.views = views;
    }

    public Ad(String authorId, String authorName, LocationCustom authorLocation, String title, String description, double price, Timestamp timeStamp, String category, String subCategory , String subSubCategory) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorLocation = authorLocation;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.adReviews =new ArrayList<>();
        this.imagesUri = new ArrayList<>();
        this.daysSchedule =new DaysSchedule();
        this.price =price;

    }


    protected Ad(Parcel in) {
        id = in.readString();
        authorId = in.readString();
        authorName = in.readString();
        authorLocation = in.readParcelable(LocationCustom.class.getClassLoader());
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocationCustom getAuthorLocation() {
        return authorLocation;
    }

    public void setAuthorLocation(LocationCustom authorLocation) {
        this.authorLocation = authorLocation;
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



    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorLocation=" + authorLocation +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", distance=" + distance +
                ", rating=" + rating +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", subSubCategory='" + subSubCategory + '\'' +
                ", adReviews=" + adReviews +
                ", price=" + price +
                ", daysSchedule=" + daysSchedule +
                ", imagesUri=" + imagesUri +
                ", imagesUrl=" + imagesUrl +
                ", views=" + views +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(authorId);
        dest.writeString(authorName);
        dest.writeParcelable(authorLocation, flags);
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
