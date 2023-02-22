package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.repository.ReviewRepo;

import java.util.ArrayList;

public class ReviewViewModel extends AndroidViewModel {
    public LiveData<AdReview> adReviewLiveData = new MutableLiveData<>();

   ReviewRepo repo ;

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        repo = new ReviewRepo(application);
    }


    public LiveData<Boolean> deleteReview(User user, Ad ad, AdReview review){
        return repo.deleteReview(user , ad ,review);
    }

    public LiveData<AdReview> editReview(Ad ad , AdReview review){
        return repo.editReview(ad,review);
    }

    public void addReview(User user,Ad ad,AdReview review){
        adReviewLiveData = repo.addReview(user , ad ,review);
    }
    public LiveData<ArrayList<AdReview>> getAdReviews(Ad ad){
        return repo.getAdReviews(ad,false);
    }
}
