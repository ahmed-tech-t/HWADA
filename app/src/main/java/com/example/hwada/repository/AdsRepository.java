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
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private Application application ;
    MutableLiveData<ArrayList<Ad>> favAdsMutableLiveData;

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private DocumentReference adDocRef;
    private DocumentReference userDocRef;
    private CollectionReference adColRef;
    private 
    //Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static final String TAG = "AdsRepository";

//********************************
    public MutableLiveData<Ad>addNewAdd(Ad newAd){
        MutableLiveData<Ad> addNewAdSuccess = new MutableLiveData<>();
        if (newAd.getCategory().equals(DbHandler.FREELANCE)){
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getCategory())
                    .document(newAd.getSubCategory())
                    .collection(newAd.getSubSubCategory())
                    .document();
        }else {
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getSubCategory())
                    .document();
        }

        newAd.setId(adDocRef.getId());
        Log.e(TAG, "addNewAdd: "+ adDocRef.getId());
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
                userDocRef = rootRef.collection(DbHandler.userCollection).document(newAd.getAuthorId());
                transaction.get(userDocRef);
                Map<String,Object> data = new HashMap<>();

                data.put("ads", FieldValue.arrayUnion(newAd));
                Log.e(TAG, "apply: "+adDocRef.getId());

                // add new Add
                transaction.set(adDocRef,newAd);
                transaction.update(userDocRef,data);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    addNewAdSuccess.setValue(newAd);
                }else {
                    addNewAdSuccess.setValue(null);
                    Log.e(TAG, "onComplete: "+task.getException().getMessage());
                    task.getException().printStackTrace();
                }
            }
        });

    }
//********************************

    public MutableLiveData<Boolean>updateImages(Ad newAd){
        MutableLiveData<Boolean> updateImagesSuccess = new MutableLiveData<>();
        if (newAd.getCategory().equals("freelance")||newAd.getCategory().equals("worker")){
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getSubCategory())
                    .document(newAd.getSubSubCategory());
        }else {
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory());
        }
        Map<String,Object> data = new HashMap<>();
        data.put("imagesList",newAd.getImagesUrl());

        adDocRef.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateImagesSuccess.setValue(true);
                }else updateImagesSuccess.setValue(false);
            }
        });
        return updateImagesSuccess;
    }

    public MutableLiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory){
        ArrayList<Ad> list = new ArrayList<>();
        MutableLiveData<ArrayList<Ad>> allAdsMutableLiveData =new MutableLiveData<>();

        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory);
        adColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        ArrayList<AdReview> adReviews = (ArrayList<AdReview>) snapshot.get("adReviews");
                        String authorId = snapshot.getString("authorId");

                        Map<String, Object> locationMap = (Map<String, Object>) snapshot.get("authorLocation");
                        LocationCustom authorLocation = new LocationCustom((Double) locationMap.get("latitude"),(Double) locationMap.get("longitude"));

                        String authorName = snapshot.getString("authorName");
                        String category = snapshot.getString("category");
                        String date =snapshot.getString("date");
                        DaysSchedule daysSchedule = (DaysSchedule) snapshot.get("dasSchedule");
                        String description = snapshot.getString("description");
                        float distance =  snapshot.getLong("distance").floatValue();
                        String id =snapshot.getString("id");
                        ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                        double price = snapshot.getDouble("price");
                        float rating = snapshot.getLong("rating").floatValue();
                        String subCategory = snapshot.getString("subCategory");
                        String subSubCategory = snapshot.getString("subSubCategory");
                        String title = snapshot.getString("title");
                        int views = snapshot.getLong("views").intValue();

                        Log.e(TAG, "onComplete: "+imagesUrl.toString() );
                       list.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));
                    }
                    allAdsMutableLiveData.setValue(list);
                }
            }
        });

        return  allAdsMutableLiveData;
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(String category ,String subCategory,String subSubCategory){
        ArrayList<Ad> list = new ArrayList<>();
        MutableLiveData<ArrayList<Ad>> allAdsMutableLiveData =new MutableLiveData<>();


        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory)
                .document(subCategory)
                .collection(subSubCategory);
        adColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        ArrayList<AdReview> adReviews = (ArrayList<AdReview>) snapshot.get("adReviews");
                        String authorId = snapshot.getString("authorId");
                        LocationCustom authorLocation = (LocationCustom) snapshot.get("authorLocation");
                        String authorName = snapshot.getString("authorName");
                        String category = snapshot.getString("category");
                        String date =snapshot.getString("date");
                        DaysSchedule daysSchedule = (DaysSchedule) snapshot.get("dasSchedule");
                        String description = snapshot.getString("description");
                        float distance = (float) snapshot.get("distance");
                        String id =snapshot.getString("id");
                        ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                        double price = snapshot.getDouble("price");
                        float rating = (float) snapshot.get("rating");
                        String subCategory = snapshot.getString("subCategory");
                        String subSubCategory = snapshot.getString("subSubCategory");
                        String title = snapshot.getString("title");
                        int views = (int) snapshot.get("views");

                        list.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));

                    }
                    allAdsMutableLiveData.setValue(list);
                }
            }
        });
        return  allAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ad>> getFavAds(User user){
        ArrayList<Ad> list = new ArrayList<>();

        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));


        favAdsMutableLiveData.setValue(list);
        return  favAdsMutableLiveData;
    }

    public AdsRepository(Application application){
        this.application = application;

        favAdsMutableLiveData =new MutableLiveData<>();

    }

}
