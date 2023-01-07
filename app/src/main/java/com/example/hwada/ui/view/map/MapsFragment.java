package com.example.hwada.ui.view.map;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.Toast;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentMapsBinding;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserViewModel;
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
    int PERMISSION_ID = 1;
    View mapView;
    View locationButton;
    boolean firstOpen = true;
    Dialog saveDialog;
    GettingPassedData mListener;
    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;

    FragmentMapsBinding binding ;

    UserViewModel userViewModel ;

    DebugViewModel debugViewModel ;
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
     try {
         setBottomSheet(view);

         SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
         if (mapFragment != null) {
             mapView = mapFragment.getView();
             mapFragment.getMapAsync(this);
             Log.e(TAG, "onViewCreated: " );
         }
     }catch (Exception e){
         reportError(e);
     }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
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

        }catch (Exception e){
            reportError(e);
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
          userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
          debugViewModel = ViewModelProviders.of(getActivity()).get(DebugViewModel.class);
      }catch (Exception e){
          reportError(e);
      }
    }

    @SuppressLint("SuspiciousIndentation")
    private void initLocationButton() {
      try {
          locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
          if (locationButton != null)
              locationButton.setVisibility(View.GONE);
      }catch (Exception e){
          reportError(e);
      }
    }

    @SuppressLint("MissingPermission")
    public void setUserLocation(LocationCustom location) {
       try {
           //mMap.clear();
           LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

           mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

           mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
           if (checkPermissions()) {
               mMap.setMyLocationEnabled(true);
               mMap.getUiSettings().setMyLocationButtonEnabled(true);
               initLocationButton();
               mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.empty_map_style));

           }
       }catch (Exception e){
            reportError(e);
       }
    }


    private void getUserAddress(LocationCustom location) {
        try {

            LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

            String address = "loading your location...";
            List<Address> addresses = geocoder.getFromLocation(locationCustom.getLatitude(), locationCustom.getLongitude(), 1);

            if(firstOpen){
                firstOpen = false ;
                locationButton.callOnClick();
            }else if(addresses.size()>0) {

                address = addresses.get(0).getAddressLine(0);
                for (String s: address.split(",")) {
                    if(address.split(",").length<3){
                        address +=s;
                    }
                }

            }else if(addresses.size()==0) {
                locationButton.callOnClick();
            }
            binding.userAddressFragment.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
            reportError(e);
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View v) {
      try {
          if(v.getId() == binding.arrowMapFragment.getId()){
              bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

          } else if (v.getId() == binding.icLocationFragment.getId()) {
              if (locationButton != null)
                  locationButton.callOnClick();
          } else if (v.getId() == binding.btSaveNewLocationFragment.getId()) {
              //Todo save to data base
              setSavingDialog();
              updateLocation(getCameraLocation());
          } else if (v.getId() == binding.imArrowFragment.getId() || v.getId() == binding.userAddressFragment.getId()) {

              //TODO

          }
      }catch (Exception e){
          reportError(e);
      }
    }

    @Override
    public void onCameraIdle() {
       try {
           binding.btSaveNewLocationFragment.setActivated(true);
           binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
           binding.btSaveNewLocationFragment.setClickable(true);

           LocationCustom location =new LocationCustom(getCameraLocation().getLatitude(),getCameraLocation().getLongitude());
           getUserAddress(location);
       }catch (Exception e){
           reportError(e);
       }
    }


    private Location getCameraLocation(){
        Location location = new Location("App");
       try {
           CameraPosition position = mMap.getCameraPosition();
           location.setLatitude(position.target.latitude);
           location.setLongitude(position.target.longitude);
       }catch (Exception e){
           reportError(e);
       }
        return location;
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
           reportError(e);
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
           reportError(e);
       }
    }

    @Override
    public void onCameraMoveStarted(int i) {
      try {
          binding.btSaveNewLocationFragment.setActivated(false);
          binding.btSaveNewLocationFragment.setClickable(false);
          binding.btSaveNewLocationFragment.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_gray));

      }catch (Exception e){
          reportError(e);
      }
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
            reportError(e);
            throw new ClassCastException(context.toString()
                    + " must implement OnDataPassListener");
        }
    }

    public void setSavingDialog() {
      try {
          if (saveDialog != null && saveDialog.isShowing()) return;

          saveDialog = new Dialog(getContext());
          saveDialog.setContentView(R.layout.dialog_saving_data_layout);
          Window window = saveDialog.getWindow();
          window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
          window.setGravity(Gravity.BOTTOM);
          saveDialog.setCanceledOnTouchOutside(false);
          saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          saveDialog.setCancelable(false);
          saveDialog.show();
      }catch (Exception e){
          reportError(e);
      }
    }

    private void updateLocation(Location location) {
       try {
           LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
           userViewModel.updateLocationUser(locationCustom);
           userViewModel.updateLocationSuccessLiveData.observe(this, success -> {
               if(success){
                   if(saveDialog.isShowing()) saveDialog.dismiss();
                   mListener.newLocation(getCameraLocation());
                   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
               }else {
                   Toast.makeText(getContext(), getText(R.string.savingError), Toast.LENGTH_SHORT).show();
               }
           });
       }catch (Exception e){
           reportError(e);
       }
    }


    private void reportError(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        debugViewModel.reportError(new DebugModel(getCurrentDate(),e.getMessage(),sw.toString(),TAG, Build.VERSION.SDK_INT,false));
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }
}