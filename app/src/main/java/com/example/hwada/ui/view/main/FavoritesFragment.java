package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsGridAdapter;
import com.example.hwada.databinding.FragmentFavoritesBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements AdsGridAdapter.OnItemListener {

    User user;
    FavViewModel favViewModel ;
    UserViewModel userViewModel ;
    AdsGridAdapter adapter;

    ArrayList<Ad> adsList;


    FragmentFavoritesBinding binding ;
    private static final String TAG = "FavoritesFragment";

    AdvertiserFragment advertiserFragment ;

    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        debounceHandler = new Handler();
        advertiserFragment = new AdvertiserFragment();
        return  binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = getArguments().getParcelable("user");

        favViewModel = FavViewModel.getInstance() ;
        userViewModel = UserViewModel.getInstance();

        binding.shimmerFavFragment.startShimmer();

        getAllFavAds();
        setUserFavListener();

    }

    private void setUserFavListener() {
        favViewModel.userFavAdsListener(user.getUId()).observe(getActivity(), new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                user.setFavAds(ads);
                if(advertiserFragment.isAdded()){
                    adsList = ads;
                    getAllFavAds();
                }
            }
        });
    }


    public void setRecycler() {
        closeShimmer();

        binding.mainRecycler.setVisibility(View.VISIBLE);
        if(user.getFavAds().size()>0){
            binding.mainRecycler.setBackgroundResource(R.drawable.recycle_view_background);
        }
        adapter = new AdsGridAdapter(getActivity());
        try {
            binding.mainRecycler.setAdapter(adapter);
            adapter.setList(user,adsList,this);
            binding.mainRecycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
            binding.mainRecycler.setNestedScrollingEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeShimmer(){
        binding.shimmerFavFragment.setVisibility(View.GONE);
        binding.shimmerFavFragment.stopShimmer();
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
        favViewModel.deleteFavAd(user.getUId(),user.getFavAds().get(position));
        adapter.removeOneItem(position);
        Log.e(TAG, "getFavItemPosition: "+adsList.size());
        if(adapter.getItemCount() == 0) binding.mainRecycler.setBackgroundResource(R.drawable.empty_page);
    }
    

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        user = getArguments().getParcelable("user");

    }

    @Override
    public void onResume() {
        super.onResume();
        user = getArguments().getParcelable("user");
    }

    private void callAdvertiserFragment(int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad",adsList.get(pos));
        advertiserFragment.setArguments(bundle);
        advertiserFragment.show(getChildFragmentManager(),advertiserFragment.getTag());
    }
    private void getAllFavAds(){
        favViewModel.getAllFavAds(user.getFavAds()).observe(getActivity(), new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                user.setFavAds(ads);
                adsList = ads ;
                setRecycler();
            }
        });

    }

}