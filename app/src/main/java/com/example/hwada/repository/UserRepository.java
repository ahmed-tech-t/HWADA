package com.example.hwada.repository;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private FirebaseAuth auth ;

    MutableLiveData<User> userMutableLiveData;

    public UserRepository (){
        this.auth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<User> updateUser(User user) {
        MutableLiveData<User> updateSuccess = new MutableLiveData();
        Map<String, Object> data = new HashMap<>();
        data.put("username",user.getUsername());
        data.put("phone",user.getPhone());
        data.put("aboutYou",user.getAboutYou());
        data.put("gender",user.getGender());
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateSuccess.setValue(user);
                }
            }
        });
        return updateSuccess ;
    }
    public MutableLiveData<Boolean> updateUserLocation(LocationCustom location) {
        MutableLiveData<Boolean> updateLocationSuccess =new MutableLiveData<>();
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
        MutableLiveData<Boolean>updateImageSuccess = new MutableLiveData<>();
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
        MutableLiveData<Boolean>updateFavAdsSuccess = new MutableLiveData<>();
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
        MutableLiveData<Boolean>updateMyReviewsSuccess = new MutableLiveData<>();
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

    public void setUserStatus(String status){
        Map<String, Object> data = new HashMap<>();
        data.put("status",status);
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data);
    }
    public void setUserLastSeen(String lastSeen){
        Map<String, Object> data = new HashMap<>();
        data.put("lastSeen",lastSeen);
        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data);
    }

    public MutableLiveData<String> getUserStatus(String id) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData();
       DocumentReference documentReference =  rootRef.collection(DbHandler.userCollection).document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String status = snapshot.getString("status");
                    mutableLiveData.setValue(status);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        return mutableLiveData;
    }
    public MutableLiveData<String> getUserLastSeen(String id) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData();
        rootRef.collection(DbHandler.userCollection).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    String status = snapshot.getString("lastSeen");
                    mutableLiveData.setValue(status);
                }
            }
        });
        return mutableLiveData;
    }


}
