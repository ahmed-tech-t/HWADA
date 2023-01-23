package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.TabLayoutFavAdapter;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentFavTapLayoutBinding;
import com.example.hwada.ui.view.ad.menu.AdDescriptionFragment;
import com.example.hwada.ui.view.ad.menu.AdReviewsFragment;
import com.example.hwada.ui.view.ad.menu.AdWorkingTimeFragment;
import com.example.hwada.util.MyGestureListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FavTapLayoutFragment extends Fragment implements View.OnTouchListener {


    ViewPager2 viewPager;
    User user ;
    TabLayoutFavAdapter adapter;
    GestureDetector gestureDetector;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    FragmentFavTapLayoutBinding binding ;



    private static final String TAG = "FavTapLayoutFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavTapLayoutBinding.inflate(inflater, container, false);
        binding.getRoot().setOnTouchListener(this);

        return  binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        setMenuTapLayoutListener();
    }

    private void setMenuTapLayoutListener(){

        gestureDetector = new GestureDetector(getContext(), new MyGestureListener(binding.tabLayoutFav));

        String [] tabTitles={DbHandler.HOME_FOOD, DbHandler.WORKER, DbHandler.FREELANCE,DbHandler.HANDCRAFT};

        for (int i = 0; i < tabTitles.length; i++) {
            binding.tabLayoutFav.addTab(binding.tabLayoutFav.newTab().setText(tabTitles[i]));
        }
        callFavFragment(DbHandler.HOME_FOOD);
        binding.tabLayoutFav.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    callFavFragment(DbHandler.HOME_FOOD);
                }else if(position==1){
                    callFavFragment(DbHandler.WORKER);
                }else if (position == 2){
                    callFavFragment(DbHandler.FREELANCE);
                }else if (position == 3){
                    callFavFragment(DbHandler.HANDCRAFT);
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


    public void callFavFragment(String category){
        try {
            FavoritesFragment fragment = new FavoritesFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            bundle.putString("category",category);
            fragment.setArguments(bundle);
            fragmentManager = getChildFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.holder_fav_tab_layout_fragment, fragment);
            fragmentTransaction.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}