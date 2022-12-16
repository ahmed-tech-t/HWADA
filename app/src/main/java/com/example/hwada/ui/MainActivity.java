package com.example.hwada.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.MainAdapter;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.viewmodel.AdsViewModel;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    AdsViewModel viewModel;
    MainAdapter adapter;
    ActivityMainBinding binding;
    Dialog locationDialog;
    int PERMISSION_ID = 1;
    private User user;

    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");

        //set user location
        if(user.getLocation()!=null) getUserAddress(user.getLocation());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.userAddress.setOnClickListener(this);
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AdsViewModel.class);

        setAdsToList();
        getLastLocation();
    }

    public void setAdsToList() {
        adapter = new MainAdapter();
        binding.mainRecycler.setAdapter(adapter);
        viewModel.getAllAds();
        viewModel.adsLiveData.observe(this, ads -> {
            adapter.setList(ads);
        });
        binding.mainRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.userAddress.getId()) {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
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
                                getUserAddress(location);

                                //TODO save to dataBase
                                if(locationDialog.isShowing()) locationDialog.dismiss();
                               // Log.e(TAG, "getLastLocation: close location dialog" );
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
           // Log.e(TAG, "onLocationChanged: "+location.getLatitude() + " " + location.getLongitude());
            user.setLocation(location);
            getUserAddress(location);
            if(locationDialog.isShowing()) locationDialog.dismiss();
            //TODO save to dataBase
        }
    };

    public void getUserAddress(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        String address = addresses.get(0).getAddressLine(0);
            address = address.split(",")[0]+ address.split(",")[1] ;
            binding.userAddress.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}