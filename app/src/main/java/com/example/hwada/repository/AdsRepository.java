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

    //********************************

    private CollectionReference getAdColHomePageRef(){
        CollectionReference adColRef;
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(DbHandler.homePage)
                    .collection(DbHandler.homePage);
        return adColRef;
    }
    private CollectionReference getAdColRef(Ad ad){
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
                    .collection(ad.getSubCategory());
        }
        return adColRef;
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
                .collection(subCategory)
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
        ArrayList<Ad> list = new ArrayList<>();
        MutableLiveData<ArrayList<Ad>> allAdsMutableLiveData =new MutableLiveData<>();

        getAdColRef(category,subCategory).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        String authorId = snapshot.getString("authorId");

                        Map<String, Object> locationMap = (Map<String, Object>) snapshot.get("authorLocation");
                        LocationCustom authorLocation = new LocationCustom((Double) locationMap.get("latitude"),(Double) locationMap.get("longitude"));

                        String authorName = snapshot.getString("authorName");
                        String category = snapshot.getString("category");
                        String date =snapshot.getString("date");
                        DaysSchedule daysSchedule = snapshot.get("daysSchedule", DaysSchedule.class);
                        String description = snapshot.getString("description");
                        float distance =  snapshot.getDouble("distance").floatValue();
                        String id =snapshot.getString("id");
                        ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                        double price = snapshot.getDouble("price");
                        float rating = snapshot.getDouble("rating").floatValue();

                        String subCategory = snapshot.getString("subCategory");
                        String subSubCategory = snapshot.getString("subSubCategory");
                        String title = snapshot.getString("title");
                        int views = snapshot.getLong("views").intValue();

                        ArrayList<AdReview> adReviews = new ArrayList<>();
                        CollectionReference reviewsRef =  snapshot.getReference().collection(DbHandler.Reviews);
                        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                                        String id =  reviewSnapshot.getString("id");
                                        String authorId = reviewSnapshot.getString("authorId");
                                        String authorImage = reviewSnapshot.getString("authorImage");
                                        String authorName =reviewSnapshot.getString("authorName");
                                        String body = reviewSnapshot.getString("body");
                                        String date =reviewSnapshot.getString("date");
                                        float rating = reviewSnapshot.getDouble("rating").floatValue();
                                        adReviews.add(new AdReview(id,date,authorId,authorName,authorImage,rating,body));
                                    }
                                }
                            }
                        });
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

        getAdColRef(category,subCategory,subSubCategory).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        String authorId = snapshot.getString("authorId");
                        LocationCustom authorLocation = (LocationCustom) snapshot.get("authorLocation");
                        String authorName = snapshot.getString("authorName");
                        String category = snapshot.getString("category");
                        String date =snapshot.getString("date");
                        DaysSchedule daysSchedule = snapshot.get("daysSchedule", DaysSchedule.class);
                        String description = snapshot.getString("description");
                        float distance = (float) snapshot.getDouble("distance").floatValue();
                        String id =snapshot.getString("id");
                        ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                        double price = snapshot.getDouble("price");
                        float rating = (float) snapshot.getDouble("rating").floatValue();
                        String subCategory = snapshot.getString("subCategory");
                        String subSubCategory = snapshot.getString("subSubCategory");
                        String title = snapshot.getString("title");
                        int views = (int) snapshot.get("views");

                        ArrayList<AdReview> adReviews = new ArrayList<>();
                        CollectionReference reviewsRef =  snapshot.getReference().collection(DbHandler.Reviews);
                        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                                        String id =  reviewSnapshot.getString("id");
                                        String authorId = reviewSnapshot.getString("authorId");
                                        String authorImage = reviewSnapshot.getString("authorImage");
                                        String authorName =reviewSnapshot.getString("authorName");
                                        String body = reviewSnapshot.getString("body");
                                        String date =reviewSnapshot.getString("date");
                                        float rating = reviewSnapshot.getDouble("rating").floatValue();
                                        adReviews.add(new AdReview(id,date,authorId,authorName,authorImage,rating,body));
                                    }
                                }
                            }
                        });
                        list.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));

                    }
                    allAdsMutableLiveData.setValue(list);
                }
            }
        });
        return  allAdsMutableLiveData;
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(){
        ArrayList<Ad> list = new ArrayList<>();
        MutableLiveData<ArrayList<Ad>> allAdsMutableLiveData =new MutableLiveData<>();

        getAdColHomePageRef().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                        String authorId = snapshot.getString("authorId");

                        Map<String, Object> locationMap = (Map<String, Object>) snapshot.get("authorLocation");
                        LocationCustom authorLocation = new LocationCustom((Double) locationMap.get("latitude"),(Double) locationMap.get("longitude"));

                        String authorName = snapshot.getString("authorName");
                        String category = snapshot.getString("category");
                        String date =snapshot.getString("date");
                        DaysSchedule daysSchedule = snapshot.get("daysSchedule", DaysSchedule.class);
                        String description = snapshot.getString("description");
                        float distance =  snapshot.getDouble("distance").floatValue();
                        String id =snapshot.getString("id");
                        ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                        double price = snapshot.getDouble("price");
                        float rating = snapshot.getDouble("rating").floatValue();

                        String subCategory = snapshot.getString("subCategory");
                        String subSubCategory = snapshot.getString("subSubCategory");
                        String title = snapshot.getString("title");
                        int views = snapshot.getLong("views").intValue();

                        ArrayList<AdReview> adReviews = new ArrayList<>();
                        CollectionReference reviewsRef =  snapshot.getReference().collection(DbHandler.Reviews);
                        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                                        String id =  reviewSnapshot.getString("id");
                                        String authorId = reviewSnapshot.getString("authorId");
                                        String authorImage = reviewSnapshot.getString("authorImage");
                                        String authorName =reviewSnapshot.getString("authorName");
                                        String body = reviewSnapshot.getString("body");
                                        String date =reviewSnapshot.getString("date");
                                        float rating = reviewSnapshot.getDouble("rating").floatValue();
                                        adReviews.add(new AdReview(id,date,authorId,authorName,authorImage,rating,body));
                                    }
                                }
                            }
                        });
                        list.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));
                    }
                    allAdsMutableLiveData.setValue(list);
                }
            }
        });

        return  allAdsMutableLiveData;
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

  /*  public MutableLiveData<Boolean>updateImages(Ad newAd){
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
*/
    //**************************************


    public AdsRepository(){}

}
