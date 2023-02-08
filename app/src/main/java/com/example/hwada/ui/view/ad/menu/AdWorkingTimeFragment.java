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
        setWorkingTimeToAdapter();
    }
    private void setWorkingTimeToAdapter(){
        ArrayList<String> days = getDays();
        ArrayList<ArrayList<WorkingTime>> tempSchedule = getSchedule();
        ArrayList<ArrayList<WorkingTime>> schedule = new ArrayList<>();

        Log.e(TAG, "setWorkingTimeToAdapter: "+schedule );

        for (int i = 0; i < tempSchedule.size();i++) {
            if(tempSchedule.get(i).size() > 0){
                schedule.add(tempSchedule.get(i));
            }
        }

        Log.e(TAG, "setWorkingTimeToAdapter: "+schedule);
        adapter = new WorkingTimePreviewDaysAdapter();
        adapter.setDaysSchedule(schedule,days,getContext());
        binding.recyclerAdWorkingTimeFragment.setAdapter(adapter);
        binding.recyclerAdWorkingTimeFragment.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private ArrayList getDays(){
        ArrayList days  =new ArrayList(Arrays.asList(getString(R.string.saturday),
                getString(R.string.sunday),
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wednesday),
                getString(R.string.thursday),
                getString(R.string.friday)));
    return days;
    }
    private ArrayList<ArrayList<WorkingTime>>getSchedule(){
        ArrayList<ArrayList<WorkingTime>> temp = new ArrayList<>();
        temp.add(ad.getDaysSchedule().getSaturday());
        temp.add(ad.getDaysSchedule().getSunday());
        temp.add(ad.getDaysSchedule().getMonday());
        temp.add(ad.getDaysSchedule().getTuesday());
        temp.add(ad.getDaysSchedule().getWednesday());
        temp.add(ad.getDaysSchedule().getThursday());
        temp.add(ad.getDaysSchedule().getFriday());
        return temp;
    }


}