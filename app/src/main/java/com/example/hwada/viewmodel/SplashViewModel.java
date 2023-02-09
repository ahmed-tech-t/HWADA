package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.User;
import com.example.hwada.repository.SplashRepository;

public class SplashViewModel extends AndroidViewModel {

    private SplashRepository splashRepository;
    public LiveData<User> isUserAuthenticatedLiveData;
    public LiveData<User> userLiveData;

    public SplashViewModel(Application application) {
        super(application);
        splashRepository = new SplashRepository(application);
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = splashRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public void setUid(String uid) {
        userLiveData = splashRepository.addUserToLiveData(uid);
    }
}