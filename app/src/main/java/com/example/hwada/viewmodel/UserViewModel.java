package com.example.hwada.viewmodel;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.User;

public class UserViewModel extends ViewModel {
   private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();;


    public void setUser(User user){
        userMutableLiveData.setValue(user);
    }
    public LiveData<User> getUser(){
       return userMutableLiveData;
    }


}
