package com.example.hwada.viewmodel;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
   private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    UserRepository userRepository ;
    public LiveData<Boolean> updateUserLiveData ;
    public LiveData<Boolean> updateLocationSuccessLiveData;
    public LiveData<Boolean> updateImageSuccessLiveData;
    public LiveData<Boolean> updateFavAdsSuccessLiveData;
    public LiveData<Boolean> updateMyReviewsSuccessLiveData;

    public UserViewModel(@NonNull Application application){
       super(application);
       userRepository = new UserRepository(application);
   }

    public void setUser(User user){
        userMutableLiveData.setValue(user);
    }
    public LiveData<User> getUser(){
       return userMutableLiveData;
    }

    public void updateUser (User user){
       updateUserLiveData = userRepository.updateUser(user);
    }
    public void updateLocationUser(LocationCustom location){
        updateLocationSuccessLiveData =userRepository.updateUserLocation(location);
    }
    public void updateImageSuccess(User user){
        updateImageSuccessLiveData = userRepository.updateUserImage(user);
    }
    public void updateFavAdsSuccess(User user){
        updateFavAdsSuccessLiveData = userRepository.updateUserFavAds(user);
    }
    public void setUpdateMyReviewsSuccess(User user){
        updateMyReviewsSuccessLiveData = userRepository.updateUserReviews(user);
    }
}
