package com.example.hwada.ui.view.category.freelance;

import android.app.ActivityOptions;
import android.content.Intent;
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
import com.example.hwada.ui.MainActivity;

public class FreelanceCategoryFragment extends Fragment implements View.OnClickListener {

    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";

    ImageView arrow ;
    TextView ride ,delivery ,nurse , maid ,other;
    String category = "freelance";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_freelance_category, container, false);
        initVarAndSetListener(v);
        return v ;
    }

    @Override
    public void onClick(View v) {
        if(target.equals(adsActivityTarget)) {
            if (v.getId() == arrow.getId()) {
                ((CategoryActivity) getActivity()).callMainActivity();
            } else if (v.getId() == ride.getId()) {
                ((CategoryActivity) getActivity()).callRideFragment(target);
            } else if (v.getId() == delivery.getId()) {
                ((CategoryActivity) getActivity()).callDeliveryFragment(target);
            } else if (v.getId() == nurse.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, "nurse", "nurse");
            } else if (v.getId() == maid.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, "maid", "maid");
            } else if (v.getId() == other.getId()) {
                ((CategoryActivity) getActivity()).callAdsActivity(category, "other", "other");
            }
        }else if(target.equals(adNewAdTarget)){
            if (v.getId() == arrow.getId()) {
                ((CategoryActivity) getActivity()).callMainActivity();
            } else if (v.getId() == ride.getId()) {
                ((CategoryActivity) getActivity()).callRideFragment(target);
            } else if (v.getId() == delivery.getId()) {
                ((CategoryActivity) getActivity()).callDeliveryFragment(target);
            } else if (v.getId() == nurse.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "nurse", "nurse");
            } else if (v.getId() == maid.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "maid", "maid");
            } else if (v.getId() == other.getId()) {
                ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "other", "other");
            }
        }
    }
    private void initVarAndSetListener(View v){
        arrow = v.findViewById(R.id.arrow_freelance_category);
        ride =v.findViewById(R.id.ride_tv);
        delivery =v.findViewById(R.id.delivery_tv);
        nurse =v.findViewById(R.id.nurse_tv);
        maid = v.findViewById(R.id.maid_tv);
        other= v.findViewById(R.id.other_tv);
        arrow.setOnClickListener(this);
        ride.setOnClickListener(this);
        delivery.setOnClickListener(this);
        nurse.setOnClickListener(this);
        maid.setOnClickListener(this);
        other.setOnClickListener(this);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }
}