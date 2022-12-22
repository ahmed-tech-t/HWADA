package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.repository.AdsRepository;

import java.util.ArrayList;

public class AdsViewModel extends AndroidViewModel {
    AdsRepository repository ;
    public LiveData<ArrayList<Ad>> workerAdsLiveData;
    public LiveData<ArrayList<Ad>> homeFoodAdsLiveData;
    public LiveData<ArrayList<Ad>> handcraftAdsLiveData;
    public LiveData<ArrayList<Ad>> freelanceAdsLiveData;

    public LiveData<ArrayList<Ad>> favAdsLiveData;


    public void getAllFreelanceAds(String subCategory){
        freelanceAdsLiveData = repository.getAllFreelanceAds(subCategory);
    }

    public void getAllWorkersAds(){
        workerAdsLiveData = repository.getAllWorkersAds();
    }

    public void getAllHandcraftAds(){
        handcraftAdsLiveData = repository.getAllHandcraftAds();
    }

    public void getAllHomeFoodAds(){
        homeFoodAdsLiveData = repository.getAllHomeFoodAds();
    }

    public void getFavAds(User user){favAdsLiveData = repository.getFavAds(user);}

    public AdsViewModel(@NonNull Application application) {
        super(application);
        repository = new AdsRepository(application);
    }
}
