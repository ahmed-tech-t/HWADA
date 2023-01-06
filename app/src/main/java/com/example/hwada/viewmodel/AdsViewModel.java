package com.example.hwada.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.repository.AdsRepository;

import java.util.ArrayList;

public class AdsViewModel extends AndroidViewModel {
    AdsRepository repository ;
    private static final String TAG = "AdsViewModel";
    public LiveData<ArrayList<Ad>> allAdsLiveData;

    public LiveData<ArrayList<Ad>> favAdsLiveData;

    public LiveData<Ad> liveDataGetNewAdd;
    public LiveData<Boolean>liveDataUpdateImagesSuccess;



    public void addNewAdd(Ad newAd ){
        liveDataGetNewAdd =  repository.addNewAdd(newAd);
    }

    public void updateImages(Ad newAd){
        liveDataUpdateImagesSuccess = repository.updateImages(newAd);
    }



    public void getAllAds(String category ,String subCategory){
        allAdsLiveData = repository.getAllAds(category,subCategory);

    }
    public void getAllAds(String category ,String subCategory,String subSubCategory){
        allAdsLiveData = repository.getAllAds(category,subCategory,subSubCategory);
    }


    public void getFavAds(User user){favAdsLiveData = repository.getFavAds(user);}

    public AdsViewModel(@NonNull Application application) {
        super(application);
        repository = new AdsRepository(application);
    }
}
