package com.example.hwada.ui.view.category.worker;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.ui.CategoryActivity;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.MapActivity;

public class WorkerCategoryFragment extends Fragment implements View.OnClickListener {


    ImageView arrow;
    User user ;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_worker_category, container, false);
        user = getArguments().getParcelable("user");


        arrow = v.findViewById(R.id.arrow_worker_category);
        arrow.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==arrow.getId()) {
            callMainActivity();
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