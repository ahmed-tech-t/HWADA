package com.example.hwada.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.WorkingTime;

import java.util.ArrayList;

public class WorkingTimeViewModel extends ViewModel {

    String TAG ="WorkingTimeViewModel";
    private MutableLiveData<ArrayList<WorkingTime>> workingTimeMutableLiveData = new MutableLiveData<>();


    public void setWorkingTimeList(ArrayList<WorkingTime>  w){
        workingTimeMutableLiveData.setValue(w);
    }
    public LiveData<ArrayList<WorkingTime>> getWorkingTimeList(){
        return workingTimeMutableLiveData;
    }


}
