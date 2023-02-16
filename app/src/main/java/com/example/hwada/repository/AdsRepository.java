package com.example.hwada.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdsRepository {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    //Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private static final String TAG = "AdsRepository";

    //********************************
    //add new ad
    public MutableLiveData<Ad>addNewAd(Ad newAd){
        MutableLiveData<Ad> addNewAdSuccess = new MutableLiveData<>();

        DocumentReference adDocRef = getAdColRef(newAd).document();

        newAd.setId(adDocRef.getId());

        StorageReference imageRef = storageRef.child("images").child(newAd.getAuthorId()).child(newAd.getCategory()).child(newAd.getSubCategory()).child(newAd.getId());

        List<String> downloadUrls = new ArrayList<>();

        for (Uri uri :newAd.getImagesUri()){
            File file= new File(uri.getPath());

            UploadTask uploadTask = imageRef.child(file.getName()).putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                            url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrls.add(String.valueOf(uri));
                                    if (downloadUrls.size() == newAd.getImagesUri().size()) {
                                        newAd.getImagesUri().clear();
                                        newAd.setImagesUrl(downloadUrls);
                                        uploadAd(newAd,addNewAdSuccess);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        return addNewAdSuccess;
    }
    private void uploadAd(Ad newAd , MutableLiveData<Ad>addNewAdSuccess){
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentReference adDocRef = getAdColRef(newAd).document(newAd.getId());

                DocumentReference userAdDocRef = getUserAdColRef(newAd).document(newAd.getId());

                // add new Ad to Ad collection
                transaction.set(adDocRef,newAd);

                //add new ad to user collection
                transaction.set(userAdDocRef,newAd);

                //add to homePage Collection
                adDocRef = getAdColHomePageRef().document(newAd.getId());

                transaction.set(adDocRef,newAd);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    addNewAdSuccess.setValue(newAd);
                    Log.e(TAG, "onComplete: upload complete" );
                }else {
                    addNewAdSuccess.setValue(null);
                    task.getException().printStackTrace();
                }
            }
        });

    }

    private void updateAd(Ad ad){

        rootRef.runTransaction(new Transaction.Function<Object>() {
           @Nullable
           @Override
           public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
               //update user in ads

               HashMap<String, Object> data = new HashMap<>();
               data.put("title", ad.getTitle());
               data.put("description", ad.getDescription());
               data.put("price", ad.getPrice());
               data.put("daysSchedule", ad.getDaysSchedule());

               DocumentReference temp0DocRef = getUserAdColRef(ad).document(ad.getId());
               DocumentReference temp1DocRef = getAdColRef(ad).document(ad.getId());
                   DocumentReference temp2DocRef = getUserAdColRef(ad).document(ad.getId());
                   DocumentReference temp3DocRef = getAdColHomePageRef().document(ad.getId());

                   transaction.update(temp1DocRef,data);
                   transaction.update(temp1DocRef,data);
                   transaction.update(temp2DocRef,data);
                   transaction.update(temp3DocRef,data);

                   transaction.update(temp0DocRef,data);
               return null;
           }});
    }

    //********************************

    private CollectionReference getAdColHomePageRef(){
           return getAdColRef(DbHandler.homePage,DbHandler.homePage);
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
    private CollectionReference getUserAdColRef(Ad ad){
        CollectionReference ref;
        ref =  rootRef.collection(DbHandler.userCollection).document(ad.getAuthorId()).collection(DbHandler.adCollection);
        return ref;
    }

    //**************************************

    public MutableLiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory){
       return getAllAds(getAdColRef(category,subCategory));
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory,String subSubCategory){
       return getAllAds(getAdColRef(category,subCategory,subSubCategory));
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(){
        return getAllAds(getAdColHomePageRef());
    }

    private MutableLiveData<ArrayList<Ad>> getAllAds(CollectionReference adColRef){
        MutableLiveData<ArrayList<Ad>> mutableLiveData =new MutableLiveData<>();
        adColRef
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Ad> ads = new ArrayList<>();
                    QuerySnapshot adQuerySnapshot = task.getResult();
                    for (QueryDocumentSnapshot adSnapshot : adQuerySnapshot) {
                        ads.add(adSnapshot.toObject(Ad.class));
                    }
                    mutableLiveData.setValue(ads);
                }else {
                    Log.e(TAG, "onComplete: error loading ads " );
                    mutableLiveData.setValue(new ArrayList<>());
                    return;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return  mutableLiveData;
    }
    //**************************************


    public void updateViews(Ad ad){

        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference adDocRef = getAdColRef(ad).document(ad.getId());
                DocumentReference userAdDocRef = getUserAdColRef(ad).document(ad.getId());
                DocumentReference adHomeDocRef = getAdColHomePageRef().document(ad.getId());

                DocumentSnapshot snapshot = transaction.get(adDocRef);
                long counter = snapshot.getLong("views");

                transaction.update(adDocRef, "views", counter + 1);
                transaction.update(userAdDocRef, "views", counter + 1);
                transaction.update(adHomeDocRef, "views", counter + 1);

                return null;
            }
        });



    }

}
