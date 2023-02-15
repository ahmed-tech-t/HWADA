package com.example.hwada.application;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.database.DbHandler;
import com.example.hwada.repository.UserAddressRepo;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class App extends Application {

    public final int PICK_IMAGE_REQUEST = 2;
    public final int REQUEST_CAMERA_PERMISSION = 3;
    public final int LOCATION_PERMISSION_ID = 1;

    DebugViewModel debugViewModel;
    private static final String TAG = "App";
    UserViewModel userViewModel ;
    private int resumeCounter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        userViewModel = UserViewModel.getInstance();
    }

    public void setUserOnline(String userId) {
        if (resumeCounter == 0) {
            // update the user's last seen time
            userViewModel.setUserStatus(DbHandler.ONLINE,userId);
        }
        resumeCounter++;
    }

    public void setUserOffline(String userId) {
        resumeCounter--;
        if (resumeCounter == 0) {
            userViewModel.setUserStatus(DbHandler.OFFLINE,userId);
            userViewModel.setUserLastSeen(getCurrentDate(),userId);
        }
    }
    public Timestamp getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return  new Timestamp(new Date(date.getTime()));
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

    public SimpleDateFormat timeFormatInSecond(){
        return new SimpleDateFormat("d MMM yyyy , h:mm , s ,a", Locale.ENGLISH);
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
    public String getDateFromTimeStamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = timeFormatInSecond();
        return sdf.format(date);
    }
    public Timestamp getTimeStampFromDate(String d){
        SimpleDateFormat dateFormat = timeFormatInSecond();
        Date date = null;
        try {
            date = dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new Timestamp(date);
    }
    public void reportError(Exception e ,Context context){
        debugViewModel = ViewModelProviders.of((FragmentActivity) context).get(DebugViewModel.class);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        debugViewModel.reportError(new DebugModel(getCurrentDate(),e.getMessage(),sw.toString(),TAG, Build.VERSION.SDK_INT,false));
    }

    public void reportError(String s , Context context){
        debugViewModel = ViewModelProviders.of((FragmentActivity) context).get(DebugViewModel.class);
        debugViewModel.reportError(new DebugModel(getCurrentDate(),s,s,TAG, Build.VERSION.SDK_INT,false));
    }

    public boolean checkLocationPermissions(Context context) {
        try {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }catch (Exception e){
            reportError(e,context);
        }
        return false;

    }
    public boolean isLocationEnabled() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        }catch (Exception e){
            reportError(e,this);
        }
        return false;
    }

    public void requestLocationPermissions(Context context) {
        try {
            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        }catch (Exception e){
            reportError(e,context);
        }
    }
    public void askUserToOpenGps(final Activity activity,boolean withCancelButton) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("GPS setting!");
            builder.setMessage("GPS is not enabled, Do you want to go to settings menu? ");
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                    dialog.dismiss();
                }
            });
            if(withCancelButton){
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            builder.setCancelable(false);
            builder.show();
        } catch (Exception e) {
            reportError(e, this);
        }
    }
    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
