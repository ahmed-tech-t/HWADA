package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.FilterModel;
import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentHomeBinding;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.view.FilterFragment;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.util.FilterFunctions;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.FilterViewModel;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener , AdsAdapter.OnItemListener , SwipeRefreshLayout.OnRefreshListener {
    AdsViewModel adsViewModel;
    FavViewModel favViewModel ;
    UserAddressViewModel userAddressViewModel ;
    FilterViewModel filterViewModel;
    AdsAdapter adapter;

    UserViewModel userViewModel;

    String target = "toAdsActivity";

    FilterModel filterModel ;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
            else binding.recyclerHomeFragment.setBackgroundResource(R.drawable.empty_page);

            adapter.setList(user, adsList,this);
            binding.recyclerHomeFragment.setAdapter(adapter);
            binding.recyclerHomeFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllAds(){
        adsViewModel.getAllAds(user).observe(getActivity(), ads -> {
            adsList = ads;
            if(filterModel!=null){
                setFilter();
            }else setRecycler();
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
        favViewModel.userFavAdsListener(user.getUId()).observe(getActivity(), ads -> {
            user.setFavAds(ads);
            if(advertiserFragment.isAdded()) setRecycler();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
               if(filterModel==null) filterModel = new FilterModel(getString(R.string.updateDateVal),false);
               callFilterDialog(filterModel);
           }
       }catch (Exception e){
           app.reportError(e,getContext());
       }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        user = getArguments().getParcelable(getString(R.string.userVal));
        userAddressViewModel = new ViewModelProvider(this).get(UserAddressViewModel.class);
        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adsViewModel =  new ViewModelProvider(this).get(AdsViewModel.class);

        favViewModel = FavViewModel.getInstance() ;

        app =(App) getContext().getApplicationContext();
        binding.swipeRefreshHomeFragment.setOnRefreshListener(this);

        getAllAds();
        setUserListener();
        userFavAdsListener();
        setFilterObserver();
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
        bundle.putParcelable(getString(R.string.userVal), user);
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
            bundle.putParcelable(getString(R.string.userVal), user);
            bundle.putParcelable(getString(R.string.adVal),adsList.get(pos));
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

    public void callFilterDialog(FilterModel filterModel) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.filterVal),filterModel);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

    @Override
    public void onRefresh() {
        getAllAds();
    }


    public void setFilterObserver(){
        filterViewModel.getFilter().observe(getActivity(), filter -> {
            filterModel = filter;
            getAllAds();
        });
    }

    private void setFilter(){
        if(filterModel!=null){
            FilterFunctions filterFunctions = new FilterFunctions(adsList,getContext());
            if(filterModel.isOpen()){
                String[] days = getResources().getStringArray(R.array.daysVal);
                adsList = new ArrayList<>(filterFunctions.removeClosedAds(app.getTime(),days,app.getDayIndex()));
            }
            if(filterModel.getSort().equals(getString(R.string.ratingVal))){
                adsList = new ArrayList<>(filterFunctions.sortAdsByRating());
            }else if(filterModel.getSort().equals(getString(R.string.theClosestVal))){
                adsList = new ArrayList<>(filterFunctions.sortAdsByTheClosest());
            }else if(filterModel.getSort().equals(getString(R.string.updateDateVal))){
                adsList = new ArrayList<>(filterFunctions.sortAdsByDate());
            }else if(filterModel.getSort().equals(getString(R.string.theCheapestVal))){
                adsList = new ArrayList<>(filterFunctions.sortAdsByTheCheapest());
            }else if(filterModel.getSort().equals(getString(R.string.theExpensiveVal))){
                adsList = new ArrayList<>(filterFunctions.sortAdsByTheExpensive());
            }
            setRecycler();
        }
   }

}