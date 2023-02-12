package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.ActivityAdsBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.util.ArrayList;

public class AdsActivity extends AppCompatActivity implements View.OnClickListener , AdsAdapter.OnItemListener {

    AdsAdapter adapter;
    ArrayList<Ad> adsList;
    private App app;

    FavViewModel favViewModel ;
    UserViewModel userViewModel ;
    AdsViewModel adsViewModel;

    User user;
    String category;
    String subCategory;
    String subSubCategory;

    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    private static final String TAG = "AdsActivity";
    ActivityAdsBinding binding;

    AdvertiserFragment advertiserFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        app = (App) getApplication();

        binding.shimmerAdsActivity.startShimmer();

        userViewModel = UserViewModel.getInstance();
        adsViewModel =  AdsViewModel.getInstance() ;
        favViewModel = FavViewModel.getInstance() ;

        category = intent.getStringExtra("category");
        subCategory = intent.getStringExtra("subCategory");
        subSubCategory = intent.getStringExtra("subSubCategory");

        advertiserFragment = new AdvertiserFragment();
        debounceHandler = new Handler();

        setUserFavListener();
        getAllAds();

    }

    private void setUserFavListener(){
        favViewModel.userFavAdsListener(user.getUId()).observe(this, new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                user.setFavAds(ads);
                if(advertiserFragment.isAdded())setRecycler();
            }
        });
    }
    private void setRecycler() {
        closeShimmer();
        try {
            adapter = new AdsAdapter(this);
            adapter.setList(user,adsList,this);
            binding.recyclerAdsActivity.setAdapter(adapter);

            binding.recyclerAdsActivity.setVisibility(View.VISIBLE);
            if(adsList.size()>0) binding.recyclerAdsActivity.setBackgroundResource(R.drawable.recycle_view_background);
            binding.recyclerAdsActivity.setLayoutManager(new LinearLayoutManager(this));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {

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

    @Override
    public void getFavItemPosition(int position, ImageView favImage) {
        String adId = adsList.get(position).getId();
        int favPos = adIsInFavList(adId);
        if (favPos != -1) {

            user.getFavAds().remove(favPos);
            favViewModel.deleteFavAd(user.getUId(),adsList.get(position));
            favImage.setImageResource(R.drawable.fav_uncheck_icon);

        } else {
            if (user.getFavAds() == null) user.initFavAdsList();

            favViewModel.addFavAd(user.getUId(),adsList.get(position));
            user.getFavAds().add(adsList.get(position));
            favImage.setImageResource(R.drawable.fav_checked_icon);
        }
    }
    private int adIsInFavList(String id) {
        for (int i =  0 ; i < user.getFavAds().size(); i++) {
            if(user.getFavAds().get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }

    private void getAllAds(){
        if(category.equals(DbHandler.FREELANCE)){
            getAllAds(category,subCategory,subSubCategory);
        }else getAllAds(category,subCategory);
    }
    private void getAllAds(String category ,String subCategory){
        adsViewModel.getAllAds(category,subCategory).observe(this, ads -> {
            adsList = ads;
            setRecycler();
        });
    }

    private void getAllAds(String category ,String subCategory, String subSubCategory){
        adsViewModel.getAllAds(category,subCategory,subSubCategory).observe(this, ads -> {
            adsList = ads;
           setRecycler();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debounceHandler.removeCallbacks(debounceRunnable);
    }
    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

    private void closeShimmer(){
        binding.shimmerAdsActivity.setVisibility(View.GONE);
        binding.shimmerAdsActivity.stopShimmer();
    }
    private void callAdvertiserFragment(int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad",adsList.get(pos));
        advertiserFragment.setArguments(bundle);
        advertiserFragment.show(getSupportFragmentManager(),advertiserFragment.getTag());
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
}