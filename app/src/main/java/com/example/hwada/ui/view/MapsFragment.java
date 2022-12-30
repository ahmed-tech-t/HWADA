package com.example.hwada.ui.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentMapsBinding;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.MapActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends BottomSheetDialogFragment implements OnMapReadyCallback,GoogleMap.OnCameraIdleListener,GoogleMap.OnCameraMoveStartedListener,
        View.OnClickListener  {


    private User user;
    private GoogleMap mMap;
    int PERMISSION_ID = 1;
    View mapView;
    View locationButton;

    GettingPassedData mListener;
    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;

    FragmentMapsBinding binding ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        binding.arrowMapFragment.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBottomSheet(view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
            Log.e(TAG, "onViewCreated: " );
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (!checkPermissions()) requestPermissions();
        Log.e(TAG, "onMapReady: Map is ready ");
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        binding.icLocationFragment.setOnClickListener(this);
        binding.imArrowFragment.setOnClickListener(this);
        binding.userAddressFragment.setOnClickListener(this);
        binding.btSaveNewLocationFragment.setOnClickListener(this);
        setUserLocation(user.getLocation());
        getUserAddress(user.getLocation());
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
        user = getArguments().getParcelable("user");
        Log.e(TAG, "onActivityCreated: "+user.getLocation().getLongitude()+"  "+ user.getLocation().getLatitude() );
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
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e(TAG, "setUserLocation: " );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        if (checkPermissions()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            initLocationButton();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));

        }
    }


    public void getUserAddress(Location location) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            String address = "loading your location...";
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses.size()>0) {

                address = addresses.get(0).getAddressLine(0);
                address = address.split(",")[0] + address.split(",")[1]+address.split(",")[2]+address.split(",")[3];


            }else if(addresses.size()==0) {
                locationButton.callOnClick();
            }
            binding.userAddressFragment.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View v) {
        if(v.getId() == binding.arrowMapFragment.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else if (v.getId() == binding.icLocationFragment.getId()) {
            if (locationButton != null)
                locationButton.callOnClick();
        } else if (v.getId() == binding.btSaveNewLocationFragment.getId()) {
            //Todo save to data base

            mListener.newLocation(getCameraLocation());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (v.getId() == binding.imArrowFragment.getId() || v.getId() == binding.userAddressFragment.getId()) {

            //TODO

        }
    }

    @Override
    public void onCameraIdle() {
        binding.btSaveNewLocationFragment.setActivated(true);
        binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
        binding.btSaveNewLocationFragment.setClickable(true);
        Log.e(TAG, "onCameraIdle: " );
        getUserAddress(getCameraLocation());
    }


    private Location getCameraLocation(){
        Location location = new Location("App");
        CameraPosition position = mMap.getCameraPosition();
        location.setLatitude(position.target.latitude);
        location.setLongitude(position.target.longitude);
        Log.e(TAG, "getCameraLocation: " );
        return location;
    }

    private boolean checkPermissions() {
        Log.e(TAG, "checkPermissions: " );
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        Log.e(TAG, "requestPermissions: " );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                Log.e(TAG, "onRequestPermissionsResult: " );
                //  Log.e(TAG, "onRequestPermissionsResult: permission granted and get new location");
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.e(TAG, "onCameraMoveStarted: " );
        binding.btSaveNewLocationFragment.setActivated(false);
        binding.btSaveNewLocationFragment.setClickable(false);
        binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_gray));

    }

    public interface GettingPassedData{
        void newLocation(Location location);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getParentFragment() == null) mListener = (GettingPassedData) getActivity();
            else  mListener = (GettingPassedData) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDataPassListener");
        }
    }
}