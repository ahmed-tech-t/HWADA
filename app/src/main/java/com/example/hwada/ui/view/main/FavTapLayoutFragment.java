package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.TabLayoutFavAdapter;
import com.example.hwada.database.DbHandler;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FavTapLayoutFragment extends Fragment {


    ViewPager2 viewPager;
    User user ;
    TabLayoutFavAdapter adapter;
    String TAG ="FavTapLayoutFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fav_tap_layout, container, false);
        return  v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String [] category={DbHandler.HOME_FOOD, DbHandler.WORKER, DbHandler.FREELANCE, DbHandler.HANDCRAFT};
        adapter = new TabLayoutFavAdapter(this,category,user);
        viewPager=view.findViewById(R.id.view_pager_fav);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_fav);

        String [] tabTitles={
                getString(R.string.homeFood),
                getString(R.string.worker),
                getString(R.string.freelance),
                getString(R.string.handcraft)};

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabTitles[position])
        ).attach();
    }
}