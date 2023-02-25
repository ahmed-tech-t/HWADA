package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Report;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.MyAdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.databinding.ActivityMyAdsBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.util.ArrayList;

public class MyAdsActivity extends AppCompatActivity implements MyAdsAdapter.OnItemListener , View.OnClickListener {

    UserViewModel userViewModel ;
    ActivityMyAdsBinding binding ;
    User user;
    MyAdsAdapter myAdsAdapter;
    AdsViewModel adsViewModel ;
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adsViewModel = new ViewModelProvider(this).get(AdsViewModel.class);
        user = intent.getParcelableExtra(getString(R.string.userVal));
        user.setAds(new ArrayList<>());

        debounceHandler = new Handler();
        app = (App) getApplication();
        binding.shimmerMyAdsActivity.startShimmer();
        myAdsAdapter = new MyAdsAdapter(this);
        setRecycler();
        setAdsListener();
    }

    private void setRecycler(){
        closeShimmer();
        binding.myAdsRecycler.setVisibility(View.VISIBLE);
        myAdsAdapter.setList(user,this);
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

    @Override
    public void getClickedItemMenu(int position,ImageView imageView) {
       callPopupMenu(position,imageView);
    }

    private void callPopupMenu(int position,ImageView imageView) {
        Ad ad = user.getAds().get(position);
        PopupMenu popup = new PopupMenu(this,imageView);
        popup.getMenuInflater().inflate(R.menu.my_ads_menu, popup.getMenu());
        Menu menu = popup.getMenu();
        MenuItem statusMenuItem = menu.findItem(R.id.ad_status_my_ads_menu);

        if(ad.isActive()){
            statusMenuItem.setTitle(getString(R.string.deactivate));
        }else statusMenuItem.setTitle(getString(R.string.activate));

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==R.id.edit_my_ads_menu){
                callAddNewAdActivity(position);
            }else if((item.getItemId()==R.id.ad_status_my_ads_menu)){
                if(ad.isActive()) ad.setActive(false);
                else ad.setActive(true);
                adsViewModel.updateAdStatus(ad);
            }
            return true;
        });

        popup.show();//showing popup menu

    }

    private void callAdvertiserFragment(int pos){
        AdvertiserFragment fragment = new AdvertiserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.userVal), user);
        bundle.putParcelable(getString(R.string.adVal),user.getAds().get(pos));
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setUserOnline(user.getUId(),this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline(user.getUId(),this);
    }




    private void closeShimmer(){
        binding.shimmerMyAdsActivity.setVisibility(View.GONE);
        binding.shimmerMyAdsActivity.stopShimmer();
    }

    @Override
    public void onClick(View v) {

    }
    public void callAddNewAdActivity(int pos){
        try {
            Intent intent = new Intent(this, AddNewAdActivity.class);
            intent.putExtra(getString(R.string.userVal),user);
            intent.putExtra(getString(R.string.adVal),user.getAds().get(pos));
            intent.putExtra(getString(R.string.modeVal),getString(R.string.editModeVal));
            startActivity(intent);
        }catch (Exception e){
            app.reportError(e,this);
        }
    }

    private void setAdsListener(){
        userViewModel.setUserAdsListener(user.getUId()).observe(this, ads -> {
            for (Ad ad: ads) {
                int pos = user.getAds().indexOf(ad);
                if(pos == -1){
                    myAdsAdapter.addNewItem(ad);
                }else {
                    myAdsAdapter.updateItem(ad,pos);
                }
            }
            if(!user.getAds().isEmpty()) binding.myAdsRecycler.setBackgroundResource(R.drawable.recycle_view_background);
        });
    }
}