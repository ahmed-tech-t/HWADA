package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.repository.DebugRepository;

public class DebugViewModel extends AndroidViewModel {

    DebugRepository repository ;

    public DebugViewModel(@NonNull Application application) {
        super(application);
        repository =new DebugRepository(application);
    }
    public void reportError(DebugModel error){
        repository.reportError(error);
    }
}
