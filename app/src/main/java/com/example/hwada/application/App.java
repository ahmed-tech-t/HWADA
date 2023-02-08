package com.example.hwada.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hwada.database.DbHandler;
import com.example.hwada.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class App extends Application {

    public final int PICK_IMAGE_REQUEST = 2;
    public final int REQUEST_CAMERA_PERMISSION = 3;

    private static final String TAG = "App";
    UserViewModel userViewModel ;
    private int resumeCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        userViewModel = UserViewModel.getInstance();
    }

    public void setUserOnline() {
        if (resumeCounter == 0) {
            // update the user's last seen time
            userViewModel.setUserStatus(DbHandler.ONLINE);
        }
        resumeCounter++;
    }

    public void setUserOffline() {
        resumeCounter--;
        if (resumeCounter == 0) {
            userViewModel.setUserStatus(DbHandler.OFFLINE);
            userViewModel.setUserLastSeen(getCurrentDate());
        }
    }
    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy , h:mm a");
        return  sdf.format(date);
    }
    public String getCurrentDateByMlSec(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy , h:mm,ss:SS, a");
        return  sdf.format(date);
    }
    public boolean checkStoragePermissions() {
        if(Build.VERSION.SDK_INT <=32){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        }else if( Build.VERSION.SDK_INT >=33){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ;

        }
        return false;
    }
    public void requestStoragePermissions(Context context) {
        Log.e(TAG, "requestPermissions: "+Build.VERSION.SDK_INT );
        if(Build.VERSION.SDK_INT <= 32){
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }else if( Build.VERSION.SDK_INT >=33){
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            }, PICK_IMAGE_REQUEST);
        }

    }

    public boolean checkCameraPermission(Context context) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            return true ;
        }
        return false ;
    }


}
