package com.example.hwada.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private String mainImage;
    private String category;
    private String subCategory;
    private String subSubCategory;
    private ArrayList<AdReview> adReviews;
    private double price;
    private DaysSchedule daysSchedule ;
    private List<String> imagesUri;
    private List<String> imagesUrl;
    private int views ;
   private boolean isActive ;
    private static final String TAG = "Ad";
    public Ad(){
        daysSchedule = new DaysSchedule();
        this.adReviews = new ArrayList<>();
        this.imagesUri = new ArrayList<>();
        this.distance = 0;
        this.rating = 0 ;
    }

    public Ad(String adId, String category, String subCategory, String subSubCategory) {
        this.id = adId;
        this.category = category;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        daysSchedule = new DaysSchedule();
        this.adReviews = new ArrayList<>();
        this.imagesUri = new ArrayList<>();
        this.distance = 0;
        this.rating = 0 ;
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
        mainImage = in.readString();
        category = in.readString();
        subCategory = in.readString();
        subSubCategory = in.readString();
        adReviews = in.createTypedArrayList(AdReview.CREATOR);
        price = in.readDouble();
        daysSchedule = in.readParcelable(DaysSchedule.class.getClassLoader());
        imagesUri = in.createStringArrayList();
        imagesUrl = in.createStringArrayList();
        views = in.readInt();
        isActive = in.readByte() != 0;
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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
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


    public List<String> getImagesUri() {
        return imagesUri;
    }

    public void setImagesUri(List<String> imagesUri) {
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
        dest.writeString(mainImage);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(subSubCategory);
        dest.writeTypedList(adReviews);
        dest.writeDouble(price);
        dest.writeParcelable(daysSchedule, flags);
        dest.writeStringList(imagesUri);
        dest.writeStringList(imagesUrl);
        dest.writeInt(views);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    public void setDistance_(LocationCustom userLocation){
        Location location1 = new Location("userLocation");

        location1.setLatitude(userLocation.getLatitude());
        location1.setLongitude(userLocation.getLongitude());

        Location location2 = new Location("adLocation");
        location2.setLatitude(this.getAuthorLocation().getLatitude());
        location2.setLongitude(this.getAuthorLocation().getLongitude());

        float distanceInMeters = location1.distanceTo(location2)/1000;

        this.distance = Float.parseFloat(String.format(Locale.US, "%.2f", distanceInMeters));
    }

    public boolean isOpen(String time , String []days , int dayIndex){
        if(dayIndex==7) dayIndex = 0 ;
        ArrayList<WorkingTime> day = this.getDaysSchedule().getDays().get(days[dayIndex]);
        if(day==null) return false;
        return day.stream().anyMatch(w -> w.isWithinPeriod(time));
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorLocation=" + authorLocation +
                ", authorAddress='" + authorAddress + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timeStamp=" + timeStamp +
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
                ", isActive=" + isActive +
                '}';
    }
}
