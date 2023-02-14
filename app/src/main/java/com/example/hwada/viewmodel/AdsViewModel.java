package com.example.hwada.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.repository.AdsRepository;

import java.util.ArrayList;

public class AdsViewModel extends ViewModel {
    AdsRepository repository ;
    public static AdsViewModel adsViewModel = new AdsViewModel();

    public LiveData<Ad> newAdLiveData = new MutableLiveData<>();

    private static final String TAG = "AdsViewModel";


    private AdsViewModel (){
       repository = new AdsRepository();
    }
    public static AdsViewModel getInstance(){
       return adsViewModel;
    }

    public void addNewAd(Ad newAd ){
        newAdLiveData = repository.addNewAd(newAd);
    }

    public void updateImages(Ad newAd){
        //liveDataUpdateImagesSuccess = repository.updateImages(newAd);
    }
    public LiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory){
       return repository.getAllAds(category,subCategory);
    }
    public LiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory,String subSubCategory){
        return repository.getAllAds(category,subCategory,subSubCategory);
    }
    public LiveData<ArrayList<Ad>> getAllAds(){
        return repository.getAllAds();
    }
    public void updateViews(Ad ad){
        repository.updateViews(ad);
    }

}
