package com.example.hwada.ui.view.ad.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.Ad;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentAdDescriptionBinding;


public class AdDescriptionFragment extends Fragment {


    FragmentAdDescriptionBinding binding ;
    Ad ad;
    App app ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdDescriptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        ad = getArguments().getParcelable("ad");
        binding.itemDescriptionAdDescriptionFragment.setText(ad.getDescription());
        app = (App) getContext().getApplicationContext();

        setViewIfAdIsOpen();
    }

    private void setViewIfAdIsOpen() {
        String[] days = getResources().getStringArray(R.array.daysVal);
        boolean adIsOpen = ad.isOpen(app.getTime(),days,app.getDayIndex());

        if(!adIsOpen||!ad.isActive()){
            binding.linearLayoutAdDescriptionFragment.setAlpha(0.5f);
        }else binding.linearLayoutAdDescriptionFragment.setAlpha(1f);
    }
}