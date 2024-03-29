package com.example.hwada.ui.view.ad;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.DebugModel;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsGridAdapter;
import com.example.hwada.adapter.ImageSliderAdapter;

import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentAdvertiserBinding;
import com.example.hwada.ui.ChatActivity;
import com.example.hwada.ui.view.ad.menu.AdDescriptionFragment;
import com.example.hwada.ui.view.ad.menu.AdReviewsFragment;
import com.example.hwada.ui.view.ad.menu.AdWorkingTimeFragment;
import com.example.hwada.ui.view.chat.SendImagesMessageFragment;
import com.example.hwada.ui.view.images.ImagesFullDialogFragment;
import com.example.hwada.ui.view.map.MapPreviewFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.ChatViewModel;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.FavViewModel;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;

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

public class AdvertiserFragment extends BottomSheetDialogFragment implements View.OnClickListener , AdsGridAdapter.OnItemListener ,ImageSliderAdapter.OnItemListener{

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    FavViewModel favViewModel ;
    UserAddressViewModel userAddressViewModel ;
    ArrayList<Ad> adsList;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    AdvertiserFragment advertiserFragment;

    ChatViewModel chatViewModel ;
    UserViewModel userViewModel;
    AdsViewModel adsViewModel;
    AdsGridAdapter adsGridAdapter;
    User user;
    Ad ad;
    App app;
    boolean adIsOpen;
    private static final String TAG = "AdvertiserFragment";

    FragmentAdvertiserBinding binding;


    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdvertiserBinding.inflate(inflater, container, false);
        binding.arrowAdvertiser.setOnClickListener(this);
        binding.tvAdLocationAdvertiserFragment.setOnClickListener(this);
        binding.buttonChatAdvertiserFragment.setOnClickListener(this);
        advertiserFragment = new AdvertiserFragment();
        setLocationArrowWhenLanguageIsArabic();
        debounceHandler = new Handler();
        app = (App) getContext().getApplicationContext();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        onBackPressed(dialog);
        return dialog;

    }

    private void scrollViewListener(){
        binding.nestedScrollViewAdvertiserFragment.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(!adIsOpen || !ad.isActive()){
                    if(scrollY>binding.fragmentsContainerMenuAdvertiserFragment.getBottom()-200){
                        binding.tvClosedAdvertiserFragment.setVisibility(View.GONE);
                    }else binding.tvClosedAdvertiserFragment.setVisibility(View.VISIBLE);

                }
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBottomSheet(view);

        assert getArguments() != null;
        user = getArguments().getParcelable(getString(R.string.userVal));
        ad = getArguments().getParcelable(getString(R.string.adVal));

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        adsViewModel =   new ViewModelProvider(this).get(AdsViewModel.class);
        favViewModel = FavViewModel.getInstance();
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        userAddressViewModel = new ViewModelProvider(this).get(UserAddressViewModel.class);

        binding.buttonCallAdvertiserFragment.setOnClickListener(this);

        binding.shimmerAdvertiser.startShimmer();
        setAdDataToView();

        if(user.getUId().equals(ad.getAuthorId())){
            binding.llContactAdvertiserFragment.setVisibility(View.GONE);
        }

        setViewIfAdIsOpen();
        updateViews();
        setToSlider();
        setMenuTapLayoutListener();
        getSimilarAds();
        scrollViewListener();
    }
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void setViewIfAdIsOpen(){
        String[] days = getResources().getStringArray(R.array.daysVal);
        adIsOpen = ad.isOpen(app.getTime(),days,app.getDayIndex());
        if(!adIsOpen || !ad.isActive()){
            binding.linearLayoutAdSectionAdvertiserFragment.setAlpha(.5f);
            binding.tvClosedAdvertiserFragment.setVisibility(View.VISIBLE);
            if(!ad.isActive()) binding.tvClosedAdvertiserFragment.setText(getString(R.string.adNotActiveAnyMoreMaessage));
            binding.buttonCallAdvertiserFragment.setEnabled(false);
            binding.buttonChatAdvertiserFragment.setEnabled(false);
            binding.buttonChatAdvertiserFragment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.white_gray));
            binding.buttonCallAdvertiserFragment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.white_gray));
        }else {
            binding.linearLayoutAdSectionAdvertiserFragment.setAlpha(1f);
            binding.tvClosedAdvertiserFragment.setVisibility(View.GONE);
            binding.buttonCallAdvertiserFragment.setEnabled(true);
            binding.buttonChatAdvertiserFragment.setEnabled(true);
            binding.buttonChatAdvertiserFragment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.background));
            binding.buttonCallAdvertiserFragment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.background));
        }

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


    private void closeShimmer(){
        binding.shimmerAdvertiser.setVisibility(View.GONE);
        binding.shimmerAdvertiser.stopShimmer();
    }
    @SuppressLint("SetTextI18n")
    private void setAdDataToView(){
        binding.tvDateAdvertiserFragment.setText(handleTime(ad.getTimeStamp()));
        binding.tvAdDistanceAdvertiserFragment.setText(ad.getDistance()+"");
        binding.tvAdLocationAdvertiserFragment.setText(ad.getAuthorAddress());
        binding.tvAdTitleAdvertiserFragment.setText(ad.getTitle());
    }
    private void updateViews(){
        adsViewModel.updateViews(ad);
    }

    private void setUserFavAdListener(){
        favViewModel.userFavAdsListener(user.getUId()).observe(getActivity(), ads -> {
            user.setFavAds(ads);
            if(advertiserFragment.isAdded()){
                setRecycler();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.arrowAdvertiser.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.tvAdLocationAdvertiserFragment.getId()){
            if(app.isGooglePlayServicesAvailable(getActivity())){
                callBottomSheet(new MapPreviewFragment());
            }else showToast(getString(R.string.googleServicesWarning));
        }else if(v.getId() == binding.buttonCallAdvertiserFragment.getId()){
            callCallActivity();
        }else if (v.getId() == binding.buttonChatAdvertiserFragment.getId()){
            Chat chat = new Chat(ad,app.getCurrentDate(),ad.getAuthorId());
            chatViewModel.addNewChat(user.getUId(),chat).observe(this, new Observer<Chat>() {
                @Override
                public void onChanged(Chat chat) {
                    callChatActivity(chat);
                }
            });
        }
    }

    public void callChatActivity(Chat chat){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("chat",chat);
        startActivity(intent);
    }

    private void getSimilarAds(){
        if(ad.getCategory().equals(DbHandler.FREELANCE)){
            getSimilarAds(ad.getCategory(),ad.getSubCategory(),ad.getSubSubCategory());
        }else getSimilarAds(ad.getCategory(),ad.getSubCategory());
    }
    private void getSimilarAds(String category ,String subCategory){
        adsViewModel.getAllAds(user,category,subCategory).observe(this, ads -> {
            adsList = ads;
            adsList.removeIf(o -> o.getId().equals(ad.getId()) );
            if(getActivity()!=null){
                setRecycler();
                setUserFavAdListener();
            }
        });
    }

    private void getSimilarAds(String category ,String subCategory, String subSubCategory){
        adsViewModel.getAllAds(user,category,subCategory,subSubCategory).observe(this, ads -> {
            adsList = ads;
            adsList.removeIf(o -> o.getId().equals(ad.getId()) );
            if(getActivity()!=null){
                setRecycler();
                setUserFavAdListener();
            }
        });
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
        ImageSliderAdapter adapter = new ImageSliderAdapter();
        adapter.setList((ArrayList<String>) ad.getImagesUrl(),getContext(),this);
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
                    callFragment(new AdDescriptionFragment());
                }else if(position==1){
                    callFragment(new AdWorkingTimeFragment());
                }else if (position == 2){
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
        bundle.putParcelable(getString(R.string.userVal), user);
        bundle.putParcelable(getString(R.string.adVal), ad);
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


    public String handleTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        String dateString = app.getDateFromTimeStamp(timestamp);
        try {

            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return getString(R.string.today)+" " + dateString.split(",")[1] + dateString.split(",")[3] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return getString(R.string.yesterday)+" "+ dateString.split(",")[1]+ dateString.split(",")[3];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0]+dateString.split(",")[1]+dateString.split(",")[3];
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
    private void setRecycler(){
        closeShimmer();
        adsGridAdapter = new AdsGridAdapter(getContext());

        adsGridAdapter.setList(user,adsList,this);
        binding.recyclerGridFragmentAdvertiser.setVisibility(View.VISIBLE);
        binding.recyclerGridFragmentAdvertiser.setAdapter(adsGridAdapter);
        binding.recyclerGridFragmentAdvertiser.setLayoutManager(new GridLayoutManager(getContext(),2));
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
   private void callCallActivity(){
        Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+user.getPhone()));
        startActivity(dial);
    }
    private void setLocationArrowWhenLanguageIsArabic(){
        Locale locale = Resources.getSystem().getConfiguration().locale;
        if (locale.getLanguage().equals("ar")) {
            binding.tvAdLocationAdvertiserFragment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, 0, 0);
        }
    }


    private void callImagesFullDialogFragment(ArrayList<String> url,int pos){
        ImagesFullDialogFragment fragment = new ImagesFullDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imagesUrl", url);
        bundle.putInt("pos",pos);
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),fragment.getTag());
    }

    @Override
    public void getImagePosition(int position) {
        callImagesFullDialogFragment((ArrayList<String>) ad.getImagesUrl(),position);
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
}