package com.example.hwada.ui.view.category.freelance;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwada.R;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentDeliveryBinding;
import com.example.hwada.ui.CategoryActivity;

public class DeliveryFragment extends Fragment implements View.OnClickListener {


    FragmentDeliveryBinding binding;

    String category = DbHandler.FREELANCE;
    String subCategory = DbHandler.DELIVERY;
    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentDeliveryBinding.inflate(inflater, container, false);

        initVarAndSetListener();


        return binding.getRoot() ;
    }

    @Override
    public void onClick(View v) {
      if(target.equals(adsActivityTarget)) {

          if (v.getId() == binding.arrowDeliveryCategory.getId()) {
              ((CategoryActivity) getActivity()).callMainFreelanceFragment();
          } else if (v.getId() == binding.onFootTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.ON_FOOT);
          } else if (v.getId() == binding.cycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.CYCLE);
          } else if (v.getId() == binding.motorcycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.MOTORCYCLE);
          } else if (v.getId() == binding.tricycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.TRICYCLE);
          } else if (v.getId() == binding.carTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.CAR);
          } else if (v.getId() == binding.miniVanTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.MINI_VAN);
          } else if (v.getId() == binding.vanTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.VAN);
          } else if (v.getId() == binding.otherTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, DbHandler.OTHER);
          }
      }else if(target.equals(adNewAdTarget)){
          if (v.getId() == binding.arrowDeliveryCategory.getId()) {
              ((CategoryActivity) getActivity()).callMainFreelanceFragment();
          } else if (v.getId() == binding.onFootTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.ON_FOOT);
          } else if (v.getId() == binding.cycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.CYCLE);
          } else if (v.getId() == binding.motorcycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.MOTORCYCLE);
          } else if (v.getId() == binding.tricycleTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.TRICYCLE);
          } else if (v.getId() == binding.carTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.CAR);
          } else if (v.getId() == binding.miniVanTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.MINI_VAN);
          } else if (v.getId() == binding.vanTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.VAN);
          } else if (v.getId() == binding.otherTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, DbHandler.OTHER);
          }
      }
    }

    private void initVarAndSetListener(){

        binding.arrowDeliveryCategory.setOnClickListener(this);
        binding.onFootTv.setOnClickListener(this);
        binding.cycleTv.setOnClickListener(this);
        binding.motorcycleTv.setOnClickListener(this);
        binding.tricycleTv.setOnClickListener(this);
        binding.carTv.setOnClickListener(this);
        binding.miniVanTv.setOnClickListener(this);
        binding.vanTv.setOnClickListener(this);
        binding.otherTv.setOnClickListener(this);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}