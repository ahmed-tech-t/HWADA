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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class SplashRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(DbHandler.userCollection);
    private static final String TAG = "SplashRepository";
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
                if (task.isSuccessful()) {
                    DocumentSnapshot userSnapshot = task.getResult();
                    User user = userSnapshot.toObject(User.class);
                    ArrayList<Ad> favAdsList = new ArrayList<>();
                   userSnapshot.getReference().collection(DbHandler.favAdsCollection).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    String adId = document.getString("adId");
                                    String category = document.getString("category");
                                    String subCategory = document.getString("subCategory");
                                    String subSubCategory = document.getString("subSubCategory");
                                    Ad ad = new Ad(adId, category, subCategory, subSubCategory);
                                    favAdsList.add(ad);
                                }
                                if(user!=null && favAdsList!=null && favAdsList != null)  user.setFavAds(favAdsList);
                                userMutableLiveData.setValue(user);
                            } else {
                                Log.e(TAG, "onComplete: error when getting userFavAds");
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "onComplete: error with getting user" );
                    User newUser = new User();
                    newUser.setUsername("newUser");
                    userMutableLiveData.setValue(newUser);
                }
            }
        });
        return userMutableLiveData ;
    }





}
