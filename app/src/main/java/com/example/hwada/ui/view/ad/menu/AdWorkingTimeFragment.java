package com.example.hwada.ui.view.ad.menu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.ReviewAdapter;
import com.example.hwada.adapter.WorkingTimePreviewDaysAdapter;
import com.example.hwada.databinding.FragmentAdWorkingTimeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdWorkingTimeFragment extends Fragment {

    WorkingTimePreviewDaysAdapter adapter ;
    Ad ad ;
    FragmentAdWorkingTimeBinding binding ;
    private static final String TAG = "AdWorkingTimeFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdWorkingTimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ad = getArguments().getParcelable("ad");
        setRecycler();
    }
    private void setRecycler(){
        ad.getDaysSchedule().getDays().entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
        adapter = new WorkingTimePreviewDaysAdapter();
        adapter.setDaysSchedule(ad.getDaysSchedule(),getContext());
        binding.recyclerAdWorkingTimeFragment.setAdapter(adapter);
        binding.recyclerAdWorkingTimeFragment.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}