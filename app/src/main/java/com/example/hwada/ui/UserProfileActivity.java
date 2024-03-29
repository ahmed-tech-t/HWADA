package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
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
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity implements AdsGridAdapter.OnItemListener, View.OnClickListener {

    ActivityUserProfileBinding binding ;
    UserViewModel userViewModel ;
    FavViewModel favViewModel ;

    User user ;
    User receiver ;
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

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        favViewModel = FavViewModel.getInstance() ;


        user = intent.getParcelableExtra("user");
        receiver = intent.getParcelableExtra("receiver");

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
        if(!adsList.isEmpty())binding.recyclerUserProfileActivity.setBackgroundResource(R.drawable.recycle_view_background);

        binding.recyclerUserProfileActivity.setAdapter(adapter);
        binding.recyclerUserProfileActivity.setLayoutManager(new GridLayoutManager(this,2));

    }
    private void getAlUserAds(){
        userViewModel.getAllUserAds(receiver.getUId()).observe(this, new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                adsList = ads;
                setRecycler();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void setDataToView(){
        Picasso.get().load(receiver.getImage()).into(binding.simUserImageUserProfileActivity, new Callback() {
            @Override
            public void onSuccess() {
                binding.progressBarUserProfileActivity.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        binding.tvUserNameUserProfileActivity.setText(receiver.getUsername());

        if(receiver.getAboutYou()==null){
            binding.llDescriptionUserProfileActivity.setVisibility(View.GONE);
        }else binding.tvUserDescriptionUserProfileActivity.setText(receiver.getAboutYou());

        String joinDate = app.getDateFromTimeStamp(receiver.getTimeStamp()).split(",")[0];
        String join = getString(R.string.joinAt)+" "+ joinDate;
        binding.tvUserJoinDateUserProfileActivity.setText(join);

        binding.tvUserRatingUserProfileActivity.setText(Double.toString(receiver.getRating()));
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
            if(receiver.getImage()!=null) callImageDialog();
        }
    }

    public void callImageDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("image", receiver.getImage());
        ImageMiniDialogFragment fragment = new ImageMiniDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

}