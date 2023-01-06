package com.example.hwada.repository;


import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
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
