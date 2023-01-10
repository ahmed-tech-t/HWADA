package com.example.hwada.ui.view.category.freelance;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentFreelanceCategoryBinding;
import com.example.hwada.ui.CategoryActivity;
import com.example.hwada.ui.MainActivity;

public class FreelanceCategoryFragment extends Fragment implements View.OnClickListener {

    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";
    private static final String TAG = "FreelanceCategoryFragment";

    FragmentFreelanceCategoryBinding binding ;
    String category = DbHandler.FREELANCE;

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentFreelanceCategoryBinding.inflate(inflater, container, false);
        initVarAndSetListener();
        return binding.getRoot() ;
    }

    @Override
    public void onClick(View v) {
        if(target.equals(adsActivityTarget)) {
            if (v.getId() == binding.arrowFreelanceCategory.getId()) {
                ((CategoryActivity) getActivity()).callMainActivity();
            } else if (v.getId() == binding.rideTv.getId()) {
                ((CategoryActivity) getActivity()).callRideFragment(target);
            } else if (v.getId() == binding.deliveryTv.getId()) {
                ((CategoryActivity) getActivity()).callDeliveryFragment(target);
            } else if (v.getId() == binding.nurseTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.NURSE, DbHandler.NURSE);
            } else if (v.getId() == binding.maidTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.MAID, DbHandler.MAID);
            } else if (v.getId() == binding.otherTv.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.OTHER, DbHandler.OTHER);
            }
        }else if(target.equals(adNewAdTarget)){
            if (v.getId() == binding.arrowFreelanceCategory.getId()) {
                ((CategoryActivity) getActivity()).callMainActivity();
            } else if (v.getId() == binding.rideTv.getId()) {
                ((CategoryActivity) getActivity()).callRideFragment(target);
            } else if (v.getId() == binding.deliveryTv.getId()) {
                ((CategoryActivity) getActivity()).callDeliveryFragment(target);
            } else if (v.getId() == binding.nurseTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.NURSE, DbHandler.NURSE);
            } else if (v.getId() == binding.maidTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.MAID, DbHandler.MAID);
            } else if (v.getId() == binding.otherTv.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.OTHER, DbHandler.OTHER);
            }
        }
    }
    private void initVarAndSetListener(){
        binding.arrowFreelanceCategory.setOnClickListener(this);
        binding.rideTv.setOnClickListener(this);
        binding.deliveryTv.setOnClickListener(this);
        binding.nurseTv.setOnClickListener(this);
        binding.maidTv.setOnClickListener(this);
        binding.otherTv.setOnClickListener(this);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}