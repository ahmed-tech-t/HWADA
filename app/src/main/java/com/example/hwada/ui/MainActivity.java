package com.example.hwada.ui;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.ui.view.AccountFragment;
import com.example.hwada.ui.view.ChatFragment;
import com.example.hwada.ui.view.FavoritesFragment;
import com.example.hwada.ui.view.HomeFragment;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Dialog locationDialog;
    int PERMISSION_ID = 1;
    private User user;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private UserViewModel userViewModel ;

    FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        callHomeFragment();
        navBarOnSelected();

        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        userViewModel= ViewModelProviders.of(this).get(UserViewModel.class);

        if(user.getLocation()!=null){
            userViewModel.setUser(user);
        }

        //disable middle button in bottom nav bar
        binding.bottomNavView.getMenu().getItem(2).setEnabled(false) ;
        //set user location

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();


    }

    public void setLocationDialog() {
        if (locationDialog != null && locationDialog.isShowing()) return;

        locationDialog = new Dialog(this);
        locationDialog.setContentView(R.layout.loading_user_location);
        Window window = locationDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);

        locationDialog.setCanceledOnTouchOutside(false);
        locationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        locationDialog.setCancelable(false);
        locationDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                  //  Log.e(TAG, "onRequestPermissionsResult: permission granted and get new location");
                getLastLocation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Log.e(TAG, "onResume: " );
        if(user.getLocation()==null){
            if(isLocationEnabled()){
                setLocationDialog();
                getLastLocation();
            }
        }
    }
    public void askUserToOpenGps() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
      alertDialog.setCancelable(false);
        alertDialog.show();
    }
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if(user.getLocation()==null) {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                   // Log.e(TAG, "getLastLocation: set location dialog" );
                    setLocationDialog();
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                               // Log.e(TAG, "location = null ");
                            } else {
                                user.setLocation(location);
                                userViewModel.setUser(user);
                                //TODO save to dataBase

                                if(locationDialog.isShowing()) locationDialog.dismiss();
                            }
                        }
                    });
                } else {
                    askUserToOpenGps();
                }
            } else {
                requestPermissions();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            user.setLocation(location);
            if(locationDialog.isShowing()) locationDialog.dismiss();
            //TODO save to dataBase
            userViewModel.setUser(user);


        }
    };

    private void callHomeFragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.main_fragment_container, new HomeFragment());
        fragmentTransaction.commit();
    }
    private void navBarOnSelected(){
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            try {
                switch (item.getItemId()){
                    case R.id.home:
                        callHomeFragment();
                        break;
                    case R.id.chat:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_fragment_container, new ChatFragment());
                        fragmentTransaction.commit();
                        break;
                    case R.id.favorites:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_fragment_container, new FavoritesFragment());
                        fragmentTransaction.commit();
                        break;
                    case R.id.account:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.main_fragment_container, new AccountFragment());
                        fragmentTransaction.commit();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        });

    }
}