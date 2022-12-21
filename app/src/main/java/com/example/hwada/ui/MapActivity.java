package com.example.hwada.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.hwada.Model.User;
import com.example.hwada.R;

import com.example.hwada.databinding.ActivityMapBinding;
import com.example.hwada.ui.view.MapBottomSheet_Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveStartedListener,
        View.OnClickListener {

    private User user;
    private GoogleMap mMap;
    int PERMISSION_ID = 1;
    View mapView;
    View locationButton;
    BottomSheetBehavior bottomSheetBehavior;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        user =  intent.getParcelableExtra("user");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

    }


    @SuppressLint("SuspiciousIndentation")
    private void initLocationButton() {
        locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        if (locationButton != null)
            locationButton.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void setUserLocation(Location location) {
        //mMap.clear();
        LatLng userLocation = new LatLng(location.getLatitude(),
                location.getLongitude());


        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        if (checkPermissions()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            initLocationButton();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.empty_map_style));

        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (!checkPermissions()) requestPermissions();
        Log.e(TAG, "onMapReady: Map is ready ");
        mMap.setOnCameraIdleListener(this);
        //  bottomSheetLayout.setOnClickListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        binding.icLocation.setOnClickListener(this);
        binding.imArrow.setOnClickListener(this);
        binding.userAddress.setOnClickListener(this);
        binding.btSaveNewLocation.setOnClickListener(this);
        setUserLocation(user.getLocation());
        getUserAddress(user.getLocation());
        // setBottomSheetListener();
    }

    public void getUserAddress(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            address = address.split(",")[0] + address.split(",")[1];
            //TODO
            binding.userAddress.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.icLocation.getId()) {
            if (locationButton != null)
                locationButton.callOnClick();
        } else if (v.getId() == binding.btSaveNewLocation.getId()) {
            //Todo save to data base
            user.setLocation(getCameraLocation());
            goToMainActivity();
        } else if (v.getId() == binding.imArrow.getId() || v.getId() == binding.userAddress.getId()) {

            //TODO
            /*
            MapBottomSheet_Fragment fragment = new MapBottomSheet_Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("address",binding.userAddress.getText().toString());
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        */
        }
    }

    @Override
    public void onCameraIdle() {
       binding.btSaveNewLocation.setActivated(true);
       binding.btSaveNewLocation.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
       binding.btSaveNewLocation.setClickable(true);
        getUserAddress(getCameraLocation());
    }
    private void goToMainActivity() {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private Location getCameraLocation(){
        CameraPosition position = mMap.getCameraPosition();
        Location location = new Location("App");
        location.setLatitude(position.target.latitude);
        location.setLongitude(position.target.longitude);
        return location;
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                //  Log.e(TAG, "onRequestPermissionsResult: permission granted and get new location");
            }else {
                goToMainActivity();
            }
        }
    }



    @Override
    public void onCameraMoveStarted(int i) {
       binding.btSaveNewLocation.setActivated(false);
       binding.btSaveNewLocation.setClickable(false);
       binding.btSaveNewLocation.setBackgroundColor(ContextCompat.getColor(this, R.color.white_gray));

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    @Override
    public void onBackPressed() {

         super.onBackPressed();
    }
}


