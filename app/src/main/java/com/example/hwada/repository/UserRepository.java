package com.example.hwada.repository;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> updateSuccess;
    private MutableLiveData<Boolean> updateLocationSuccess;
    private MutableLiveData<Boolean> updateImageSuccess;
    private MutableLiveData<Boolean> updateFavAdsSuccess;
    private MutableLiveData<Boolean> updateMyReviewsSuccess;

    private FirebaseAuth auth ;

    public UserRepository (Application application){
        this.updateSuccess = new MutableLiveData<>();
        this.updateLocationSuccess = new MutableLiveData<>();
        this.updateImageSuccess = new MutableLiveData<>();
        this.updateFavAdsSuccess = new MutableLiveData<>();
        this.updateMyReviewsSuccess = new MutableLiveData<>();
        this.auth = FirebaseAuth.getInstance();
    }

    public MutableLiveData<Boolean> updateUser(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username",user.getUsername());
        data.put("phone",user.getPhone());
        data.put("about you",user.getAboutYou());
        data.put("image",user.getImage());
        data.put("gender",user.getGender());
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateSuccess.setValue(true);
                } else updateSuccess.setValue(false);
            }
        });
        return updateSuccess ;
    }
    public MutableLiveData<Boolean> updateUserLocation(LocationCustom location) {
        Map<String, Object> data = new HashMap<>();
        data.put("location",location);
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateLocationSuccess.setValue(true);
                } else updateLocationSuccess.setValue(false);
            }
        });
        return updateLocationSuccess ;
    }
    public MutableLiveData<Boolean> updateUserImage(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("image",user.getImage());
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateImageSuccess.setValue(true);
                } else updateImageSuccess.setValue(false);
            }
        });
        return updateImageSuccess;
    }

    public MutableLiveData<Boolean> updateUserFavAds(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("favoriteAds",user.getFavAds());
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateFavAdsSuccess.setValue(true);
                } else updateFavAdsSuccess.setValue(false);
            }
        });
        return updateFavAdsSuccess ;
    }

    public MutableLiveData<Boolean> updateUserReviews(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("myReviews",user.getMyReviews());
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateMyReviewsSuccess.setValue(true);
                } else updateMyReviewsSuccess.setValue(false);
            }
        });
        return updateMyReviewsSuccess;
    }


}
