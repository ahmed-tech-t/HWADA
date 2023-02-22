package com.example.hwada.repository;

import android.app.Application;
import android.graphics.Path;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private static final String TAG = "ReviewRepo";

    Application application;
    AdsRepository adsRepo;
    public ReviewRepo(Application application){
        this.application = application;
        adsRepo = new AdsRepository(application);
    }


    /**
     * GET ADD REVIEWS
     **/
    public MutableLiveData<ArrayList<AdReview>> getAdReviews(Ad ad,boolean updateRating){
        MutableLiveData<ArrayList<AdReview>> mutableLiveData = new MutableLiveData<>();
        ArrayList<AdReview> adReviews = new ArrayList<>();
        adsRepo.getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                        adReviews.add(reviewSnapshot.toObject(AdReview.class));
                    }

                    if(updateRating){
                        adsRepo.updateAdRating(ad,adReviews);
                    }
                    mutableLiveData.setValue(adReviews);
                } else {
                    Log.d(TAG, "onComplete: can't load ad review");
                    mutableLiveData.setValue(new ArrayList<>());
                }
            }

                });

        return mutableLiveData;
    }


    /**
     * ADD NEW REVIEW
     **/

    public MutableLiveData<AdReview> addReview(User user, Ad ad , AdReview review){
        MutableLiveData<AdReview> reviewMutableLiveData = new MutableLiveData<>();
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference reviewDocRef;


                //set ad review to ad collection
                reviewDocRef = adsRepo.getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document();
                review.setId(reviewDocRef.getId());
                transaction.set(reviewDocRef,review);

                //set ad review to user ad collection
                reviewDocRef = adsRepo.getUserAdColRef(ad.getAuthorId()).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.set(reviewDocRef, review);


                //set to my reviews to user collection
                reviewDocRef = rootRef.collection(DbHandler.userCollection).document(user.getUId()).collection(DbHandler.MyReviews).document(review.getId());
                MyReview myReview = new MyReview(review.getId(), ad.getId(),ad.getAuthorId(), ad.getCategory(), ad.getSubCategory(), ad.getSubSubCategory());
                transaction.set(reviewDocRef, myReview);

                //set ad review to home page ad collection
                reviewDocRef = adsRepo.getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.set(reviewDocRef,review);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    getAdReviews(ad, true);
                    reviewMutableLiveData.setValue(review);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return reviewMutableLiveData ;

    }


    /**
     * EDIT REVIEW
     **/
    public MutableLiveData<AdReview> editReview(Ad ad , AdReview review){
        MutableLiveData<AdReview> reviewMutableLiveData = new MutableLiveData<>();
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference reviewDocRef;

                Map<String ,Object> data = new HashMap<>();
                data.put("body",review.getBody());
                data.put("rating",review.getRating());

                //set ad review to ad collection
                reviewDocRef = adsRepo.getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                review.setId(reviewDocRef.getId());
                transaction.update(reviewDocRef,data);

                //set ad review to user ad collection
                reviewDocRef = adsRepo.getUserAdColRef(ad.getAuthorId()).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.update(reviewDocRef, data);


                //set ad review to home page ad collection
                reviewDocRef = adsRepo.getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.update(reviewDocRef,data);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    getAdReviews(ad, true);
                    reviewMutableLiveData.setValue(review);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return reviewMutableLiveData ;

    }

    /**
     * DELETE REVIEW
     **/
    public MutableLiveData<Boolean> deleteReview(User user , Ad ad , AdReview review){
        MutableLiveData<Boolean> deleteReviewMutableLiveDate = new MutableLiveData<>();
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference reviewDocRef;
                Log.e(TAG, "apply: "+ad.getId());
                Log.e(TAG, "apply: "+user.getEmail() );

                //delete review from ad collection
                reviewDocRef = adsRepo.getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.delete(reviewDocRef);

                //delete review from home page ad collection
                reviewDocRef = adsRepo.getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.delete(reviewDocRef);

                //set to my reviews to user collection
                reviewDocRef = rootRef.collection(DbHandler.userCollection).document(user.getUId()).collection(DbHandler.MyReviews).document(review.getId());
                transaction.delete(reviewDocRef);

                //**************************************

                //delete review from user ad collection

                reviewDocRef = rootRef.collection(DbHandler.userCollection).document(ad.getAuthorId()).collection(DbHandler.adCollection)
                        .document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.delete(reviewDocRef);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if (task.isSuccessful()){
                    getAdReviews(ad, true);
                    deleteReviewMutableLiveDate.setValue(true);
                }
            }
        });
        return deleteReviewMutableLiveDate;
    }
}
