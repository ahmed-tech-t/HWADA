package com.example.hwada.repository;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;

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

    UserViewModel userViewModel ;

    public UserRepository (){
        this.auth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        userViewModel = UserViewModel.getInstance();
    }


    public MutableLiveData<User> updateUser(User user) {
        MutableLiveData<User> updateSuccess = new MutableLiveData();
        Map<String, Object> data = new HashMap<>();
        data.put("username",user.getUsername());
        data.put("phone",user.getPhone());
        data.put("aboutYou",user.getAboutYou());
        data.put("gender",user.getGender());

        rootRef.collection(DbHandler.userCollection).document(user.getUId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) updateSuccess.setValue(user);
            }
        });
           return updateSuccess ;
    }
    public MutableLiveData<Boolean> updateUserLocation(LocationCustom location,String address) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("location",location);
        data.put("address",address);

        rootRef.collection(DbHandler.userCollection).document(auth.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mutableLiveData.setValue(true);
                }else {
                    mutableLiveData.setValue(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                mutableLiveData.setValue(false);
            }
        });
        return mutableLiveData;
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

    public void setUserStatus(String status,String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("status",status);
        rootRef.collection(DbHandler.userCollection).document(userId).update(data);
    }
    public void setUserLastSeen(Timestamp lastSeen, String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("lastSeen",lastSeen);
        rootRef.collection(DbHandler.userCollection).document(userId).update(data);
    }


    public MutableLiveData<ArrayList<Ad>> getAllUserAds(String id){
        MutableLiveData<ArrayList<Ad>> mutableLiveData = new MutableLiveData<>();
        userDocumentRef(id).collection(DbHandler.adCollection)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<Ad>ads = new ArrayList<>();
                    QuerySnapshot adQuerySnapshot = task.getResult();
                    for (QueryDocumentSnapshot document : adQuerySnapshot) {
                        ads.add(document.toObject(Ad.class));
                    }
                    mutableLiveData.setValue(ads);
                }
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<User> userListener(String id){
        MutableLiveData<User>mutableLiveData = new MutableLiveData<>();
        userDocumentRef(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    mutableLiveData.setValue(snapshot.toObject(User.class));
                }
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<User> getUserById(String id){
        MutableLiveData <User> mutableLiveData = new MutableLiveData<>();
        userDocumentRef(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mutableLiveData.setValue(task.getResult().toObject(User.class));
                }else{
                    mutableLiveData.setValue(new User());
                }
            }
        });
        return mutableLiveData;
    }

    private DocumentReference userDocumentRef(String id){
        return rootRef.collection(DbHandler.userCollection).document(id);
    }

    private CollectionReference getAdColRef(String category , String subCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory);
        return adColRef;
    }


}
