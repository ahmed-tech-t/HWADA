package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.databinding.ActivityHomeFoodBinding;
import com.example.hwada.viewmodel.AdsViewModel;

import java.util.ArrayList;

public class AdsActivity extends AppCompatActivity implements View.OnClickListener , AdsAdapter.OnItemListener {

    AdsAdapter adapter;
    ArrayList<Ads> adsList;

    AdsViewModel viewModel;
    User user;
    String category;
    String subCategory;

    ActivityHomeFoodBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeFoodBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        category = intent.getStringExtra("category");
        subCategory = intent.getStringExtra("subCategory");
        viewModel = ViewModelProviders.of(this).get(AdsViewModel.class);

        setAdsToList();
    }

    public void setAdsToList() {
        adapter = new AdsAdapter();
        try {
            binding.homeFoodRecycler.setAdapter(adapter);
            switch (category){
                case "homeFood":
                    getHomeFoodAds();
                    break;
                case "worker":
                    getWorkersAds();
                    break;
                case "freelance":
                    getFreelanceAds();
                    break;
                case "handcraft":
                    getHandcraftAds();
                    break;
            }
            binding.homeFoodRecycler.setLayoutManager(new LinearLayoutManager(this));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void getItemPosition(int position) {

    }

    @Override
    public void getFavItemPosition(int position, ImageView favImage) {
        String adId = String.valueOf(position);
        if(adIsInFavList(adId)){
            user.removeOneAdFromFavorite(adId);
            favImage.setImageResource(R.drawable.fav_uncheck_icon);
        }else if(!adIsInFavList(adId))  {
            user.addOneAdToFavorite(adId);
            favImage.setImageResource(R.drawable.fav_checked_icon);
        }
    }
    private boolean adIsInFavList(String id){
        return user.getFavoriteAds().contains(id);
    }

    private void getWorkersAds(){
        viewModel.getAllWorkersAds();
        viewModel.workerAdsLiveData.observe(this, ads -> {
            adsList = ads;
            if(user!=null) {
                adapter.setList(user,ads,this);
            }
        });
    }
    private void getHandcraftAds(){
        viewModel.getAllHandcraftAds();
        viewModel.handcraftAdsLiveData.observe(this, ads -> {
            adsList = ads;
            if(user!=null) {
                adapter.setList(user,ads,this);
            }
        });
    }
    private void getHomeFoodAds(){
        viewModel.getAllHomeFoodAds();
        viewModel.homeFoodAdsLiveData.observe(this, ads -> {
            adsList = ads;
            if(user!=null) {
                adapter.setList(user,ads,this);
            }
        });
    }
    private void getFreelanceAds(){
        viewModel.getAllFreelanceAds(subCategory);
        viewModel.freelanceAdsLiveData.observe(this, ads -> {
            adsList = ads;
            if(user!=null) {
                adapter.setList(user,ads,this);
            }
        });
    }

}