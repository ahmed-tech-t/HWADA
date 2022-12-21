package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;
import com.example.hwada.repository.AdsRepository;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AdsViewModel extends AndroidViewModel {
    AdsRepository repository ;
    public LiveData<ArrayList<Ads>> workerAdsLiveData;
    public LiveData<ArrayList<Ads>> homeFoodAdsLiveData;
    public LiveData<ArrayList<Ads>> handcraftAdsLiveData;
    public LiveData<ArrayList<Ads>> freelanceAdsLiveData;

    public LiveData<ArrayList<Ads>> favAdsLiveData;


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
