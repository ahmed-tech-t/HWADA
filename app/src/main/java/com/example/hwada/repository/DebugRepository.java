package com.example.hwada.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DebugRepository {

    private FirebaseAuth auth ;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference debugRef = rootRef.collection(DbHandler.debugCollection);
   private Application application ;

   public DebugRepository(){}
    public DebugRepository(Application application){
        this.application = application;
    }
    public void reportError(DebugModel debugModel){
        Map<String,Object> data = new HashMap<>();
        data.put("tag",debugModel.getTag());
        data.put("massage",debugModel.getMassage());
        data.put("date",debugModel.getDate());
        data.put("body",debugModel.getBody());
        data.put("androidSdk",debugModel.getAndroidSdk());
        data.put("fixed",debugModel.isFixed());
        debugRef.document(getCurrentDate()).set(data);
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }
}
