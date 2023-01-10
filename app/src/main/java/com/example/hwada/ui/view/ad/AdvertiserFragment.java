package com.example.hwada.ui.view.ad;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsGridAdapter;
import com.example.hwada.adapter.ImageSliderAdapter;

import com.example.hwada.databinding.FragmentAdvertiserBinding;
import com.example.hwada.ui.view.ad.menu.AdDescriptionFragment;
import com.example.hwada.ui.view.ad.menu.AdReviewsFragment;
import com.example.hwada.ui.view.ad.menu.AdWorkingTimeFragment;
import com.example.hwada.ui.view.map.MapPreviewFragment;
import com.example.hwada.viewmodel.DebugViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdvertiserFragment extends BottomSheetDialogFragment implements View.OnClickListener , AdsGridAdapter.OnItemListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    ArrayList<Ad> adsList;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    int PASSED_POSITION =0;
    DebugViewModel debugViewModel ;

    AdsGridAdapter adsGridAdapter;
    User user;
    Ad ad;
    private static final String TAG = "AdvertiserFragment";

    FragmentAdvertiserBinding binding;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdvertiserBinding.inflate(inflater, container, false);
        binding.arrowAdvertiser.setOnClickListener(this);
        binding.tvAdLocationAdvertiserFragment.setOnClickListener(this);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        onBackPressed(dialog);
        return dialog;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBottomSheet(view);
    }

    private void setBottomSheet(View view){
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CoordinatorLayout layout = dialog.findViewById(R.id.bottom_sheet_advertiser);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        ad = getArguments().getParcelable("ad");
        adsList = getArguments().getParcelableArrayList("adsList");
        PASSED_POSITION = getArguments().getInt("pos");
        binding.tvDateAdvertiserFragment.setText(handleTime(ad.getDate()));
        binding.tvAdDistanceAdvertiserFragment.setText(ad.getDistance()+"");
        debugViewModel = ViewModelProviders.of(getActivity()).get(DebugViewModel.class);
        try {
            binding.tvAdLocationAdvertiserFragment.setText(getUserAddress(ad.getAuthorLocation()));
        }catch (Exception e){
            reportError(e);
        }
        setToSlider();
        setMenuTapLayoutListener();
        setAdGridAdapter();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.arrowAdvertiser.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.tvAdLocationAdvertiserFragment.getId()){
            callBottomSheet(new MapPreviewFragment());
        }
    }



    private void onBackPressed(Dialog dialog){
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                // getAction to make sure this doesn't double fire
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return true; // Capture onKey
                }
                return false; // Don't capture
            }
        });
    }




    private void setToSlider(){
        ImageSliderAdapter adapter = new ImageSliderAdapter(getActivity(), ad.getImagesUrl());
        binding.vp2ImageSliderAdvertiserFragment.setAdapter(adapter);
        binding.circleIndicator.setViewPager(binding.vp2ImageSliderAdvertiserFragment);
        adapter.registerAdapterDataObserver(binding.circleIndicator.getAdapterDataObserver());
    }

    private void setMenuTapLayoutListener(){
        String [] tabTitles={ getString(R.string.description), getString(R.string.workingTimeTitle), getString(R.string.reviews)};

        for (int i = 0; i < tabTitles.length; i++) {
            binding.tabLayoutAdvertiser.addTab(binding.tabLayoutAdvertiser.newTab().setText(tabTitles[i]));
        }
        callFragment(new AdDescriptionFragment());
        binding.tabLayoutAdvertiser.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    Log.e(TAG, "onTabSelected: 0" );
                    callFragment(new AdDescriptionFragment());
                }else if(position==1){
                    Log.e(TAG, "onTabSelected: 1" );
                    callFragment(new AdWorkingTimeFragment());
                }else if (position == 2){
                    Log.e(TAG, "onTabSelected: 2" );
                    callFragment(new AdReviewsFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

    }

    public void callFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putParcelable("ad", ad);
        bundle.putParcelable("user",user);
        fragment.setArguments(bundle);
        fragmentManager = getChildFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container_menu_advertiser_fragment, fragment);
        fragmentTransaction.commit();
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("ad", ad);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }
    private String getUserAddress(LocationCustom location) {
        try {
            LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            String address = "loading your location...";
            List<Address> addresses = geocoder.getFromLocation(locationCustom.getLatitude(), locationCustom.getLongitude(), 1);
            if(addresses.size()>0) {
                address = addresses.get(0).getAddressLine(0);
                for (String s: address.split(",")) {
                    if(address.split(",").length<2){
                        address +=s;
                    }
                }

            }
            return address ;
        } catch (IOException e) {
            e.printStackTrace();
            reportError(e);
        }
        return "";
    }

    public String handleTime(String dateString){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy , h:mm a");
            Date date = dateFormat.parse(dateString);

            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return getString(R.string.today)+" " + dateString.split(",")[1] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return getString(R.string.yesterday)+" "+ dateString.split(",")[1];
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    private void reportError(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        debugViewModel.reportError(new DebugModel(getCurrentDate(),e.getMessage(),sw.toString(),TAG, Build.VERSION.SDK_INT,false));
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }

    @Override
    public void getItemPosition(int position) {
        callAdvertiserFragment(position);
    }

    @Override
    public void getFavItemPosition(int position, ImageView imageView) {

    }

    private void setAdGridAdapter(){
        adsGridAdapter = new AdsGridAdapter();
        adsGridAdapter.setList(user,adsList,getContext(),this);
        binding.recyclerGridFragmentAdvertiser.setAdapter(adsGridAdapter);
        binding.recyclerGridFragmentAdvertiser.setLayoutManager(new GridLayoutManager(getContext(),2));
    }
    private void callAdvertiserFragment(int pos){
        AdvertiserFragment fragment = new AdvertiserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad",adsList.get(pos));
        bundle.putParcelableArrayList("adsList",adsList);
        bundle.putInt("pos",pos);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),fragment.getTag());
    }
}