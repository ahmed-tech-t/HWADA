package com.example.hwada.ui.view.category.freelance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwada.R;
import com.example.hwada.ui.CategoryActivity;

public class RideFragment extends Fragment implements View.OnClickListener {


    ImageView arrow ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_ride, container, false);
        arrow = v.findViewById(R.id.arrow_ride_category);
        arrow.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==arrow.getId()){
            ((CategoryActivity)getActivity()).callMainFreelanceFragment();
        }
    }
}