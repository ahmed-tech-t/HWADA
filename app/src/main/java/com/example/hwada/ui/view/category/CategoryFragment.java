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
import com.example.hwada.ui.AdsActivity;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.view.category.worker.WorkerCategoryFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class CategoryFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    ImageView arrow;
    LinearLayout homeFood ,worker ,freelance ,handcraft;
    String TAG ="CategoryFragment";

    String adNewAdTarget ="toAdNewAd";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        initVarAndSetListener(v);
        return v ;
    }



    private void initVarAndSetListener(View v){
        arrow = v.findViewById(R.id.arrow_category_fragment);
        homeFood = v.findViewById(R.id.home_food_category_fragment);
        worker =v.findViewById(R.id.worker_category_fragment);
        freelance =v.findViewById(R.id.freelance_category_fragment);
        handcraft =v.findViewById(R.id.handcraft_category_fragment);

        homeFood.setOnClickListener(this);
        worker.setOnClickListener(this);
        freelance.setOnClickListener(this);
        handcraft.setOnClickListener(this);
        arrow.setOnClickListener(this);
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
        if (v.getId()==arrow.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == homeFood.getId()){
            ((MainActivity)getActivity()).callAddNewAdActivity("homeFood","","");
        }else if(v.getId() == worker.getId()){
            ((MainActivity)getActivity()).callCategoryActivity("worker",adNewAdTarget);
        }else if(v.getId()== freelance.getId()){
            ((MainActivity)getActivity()).callCategoryActivity("freelance",adNewAdTarget);

        }else if(v.getId() == handcraft.getId()){
            ((MainActivity)getActivity()).callAddNewAdActivity("handcraft","","");
        }
    }

}