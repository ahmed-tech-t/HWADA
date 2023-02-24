package com.example.hwada.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
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

    UserRepository userRepo ;
    Application application ;
    private static final String TAG = "AdsRepository";


    public AdsRepository(Application application){
        this.application = application;
        userRepo = new UserRepository(application);
    }
    public AdsRepository(UserRepository userRepo ,Application application) {
        this.application =application;
        this.userRepo = userRepo;
    }
    //********************************
    /**
     * add new add
     **/

    public void updateExistAd(Ad ad,String mainImageName,ArrayList<String> imagesToDelete){
      if(!imagesToDelete.isEmpty()){
          deleteAdImages(ad,mainImageName,imagesToDelete);
      }else if(!ad.getImagesUri().isEmpty()){
          uploadAdImages(ad,mainImageName,false);
      }else {
          updateExistAd(ad,mainImageName);
      }
    }
    public void addNewAd(Ad ad ,String mainImageName){
        uploadAdImages(ad, mainImageName,true);
    }
/**
 * delete Images
 * **/
    private void deleteAdImages(Ad ad ,String mainImageName,ArrayList<String> imagesToDelete) {
        StorageReference imageRef = storageRef.child("adImages").child(ad.getAuthorId()).child(ad.getCategory()).child(ad.getSubCategory()).child(ad.getId());
        imageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // Delete the file
                    if(!imagesToDelete.isEmpty()){
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                if (imagesToDelete.contains(imageUrl)) {
                                    deleteItem(item);
                                }
                            }
                        });
                    }else{
                        deleteItem(item);
                    }
                }
                uploadAdImages(ad,mainImageName,false);
            }
        });
    }

    private void deleteItem(StorageReference item){
        item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Deleted  onClick " + item.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error deleting onClick  " + item.getName() + ": " + e.getMessage());
            }
        });
    }


    private void uploadAdImages(Ad ad ,String mainImageName, boolean isNewAd) {
        if(isNewAd){
            DocumentReference adDocRef = getAdColRef(ad).document();
            ad.setId(adDocRef.getId());
        }

        StorageReference imageRef = storageRef.child("adImages").child(ad.getAuthorId()).child(ad.getCategory()).child(ad.getSubCategory()).child(ad.getId());
        List<String> downloadUrls = new ArrayList<>();

        if(ad.getImagesUri().isEmpty() && !isNewAd) updateExistAd(ad,mainImageName);
        for (String uri :ad.getImagesUri()){
            if (uri != null) {
                Uri myUri = Uri.parse(uri);

                    UploadTask uploadTask = imageRef.child(getFileName(myUri)).putFile(myUri);
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
                                            if (downloadUrls.size() == ad.getImagesUri().size()) {
                                                ad.getImagesUri().clear();
                                                if (isNewAd) {
                                                    ad.setImagesUrl(downloadUrls);
                                                    adNewAd(ad, mainImageName);
                                                } else {
                                                    ad.getImagesUrl().addAll(downloadUrls);
                                                    updateExistAd(ad, mainImageName);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                else Log.e(TAG, "uploadAdImages: un valid schema");
            }
        }


    private String getFileName(Uri myUri) {
        String scheme = myUri.getScheme();
        if (scheme != null && scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = application.getContentResolver().query(myUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(filePath);
                if(file.exists()) return file.getName();
                else {
                    Log.e(TAG, "getFileName: file not exist " );
                    return "";
                }
            }
        }
        return "";
    }

    private void adNewAd(Ad newAd,String mainImageName){
        Log.d(TAG, "uploadAd: starting transactions" );
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

               newAd.setMainImage(setMainImage(newAd,mainImageName));

                Log.d(TAG, "apply: "+newAd.getId());
                DocumentReference adDocRef = getAdColRef(newAd).document(newAd.getId());

                DocumentReference userAdDocRef = getUserAdColRef(newAd.getAuthorId()).document(newAd.getId());

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
                    Log.e(TAG, "onComplete: upload complete" );
                }else {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                e.getMessage();
                        }
                    });
                    task.getException().printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+e.getMessage());
                e.printStackTrace();
            }
        });

    }

    /**
     * update ad
     **/
    public void updateExistAd(Ad ad,String mainImageName){
        Log.d(TAG, "updateExistAd: update exist ad");
        rootRef.runTransaction(new Transaction.Function<Object>() {
           @Nullable
           @Override
           public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
               //update user in ads

               ad.setMainImage(setMainImage(ad,mainImageName));


               Log.d(TAG, "apply: start updating");
               HashMap<String, Object> data = new HashMap<>();
               data.put("title", ad.getTitle());
               data.put("description", ad.getDescription());
               data.put("price", ad.getPrice());
               data.put("daysSchedule", ad.getDaysSchedule());
               data.put("imagesUrl",ad.getImagesUrl());
               data.put("mainImage",ad.getMainImage());
               data.put("authorAddress",ad.getAuthorAddress());
               data.put("authorLocation",ad.getAuthorLocation());

               DocumentReference temp0DocRef = getUserAdColRef(ad.getAuthorId()).document(ad.getId());
               DocumentReference temp1DocRef = getAdColRef(ad).document(ad.getId());
               DocumentReference temp2DocRef = getUserAdColRef(ad.getAuthorId()).document(ad.getId());
               DocumentReference temp3DocRef = getAdColHomePageRef().document(ad.getId());

                   transaction.update(temp1DocRef,data);
                   transaction.update(temp1DocRef,data);
                   transaction.update(temp2DocRef,data);
                   transaction.update(temp3DocRef,data);

                   transaction.update(temp0DocRef,data);
               return null;
           }}).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: update success");
                }else {
                    task.getException().printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getMessage();
            }
        });
    }

    private String setMainImage(Ad ad,String mainImageName) {
        for (String url: ad.getImagesUrl()) {
            String urlFilename = getImageNameFromUri(url);
            if(urlFilename.equals(mainImageName)){
                Log.d(TAG, "handelAdUpdate: setMainImage "+mainImageName);
                return url;
            }
        }
        return "";
    }
    private String getImageNameFromUri(String url){
        Uri myUri = Uri.parse(url);
        File file = new File(myUri.getPath());
        return file.getName();
    }

    //********************************

    /**
     *get Ad collections references
     **/
    public CollectionReference getAdColHomePageRef(){
           return getAdColRef(DbHandler.homePage,DbHandler.homePage);
    }
    public CollectionReference getAdColRef(Ad ad){
        if (ad.getCategory().equals(DbHandler.FREELANCE)){
            return getAdColRef(ad.getCategory(),ad.getSubCategory(),ad.getSubSubCategory());
        }else {
           return getAdColRef(ad.getCategory(),ad.getSubCategory());
        }
    }
    public CollectionReference getAdColRef(MyReview myReview){
        Log.e(TAG, "getAdColRef: 1" );
        if (myReview.getCategory().equals(DbHandler.FREELANCE)){
            return getAdColRef(myReview.getCategory(),myReview.getSubCategory(),myReview.getSubSubCategory());
        }else {
            return getAdColRef(myReview.getCategory(),myReview.getSubCategory());
        }
    }
    public CollectionReference getAdColRef(String category , String subCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory);
        return adColRef;
    }
    public CollectionReference getAdColRef(String category , String subCategory , String subSubCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(category)
                .document(subCategory)
                .collection(subSubCategory);
        return adColRef;
    }
    public CollectionReference getUserAdColRef(String userId){
        CollectionReference ref;
        ref =  rootRef.collection(DbHandler.userCollection).document(userId).collection(DbHandler.adCollection);
        return ref;
    }

    //**************************************

    /**
     * get All Ads
     **/
    public MutableLiveData<ArrayList<Ad>> getAllAds(User user, String category , String subCategory){
       return getAllAds(user,getAdColRef(category,subCategory));
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(User user,String category ,String subCategory,String subSubCategory){
       return getAllAds(user,getAdColRef(category,subCategory,subSubCategory));
    }
    public MutableLiveData<ArrayList<Ad>> getAllAds(User user){
        return getAllAds(user,getAdColHomePageRef());
    }

    private MutableLiveData<ArrayList<Ad>> getAllAds(User user,CollectionReference adColRef){
        MutableLiveData<ArrayList<Ad>> mutableLiveData =new MutableLiveData<>();
        adColRef
                .whereEqualTo("active",true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Ad> ads = new ArrayList<>();
                        QuerySnapshot adQuerySnapshot = task.getResult();
                        for (QueryDocumentSnapshot adSnapshot : adQuerySnapshot) {
                            Ad ad = adSnapshot.toObject(Ad.class);
                            ad.setDistance_(user.getLocation());
                            ads.add(ad);
                        }
                        mutableLiveData.setValue(ads);
                    }else {
                        Log.e(TAG, "onComplete: error loading ads " );
                        mutableLiveData.setValue(new ArrayList<>());
                        return;
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


    /**
     * update ad status
     * **/

    public void updateAdStatus(Ad ad){
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference adDocRef = getAdColRef(ad).document(ad.getId());
                DocumentReference userAdDocRef = getUserAdColRef(ad.getAuthorId()).document(ad.getId());
                DocumentReference adHomeDocRef = getAdColHomePageRef().document(ad.getId());

                transaction.update(adDocRef, "active", ad.isActive());
                transaction.update(userAdDocRef, "active", ad.isActive());
                transaction.update(adHomeDocRef, "active", ad.isActive());

                return null;
            }
        });

    }

    /**
     * update views
     **/
    public void updateViews(Ad ad){

        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference adDocRef = getAdColRef(ad).document(ad.getId());
                DocumentReference userAdDocRef = getUserAdColRef(ad.getAuthorId()).document(ad.getId());
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

    /**
     * UPDATE AD RATING
     **/

    private double calcAdRating(ArrayList<AdReview> adReviews){
        double rating = 0 ;
        for (AdReview review:adReviews) {
            rating+=review.getRating();
        }
        Log.e(TAG, "calcAdRating: "+rating/adReviews.size() );
        if(rating == 0)return 0;
        return rating/adReviews.size();
    }
    public void updateAdRating(Ad ad,ArrayList<AdReview> adReviews) {

        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object>data = new HashMap<>();
                data.put("rating",calcAdRating(adReviews));

                transaction.update(getAdColRef(ad).document(ad.getId()),data);
                transaction.update(getAdColHomePageRef().document(ad.getId()),data);
                transaction.update(getUserAdColRef(ad.getAuthorId()).document(ad.getId()),data);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    Log.e(TAG, "onComplete: "+ ad.getAuthorId());

                    userRepo.getAllUserAds(ad.getAuthorId(),true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure: failed to update ad rating" );
            }
        });
    }

}
