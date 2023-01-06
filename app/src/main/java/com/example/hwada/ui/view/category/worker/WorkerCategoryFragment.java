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
    ImageView arrow;

    String TAG ="WorkerCategoryFragment";
    String category = "worker";
    TextView plumber,technical,plaster,painter,electrical,
            carpenter,builder,upholsterers,ceramicWorker,
            other;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_worker_category, container, false);
        initVarAndSetListener(v);
        return v;
    }

    @Override
    public void onClick(View v) {
      if(target.equals(adsActivityTarget)) {
          if (v.getId() == arrow.getId()) {
              ((CategoryActivity) getActivity()).callMainActivity();
          } else if (v.getId() == plumber.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "plumber", "");
          } else if (v.getId() == technical.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "technical", "");
          } else if (v.getId() == plaster.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "plaster", "");
          } else if (v.getId() == painter.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "painter", "");
          } else if (v.getId() == electrical.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "electrical", "");
          } else if (v.getId() == carpenter.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "carpenter", "");
          } else if (v.getId() == builder.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "builder", "");
          } else if (v.getId() == upholsterers.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "upholsterers", "");
          } else if (v.getId() == ceramicWorker.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "ceramicWorker", "");
          } else if (v.getId() == other.getId()) {
              ((CategoryActivity) getActivity()).callAdsActivity(category, "other", "other");
          }
      }
      else if(target.equals(adNewAdTarget)){
          if (v.getId() == arrow.getId()) {
              ((CategoryActivity) getActivity()).callMainActivity();
          } else if (v.getId() == plumber.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "plumber", "");
          } else if (v.getId() == technical.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "technical", "");
          } else if (v.getId() == plaster.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "plaster", "");
          } else if (v.getId() == painter.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "painter", "");
          } else if (v.getId() == electrical.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "electrical", "");
          } else if (v.getId() == carpenter.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "carpenter", "");
          } else if (v.getId() == builder.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "builder", "");
          } else if (v.getId() == upholsterers.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "upholsterers", "");
          } else if (v.getId() == ceramicWorker.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "ceramicWorker", "");
          } else if (v.getId() == other.getId()) {
              ((CategoryActivity) getActivity()).callAddNewAdActivity(category, "other", "");
          }
      }
    }

    private void initVarAndSetListener(View v){
        arrow = v.findViewById(R.id.arrow_worker_category);
        plumber = v.findViewById(R.id.plumber_tv);
        technical = v.findViewById(R.id.technical_tv);
        plaster = v.findViewById(R.id.plasterer_tv);
        painter = v.findViewById(R.id.painter_tv);
        electrical = v.findViewById(R.id.electrical_tv);
        carpenter = v.findViewById(R.id.carpenter_tv);
        builder = v.findViewById(R.id.builder_tv);
        upholsterers = v.findViewById(R.id.upholsterers_tv);
        ceramicWorker = v.findViewById(R.id.ceramic_Worker_tv);
        other = v.findViewById(R.id.other_tv);

        arrow.setOnClickListener(this);
        plumber.setOnClickListener(this);
        technical.setOnClickListener(this);
        plaster.setOnClickListener(this);
        painter.setOnClickListener(this);
        electrical.setOnClickListener(this);
        carpenter.setOnClickListener(this);
        builder.setOnClickListener(this);
        upholsterers.setOnClickListener(this);
        ceramicWorker.setOnClickListener(this);
        other.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        target =getArguments().getString("target");
    }

}