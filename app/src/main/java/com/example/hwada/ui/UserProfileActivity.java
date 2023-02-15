package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsGridAdapter;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ActivityUserProfileBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity implements AdsGridAdapter.OnItemListener, View.OnClickListener {

    ActivityUserProfileBinding binding ;
    UserViewModel userViewModel ;
    FavViewModel favViewModel ;

    User user ;
    ArrayList<Ad> adsList;
    App app ;
    AdvertiserFragment advertiserFragment ;

    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        debounceHandler = new Handler();
        advertiserFragment = new AdvertiserFragment();


        app = (App) getApplication();
        Intent intent = getIntent();

        userViewModel = UserViewModel.getInstance();
        favViewModel = FavViewModel.getInstance() ;


        user = intent.getParcelableExtra("user");

        binding.simUserImageUserProfileActivity.setOnClickListener(this);
        binding.shimmerUserProfileActivity.startShimmer();
        setDataToView();
        getAlUserAds();


    }

    private void closeShimmer(){
        binding.shimmerUserProfileActivity.setVisibility(View.GONE);
        binding.shimmerUserProfileActivity.stopShimmer();
    }

    private void setRecycler(){
        closeShimmer();
        AdsGridAdapter adapter = new AdsGridAdapter(this);
        adapter.setList(user,adsList,this);
        binding.recyclerUserProfileActivity.setVisibility(View.VISIBLE);
        if(adsList.size()>0) {
            binding.recyclerUserProfileActivity.setBackgroundResource(R.drawable.recycle_view_background);
        }
        binding.recyclerUserProfileActivity.setAdapter(adapter);
        binding.recyclerUserProfileActivity.setLayoutManager(new GridLayoutManager(this,2));

    }
    private void getAlUserAds(){
        userViewModel.getAllUserAds(user.getUId()).observe(this, new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                adsList = ads;
                setRecycler();
            }
        });
    }
    private void setDataToView(){
        Picasso.get().load(user.getImage()).into(binding.simUserImageUserProfileActivity);
        binding.tvUserNameUserProfileActivity.setText(user.getUsername());

        if(user.getAboutYou()==null){
            binding.llDescriptionUserProfileActivity.setVisibility(View.GONE);
        }
        binding.tvUserDescriptionUserProfileActivity.setText(user.getAboutYou());

        String joinDate = app.getDateFromTimeStamp(user.getTimeStamp()).split(",")[0];
        String join = getString(R.string.joinAt)+" "+ joinDate;
        binding.tvUserJoinDateUserProfileActivity.setText(join);

        binding.tvUserRatingUserProfileActivity.setText(Double.toString(user.getRating()));
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

    private void callAdvertiserFragment(int pos){
        if(!advertiserFragment.isAdded()){
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            bundle.putParcelable("ad",adsList.get(pos));
            advertiserFragment.setArguments(bundle);
            advertiserFragment.show(getSupportFragmentManager(),advertiserFragment.getTag());
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

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.simUserImageUserProfileActivity.getId()){
            if(user.getImage()!=null) callImageDialog();
        }
    }

    public void callImageDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("image", user.getImage());
        ImageMiniDialogFragment fragment = new ImageMiniDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

}