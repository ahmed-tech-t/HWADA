package com.example.hwada.repository;


import android.app.Application;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;

import java.util.ArrayList;

public class AdsRepository {
    private Application application ;
    MutableLiveData<ArrayList<Ad>> workerAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> homeFoodAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> handcraftAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> freeLanceAdsMutableLiveData;

    MutableLiveData<ArrayList<Ad>> favAdsMutableLiveData;

    public MutableLiveData<ArrayList<Ad>> getAllWorkersAds(){
        ArrayList<Ad> list = new ArrayList<>();
        Location authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));


        workerAdsMutableLiveData.setValue(list);
        return  workerAdsMutableLiveData;
    }
    public MutableLiveData<ArrayList<Ad>> getAllFreelanceAds(String subCategory){
        ArrayList<Ad> list = new ArrayList<>();

        Location authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));


        freeLanceAdsMutableLiveData.setValue(list);
        return  freeLanceAdsMutableLiveData;
    }


    public MutableLiveData<ArrayList<Ad>> getAllHomeFoodAds(){
        ArrayList<Ad> list = new ArrayList<>();
        Location authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));


        homeFoodAdsMutableLiveData.setValue(list);
        return  homeFoodAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ad>> getFavAds(User user){
        ArrayList<Ad> list = new ArrayList<>();

        Location authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));


        favAdsMutableLiveData.setValue(list);
        return  favAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ad>> getAllHandcraftAds(){
        ArrayList<Ad> list = new ArrayList<>();
        Location authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description","date","category","subCategory"));


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
