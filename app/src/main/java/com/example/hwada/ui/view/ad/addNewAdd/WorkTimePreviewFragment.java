package com.example.hwada.ui.view.ad.addNewAdd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.DaysSchedule;
import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.WorkingTimeEditDaysAdapter;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentWorkTimePreviewBinding;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.ui.MyAdsActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class WorkTimePreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener , WorkTimeEditFragment.GettingPassedData , WorkingTimeEditDaysAdapter.OnItemListener  {

    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;
    User user;
    App app ;

    
    WorkingTimeEditDaysAdapter adapter ;
    String DAY_TAG ;
    private static final String TAG = "WorkTimePreviewFragment";
    Ad ad ;
    ArrayList<Boolean> switches = new ArrayList<>(Arrays.asList(false,false,false,false,false,false,false));

    String mode;
    AdsViewModel adsViewModel;
    FragmentWorkTimePreviewBinding binding ;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkTimePreviewBinding.inflate(inflater, container, false);
        binding.arrowWorkTime.setOnClickListener(this);
        binding.saveButtonAddNewAd.setOnClickListener(this);
        app = (App) getContext().getApplicationContext();
        return binding.getRoot();
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
        LinearLayout layout = dialog.findViewById(R.id.bottom_sheet_work_time_preview);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels/2);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_DRAGGING)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(newState==BottomSheetBehavior.STATE_HIDDEN) dismiss();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getArguments() != null;
        ad = getArguments().getParcelable(getString(R.string.adVal));
        user = getArguments().getParcelable(getString(R.string.userVal));
        mode = getArguments().getString(getString(R.string.modeVal));
        adsViewModel = new ViewModelProvider(this).get(AdsViewModel.class);
        if(mode.equals(getString(R.string.editModeVal))) setSwitches();
        setRecycler();

    }

    private void setSwitches(){
        for (int i = 0; i < ad.getDaysSchedule().getDays().size(); i++) {
            String day = ad.getDaysSchedule().getDayValFromPosition(i);
            if(!Objects.requireNonNull(ad.getDaysSchedule().getDays().get(day)).isEmpty()) switches.set(i,true);
        }

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == binding.arrowWorkTime.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (v.getId() == binding.saveButtonAddNewAd.getId()){
           if(dataValidated()){
               if(mode.equals(getString(R.string.newModeVal))){
                   ad.setTimeStamp(app.getCurrentDate());
                   ad.setActive(true);
                   adsViewModel.addOrUpdateAd(ad,true);
                   goToMainActivity();
               }else if(mode.equals(getString(R.string.editModeVal))){
                   adsViewModel.addOrUpdateAd(ad,false);
                   Toast.makeText(app, getString(R.string.updateAdMessage), Toast.LENGTH_LONG).show();
                   goToMyAdsActivity();
               }
           }
        }
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment , ArrayList<WorkingTime> workingTimes, String tag){
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.tagVal), tag);
        bundle.putParcelableArrayList(getString(R.string.workingTimesVal),workingTimes);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager,fragment.getTag());
    }


    private void setRecycler(){
        adapter = new WorkingTimeEditDaysAdapter();
        try {
            binding.recyclerWorkingTimePreview.setAdapter(adapter);
            adapter.setList(ad.getDaysSchedule(),DAY_TAG,getContext(),this);
            binding.recyclerWorkingTimePreview.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void passedData(ArrayList<WorkingTime> list, ArrayList<String> applyToList) {
        for (String day : applyToList) {
            int pos =  ad.getDaysSchedule().getPosFromDay(day);
            ad.getDaysSchedule().getDays().put(day,list);
            adapter.updateRecycler(list,pos);
        }
    }


    @Override
    public void rightButtonClicked(int pos) {
        String day = ad.getDaysSchedule().getDayValFromPosition(pos);
        ArrayList<WorkingTime> workingTimes = (ArrayList<WorkingTime>) ad.getDaysSchedule().getDays().get(day);
        String dayVal = ad.getDaysSchedule().getDayValFromPosition(pos);
        callBottomSheet(new WorkTimeEditFragment(),workingTimes,dayVal);
    }



    @Override
    public void switchClicked(boolean isChecked, int pos) {
        String day = ad.getDaysSchedule().getDayValFromPosition(pos);
        if(!isChecked){
            ad.getDaysSchedule().getDays().put(day,new ArrayList<>());
            switches.set(pos,false);
        }else switches.set(pos,true);
    }


    private boolean dataValidated(){
        for (int i = 0; i < switches.size(); i++) {
            if(switches.get(i)){
                String day = ad.getDaysSchedule().getDayValFromPosition(i);
                ArrayList<WorkingTime> workingTimes = (ArrayList<WorkingTime>) ad.getDaysSchedule().getDays().get(day);
                if(workingTimes.isEmpty()){
                    showDialog(getString(R.string.failed),getString(R.string.periodError));
                    return false ;
                }
            }
        }

        if (switches.contains(true)){
            return true;
        }
        showDialog(getString(R.string.failed),getString(R.string.workingTimePreviewSwitchesOfError));
        return false;
    }



    private void showDialog(String title ,String body){
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    public void goToMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(getString(R.string.userVal), user);
        startActivity(intent);
        getActivity().finish();
    }
    public void goToMyAdsActivity() {
        Intent intent = new Intent(getContext(), MyAdsActivity.class);
        intent.putExtra(getString(R.string.userVal), user);
        startActivity(intent);
        getActivity().finish();
    }


}