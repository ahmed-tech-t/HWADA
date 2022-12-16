package com.example.hwada.repository;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class AuthenticationRepository {

private Application application ;
private MutableLiveData<Boolean> userLoggedMutableLiveData ;
private FirebaseAuth auth ;


    MutableLiveData<User> authenticatedUserMutableLiveData;

//*********************

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(DbHandler.userCollection);

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        auth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String phone = firebaseUser.getPhoneNumber();
                    User user = new User(uid, name, email,phone);
                    user.setImage(firebaseUser.getPhotoUrl().toString());
                    user.setNew(isNewUser);
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                Toast.makeText(application, authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "firebaseSignInWithGoogle: "+authTask.getException().getMessage() );
            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.getuId());
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.setCreated(true);
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Toast.makeText(application, userCreationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "createUserInFirestoreIfNotExists: "+userCreationTask.getException().getMessage() );
                        }
                    });
                } else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                Toast.makeText(application, uidTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "createUserInFirestoreIfNotExists: "+uidTask.getException().getMessage());
            }
        });
        return newUserMutableLiveData;
    }
    //**************************************

    public AuthenticationRepository(Application application) {
        this.application = application;
        authenticatedUserMutableLiveData = new MutableLiveData<>();
        userLoggedMutableLiveData =new MutableLiveData<>();
        auth = FirebaseAuth.getInstance();
    }


    public MutableLiveData<Boolean> getUserLoggedMutableLiveData() {
        return userLoggedMutableLiveData;
    }

    public MutableLiveData<User> signUpWithEmail(User user , String password){
        Log.e(TAG, "signUpWithEmail: "+ user.getEmail());
        auth.createUserWithEmailAndPassword(user.getEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                         FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            String name = user.getUsername();
                            String email = user.getEmail();
                            String phone = user.getPhone();
                            User user = new User(uid, name, email,phone);
                            user.setNew(isNewUser);
                            authenticatedUserMutableLiveData.setValue(user);
                         }
                }else {
                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, task.getException().getMessage());
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<User> loginWithEmail(String email , String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        User user = new User(uid, email);
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                }else {
                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }


    public void logout(){
        auth.signOut();
        userLoggedMutableLiveData.postValue(true);
    }

}
