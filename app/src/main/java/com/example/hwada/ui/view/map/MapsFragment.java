package com.example.hwada.ui.view.map;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentMapsBinding;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends BottomSheetDialogFragment implements OnMapReadyCallback,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveStartedListener,
        View.OnClickListener  {


    private User user;
    private GoogleMap mMap;
    View mapView;
    View locationButton;
    UserAddressViewModel userAddressViewModel ;

    Dialog saveDialog;
    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;

    FragmentMapsBinding binding ;

    UserViewModel userViewModel ;

    DebugViewModel debugViewModel ;
    App app;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        binding.arrowMapFragment.setOnClickListener(this);
        app =(App) getContext().getApplicationContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         try {
             setBottomSheet(view);

             SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
             if (mapFragment != null) {
                 mapView = mapFragment.getView();
                 mapFragment.getMapAsync(this);
                 Log.e(TAG, "onViewCreated: " );
             }
         }catch (Exception e){
             app.reportError(e,getContext());
         }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            mMap = googleMap;
            if (!app.checkLocationPermissions(getContext()))app.requestLocationPermissions(getActivity());
            Log.e(TAG, "onMapReady: Map is ready ");
            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveStartedListener(this);
            binding.icLocationFragment.setOnClickListener(this);
            binding.imArrowFragment.setOnClickListener(this);
            binding.tvUserAddressMapsFragment.setOnClickListener(this);
            binding.btSaveNewLocationFragment.setOnClickListener(this);

            if(user.getLocation()!=null){
                setUserLocation(user.getLocation());
                getUserAddress(user.getLocation());
            }else {
                app.reportError("location is null in Map fragment",getContext());
            }
        }catch (Exception e){
            app.reportError(e,getContext());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        onBackPressed(dialog);
        return dialog;

    }



    private void setBottomSheet(View view){
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_DRAGGING)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(newState==BottomSheetBehavior.STATE_HIDDEN) dismiss();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void onBackPressed(Dialog dialog){
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                // getAction to make sure this doesn't double fire
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return true; // Capture onKey
                }
                return false; // Don't capture
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      try {
          user = getArguments().getParcelable("user");
          userViewModel = UserViewModel.getInstance();
          debugViewModel = ViewModelProviders.of(getActivity()).get(DebugViewModel.class);
          userAddressViewModel = ViewModelProviders.of(this).get(UserAddressViewModel.class);

      }catch (Exception e){
          app.reportError(e,getContext());
      }
    }

    @SuppressLint("SuspiciousIndentation")
    private void initLocationButton() {
      try {
          locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
          if (locationButton != null)
              locationButton.setVisibility(View.GONE);
      }catch (Exception e){
          app.reportError(e,getContext());
      }
    }


    @SuppressLint("MissingPermission")
    public void setUserLocation(LocationCustom location) {
       try {
           //mMap.clear();
           LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
           CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
           mMap.animateCamera(cameraUpdate);

          handelMapIfLocationGranted();

       }catch (Exception e){
           app.reportError(e,getContext());
       }
    }

    @SuppressLint("MissingPermission")
    private void handelMapIfLocationGranted(){
        if (app.checkLocationPermissions(getContext())) {
            if(mMap!=null){
                mMap.setMyLocationEnabled(true);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));
                initLocationButton();
            }
        }
    }
    public void getUserAddress(LocationCustom location) {
        userAddressViewModel.getUserAddress(location).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.tvUserAddressMapsFragment.setText(s);
            }
        });
    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View v) {
      try {
          if(v.getId() == binding.arrowMapFragment.getId()){
              bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

          } else if (v.getId() == binding.icLocationFragment.getId()) {
              if (locationButton != null){
                  if(app.isLocationEnabled()){
                      locationButton.callOnClick();
                  }else {
                      app.askUserToOpenGps(getActivity(),true);
                  }
              }else{
                showToast(getString(R.string.locationPermissionWarning));
              }

          } else if (v.getId() == binding.btSaveNewLocationFragment.getId()) {
              //Todo save to data base
              updateLocation(getCameraLocation());
          } else if (v.getId() == binding.imArrowFragment.getId() || v.getId() == binding.tvUserAddressMapsFragment.getId()) {

              //TODO

          }
      }catch (Exception e){
          app.reportError(e,getContext());
      }
    }
    private Toast mCurrentToast;
    public void showToast(String message) {
        if (mCurrentToast == null) {
            mCurrentToast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
            mCurrentToast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mCurrentToast.addCallback(new Toast.Callback() {
                    @Override
                    public void onToastShown() {
                        super.onToastShown();
                    }

                    @Override
                    public void onToastHidden() {
                        super.onToastHidden();
                        mCurrentToast = null;
                    }
                });
            }
        }
    }
    @Override
    public void onCameraIdle() {
       try {
           binding.btSaveNewLocationFragment.setActivated(true);
           binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
           binding.btSaveNewLocationFragment.setClickable(true);

           LocationCustom location = new LocationCustom(getCameraLocation().getLatitude(),getCameraLocation().getLongitude());
          if(location!=null) getUserAddress(location);
       }catch (Exception e){
           app.reportError(e,getContext());
       }
    }


    private Location getCameraLocation(){
        Location location = new Location("App");
       try {
           CameraPosition position = mMap.getCameraPosition();
           location.setLatitude(position.target.latitude);
           location.setLongitude(position.target.longitude);
       }catch (Exception e){
           app.reportError(e,getContext());
       }
        return location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       try {
           if (requestCode == app.LOCATION_PERMISSION_ID) {
               Log.e(TAG, "onRequestPermissionsResult: " );
               if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                   Log.e(TAG, "onRequestPermissionsResult: " );
                   initLocationButton();
                   //  Log.e(TAG, "onRequestPermissionsResult: permission granted and get new location");
               }else {
                   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
               }
           }
       }catch (Exception e){
           app.reportError(e,getContext());
       }
    }

    @Override
    public void onCameraMoveStarted(int i) {
      try {
          binding.btSaveNewLocationFragment.setActivated(false);
          binding.btSaveNewLocationFragment.setClickable(false);
          binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_gray));

      }catch (Exception e){
          app.reportError(e,getContext());
      }
    }

    private void updateLocation(Location location) {
       try {
           LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
           userViewModel.updateLocationUser(locationCustom,binding.tvUserAddressMapsFragment.getText().toString());
           bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
       }catch (Exception e){
          app.reportError(e,getContext());
       }
    }

    @Override
    public void onResume() {
        super.onResume();
        handelMapIfLocationGranted();
    }
}