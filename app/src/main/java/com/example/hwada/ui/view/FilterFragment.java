package com.example.hwada.ui.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.FilterModel;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentFilterBinding;
import com.example.hwada.viewmodel.FilterViewModel;

public class FilterFragment extends DialogFragment implements View.OnClickListener {


    FragmentFilterBinding binding ;

    FilterModel filter ;
    FilterViewModel filterViewModel ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assert getArguments() != null;
        filter = getArguments().getParcelable(getString(R.string.filterVal));
        binding = FragmentFilterBinding.inflate(inflater,container,false);
        binding.buApplyFilterFragment.setOnClickListener(this);
        setDataToView();
        setRadioButtonListener();
        setCheckBosListener();
        return binding.getRoot();

    }

    private void setDataToView() {
        if(filter.getSort().equals(getString(R.string.updateDateVal))){
            binding.rbUpdateDateFilterFragment.setChecked(true);
        }else if(filter.getSort().equals(getString(R.string.ratingVal))){
            binding.rbRatingFilterFragment.setChecked(true);
        }else if(filter.getSort().equals(getString(R.string.theCheapestVal))){
            binding.rbTheCheapestFilterFragment.setChecked(true);
        }else if(filter.getSort().equals(getString(R.string.theExpensiveVal))){
            binding.rbTheExpensiveFilterFragment.setChecked(true);
        }else if(filter.getSort().equals(getString(R.string.theClosestVal))){
            binding.rbTheClosestFilterFragment.setChecked(true);
        }
        if(filter.isOpen()) binding.cbAdStatusFilterFragment.setChecked(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getParentFragment()!=null){
            filterViewModel = new ViewModelProvider(getParentFragment()).get(FilterViewModel.class);
        }else if(getActivity()!=null){
            filterViewModel = new ViewModelProvider(getActivity()).get(FilterViewModel.class);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== binding.buApplyFilterFragment.getId()){
            filterViewModel.setFilter(filter);
            dismiss();
        }
    }
    private void setRadioButtonListener(){
        binding.rbRatingFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                filter.setSort(getString(R.string.ratingVal));
                if(binding.rbUpdateDateFilterFragment.isChecked()) binding.rbUpdateDateFilterFragment.setChecked(false);
                else if(binding.rbTheClosestFilterFragment.isChecked()) binding.rbTheClosestFilterFragment.setChecked(false);
                else if(binding.rbTheCheapestFilterFragment.isChecked()) binding.rbTheCheapestFilterFragment.setChecked(false);
                else if(binding.rbTheExpensiveFilterFragment.isChecked()) binding.rbTheExpensiveFilterFragment.setChecked(false);
            }
        });

        binding.rbTheClosestFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                filter.setSort(getString(R.string.theClosestVal));
               if(binding.rbRatingFilterFragment.isChecked()) binding.rbRatingFilterFragment.setChecked(false);
               else if(binding.rbUpdateDateFilterFragment.isChecked()) binding.rbUpdateDateFilterFragment.setChecked(false);
               else if(binding.rbTheCheapestFilterFragment.isChecked()) binding.rbTheCheapestFilterFragment.setChecked(false);
               else if(binding.rbTheExpensiveFilterFragment.isChecked()) binding.rbTheExpensiveFilterFragment.setChecked(false);
            }
        });

        binding.rbUpdateDateFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                filter.setSort(getString(R.string.updateDateVal));
                if(binding.rbRatingFilterFragment.isChecked()) binding.rbRatingFilterFragment.setChecked(false);
                else if(binding.rbTheClosestFilterFragment.isChecked())binding.rbTheClosestFilterFragment.setChecked(false);
                else if(binding.rbTheCheapestFilterFragment.isChecked()) binding.rbTheCheapestFilterFragment.setChecked(false);
                else if(binding.rbTheExpensiveFilterFragment.isChecked()) binding.rbTheExpensiveFilterFragment.setChecked(false);
            }
        });

        binding.rbTheCheapestFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> {
          if(isChecked) {
              filter.setSort(getString(R.string.theCheapestVal));
              if (binding.rbRatingFilterFragment.isChecked())
                  binding.rbRatingFilterFragment.setChecked(false);
              else if (binding.rbUpdateDateFilterFragment.isChecked())
                  binding.rbUpdateDateFilterFragment.setChecked(false);
              else if (binding.rbTheClosestFilterFragment.isChecked())
                  binding.rbTheClosestFilterFragment.setChecked(false);
              else if (binding.rbTheExpensiveFilterFragment.isChecked())
                  binding.rbTheExpensiveFilterFragment.setChecked(false);
          }
        });
        binding.rbTheExpensiveFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> {
          if(isChecked) {
              filter.setSort(getString(R.string.theExpensiveVal));
              if (binding.rbRatingFilterFragment.isChecked())
                  binding.rbRatingFilterFragment.setChecked(false);
              else if (binding.rbUpdateDateFilterFragment.isChecked())
                  binding.rbUpdateDateFilterFragment.setChecked(false);
              else if (binding.rbTheClosestFilterFragment.isChecked())
                  binding.rbTheClosestFilterFragment.setChecked(false);
              else if (binding.rbTheCheapestFilterFragment.isChecked())
                  binding.rbTheCheapestFilterFragment.setChecked(false);
          }
        });
    }
    public void setCheckBosListener(){
        binding.cbAdStatusFilterFragment.setOnCheckedChangeListener((buttonView, isChecked) -> filter.setOpen(isChecked));
    }

}