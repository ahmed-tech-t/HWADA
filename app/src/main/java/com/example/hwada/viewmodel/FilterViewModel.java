package com.example.hwada.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilterViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData;

    public FilterViewModel(){
        mutableLiveData= new MutableLiveData<>();
    }

    public LiveData<String> getFilter(){
        return mutableLiveData;
    }
    public void setFilter(String filterType){
        this.mutableLiveData.setValue(filterType);
    }
}
