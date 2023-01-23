package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.database.DbHandler;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener , AdsAdapter.OnItemListener ,MapsFragment.GettingPassedData {
    AdsViewModel adsViewModel;
    FavViewModel favViewModel ;
    AdsAdapter adapter;
    RecyclerView mainRecycler;

    EditText userAddress;
    UserViewModel userViewModel;

    String target = "toAdsActivity";

    DebugViewModel debugViewModel ;
    private User user;
    ArrayList<Ad> adsList;
    LinearLayout foodCategory, workerCategory, freelanceCategory, handcraftCategory;

    AdvertiserFragment advertiserFragment ;

    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    private static final String TAG = "HomeFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        initCategoryLayout(v);
        userAddress = v.findViewById(R.id.user_address);
        mainRecycler = v.findViewById(R.id.main_recycler);
        userAddress.setOnClickListener(this);
        debounceHandler = new Handler();
        advertiserFragment = new AdvertiserFragment();
        return v;
    }

    public void setAdsToList() {
        try {
            adapter = new AdsAdapter();
            mainRecycler.setAdapter(adapter);
            // TODO
            adsViewModel.getAllAds().observe(getActivity(), ads -> {
                adsList = ads;
                if (user != null) {
                    adapter.setList(user, ads, getContext(),this);
                }
            });
            mainRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
            reportError(e);
        }
    }


    public void newAdObserver(){
        adsViewModel.newAdLiveData.observe(getActivity(), new Observer<Ad>() {
            @Override
            public void onChanged(Ad ad) {
                user.getAds().add(ad);
                adapter.addItem(ad);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v) {
       try {
           if (v.getId() == userAddress.getId()) {
               callBottomSheet(new MapsFragment());
           } else if (v.getId() == foodCategory.getId()) {
               ((MainActivity) getActivity()).callAdsActivity(DbHandler.HOME_FOOD, DbHandler.HOME_FOOD,"");
           } else if (v.getId() == workerCategory.getId()) {
               ((MainActivity) getActivity()).callCategoryActivity(DbHandler.WORKER, target);
           } else if (v.getId() == freelanceCategory.getId()) {
               ((MainActivity) getActivity()).callCategoryActivity(DbHandler.FREELANCE, target);
           } else if (v.getId() == handcraftCategory.getId()) {
               ((MainActivity) getActivity()).callAdsActivity(DbHandler.HANDCRAFT, DbHandler.HANDCRAFT,"");
           }
       }catch (Exception e){
           reportError(e);
       }
    }

    public String getUserAddress(LocationCustom location) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            String address = "loading your location....";
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            for (String s: address.split(",")) {
                if(address.split(",").length<3){
                    address +=s;
                }
            }
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            reportError(e);
        }
        return "";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get user from Main activity
      try {
          user = getArguments().getParcelable("user");
          debugViewModel = ViewModelProviders.of(this).get(DebugViewModel.class);
          userViewModel = UserViewModel.getInstance();
          adsViewModel =  AdsViewModel.getInstance() ;
          favViewModel = FavViewModel.getInstance() ;

         setUserObserver();
          setAdsToList();
          newAdObserver();

      }catch (Exception e){
          reportError(e);
      }
    }

    private void setUserObserver(){
        userViewModel.getUser().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User u) {
                Log.e(TAG, "onChanged: user observer " );
                user = u;

                if (advertiserFragment.isAdded()) {
                    setAdsToList();
                }

                if(user.getLocation()!=null){
                    if (isAdded()) userAddress.setText(getUserAddress(user.getLocation()));
                }else {
                    reportError("location is null in home fragment");
                }            }
        });
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
            favImage.setImageResource(R.drawable.fav_uncheck_icon);

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

    private void initCategoryLayout(View v) {
        foodCategory = v.findViewById(R.id.home_food_category);
        workerCategory = v.findViewById(R.id.worker_category);
        freelanceCategory = v.findViewById(R.id.freelance_category);
        handcraftCategory = v.findViewById(R.id.handcraft_category);
        foodCategory.setOnClickListener(this);
        workerCategory.setOnClickListener(this);
        freelanceCategory.setOnClickListener(this);
        handcraftCategory.setOnClickListener(this);
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

    @Override
    public void newLocation(Location location) {
        LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
        user.setLocation(locationCustom);
        userAddress.setText(getUserAddress(user.getLocation()));
    }
    private void reportError(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        debugViewModel.reportError(new DebugModel(getCurrentDate(),e.getMessage(),sw.toString(),TAG, Build.VERSION.SDK_INT,false));
    }
    private void reportError(String s){
        debugViewModel.reportError(new DebugModel(getCurrentDate(),s,s,TAG, Build.VERSION.SDK_INT,false));
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
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

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: " );
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
    }
}