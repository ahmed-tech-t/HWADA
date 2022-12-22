package com.example.hwada.ui.view.main;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.ui.CategoryActivity;
import com.example.hwada.ui.AdsActivity;
import com.example.hwada.ui.MapActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener , AdsAdapter.OnItemListener {
    AdsViewModel viewModel;

    AdsAdapter adapter;
    RecyclerView mainRecycler;

    EditText userAddress ;
    UserViewModel userViewModel ;

    private User user;
    ArrayList<Ad> adsList;
    LinearLayout foodCategory , workerCategory, freelanceCategory , handcraftCategory ;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        initCategoryLayout(v);
        userAddress = v.findViewById(R.id.user_address);
        mainRecycler = v.findViewById(R.id.main_recycler);
        userAddress.setOnClickListener(this);
        return v;
    }



    public void setAdsToList() {
        adapter = new AdsAdapter();
        try {
            mainRecycler.setAdapter(adapter);
            viewModel.getAllWorkersAds();
            viewModel.workerAdsLiveData.observe(getActivity(), ads -> {
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
        }else if (v.getId() == foodCategory.getId()){
            callAdsActivity("homeFood","");
        }else if (v.getId()==workerCategory.getId()){
            callCategoryActivity("worker");
        }else if (v.getId()==freelanceCategory.getId()){
            callCategoryActivity("freelance");
        }else if (v.getId()==handcraftCategory.getId()){
            callAdsActivity("handcraft","");
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
                    if(isAdded()) userAddress.setText(getUserAddress(user.getLocation()));
                }
            });
        viewModel = ViewModelProviders.of(getActivity()).get(AdsViewModel.class);
        setAdsToList();
    }

    @Override
    public void getItemPosition(int position) {


    }

    @Override
    public void getFavItemPosition(int position , ImageView favImage) {
        String adId = String.valueOf(position);
        if(adIsInFavList(adId)){
            user.removeAdFromAdsFavList(position);
            userViewModel.setUser(user);
            favImage.setImageResource(R.drawable.fav_uncheck_icon);
        }else if(!adIsInFavList(adId))  {
            if(user.getFavAds() == null) user.initFavAdsList();
            user.setAdToFavAdsList(adsList.get(position));
            userViewModel.setUser(user);
            favImage.setImageResource(R.drawable.fav_checked_icon);
        }
    }
    private boolean adIsInFavList(String id){
        return false;
    }

    private void initCategoryLayout(View v){
        foodCategory = v.findViewById(R.id.home_food_category);
        workerCategory = v.findViewById(R.id.worker_category);
        freelanceCategory = v.findViewById(R.id.freelance_category);
        handcraftCategory = v.findViewById(R.id.handcraft_category);
        foodCategory.setOnClickListener(this);
        workerCategory.setOnClickListener(this);
        freelanceCategory.setOnClickListener(this);
        handcraftCategory.setOnClickListener(this);
    }


    private void callCategoryActivity(String tag){
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("tag",tag);
        startActivity(intent);
       // getActivity().overridePendingTransition(R.anim.to_down,R.anim.to_up);
    }
    public void callAdsActivity(String category,String subCategory){
        Intent intent = new Intent(getActivity(), AdsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category",category);
        intent.putExtra("subCategory",subCategory);
        startActivity(intent);
    }

}