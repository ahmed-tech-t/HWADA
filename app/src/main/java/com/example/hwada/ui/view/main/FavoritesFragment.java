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
import com.example.hwada.adapter.FavoritesAdapter;
import com.example.hwada.databinding.FragmentFavoritesBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnItemListener {

    User user;
    FavViewModel favViewModel ;
    UserViewModel userViewModel ;
    FavoritesAdapter adapter;
    ArrayList<Ad> adsList;
    String category ;

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
        category = getArguments().getString("category");
        favViewModel = FavViewModel.getInstance() ;
        userViewModel = UserViewModel.getInstance();

        setUserObserver();
        setAdsToList();
        if(adsList.size()==0) binding.mainRecycler.setBackgroundColor(Color.WHITE);
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
        adapter = new FavoritesAdapter();
        try {
            Log.e(TAG, "setAdsToList: " );
            binding.mainRecycler.setAdapter(adapter);
            adsList = getAdsList();
            adapter.setList(adsList,getContext(),this);
            binding.mainRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        String adId = adsList.get(position).getId();
        int favPos = adIsInFavList(adId);
        if (favPos != -1) {
            user.getFavAds().remove(favPos);
            userViewModel.setUser(user);
            favViewModel.deleteFavAd(user.getUId(),adsList.get(position));
            adapter.removeOneItem(position);
            favImage.setImageResource(R.drawable.fav_uncheck_icon);
            if(adsList.size()==0) binding.mainRecycler.setBackgroundColor(Color.WHITE);
        } else {
            if (user.getFavAds() == null) user.initFavAdsList();
            favViewModel.addFavAd(user.getUId(),adsList.get(position));
            user.getFavAds().add(adsList.get(position));
            userViewModel.setUser(user);
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

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        category = getArguments().getString("category");
        user = getArguments().getParcelable("user");

    }

    @Override
    public void onResume() {
        super.onResume();
        category = getArguments().getString("category");
        user = getArguments().getParcelable("user");
    }

    private ArrayList<Ad> getAdsList(){
        ArrayList<Ad> temp = new ArrayList<>();
        for (Ad ad : user.getFavAds()) {
            if(ad.getCategory().equals(category)){
                temp.add(ad);
            }
        }
        return temp;
    }

    private void callAdvertiserFragment(int pos){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad",adsList.get(pos));
        bundle.putParcelableArrayList("adsList",adsList);
        bundle.putInt("pos",pos);
        advertiserFragment.setArguments(bundle);
        advertiserFragment.show(getChildFragmentManager(),advertiserFragment.getTag());
    }
}