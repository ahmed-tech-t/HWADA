package com.example.hwada.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Ad;
import com.example.hwada.repository.FavRepo;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class FavViewModel extends ViewModel {
   public static FavViewModel favViewModel = new FavViewModel();
   public LiveData<Ad> addFavAdLiveData = new MutableLiveData<>();
   FavRepo repo = new FavRepo();

   public static FavViewModel getInstance(){
       return  favViewModel;
   }


   public void addFavAd(String userId , Ad ad){
    addFavAdLiveData = repo.addFavAd(userId ,ad);
   }

    public void deleteFavAd(String userId , Ad ad){
         repo.deleteFavAd(userId ,ad);
    }

    public LiveData<ArrayList<Ad>> getAllFavAds(ArrayList<Ad> list){
       return repo.getAllFavAds(list);
    }
}
