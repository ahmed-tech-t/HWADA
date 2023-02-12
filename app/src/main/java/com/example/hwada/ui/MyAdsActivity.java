package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.adapter.MyAdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.databinding.ActivityMyAdsBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.UserViewModel;

import java.util.ArrayList;

public class MyAdsActivity extends AppCompatActivity implements MyAdsAdapter.OnItemListener {

    UserViewModel userViewModel ;
    ActivityMyAdsBinding binding ;
    User user;
    private App app;
    private static final String TAG = "MyAdsActivity";

    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAdsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        userViewModel = UserViewModel.getInstance();
        user = intent.getParcelableExtra("user");
        debounceHandler = new Handler();
        app = (App) getApplication();
        binding.shimmerMyAdsActivity.startShimmer();
        handelData();
    }

    private void setMyAdsAdapter(){
        closeShimmer();
        binding.myAdsRecycler.setVisibility(View.VISIBLE);

        MyAdsAdapter myAdsAdapter = new MyAdsAdapter(this);
        myAdsAdapter.setList(user ,this);
        binding.myAdsRecycler.setAdapter(myAdsAdapter);
        binding.myAdsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void getItemPosition(int position) {
        if (debouncing) {
            // Remove the previous runnable
            debounceHandler.removeCallbacks(debounceRunnable);
        } else {
            // This is the first click, so open the item
            debouncing = true;
            callAdvertiserFragment(position);
        }
        // Start a new timer
        debounceRunnable = () -> debouncing = false;
        debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY_MILLIS);

    }

    private void callAdvertiserFragment(int pos){
        AdvertiserFragment fragment = new AdvertiserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad",user.getAds().get(pos));
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setUserOnline(user.getUId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline(user.getUId());
    }

    private void getAlUserAds(){
        userViewModel.getAllUserAds(user.getUId()).observe(this, new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                user.setAds(ads);
                setMyAdsAdapter();
            }
        });
    }

    private void handelData(){
        if(user.getAds().isEmpty() ){
            Log.e(TAG, "handelData: " );
            getAlUserAds();
        }else {
            setMyAdsAdapter();
        }
    }

    private void closeShimmer(){
        binding.shimmerMyAdsActivity.setVisibility(View.GONE);
        binding.shimmerMyAdsActivity.stopShimmer();
    }
}