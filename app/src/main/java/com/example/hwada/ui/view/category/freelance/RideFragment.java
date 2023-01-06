package com.example.hwada.ui.view.category.freelance;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentRideBinding;
import com.example.hwada.ui.CategoryActivity;

public class RideFragment extends Fragment implements View.OnClickListener {

    String category =  DbHandler.FREELANCE;
    String subCategory = DbHandler.RIDE;
    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";
    FragmentRideBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentRideBinding.inflate(inflater, container, false);
        initVarAndSetListener();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

        if(target.equals(adsActivityTarget)) {
            if (v.getId() == binding.arrowRideCategory.getId()) {
                ((CategoryActivity) getActivity()).callMainFreelanceFragment();
            } else if (v.getId() == binding.carTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.CAR);
            } else if (v.getId() == binding.busTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.BUS);
            } else if (v.getId() == binding.otherTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.OTHER);
            }
        }else if(target.equals(adNewAdTarget)){
            if (v.getId() == binding.arrowRideCategory.getId()) {
                ((CategoryActivity) getActivity()).callMainFreelanceFragment();
            } else if (v.getId() ==binding.carTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.CAR);
            } else if (v.getId() == binding.busTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.BUS);
            } else if (v.getId() == binding.otherTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.OTHER);
            }
        }
    }

    private void initVarAndSetListener(){
        binding.carTv.setOnClickListener(this);
        binding.busTv.setOnClickListener(this);
        binding.otherTv.setOnClickListener(this);
        binding.arrowRideCategory.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}