package com.example.hwada.repository;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
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

    public MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
       try {
           if (firebaseUser == null) {
               user.isAuthenticated = false;
               isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
           } else {
               user.setuId( firebaseUser.getUid());
               user.isAuthenticated = true;
               isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
           }
       }catch (Exception e){
           e.printStackTrace();
           reportError(e);
       }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }


    public MutableLiveData<User> addUserToLiveData(String id){
        Log.e(TAG, "getUser: 4" );
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        DocumentReference userRef = rootRef.collection(DbHandler.userCollection).document(id);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    DocumentSnapshot snapshot = task.getResult();
                    String id = snapshot.getString("uId");
                    String username =snapshot.getString("username");
                    LocationCustom locationCustom = snapshot.get("location",LocationCustom.class);
                    float rating = snapshot.getDouble("rating").floatValue();
                    String email = snapshot.getString("email");
                    String phone =snapshot.getString("phone");
                    String aboutYou =snapshot.getString("aboutYou");
                    String image =snapshot.getString("image");
                    String gender =snapshot.getString("gender");


                    ArrayList adsList = new ArrayList<>();
                    ArrayList favAdsList = new ArrayList<>();
                    ArrayList myReviewsList = new ArrayList<>();

                    CollectionReference adsRef = snapshot.getReference().collection(DbHandler.adCollection);
                    CollectionReference favRef = snapshot.getReference().collection(DbHandler.favAds);
                    CollectionReference myReviewsRef = snapshot.getReference().collection(DbHandler.MyReviews);

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

                            favRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                        favAdsList.add(new Ad(id,authorId,authorName,authorLocation,title,description,date,distance,rating,category,subCategory,subSubCategory,adReviews,price,daysSchedule,imagesUrl,views));

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

                                            User user = new User(id,username,locationCustom,favAdsList,adsList,rating,myReviewsList,email,phone,aboutYou,image,gender);
                                            Log.e(TAG, "onComplete: " );
                                            userMutableLiveData.setValue(user);
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


/*
    public MutableLiveData<User> addUserToLiveData(String uid) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        try {
           usersRef.document(uid).get().addOnCompleteListener(userTask -> {
               if (userTask.isSuccessful()) {
                   DocumentSnapshot document = userTask.getResult();
                   if(document.exists()) {
                       User user = document.toObject(User.class);
                       userMutableLiveData.setValue(user);
                   }
               } else {
                   reportError(userTask.getException());
                   Log.e(TAG,userTask.getException().getMessage());
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           reportError(e);
       }
        return userMutableLiveData;
    }

 */
    private void reportError(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        debugRepository.reportError(new DebugModel(getCurrentDate(),e.getMessage(),sw.toString(),TAG, Build.VERSION.SDK_INT,false));
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }
}
