package com.example.hwada.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.ui.view.category.CategoryFragment;
import com.example.hwada.ui.view.main.FavTapLayoutFragment;
import com.example.hwada.ui.view.main.AccountFragment;
import com.example.hwada.ui.view.main.ChatFragment;
import com.example.hwada.ui.view.main.HomeFragment;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    Dialog locationDialog;
    int PERMISSION_ID = 1;
    private User user;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private UserViewModel userViewModel ;
    FusedLocationProviderClient mFusedLocationClient;
    DebugViewModel debugViewModel;


    String TAG = "MainActivity";
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // get user data
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");

        // ##############
            binding.addFloatActionBar.setOnClickListener(this);
        //****************
        callFragment(new HomeFragment(),"home");
        navBarOnSelected();
        userViewModel= ViewModelProviders.of(this).get(UserViewModel.class);
        debugViewModel = ViewModelProviders.of(this).get(DebugViewModel.class);
        ViewModelProviders.of(this).get(UserViewModel.class).getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User u) {
                user = u;
                //TODO SAVE TO DATA BASE
            }
        });


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
                                updateLocation(locationCustom);
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
           try {
               Location location = locationResult.getLastLocation();
               LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
               //save to database
               updateLocation(locationCustom);
           }catch (Exception e){
             reportError(e);
           }
       }
    };

    private void navBarOnSelected(){
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            try {
                switch (item.getItemId()){
                    case R.id.home:

                        callFragment(new HomeFragment(),"home");
                        break;
                    case R.id.chat:
                      callFragment(new ChatFragment(),"chat");
                        break;
                    case R.id.favorites:
                     callFragment(new FavTapLayoutFragment(),"fav");
                        break;
                    case R.id.account:
                      callFragment(new AccountFragment(),"account");
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
               reportError(e);
            }
            return true;
        });

    }

    public void callFragment(Fragment fragment,String tag){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.to_up,R.anim.to_down);
        //set Animation
        fragmentTransaction.replace(R.id.main_fragment_container, fragment,tag);
        fragmentTransaction.commit();
    }
    public void callAddNewAdActivity(String category ,String subCategory , String subSubCategory){
        Intent intent = new Intent(this, AddNewAdActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category",category);
        intent.putExtra("subCategory",subCategory);
        intent.putExtra("subSubCategory",subSubCategory);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==binding.addFloatActionBar.getId()){
            callBottomSheet(new CategoryFragment());
        }
    }
    public void callBottomSheet(BottomSheetDialogFragment fragment){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }
    public void callCategoryActivity(String tag,String target){
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("tag",tag);
        intent.putExtra("target",target);
        startActivity(intent);
    }
    public void callAdsActivity(String category,String subCategory){
        Intent intent = new Intent(this, AdsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category",category);
        intent.putExtra("subCategory",subCategory);
        startActivity(intent);
    }
    private void updateLocation(LocationCustom location) {
        userViewModel.updateLocationUser(location);
        userViewModel.updateLocationSuccessLiveData.observe(this, success -> {
            if(success){
                user.setLocation(location);
                userViewModel.setUser(user);
                if(locationDialog.isShowing()) locationDialog.dismiss();
            }else {
                Toast.makeText(this, getText(R.string.savingError), Toast.LENGTH_SHORT).show();
                updateLocation(location);
            }
        });
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