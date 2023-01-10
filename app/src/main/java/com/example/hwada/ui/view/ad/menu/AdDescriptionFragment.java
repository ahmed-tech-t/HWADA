package com.example.hwada.ui.view.ad.menu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.Ad;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentAdDescriptionBinding;


public class AdDescriptionFragment extends Fragment {


    FragmentAdDescriptionBinding binding ;
    Ad ad;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdDescriptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ad = getArguments().getParcelable("ad");
        binding.itemDescriptionAdDescriptionFragment.setText(ad.getDescription());
    }

}