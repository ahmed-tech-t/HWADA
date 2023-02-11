package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.hwada.Model.LocationCustom;
import com.example.hwada.repository.UserAddressRepo;

public class UserAddressViewModel extends AndroidViewModel {
    UserAddressRepo repo ;

    public UserAddressViewModel(@NonNull Application application) {
        super(application);
        repo = new UserAddressRepo(application);
    }


    public LiveData<String> getUserAddress(LocationCustom locationCustom){
        return repo.getUserAddress(locationCustom);
    }

}
