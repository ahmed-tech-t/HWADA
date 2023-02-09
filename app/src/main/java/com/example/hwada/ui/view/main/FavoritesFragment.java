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

        setUserObserver();
        setAdsToList();
        binding.mainRecycler.setNestedScrollingEnabled(false);

    }
    private void setUserObserver(){
        userViewModel.getUser().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User u) {
                Log.e(TAG, "onChanged: user observer " );
                user = u;
                if(advertiserFragment.isAdded()) setAdsToList();
            }
        });
    }
    public void setAdsToList() {
        if(user.getFavAds().size()>0){
            binding.mainRecycler.setBackgroundResource(R.drawable.recycle_view_background);
        }
        adapter = new AdsGridAdapter(getContext());
        try {
            binding.mainRecycler.setAdapter(adapter);
            adapter.setList(user,user.getFavAds(),this);
            binding.mainRecycler.setLayoutManager(new GridLayoutManager(getActivity(),2));
        }catch (Exception e){
            e.printStackTrace();
        }
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
        userViewModel.setUser(user);
        Log.e(TAG, "getFavItemPosition: "+user.getFavAds().size() );
        if(user.getFavAds().size() == 0) binding.mainRecycler.setBackgroundResource(R.drawable.empty_page);

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
        bundle.putParcelable("ad",user.getFavAds().get(pos));
        bundle.putParcelableArrayList("adsList",user.getFavAds());
        bundle.putInt("pos",pos);
        advertiserFragment.setArguments(bundle);
        advertiserFragment.show(getChildFragmentManager(),advertiserFragment.getTag());
    }
}