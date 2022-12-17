package com.example.hwada.ui.view;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hwada.Model.Ads;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.HomeAdapter;
import com.example.hwada.ui.MapActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener , HomeAdapter.OnItemListener {
    AdsViewModel viewModel;
    HomeAdapter adapter;
    EditText userAddress ;
    RecyclerView mainRecycler;
    UserViewModel userViewModel ;
    private User user;
    ArrayList<Ads> adsList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        userAddress = v.findViewById(R.id.user_address);
        mainRecycler = v.findViewById(R.id.main_recycler);
        userAddress.setOnClickListener(this);
        return v;
    }



    public void setAdsToList() {
        adapter = new HomeAdapter();
        try {
            mainRecycler.setAdapter(adapter);
            viewModel.getAllAds();
            viewModel.adsLiveData.observe(getActivity(), ads -> {
                adsList = ads;
                if(user!=null) {
                    adapter.setList(user,ads,this);
                }
            });
            mainRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == userAddress.getId()) {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
    public String getUserAddress(Location location){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            address = address.split(",")[0]+ address.split(",")[1] ;
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get user from Main activity
        user = getArguments().getParcelable("user");
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser().observe(getActivity(), new Observer<User>() {
                @Override
                public void onChanged(User u) {
                    user = u;
                    if(isVisible()) userAddress.setText(getUserAddress(user.getLocation()));
                }
            });
            viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AdsViewModel.class);
        setAdsToList();
    }

    @Override
    public void getItemPosition(int position) {


    }

    @Override
    public void getFavItemPosition(int position , ImageView favImage) {
        String adId = String.valueOf(position);
        if(adIsInFavList(adId)){
            user.removeOneAdFromFavorite(adId);
            userViewModel.setUser(user);
            favImage.setImageResource(R.drawable.fav_uncheck_icon);
        }else if(!adIsInFavList(adId))  {
            user.addOneAdToFavorite(adId);
            userViewModel.setUser(user);
            favImage.setImageResource(R.drawable.fav_checked_icon);
        }
    }
    private boolean adIsInFavList(String id){
        return user.getFavoriteAds().contains(id);
    }
}