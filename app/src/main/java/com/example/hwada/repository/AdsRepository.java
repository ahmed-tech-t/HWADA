package com.example.hwada.repository;


import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;

import java.util.ArrayList;

public class AdsRepository {
    private Application application ;
    MutableLiveData<ArrayList<Ads>> workerAdsMutableLiveData;
    MutableLiveData<ArrayList<Ads>> homeFoodAdsMutableLiveData;
    MutableLiveData<ArrayList<Ads>> handcraftAdsMutableLiveData;
    MutableLiveData<ArrayList<Ads>> freeLanceAdsMutableLiveData;

    MutableLiveData<ArrayList<Ads>> favAdsMutableLiveData;

    public MutableLiveData<ArrayList<Ads>> getAllWorkersAds(){
        ArrayList<Ads> list = new ArrayList<>();

        list.add(new Ads("worker1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker8","mohamed said","4 Nov" ,"10 km"));

        workerAdsMutableLiveData.setValue(list);
        return  workerAdsMutableLiveData;
    }
    public MutableLiveData<ArrayList<Ads>> getAllFreelanceAds(String subCategory){
        ArrayList<Ads> list = new ArrayList<>();

        list.add(new Ads("nurse1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("driver2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("nurse3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("driver4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("nurse5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("driver6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("nurse7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("driver8","mohamed said","4 Nov" ,"10 km"));

        freeLanceAdsMutableLiveData.setValue(list);
        return  freeLanceAdsMutableLiveData;
    }


    public MutableLiveData<ArrayList<Ads>> getAllHomeFoodAds(){
        ArrayList<Ads> list = new ArrayList<>();

        list.add(new Ads("chef1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("chef8","mohamed said","4 Nov" ,"10 km"));

        homeFoodAdsMutableLiveData.setValue(list);
        return  homeFoodAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ads>> getFavAds(User user){
        ArrayList<Ads> list = new ArrayList<>();

        list.add(new Ads("worker1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("worker8","mohamed said","4 Nov" ,"10 km"));

        favAdsMutableLiveData.setValue(list);
        return  favAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ads>> getAllHandcraftAds(){
        ArrayList<Ads> list = new ArrayList<>();
        list.add(new Ads("ad1","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad2","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad3","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad4","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad5","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad6","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad7","mohamed said","4 Nov" ,"10 km"));
        list.add(new Ads("ad8","mohamed said","4 Nov" ,"10 km"));

        handcraftAdsMutableLiveData.setValue(list);
        return  handcraftAdsMutableLiveData;
    }


    public AdsRepository(Application application){
        this.application = application;
        workerAdsMutableLiveData = new MutableLiveData<>();
        homeFoodAdsMutableLiveData =new MutableLiveData<>();
        favAdsMutableLiveData =new MutableLiveData<>();
        handcraftAdsMutableLiveData =new MutableLiveData<>();
        freeLanceAdsMutableLiveData =new MutableLiveData<>();
    }

}
