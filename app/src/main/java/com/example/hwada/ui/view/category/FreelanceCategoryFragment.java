package com.example.hwada.ui.view.category;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

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

    ImageView arrow ;
    TextView ride ,delivery;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_freelance_category, container, false);
        user = getArguments().getParcelable("user");

        arrow = v.findViewById(R.id.arrow_freelance_category);
        ride =v.findViewById(R.id.ride_tv);
        delivery =v.findViewById(R.id.delivery_tv);
        arrow.setOnClickListener(this);
        ride.setOnClickListener(this);
        delivery.setOnClickListener(this);
        return v ;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==arrow.getId()){
            callMainActivity();
        }else if (v.getId()==ride.getId()){
            ((CategoryActivity) getActivity()).callRideFragment();
        }else if (v.getId()==delivery.getId()){
            ((CategoryActivity) getActivity()).callDeliveryFragment();
        }
    }

    private void callMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("user",user);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
        startActivity(intent,b);
        getActivity().finish();
    }
}