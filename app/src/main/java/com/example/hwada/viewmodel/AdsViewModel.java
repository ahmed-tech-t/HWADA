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
    public LiveData<ArrayList<Ads>> adsLiveData;


    public void getAllAds(){
        adsLiveData = repository.getAllAds();
    }

    public AdsViewModel(@NonNull Application application) {
        super(application);

        repository = new AdsRepository(application);
    }
}
