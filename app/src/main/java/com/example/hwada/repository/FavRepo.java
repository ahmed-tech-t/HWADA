package com.example.hwada.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
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


    public MutableLiveData<ArrayList<Ad>> getAllFavAds(ArrayList<Ad> list){
        MutableLiveData<ArrayList<Ad>> mutableLiveData = new MutableLiveData<>();
        ArrayList<Ad> favAds = new ArrayList<>();
        for (Ad ad:list) {
            getFavAdColRef(ad).document(ad.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        Ad ad = snapshot.toObject(Ad.class);
                        ArrayList<AdReview> reviews = new ArrayList<>();
                        snapshot.getReference().collection(DbHandler.Reviews).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                                        reviews.add(reviewSnapshot.toObject(AdReview.class));
                                    }
                                    ad.setAdReviews(reviews);
                                    favAds.add(ad);
                                    if(favAds.size()==list.size()){
                                        mutableLiveData.setValue(favAds);
                                        return;
                                    }
                                }
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: Ad favorites" );
                    mutableLiveData.setValue(new ArrayList<>());
                }
            });
        }
        return mutableLiveData;
    }
    public void deleteFavAd(String userId , Ad ad){
        getUserFavAdColRef(userId).document(ad.getId()).delete();
    }

    private CollectionReference getFavAdColRef(Ad ad){
        CollectionReference adColRef;
        if (ad.getCategory().equals(DbHandler.FREELANCE)){
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(ad.getCategory())
                    .collection(ad.getCategory())
                    .document(ad.getSubCategory())
                    .collection(ad.getSubSubCategory());
        }else {
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(ad.getCategory())
                    .collection(ad.getCategory());
        }
        return adColRef;
    }
}
