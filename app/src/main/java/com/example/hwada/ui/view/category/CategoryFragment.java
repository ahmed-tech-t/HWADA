package com.example.hwada.ui.view.category;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentCategoryBinding;
import com.example.hwada.ui.AdsActivity;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.view.category.worker.WorkerCategoryFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class CategoryFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
      private static final String TAG = "CategoryFragment";

    String adNewAdTarget ="toAdNewAd";

    FragmentCategoryBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        initVarAndSetListener();
        return binding.getRoot() ;
    }



    private void initVarAndSetListener(){
        binding.homeFoodCategoryFragment.setOnClickListener(this);
        binding.workerCategoryFragment.setOnClickListener(this);
        binding.freelanceCategoryFragment.setOnClickListener(this);
        binding.handcraftCategoryFragment.setOnClickListener(this);
        binding.arrowCategoryFragment.setOnClickListener(this);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        onBackPressed(dialog);
        return dialog;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         setBottomSheet(view);
    }


    private void setBottomSheet(View view){
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
         bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
         LinearLayout layout = dialog.findViewById(R.id.bottom_sheet_category);
         assert layout != null;
         layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels/2);
    }

    private void onBackPressed(Dialog dialog){
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                // getAction to make sure this doesn't double fire
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return true; // Capture onKey
                }
                return false; // Don't capture
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==binding.arrowCategoryFragment.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.homeFoodCategoryFragment.getId()){
            ((MainActivity)getActivity()).callAddNewAdActivity(DbHandler.HOME_FOOD,DbHandler.HOME_FOOD,"");
        }else if(v.getId() == binding.workerCategoryFragment.getId()){
            ((MainActivity)getActivity()).callCategoryActivity(DbHandler.WORKER,adNewAdTarget);
        }else if(v.getId()== binding.freelanceCategoryFragment.getId()){
            ((MainActivity)getActivity()).callCategoryActivity(DbHandler.FREELANCE,adNewAdTarget);
        }else if(v.getId() == binding.handcraftCategoryFragment.getId()){
            ((MainActivity)getActivity()).callAddNewAdActivity(DbHandler.HANDCRAFT,DbHandler.HANDCRAFT,"");
        }
    }

}