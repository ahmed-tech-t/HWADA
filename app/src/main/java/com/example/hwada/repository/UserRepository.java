package com.example.hwada.repository;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private FirebaseAuth auth ;

    MutableLiveData<User> userMutableLiveData;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    AdsRepository adRepo;
    Application application ;

    public UserRepository (Application application){
        this.application = application;
        this.auth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        adRepo = new AdsRepository(this,application);
    }


    /**
     *
     *  update user block
     * **/

    public void updateUser(User user){
       if(user.getImage()!=null){
          deleteOldImage(user);
       }else {
           getAllUserReviews(user);
       }
    }
    private void deleteOldImage(User user){
        StorageReference imageRef = storageRef.child(DbHandler.userImage).child(user.getUId()).child(DbHandler.profileImage);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                /**
                 * upload user image
                 * **/
                uploadUserImage(user,imageRef);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Log.e(TAG, "Image does not exist at location");
                    uploadUserImage(user,imageRef);
                } else {
                    Log.e(TAG, "Error deleting image: " + e.getMessage());
                }
            }
        });
    }
    private void uploadUserImage(User user , StorageReference imageRef){
        imageRef.putFile(Uri.parse(user.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getMetadata()!=null){
                        Task<Uri> url = task.getResult().getStorage().getDownloadUrl();
                        url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                user.setImage(String.valueOf(uri));
                                /**
                                 *
                                 * get all user reviews
                                 **/
                               getAllUserReviews(user);
                            }
                        });
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void getAllUserReviews(User user){
        DocumentReference userDocRef = rootRef.collection(DbHandler.userCollection).document(user.getUId());
        userDocRef.collection(DbHandler.MyReviews).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<MyReview> myReviews = new ArrayList<>();
                    if(task.getResult().isEmpty()){
                        Log.e(TAG, "onComplete: my reviews is Empty" );
                        _updateUser(user);
                        return;
                    }
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        myReviews.add(snapshot.toObject(MyReview.class));
                    }
                    user.setMyReviews(myReviews);
                    _updateUser(user);
                }
            }
        });
    }
    private void _updateUser(User user){
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                Map<String, Object> myReviewData = new HashMap<>();
                myReviewData.put("authorName",user.getUsername());
                if(user.getImage()!=null) myReviewData.put("authorImage",user.getImage());

                for (MyReview myReview: user.getMyReviews()) {

                    DocumentReference myReviewAdDocRef = getAdReviewsColRef(adRepo.getAdColRef(myReview),myReview).document(myReview.getReviewId());
                    transaction.update(myReviewAdDocRef,myReviewData);
                    DocumentReference myReviewHomeAdDocRef = getAdReviewsColRef(adRepo.getAdColHomePageRef(),myReview).document(myReview.getReviewId());
                    transaction.update(myReviewHomeAdDocRef,myReviewData);
                    DocumentReference myReviewUserAdDocRef = getAdReviewsColRef(adRepo.getUserAdColRef(myReview.getAdAuthorId()),myReview).document(myReview.getReviewId());
                    transaction.update(myReviewUserAdDocRef,myReviewData);
                }

                Map<String, Object> userData = new HashMap<>();
                userData.put("username",user.getUsername());
                userData.put("phone",user.getPhone());
                userData.put("aboutYou",user.getAboutYou());
                userData.put("gender",user.getGender());
                if(user.getImage()!=null) userData.put("image",user.getImage());

                DocumentReference userDocRef = rootRef.collection(DbHandler.userCollection).document(user.getUId());
                transaction.update(userDocRef,userData);
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure: "+e.getMessage());
                Log.e(TAG, "onFailure: failed to update user" );
            }
        });
    }

/**
 *
 *
 * **/

private CollectionReference getAdReviewsColRef(CollectionReference adColRef,MyReview myReview){
   return adColRef.document(myReview.getAdId()).collection(DbHandler.Reviews);
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


    public MutableLiveData<ArrayList<Ad>> userAdsListener(String id) {
        MutableLiveData<ArrayList<Ad>> mutableLiveData = new MutableLiveData<>();
        userDocumentRef(id).collection(DbHandler.adCollection)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            mutableLiveData.setValue(new ArrayList<>());
                            return;
                        }
                        assert value != null;
                        ArrayList<Ad>ads = new ArrayList<>();
                        for (DocumentChange change : value.getDocumentChanges()) {
                            ads.add(change.getDocument().toObject(Ad.class));
                        }
                        mutableLiveData.setValue(ads);
                    }
                });
        return mutableLiveData;
    }


    public MutableLiveData<ArrayList<Ad>> getAllUserAds(String id,boolean updateRating){
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
                    if(updateRating) updateUserRating(ads);
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


    private double calcUserRating(ArrayList<Ad> ads){
        double rating = 0 ;
        int i =0;
        for (Ad ad:ads) {
            rating+=ad.getRating();
            if (ad.getRating()==0)i++;

        }
        if(rating == 0 || (ads.size()-i) == 0 ) return 0;
        return rating/(ads.size()-i);
    }
    public void updateUserRating(ArrayList<Ad> ads) {
        userDocumentRef(ads.get(0).getAuthorId()).update("rating",calcUserRating(ads));
    }
}
