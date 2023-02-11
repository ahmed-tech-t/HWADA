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

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.repository.UserRepository;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {

    private static UserViewModel userViewModel = new UserViewModel();
    public static UserViewModel getInstance(){
        return  userViewModel;
    }
    private UserViewModel(){
        userRepository = new UserRepository();
    }


   private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
   public MutableLiveData<User> updateUserLiveData = new MutableLiveData<>();

    private static final String TAG = "UserViewModel";
    UserRepository userRepository ;


    public LiveData<Boolean> updateLocationSuccessLiveData;

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
        updateLocationSuccessLiveData = userRepository.updateUserLocation(location);
    }

    public void setUserStatus(String status,String userId){
        userRepository.setUserStatus(status,userId);
    }
    public void setUserLastSeen(Timestamp lastSeen, String userId){
        userRepository.setUserLastSeen(lastSeen,userId);
    }
    public LiveData<String> getUserStatus(String id){
        return userRepository.getUserStatus(id);
    }
    public LiveData<String> getUserLastSeen(String id){
        return userRepository.getUserLastSeen(id);
    }

    public LiveData<ArrayList<Ad>> getAllUserAds(String id){

        return userRepository.getAllUserAds(id);
    }
    /*
    public void updateImageSuccess(User user){
        updateImageSuccessLiveData = userRepository.updateUserImage(user);
    }
    public void updateFavAdsSuccess(User user){
        updateFavAdsSuccessLiveData = userRepository.updateUserFavAds(user);
    }
    public void setUpdateMyReviewsSuccess(User user){
        updateMyReviewsSuccessLiveData = userRepository.updateUserReviews(user);
    }

 */
}
