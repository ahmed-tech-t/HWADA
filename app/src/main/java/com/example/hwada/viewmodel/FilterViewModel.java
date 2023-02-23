package com.example.hwada.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.FilterModel;

public class FilterViewModel extends ViewModel {

    MutableLiveData<FilterModel> mutableLiveData;

    public FilterViewModel(){
        mutableLiveData= new MutableLiveData<>();
    }

    public LiveData<FilterModel> getFilter(){
        return mutableLiveData;
    }
    public void setFilter(FilterModel filter){
        this.mutableLiveData.setValue(filter);
    }
}
