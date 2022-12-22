package com.example.hwada.ui.view.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.FavoritesAdapter;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnItemListener {

    UserViewModel userViewModel ;
    User user;
    FavoritesAdapter adapter;
    AdsViewModel viewModel;
    RecyclerView mainRecycler;
    ArrayList<Ad> adsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_favorites, container, false);
        mainRecycler = v.findViewById(R.id.main_recycler);
        return  v;
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
            }
        });

        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AdsViewModel.class);
        setAdsToList();
    }

    public void setAdsToList() {
        adapter = new FavoritesAdapter();
        try {
            mainRecycler.setAdapter(adapter);
            viewModel.getFavAds(user);
            viewModel.favAdsLiveData.observe(getActivity(), ads -> {
                adsList = ads;
                adapter.setList(ads,this);
            });
            mainRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void getItemPosition(int position) {

    }

    @Override
    public void getFavItemPosition(int position, ImageView imageView) {
        imageView.setImageResource(R.drawable.fav_uncheck_icon);
        user.removeAdFromAdsFavList(position);
        userViewModel.setUser(user);
        adapter.removeOneItem(position);
    }
}