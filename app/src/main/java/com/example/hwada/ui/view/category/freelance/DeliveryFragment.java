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
import com.example.hwada.ui.CategoryActivity;

public class DeliveryFragment extends Fragment implements View.OnClickListener {

    ImageView arrow;
    TextView onFoot , cycle , motorcycle, tricycle, car, miniVan,van,other;
    String category = "freelance";
    String subCategory = "delivery";
    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);

        initVarAndSetListener(v);


        return v ;
    }

    @Override
    public void onClick(View v) {
      if(target.equals(adsActivityTarget)) {

          if (v.getId() == arrow.getId()) {
              ((CategoryActivity) getActivity()).callMainFreelanceFragment();
          } else if (v.getId() == onFoot.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "onFoot");
          } else if (v.getId() == cycle.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "cycle");
          } else if (v.getId() == motorcycle.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "motorcycle");
          } else if (v.getId() == tricycle.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "tricycle");
          } else if (v.getId() == car.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "car");
          } else if (v.getId() == miniVan.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "miniVan");
          } else if (v.getId() == van.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "van");
          } else if (v.getId() == other.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "other");
          }
      }else if(target.equals(adNewAdTarget)){
          if (v.getId() == arrow.getId()) {
              ((CategoryActivity) getActivity()).callMainFreelanceFragment();
          } else if (v.getId() == onFoot.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "onFoot");
          } else if (v.getId() == cycle.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "cycle");
          } else if (v.getId() == motorcycle.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "motorcycle");
          } else if (v.getId() == tricycle.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "tricycle");
          } else if (v.getId() == car.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "car");
          } else if (v.getId() == miniVan.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "miniVan");
          } else if (v.getId() == van.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "van");
          } else if (v.getId() == other.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "other");
          }
      }
    }

    private void initVarAndSetListener(View v){
        arrow  = v.findViewById(R.id.arrow_delivery_category);
        onFoot =v.findViewById(R.id.on_foot_tv);
        cycle =v.findViewById(R.id.cycle_tv);
        motorcycle =v.findViewById(R.id.motorcycle_tv);
        tricycle =v.findViewById(R.id.tricycle_tv);
        car =v.findViewById(R.id.car_tv);
        miniVan =v.findViewById(R.id.mini_van_tv);
        van =v.findViewById(R.id.van_tv);
        other =v.findViewById(R.id.other_tv);


        arrow.setOnClickListener(this);
        onFoot.setOnClickListener(this);
        cycle.setOnClickListener(this);
        motorcycle.setOnClickListener(this);
        tricycle.setOnClickListener(this);
        car.setOnClickListener(this);
        miniVan.setOnClickListener(this);
        van.setOnClickListener(this);
        other.setOnClickListener(this);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}