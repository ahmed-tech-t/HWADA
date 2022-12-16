package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.User;
import com.example.hwada.repository.AuthenticationRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

import java.net.PortUnreachableException;

public class AuthViewModel extends AndroidViewModel {
    AuthenticationRepository repository ;
    MutableLiveData<Boolean> loggedStatus;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;


    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthenticationRepository(application);
        loggedStatus = repository.getUserLoggedMutableLiveData();
    }

    public void signUpWithEmail (User user ,String password ){
        authenticatedUserLiveData = repository.signUpWithEmail(user,password);
    }
    public void loginWithEmail (String email ,String password){
        authenticatedUserLiveData = repository.loginWithEmail(email,password);
    }
    public void logout(){
        repository.logout();
    }


    //****************************************
    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = repository.firebaseSignInWithGoogle(googleAuthCredential);
    }
    public void createUser(User authenticatedUser) {
        createdUserLiveData = repository.createUserInFirestoreIfNotExists(authenticatedUser);
    }
}
