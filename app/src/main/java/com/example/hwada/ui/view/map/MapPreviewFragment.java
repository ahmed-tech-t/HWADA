package com.example.hwada.ui.view.map;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentAdReviewsBinding;
import com.example.hwada.databinding.FragmentMapPreviewBinding;
import com.example.hwada.databinding.FragmentMapsBinding;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class MapPreviewFragment extends BottomSheetDialogFragment implements OnMapReadyCallback, View.OnClickListener  {


    Ad ad ;
    private GoogleMap mMap;
    int PERMISSION_ID = 1;
    View mapView;
    View locationButton;

    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;

    FragmentMapPreviewBinding binding ;
    DebugViewModel debugViewModel ;

    App app ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapPreviewBinding.inflate(inflater, container, false);
        binding.arrowMapPreviewFragment.setOnClickListener(this);
        app = (App) getContext().getApplicationContext();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            setBottomSheet(view);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_preview_fragment);
            if (mapFragment != null) {
                mapView = mapFragment.getView();
                mapFragment.getMapAsync(this);
            }
        }catch (Exception e){
           // reportError(e);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            mMap = googleMap;
            if (!checkPermissions()) requestPermissions();
            Log.e(TAG, "onMapReady: Map is ready ");

            binding.icLocationMapPreviewFragment.setOnClickListener(this);
            if(ad.getAuthorLocation()!=null) {
                setMapSettingAndMoveCamera(ad.getAuthorLocation());
            }

        }catch (Exception e){
            e.printStackTrace();
           // reportError(e);
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
        ConstraintLayout layout = dialog.findViewById(R.id.bottom_sheet_map_preview_fragment);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
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
            ad = getArguments().getParcelable("ad");
            debugViewModel = ViewModelProviders.of(getActivity()).get(DebugViewModel.class);


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
    public void setMapSettingAndMoveCamera(LocationCustom location) {
        try {
            Log.e(TAG, "setMapSettingAndMoveCamera: " );
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);

            if (checkPermissions()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                initLocationButton();
            }
            mMap.addMarker(new MarkerOptions()
                    .position(latLng) // position
                    .title(getUserAddress(ad.getAuthorLocation()))
                    .icon(markerImage())
            );
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));

        }catch (Exception e){
            app.reportError(e,getContext());
        }
    }

    private BitmapDescriptor markerImage(){

        VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(getContext(),R.drawable.marker_icon);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View v) {
        try {
            if(v.getId() == binding.arrowMapPreviewFragment.getId()){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else if (v.getId() == binding.icLocationMapPreviewFragment.getId()) {
                if (locationButton != null)
                    locationButton.callOnClick();
            }
        }catch (Exception e){
            app.reportError(e,getContext());
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }catch (Exception e){
            app.reportError(e,getContext());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == PERMISSION_ID) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    Log.e(TAG, "onRequestPermissionsResult: " );
                    //  Log.e(TAG, "onRequestPermissionsResult: permission granted and get new location");
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        }catch (Exception e){
            app.reportError(e,getContext());
        }
    }
    private String getUserAddress(LocationCustom location) {
        try {

            LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            String address = "loading your location...";
            List<Address> addresses = geocoder.getFromLocation(locationCustom.getLatitude(), locationCustom.getLongitude(), 1);
           if(addresses.size()>0) {
                address = addresses.get(0).getAddressLine(0);
                for (String s: address.split(",")) {
                    if(address.split(",").length<3){
                        address +=s;
                    }
                }

            }
            return address ;
        } catch (IOException e) {
            e.printStackTrace();
            app.reportError(e,getContext());
        }
        return "";
    }


}