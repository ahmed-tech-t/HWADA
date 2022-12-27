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
import com.example.hwada.ui.CategoryActivity;

public class RideFragment extends Fragment implements View.OnClickListener {


    ImageView arrow ;
    TextView car ,bus ,other;
    String category = "freelance";
    String subCategory = "ride";
    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_ride, container, false);
        initVarAndSetListener(v);
        return v;
    }

    @Override
    public void onClick(View v) {

        if(target.equals(adsActivityTarget)) {
            if (v.getId() == arrow.getId()) {
                ((CategoryActivity) getActivity()).callMainFreelanceFragment();
            } else if (v.getId() == car.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "car");
            } else if (v.getId() == bus.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "bus");
            } else if (v.getId() == other.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, subCategory, "other");
            }
        }else if(target.equals(adNewAdTarget)){
            if (v.getId() == arrow.getId()) {
                ((CategoryActivity) getActivity()).callMainFreelanceFragment();
            } else if (v.getId() == car.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "car");
            } else if (v.getId() == bus.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "bus");
            } else if (v.getId() == other.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, subCategory, "other");
            }
        }
    }

    private void initVarAndSetListener(View v){
        arrow = v.findViewById(R.id.arrow_ride_category);
        car = v.findViewById(R.id.car_tv);
        bus = v.findViewById(R.id.bus_tv);
        other = v.findViewById(R.id.other_tv);

        car.setOnClickListener(this);
        bus.setOnClickListener(this);
        other.setOnClickListener(this);

        arrow.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}