package com.example.hwada.ui.view;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.MainAdapter;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.MapActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener {
    AdsViewModel viewModel;
    MainAdapter adapter;
    EditText userAddress ;
    RecyclerView mainRecycler;
    UserViewModel userViewModel ;
    private User user;

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
        adapter = new MainAdapter();
        try {
            mainRecycler.setAdapter(adapter);
            viewModel.getAllAds();
            viewModel.adsLiveData.observe(getActivity(), ads -> {
                adapter.setList(ads);
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
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser().observe(this, new Observer<User>() {
                @Override
                public void onChanged(User u) {
                    user = u;
                    userAddress.setText(getUserAddress(user.getLocation()));
                }
            });

            viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AdsViewModel.class);
        setAdsToList();


    }
}