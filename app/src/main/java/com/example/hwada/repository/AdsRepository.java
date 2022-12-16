package com.example.hwada.repository;


import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;

import java.util.ArrayList;

public class AdsRepository {
    private Application application ;
    MutableLiveData<ArrayList<Ads>> adsMutableLiveData;

    public MutableLiveData<ArrayList<Ads>> getAllAds(){
        ArrayList<Ads> list = new ArrayList<>();

        list.add(new Ads("worker1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker8","mohamed said","4 Nov" ,"10 km"));

        adsMutableLiveData.setValue(list);
        return  adsMutableLiveData;
    }

    public AdsRepository(Application application){
        this.application = application;
        adsMutableLiveData = new MutableLiveData<>();

    }

}
