package com.example.hwada.repository;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
        DocumentReference docRef = getUserFavAdColRef(userId).document(ad.getId());
        Map<String , Object> data = new HashMap<>();
        data.put("adId",ad.getId());
        data.put("category",ad.getCategory());
        data.put("subCategory",ad.getSubCategory());
        data.put("subSubCategory",ad.getSubSubCategory());

        docRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
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
        if(list==null || list.size()==0) {
            mutableLiveData.setValue(new ArrayList<>());
            return mutableLiveData;
        }
        for (Ad ad:list) {
            getAdColRef(ad).document(ad.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        favAds.add(snapshot.toObject(Ad.class)) ;
                    }else {
                        Log.e(TAG, "onComplete: error loading fav ad " );
                        mutableLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if(list.size()==favAds.size()){
                        mutableLiveData.setValue(favAds);
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


    public MutableLiveData<ArrayList<Ad>> userFavAdsListener(String id){
        MutableLiveData<ArrayList<Ad>> mutableLiveData = new MutableLiveData<>();
        userDocumentRef(id).collection(DbHandler.favAdsCollection).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                ArrayList<Ad> favAds = new ArrayList<>();
                assert value != null;
                for (QueryDocumentSnapshot document : value) {
                    String adId = document.getString("adId");
                    String category = document.getString("category");
                    String subCategory = document.getString("subCategory");
                    String subSubCategory = document.getString("subSubCategory");
                    Ad ad = new Ad(adId, category, subCategory, subSubCategory);
                    favAds.add(ad);
                }
                mutableLiveData.setValue(favAds);
            }
        });
        return mutableLiveData;
    }
    public void deleteFavAd(String userId , Ad ad){
        getUserFavAdColRef(userId).document(ad.getId()).delete();
    }

    private CollectionReference getAdColRef(Ad ad){
        if (ad.getCategory().equals(DbHandler.FREELANCE)){
            return getAdColRef(ad.getCategory(),ad.getSubCategory(),ad.getSubSubCategory());
        }else {
            return getAdColRef(ad.getCategory(),ad.getSubCategory());
        }
    }
    private CollectionReference getAdColRef(String category , String subCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory);
        return adColRef;
    }
    private CollectionReference getAdColRef(String category , String subCategory , String subSubCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(category)
                .document(subCategory)
                .collection(subSubCategory);
        return adColRef;
    }

    private DocumentReference userDocumentRef(String id){
        return rootRef.collection(DbHandler.userCollection).document(id);
    }


}
