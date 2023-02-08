package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.hwada.Model.User;
import com.example.hwada.adapter.MyAdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.databinding.ActivityMyAdsBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;

public class MyAdsActivity extends AppCompatActivity implements MyAdsAdapter.OnItemListener {

    MyAdsAdapter myAdsAdapter;
    ActivityMyAdsBinding binding ;
    User user;
    private App app;

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
        user = intent.getParcelableExtra("user");
        debounceHandler = new Handler();
        app = (App) getApplication();

        setMyAdsAdapter();
    }

    private void setMyAdsAdapter(){
        myAdsAdapter = new MyAdsAdapter();
        myAdsAdapter.setList(user,this ,this);
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
        bundle.putInt("pos",pos);
        bundle.putParcelable("ad",user.getAds().get(pos));
        bundle.putParcelableArrayList("adsList",user.getAds());
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setUserOnline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline();
    }
}