package com.example.hwada.viewmodel;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.repository.ImagesRepository;
import java.util.ArrayList;
import java.util.List;

public class ImagesViewModel extends AndroidViewModel {

    private static final String TAG = "ImagesViewModel";
    private ImagesRepository repository ;
    public LiveData<List<Uri>> liveDataImagesList;

    public ImagesViewModel(@NonNull Application application) {
        super(application);
        repository =new ImagesRepository(application);
        liveDataImagesList =new MutableLiveData<>();
    }

    public void uploadImages (Ad ad){
        liveDataImagesList = repository.uploadImages(ad);
    }

}
