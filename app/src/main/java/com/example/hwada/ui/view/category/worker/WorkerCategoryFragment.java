package com.example.hwada.ui.view.category.worker;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentWorkerCategoryBinding;
import com.example.hwada.ui.AdsActivity;
import com.example.hwada.ui.CategoryActivity;
import com.example.hwada.ui.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class WorkerCategoryFragment extends Fragment implements View.OnClickListener {


    String target;
    String adsActivityTarget = "toAdsActivity";
    String adNewAdTarget ="toAdNewAd";

    private static final String TAG = "WorkerCategoryFragment";
    String category = DbHandler.WORKER;
    FragmentWorkerCategoryBinding binding ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWorkerCategoryBinding.inflate(inflater, container, false);
        initVarAndSetListener();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
      if(target.equals(adsActivityTarget)) {
          if (v.getId() == binding.arrowWorkerCategory.getId()) {
              ((CategoryActivity) getActivity()).callMainActivity();
          } else if (v.getId() == binding.plumberTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.PLUMBER, "");
          } else if (v.getId() == binding.technicalTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.TECHNICAL, "");
          } else if (v.getId() == binding.plastererTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.PLASTER, "");
          } else if (v.getId() == binding.painterTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.PAINTER, "");
          } else if (v.getId() == binding.electricalTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.ELECTRICAL, "");
          } else if (v.getId() == binding.carpenterTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.CARPENTER, "");
          } else if (v.getId() == binding.builderTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.BUILDER, "");
          } else if (v.getId() == binding.upholsterersTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.UPHOLSTERERS, "");
          } else if (v.getId() == binding.ceramicWorkerTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.CERAMIC_WORKER, "");
          } else if (v.getId() == binding.otherTv.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, DbHandler.OTHER,"" );
          }
      }
      else if(target.equals(adNewAdTarget)){
          if (v.getId() == binding.arrowWorkerCategory.getId()) {
              ((CategoryActivity) getActivity()).callMainActivity();
          } else if (v.getId() == binding.plumberTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.PLUMBER, "");
          } else if (v.getId() == binding.technicalTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.TECHNICAL, "");
          } else if (v.getId() == binding.plastererTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.PLASTER, "");
          } else if (v.getId() == binding.painterTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.PAINTER, "");
          } else if (v.getId() == binding.electricalTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.ELECTRICAL, "");
          } else if (v.getId() == binding.carpenterTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.CARPENTER, "");
          } else if (v.getId() == binding.builderTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.BUILDER, "");
          } else if (v.getId() == binding.upholsterersTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.UPHOLSTERERS, "");
          } else if (v.getId() == binding.ceramicWorkerTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.CERAMIC_WORKER, "");
          } else if (v.getId() == binding.otherTv.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, DbHandler.OTHER, "");
          }
      }
    }

    private void initVarAndSetListener(){

        binding.arrowWorkerCategory.setOnClickListener(this);
        binding.plumberTv.setOnClickListener(this);
        binding.technicalTv.setOnClickListener(this);
        binding.plastererTv.setOnClickListener(this);
        binding.painterTv.setOnClickListener(this);
        binding.electricalTv.setOnClickListener(this);
        binding.carpenterTv.setOnClickListener(this);
        binding.builderTv.setOnClickListener(this);
        binding.upholsterersTv.setOnClickListener(this);
        binding.ceramicWorkerTv.setOnClickListener(this);
        binding.otherTv.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }

}