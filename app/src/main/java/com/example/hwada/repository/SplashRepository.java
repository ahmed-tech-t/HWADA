package com.example.hwada.repository;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SplashRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(DbHandler.userCollection);
    private static final String TAG = "SplashRepository";
    DebugRepository debugRepository = new DebugRepository();
    Application application;
    App app;

    public SplashRepository(Application application){
        this.application =application ;
        app =(App) application.getApplicationContext();
    }
    public MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
       try {
           if (firebaseUser == null) {
               user.isAuthenticated = false;
               isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
           } else {
               user.setUId(firebaseUser.getUid());
               user.isAuthenticated = true;
               isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
           }
       }catch (Exception e){
           e.printStackTrace();
           app.reportError(e,application);
       }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }


    public MutableLiveData<User> addUserToLiveData(String id){
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        DocumentReference userRef = rootRef.collection(DbHandler.userCollection).document(id);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot userSnapshot = task.getResult();
                    String userId = userSnapshot.getString("uId");
                    String username = userSnapshot.getString("username");
                    LocationCustom userLocation = userSnapshot.get("location",LocationCustom.class);
                    float userRating = userSnapshot.getDouble("rating").floatValue();
                    String userEmail = userSnapshot.getString("email");
                    String userPhone = userSnapshot.getString("phone");
                    String userAbout = userSnapshot.getString("aboutYou");
                    String userImage = userSnapshot.getString("image");
                    String userGender = userSnapshot.getString("gender");


                    ArrayList adsList = new ArrayList<>();
                    ArrayList favAdsList = new ArrayList<>();
                    ArrayList myReviewsList = new ArrayList<>();

                    CollectionReference adsRef = userSnapshot.getReference().collection(DbHandler.adCollection);
                    CollectionReference favRef = userSnapshot.getReference().collection(DbHandler.favAdsCollection);
                    CollectionReference myReviewsRef = userSnapshot.getReference().collection(DbHandler.MyReviews);

                    adsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                String authorId = snapshot.getString("authorId");
                                LocationCustom authorLocation = snapshot.get("authorLocation",LocationCustom.class);
                                String authorName = snapshot.getString("authorName");
                                String category = snapshot.getString("category");
                                String date =snapshot.getString("date");
                                DaysSchedule daysSchedule = snapshot.get("daysSchedule", DaysSchedule.class);
                                String description = snapshot.getString("description");
                                float distance = 0 ;
                                if(snapshot.getDouble("distance")!=null){
                                    distance = snapshot.getDouble("distance").floatValue();
                                }
                                String id =snapshot.getString("id");
                                ArrayList<String>imagesUrl = (ArrayList<String>) snapshot.get("imagesUrl");
                                double price =0 ;
                                if( snapshot.getDouble("price")!= null) {
                                    price = snapshot.getDouble("price");
                                }
                                float rating = 0 ;
                                if(snapshot.getDouble("rating")!=null){
                                    rating =snapshot.getDouble("rating").floatValue();
                                }
                                String subCategory = snapshot.getString("subCategory");
                                String subSubCategory = snapshot.getString("subSubCategory");
                                String title = snapshot.getString("title");
                                int views = 0 ;
                                if(snapshot.getLong("views")!=null){
                                    views =snapshot.getLong("views").intValue();
                                }

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
                                                float rating = 0 ;
                                                if(reviewSnapshot.getDouble("rating")!=null){
                                                    rating =reviewSnapshot.getDouble("rating").floatValue();
                                                }
                                                adReviews.add(new AdReview(id,date,authorId,authorName,authorImage,rating,body));
                                            }
                                        }
                                    }
                                });
                                adsList.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));

                            }
                            myReviewsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                        String reviewId = snapshot.getString("reviewId");
                                        String addId = snapshot.getString("addId");
                                        String category = snapshot.getString("category");
                                        String subCategory = snapshot.getString("subCategory");
                                        String subSubCategory = snapshot.getString("subSubCategory");


                                        myReviewsList.add(new MyReview(addId ,reviewId,category,subCategory,subSubCategory));
                                    }
                                    favRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            int favAdLength = task.getResult().size();
                                            Log.e(TAG, "onComplete: "+favAdLength);
                                            for (QueryDocumentSnapshot favSnapshot : task.getResult()) {
                                                String adId = favSnapshot.getString("adId");
                                                String category = favSnapshot.getString("category");
                                                String subCategory = favSnapshot.getString("subCategory");
                                                String subSubCategory = favSnapshot.getString("subSubCategory");
                                                DocumentReference tempRef = getTempAdColRef(category, subCategory, subSubCategory).document(adId);
                                                tempRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot tempSnapshot) {
                                                        Log.e(TAG, "onComplete: 3");
                                                        String authorId = tempSnapshot.getString("authorId");
                                                        LocationCustom authorLocation = tempSnapshot.get("authorLocation", LocationCustom.class);
                                                        String authorName = tempSnapshot.getString("authorName");
                                                        String category = tempSnapshot.getString("category");
                                                        String date = tempSnapshot.getString("date");
                                                        DaysSchedule daysSchedule = tempSnapshot.get("daysSchedule", DaysSchedule.class);
                                                        String description = tempSnapshot.getString("description");
                                                        float distance = 0;
                                                        if (tempSnapshot.getDouble("distance") != null) {
                                                            distance = tempSnapshot.getDouble("distance").floatValue();
                                                        }
                                                        String id = tempSnapshot.getString("id");
                                                        ArrayList<String> imagesUrl = (ArrayList<String>) tempSnapshot.get("imagesUrl");
                                                        double price = 0;
                                                        if (tempSnapshot.getDouble("price") != null) {
                                                            price = tempSnapshot.getDouble("price");
                                                        }
                                                        float rating = 0;
                                                        if (tempSnapshot.getDouble("rating") != null) {
                                                            rating = tempSnapshot.getDouble("rating").floatValue();
                                                        }
                                                        String subCategory = tempSnapshot.getString("subCategory");
                                                        String subSubCategory = tempSnapshot.getString("subSubCategory");
                                                        String title = tempSnapshot.getString("title");
                                                        int views = 0;
                                                        if (tempSnapshot.getLong("views") != null) {
                                                            views = tempSnapshot.getLong("views").intValue();
                                                        }

                                                        ArrayList<AdReview> adReviews = new ArrayList<>();
                                                        CollectionReference reviewsRef = tempSnapshot.getReference().collection(DbHandler.Reviews);
                                                        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.e(TAG, "onComplete: 4");
                                                                    for (QueryDocumentSnapshot reviewSnapshot : task.getResult()) {
                                                                        String id = reviewSnapshot.getString("id");
                                                                        String authorId = reviewSnapshot.getString("authorId");
                                                                        String authorImage = reviewSnapshot.getString("authorImage");
                                                                        String authorName = reviewSnapshot.getString("authorName");
                                                                        String body = reviewSnapshot.getString("body");
                                                                        String date = reviewSnapshot.getString("date");
                                                                        float rating = 0;
                                                                        if (reviewSnapshot.getDouble("rating") != null) {
                                                                            rating = reviewSnapshot.getDouble("rating").floatValue();
                                                                        }
                                                                        adReviews.add(new AdReview(id, date, authorId, authorName, authorImage, rating, body));
                                                                    }
                                                                }
                                                            }
                                                        });
                                                        favAdsList.add(new Ad(id, authorId, authorName, authorLocation, title, description, date, distance, rating, category, subCategory, subSubCategory, adReviews, price, daysSchedule, imagesUrl, views));

                                                        if(favAdsList.size() == favAdLength){
                                                            User user = new User(userId,username,userLocation,favAdsList,adsList,userRating,myReviewsList,userEmail,userPhone,userAbout,userImage,userImage);
                                                            userMutableLiveData.setValue(user);
                                                        }
                                                    }

                                                });
                                            }

                                            if(favAdLength == 0){
                                                User user = new User(userId,username,userLocation,favAdsList,adsList,userRating,myReviewsList,userEmail,userPhone,userAbout,userImage,userImage);
                                                userMutableLiveData.setValue(user);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
        return userMutableLiveData ;

    }



    private CollectionReference getTempAdColRef(String category , String subCategory , String subSubCategory){
        CollectionReference adColRef;
        if (category.equals(DbHandler.FREELANCE)){
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(category)
                    .collection(category)
                    .document(subCategory)
                    .collection(subSubCategory);
        }else {
            adColRef = rootRef.collection(DbHandler.adCollection)
                    .document(category)
                    .collection(subCategory);
        }
        return adColRef;
    }

}
