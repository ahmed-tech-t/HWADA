package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentHomeBinding;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.view.FilterFragment;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener , AdsAdapter.OnItemListener , SwipeRefreshLayout.OnRefreshListener {
    AdsViewModel adsViewModel;
    FavViewModel favViewModel ;
    UserAddressViewModel userAddressViewModel ;
    AdsAdapter adapter;

    UserViewModel userViewModel;

    String target = "toAdsActivity";

    private User user;
    ArrayList<Ad> adsList;

    AdvertiserFragment advertiserFragment ;

    App app ;
    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    FragmentHomeBinding binding ;

    private static final String TAG = "HomeFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentHomeBinding.inflate(inflater, container, false);

        initCategoryLayout();
        binding.shimmerHomeFragment.startShimmer();

        binding.userAddress.setOnClickListener(this);
        binding.imFilterHomeFragment.setOnClickListener(this);
        setLocationArrowWhenLanguageIsArabic();

        debounceHandler = new Handler();
        advertiserFragment = new AdvertiserFragment();


        return binding.getRoot();
    }

    public void setRecycler() {
        closeShimmer();
        try {
            adapter = new AdsAdapter(getContext());
            binding.swipeRefreshHomeFragment.setVisibility(View.VISIBLE);
            if(adsList.size()>0)binding.recyclerHomeFragment.setBackgroundResource(R.drawable.recycle_view_background);

            adapter.setList(user, adsList,this);
            binding.recyclerHomeFragment.setAdapter(adapter);
            binding.recyclerHomeFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
          //  app.reportError(e,getContext());
        }
    }

    private void getAllAds(){
        adsViewModel.getAllAds().observe(getActivity(), ads -> {
            adsList = ads;
            setRecycler();
            if(binding.swipeRefreshHomeFragment.isRefreshing()){
                binding.swipeRefreshHomeFragment.setRefreshing(false);
            }
        });
    }

    private void closeShimmer(){
        binding.shimmerHomeFragment.setVisibility(View.GONE);
        binding.shimmerHomeFragment.stopShimmer();
    }

    private void userFavAdsListener(){
        favViewModel.userFavAdsListener(user.getUId()).observe(getActivity(), new Observer<ArrayList<Ad>>() {
            @Override
            public void onChanged(ArrayList<Ad> ads) {
                user.setFavAds(ads);
                if(advertiserFragment.isAdded()) setRecycler();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!= null){
            adapter.setList(user,adsList,this);
            adapter.notifyDataSetChanged();
        }
    }

    private Toast mCurrentToast;
    public void showToast(String message) {
        if (mCurrentToast == null) {
            mCurrentToast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
            mCurrentToast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mCurrentToast.addCallback(new Toast.Callback() {
                    @Override
                    public void onToastShown() {
                        super.onToastShown();
                    }

                    @Override
                    public void onToastHidden() {
                        super.onToastHidden();
                        mCurrentToast = null;
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
       try {
           if (v.getId() == binding.userAddress.getId()) {
               if(app.isGooglePlayServicesAvailable(getActivity())){
                   callBottomSheet(new MapsFragment());
               }else showToast(getString(R.string.googleServicesWarning));
           } else if (v.getId() == binding.homeFoodCategory.getId()) {
               ((MainActivity) getActivity()).callAdsActivity(DbHandler.HOME_FOOD, DbHandler.HOME_FOOD,"");
           } else if (v.getId() == binding.workerCategory.getId()) {
               ((MainActivity) getActivity()).callCategoryActivity(DbHandler.WORKER, target);
           } else if (v.getId() == binding.freelanceCategory.getId()) {
               ((MainActivity) getActivity()).callCategoryActivity(DbHandler.FREELANCE, target);
           } else if (v.getId() == binding.handcraftCategory.getId()) {
               ((MainActivity) getActivity()).callAdsActivity(DbHandler.HANDCRAFT, DbHandler.HANDCRAFT,"");
           }else if(v.getId() == binding.imFilterHomeFragment.getId()){
               callFilterDialog();
           }
       }catch (Exception e){
           app.reportError(e,getContext());
       }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get user from Main activity
      try {
          user = getArguments().getParcelable("user");

          userAddressViewModel = ViewModelProviders.of(this).get(UserAddressViewModel.class);

          userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
          adsViewModel =  new ViewModelProvider(this).get(AdsViewModel.class);
          favViewModel = FavViewModel.getInstance() ;

          app =(App) getContext().getApplicationContext();
          binding.swipeRefreshHomeFragment.setOnRefreshListener(this);

          getAllAds();
          setUserListener();
          userFavAdsListener();
      }catch (Exception e){
          app.reportError(e,getContext());
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
            favViewModel.deleteFavAd(user.getUId(),adsList.get(position));
            favImage.setImageResource(R.drawable.fav_uncheck_icon);

        } else {
            if (user.getFavAds() == null) user.initFavAdsList();
            favViewModel.addFavAd(user.getUId(),adsList.get(position));
            user.getFavAds().add(adsList.get(position));
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

    private void initCategoryLayout() {
        binding.homeFoodCategory.setOnClickListener(this);
        binding.workerCategory.setOnClickListener(this);
        binding.freelanceCategory.setOnClickListener(this);
        binding.handcraftCategory.setOnClickListener(this);
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }


    private void setUserListener(){
        userViewModel.userListener(user.getUId()).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User updatedUser) {
                user.updateUser(updatedUser);
                if(user.getLocation()!=null) binding.userAddress.setText(user.getAddress());
            }
        });
    }

    private void callAdvertiserFragment(int pos){
        if(!advertiserFragment.isAdded()){
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            bundle.putParcelable("ad",adsList.get(pos));
            advertiserFragment.setArguments(bundle);
            advertiserFragment.show(getChildFragmentManager(),advertiserFragment.getTag());
        }
    }

  private void setLocationArrowWhenLanguageIsArabic(){
      Locale locale = Resources.getSystem().getConfiguration().locale;
      if (locale.getLanguage().equals("ar")) {
          binding.userAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, R.drawable.distance_icon, 0);
      }
  }

    public void callFilterDialog() {
        FilterFragment fragment = new FilterFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

    @Override
    public void onRefresh() {
        getAllAds();
    }
}