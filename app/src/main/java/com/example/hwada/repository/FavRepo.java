package com.example.hwada.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;


public class FavRepo {
    private static final String TAG = "FavRepo";
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private CollectionReference getUserFavAdColRef(String id){
        CollectionReference ref;
        ref =  rootRef.collection(DbHandler.userCollection).document(id).collection(DbHandler.favAdsCollection);
        return ref;
    }

    public MutableLiveData<Ad> addFavAd(String userId , Ad ad){
    MutableLiveData<Ad> mutableLiveData = new MutableLiveData<>();
        Log.e(TAG, "addFavAd: "+userId );
        Log.e(TAG, "addFavAd: "+ad.getId() );
        DocumentReference docRef = getUserFavAdColRef(userId).document(ad.getId());
        Log.e(TAG, "addFavAd: " );
        Map<String , Object> data = new HashMap<>();
        data.put("adId",ad.getId());
        data.put("category",ad.getCategory());
        data.put("subCategory",ad.getSubCategory());
        data.put("subSubCategory",ad.getSubSubCategory());

        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.e(TAG, "onComplete: added to fav ad" );
                    mutableLiveData.setValue(ad);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return mutableLiveData;
    }

    public void deleteFavAd(String userId , Ad ad){
        getUserFavAdColRef(userId).document(ad.getId()).delete();
    }
}
