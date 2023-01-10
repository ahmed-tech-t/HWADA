package com.example.hwada.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.repository.AdsRepository;

import java.util.ArrayList;

public class AdsViewModel extends AndroidViewModel {
    AdsRepository repository ;
    private static final String TAG = "AdsViewModel";
    public LiveData<ArrayList<Ad>> allAdsLiveData;

    public LiveData<ArrayList<Ad>> favAdsLiveData;

    public LiveData<Ad> liveDataGetNewAdd;
    public LiveData<AdReview> liveDataAddReview;
    public LiveData<Boolean> liveDataDeleteReview;

    public LiveData<Boolean>liveDataUpdateImagesSuccess;




    public void deleteReview(User user,Ad ad,AdReview review){
        liveDataDeleteReview = repository.deleteReview(user , ad ,review);
    }

    public void addReview(User user,Ad ad,AdReview review){
        liveDataAddReview = repository.addReview(user , ad ,review);
    }
    public void addNewAd(Ad newAd ){
        liveDataGetNewAdd =  repository.addNewAd(newAd);
    }

    public void updateImages(Ad newAd){
        //liveDataUpdateImagesSuccess = repository.updateImages(newAd);
    }
    public void getAllAds(String category ,String subCategory){
        allAdsLiveData = repository.getAllAds(category,subCategory);
    }
    public void getAllAds(String category ,String subCategory,String subSubCategory){
        allAdsLiveData = repository.getAllAds(category,subCategory,subSubCategory);
    }
    public void getAllAds(){
        allAdsLiveData = repository.getAllAds();
    }

    public void getFavAds(User user){favAdsLiveData = repository.getFavAds(user);}

    public AdsViewModel(@NonNull Application application) {
        super(application);
        repository = new AdsRepository(application);
    }
}
