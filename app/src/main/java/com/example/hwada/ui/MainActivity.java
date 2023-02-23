package com.example.hwada.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.ui.view.EditUserFragment;
import com.example.hwada.ui.view.FilterFragment;
import com.example.hwada.ui.view.category.CategoryFragment;
import com.example.hwada.ui.view.main.AccountFragment;
import com.example.hwada.ui.view.main.ChatFragment;
import com.example.hwada.ui.view.main.FavoritesFragment;
import com.example.hwada.ui.view.main.HomeFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    Dialog locationDialog;
    private User user;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private UserViewModel userViewModel ;
    FusedLocationProviderClient mFusedLocationClient;
    DebugViewModel debugViewModel;
    UserAddressViewModel userAddressViewModel ;

    private App app;

    private int numberOfRequest =0;
    private static final String TAG = "MainActivity";
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      try {
          binding = ActivityMainBinding.inflate(getLayoutInflater());
          View view = binding.getRoot();
          setContentView(view);
          // get user data
          Intent intent = getIntent();
          user = (User) intent.getParcelableExtra("user");

          // ##############
          binding.addFloatActionBar.setOnClickListener(this);
          //****************
          callFragment(new HomeFragment(), "home");
          navBarOnSelected();
          userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
          userAddressViewModel = ViewModelProviders.of(this).get(UserAddressViewModel.class);

          app = (App) getApplication();


          setUserListener();

          //disable middle button in bottom nav bar
          binding.bottomNavView.getMenu().getItem(2).setEnabled(false);
          //set user location
          mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


      }catch (Exception e){
          app.reportError(e,this);
      }
    }

    private void setUserListener() {
        userViewModel.userListener(user.getUId()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User updatedUser) {
                user.updateUser(updatedUser);
            }
        });
    }

    private void setLocationManually(){
        if(!app.checkLocationPermissions(this)){
            LocationCustom locationCustom = new LocationCustom(31.23528,30.04167);
            getUserAddress(locationCustom);
        }
    }


    public void setLocationDialog() {
     try {
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
     }catch (Exception e){
         app.reportError(e,this);
     }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: "+user.getUId() );
        app.setUserOnline(user.getUId(),this);
        try {
           if(user.getLocation()==null){
               if(app.isLocationEnabled()){
                   getLastLocation();
               }else{
                   app.askUserToOpenGps(this,false);
               }
           }
       }catch (Exception e){
            app.reportError(e,this);
       }
    }




    @SuppressLint("MissingPermission")
    private void getLastLocation() {
      try {
          if(user.getLocation()==null) {
              if (app.checkLocationPermissions(this)) {
                  if (app.isLocationEnabled()) {
                      try {
                           setLocationDialog();
                          mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                              @Override
                              public void onComplete(@NonNull Task<Location> task) {
                                  Location location = task.getResult();
                                  if (location == null) {
                                      requestNewLocationData();
                                      // Log.e(TAG, "location = null ");
                                  } else {
                                      LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
                                      //save to database
                                      getUserAddress(locationCustom);
                                  }
                              }
                          });
                      }catch (Exception e){
                          app.reportError(e,this);
                      }

                  } else {
                      Log.e(TAG, "getLastLocation: " );
                      app.askUserToOpenGps(this,false);
                  }
              } else {
                  app.requestLocationPermissions(this);
              }
          }
      }catch (Exception e){
          app.reportError(e,this);
      }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

       try {
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
       }catch (Exception e){
           app.reportError(e,this);
       }
    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
           try {
               Location location = locationResult.getLastLocation();
               assert location != null;
               LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
               //save to database
               getUserAddress(locationCustom);
           }catch (Exception e){
               app.reportError(e,getApplication());
           }
       }
    };

    @SuppressLint("NonConstantResourceId")
    private void navBarOnSelected(){
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            try {
                switch (item.getItemId()){
                    case R.id.home:
                        callFragment(new HomeFragment(),getString(R.string.homeVal));
                        break;
                    case R.id.chat:
                      callFragment(new ChatFragment(),getString(R.string.chatVal));
                        break;
                    case R.id.favorites:
                     callFragment(new FavoritesFragment(),getString(R.string.favVal));
                        break;
                    case R.id.account:
                      callFragment(new AccountFragment(),getString(R.string.accountVal));
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                app.reportError(e,this);
            }
            return true;
        });

    }

    public void callFragment(Fragment fragment,String tag){
          try {
              if(getSupportFragmentManager().findFragmentByTag(tag) == null) {
                  Bundle bundle = new Bundle();
                  bundle.putParcelable(getString(R.string.userVal), user);
                  fragment.setArguments(bundle);
                  fragmentManager = getSupportFragmentManager();
                  fragmentTransaction = fragmentManager.beginTransaction();
                  fragmentTransaction.replace(R.id.main_fragment_container, fragment, tag);
                  fragmentTransaction.commit();
              }
          }catch (Exception e){
              app.reportError(e,this);
          }
    }
    public void callAddNewAdActivity(String category ,String subCategory , String subSubCategory){
       try {
           Intent intent = new Intent(this, AddNewAdActivity.class);
           intent.putExtra(getString(R.string.userVal),user);
           intent.putExtra(getString(R.string.modeVal),getString(R.string.newModeVal));
           intent.putExtra(getString(R.string.categoryVal),category);
           intent.putExtra(getString(R.string.subCategoryVal),subCategory);
           intent.putExtra(getString(R.string.subSubCategoryVal),subSubCategory);
           startActivity(intent);
       }catch (Exception e){
           app.reportError(e,this);
       }
    }


    @Override
    public void onClick(View v) {
        try {
            if(v.getId()==binding.addFloatActionBar.getId()){
                if(user.getPhone()!=null){
                    callBottomSheet(new CategoryFragment());
                }else {
                    showDialog(getString(R.string.warning),getString(R.string.unCompletedProfile));
                }
            }
        }catch (Exception e){
            app.reportError(e,this);
        }
    }
    public void callBottomSheet(BottomSheetDialogFragment fragment){
       try {
               Bundle bundle = new Bundle();
               bundle.putParcelable(getString(R.string.userVal), user);
               fragment.setArguments(bundle);
               fragment.show(getSupportFragmentManager(),fragment.getTag());

       }catch (Exception e){
           app.reportError(e,this);
       }
    }
    public void callCategoryActivity(String tag,String target){
       try {
           Intent intent = new Intent(this, CategoryActivity.class);
           intent.putExtra(getString(R.string.userVal),user);
           intent.putExtra(getString(R.string.tagVal),tag);
           intent.putExtra(getString(R.string.targetVal),target);
           startActivity(intent);
       }catch (Exception e){
           app.reportError(e,this);
       }
    }
    public void callAdsActivity(String category,String subCategory,String subSubCategory){
       try {
           Intent intent = new Intent(this, AdsActivity.class);
           intent.putExtra(getString(R.string.userVal),user);
           intent.putExtra(getString(R.string.categoryVal),category);
           intent.putExtra(getString(R.string.subCategoryVal),subCategory);
           intent.putExtra(getString(R.string.subSubCategoryVal),subSubCategory);
           startActivity(intent);
       }catch (Exception e){
           app.reportError(e,this);
       }
    }
    public void getUserAddress(LocationCustom location) {
        userAddressViewModel.getUserAddress(location).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                updateLocation(location , s);
            }
        });
    }

    private void updateLocation(LocationCustom location,String address) {
       userViewModel.updateLocationUser(location,address).observe(this, success -> {
           if(success){
               if(locationDialog!=null && locationDialog.isShowing()) locationDialog.dismiss();
           }else {
               Toast.makeText(this, getText(R.string.savingError), Toast.LENGTH_SHORT).show();
               updateLocation(location,address);
           }
       });
    }

    private void showDialog(String title ,String body){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(R.string.toProfile, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callBottomSheet(new EditUserFragment());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline(user.getUId(),this);
    }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          try {
              if (requestCode == app.LOCATION_PERMISSION_ID) {
                  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                      getLastLocation();
                  }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                      if(numberOfRequest<2){
                          if(numberOfRequest < 1 )showToast(getString(R.string.locationPermissionWarning));
                          app.requestLocationPermissions(this);
                      }else {
                          setLocationManually();
                      }
                      numberOfRequest += 1 ;
                  }
              }
          }catch (Exception e){
              app.reportError(e,this);
          }
        }

    private Toast mCurrentToast;
    public void showToast(String message) {
        if (mCurrentToast == null) {
            mCurrentToast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT);
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
    public void onBackPressed() {
        super.onBackPressed();
    }


}