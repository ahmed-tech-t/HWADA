package com.example.hwada.repository;

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
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class ReviewRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private static final String TAG = "ReviewRepo";

    private CollectionReference getAdColRef(Ad newAd){
        CollectionReference adColRef;
        if (newAd.getCategory().equals(DbHandler.FREELANCE)){
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getCategory())
                    .document(newAd.getSubCategory())
                    .collection(newAd.getSubSubCategory());
        }else {
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getSubCategory());
        }
        return adColRef;
    }
    private CollectionReference getUserAdColRef(Ad ad){
        CollectionReference ref;
        ref =  rootRef.collection(DbHandler.userCollection).document(ad.getAuthorId()).collection(DbHandler.adCollection);
        return ref;
    }
    private CollectionReference getAdColHomePageRef(){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(DbHandler.homePage)
                .collection(DbHandler.homePage);
        return adColRef;
    }

    public MutableLiveData<AdReview> addReview(User user, Ad ad , AdReview review){
        MutableLiveData<AdReview> reviewMutableLiveData = new MutableLiveData<>();
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference reviewDocRef;


                //set ad review to ad collection
                reviewDocRef = getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document();
                review.setId(reviewDocRef.getId());
                transaction.set(reviewDocRef,review);

                //set ad review to user ad collection
                reviewDocRef = getUserAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.set(reviewDocRef, review);


                //set to my reviews to user collection
                reviewDocRef = rootRef.collection(DbHandler.userCollection).document(user.getUId()).collection(DbHandler.MyReviews).document(review.getId());
                MyReview myReview = new MyReview(review.getId(), ad.getId(), ad.getCategory(), ad.getSubCategory(), ad.getSubSubCategory());
                transaction.set(reviewDocRef, myReview);

                //set ad review to home page ad collection
                reviewDocRef = getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.set(reviewDocRef,review);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
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
                reviewDocRef = getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                review.setId(reviewDocRef.getId());
                transaction.update(reviewDocRef,data);

                //set ad review to user ad collection
                reviewDocRef = getUserAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.update(reviewDocRef, data);


                //set ad review to home page ad collection
                reviewDocRef = getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.update(reviewDocRef,data);

                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
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
                reviewDocRef = getAdColRef(ad).document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
                transaction.delete(reviewDocRef);

                //delete review from home page ad collection
                reviewDocRef = getAdColHomePageRef().document(ad.getId()).collection(DbHandler.Reviews).document(review.getId());
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
                    deleteReviewMutableLiveDate.setValue(true);
                }
            }
        });
        return deleteReviewMutableLiveDate;
    }
}
