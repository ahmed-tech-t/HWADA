package com.example.hwada.ui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.WorkingTimeAdapter;
import com.example.hwada.adapter.WorkingTimePreviewAdapter;
import com.example.hwada.databinding.FragmentWorkTimePreviewBinding;
import com.example.hwada.viewmodel.WorkingTimeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class WorkTimePreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener , WorkTimeEditFragment.GettingPassedData  {

    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;

    String SATURDAY = "saturday" ,SUNDAY ="sunday" ,MONDAY ="monday",TUESDAY ="tuesday"
            ,WEDNESDAY="wednesday",THURSDAY="thursday",FRIDAY="friday";
    
    WorkingTimePreviewAdapter showAdapterSaturday ,showAdapterSunday,showAdapterMonday,showAdapterTuesday
            ,showAdapterWednesday,showAdapterThursday, showAdapterFriday;
    
    String TAG ="WorkTimeFragment";

    Ad newAd ;
    FragmentWorkTimePreviewBinding binding ;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding = FragmentWorkTimePreviewBinding.inflate(inflater, container, false);
        newAd = new Ad();
        initView();
        setWorkingTimePreviewAdapter();
        return binding.getRoot();
    }

    private void initView(){
        binding.arrowWorkTime.setOnClickListener(this);
        binding.buttonSaturdayWorkTime.setOnClickListener(this);
        binding.buttonSundayWorkTime.setOnClickListener(this);
        binding.buttonMondayWorkTime.setOnClickListener(this);
        binding.buttonTuesdayWorkTime.setOnClickListener(this);
        binding.buttonWednesdayWorkTime.setOnClickListener(this);
        binding.buttonThursdayWorkTime.setOnClickListener(this);
        binding.buttonFridayWorkTime.setOnClickListener(this);
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
        Log.e(TAG, "onPause: " );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated: " );
        newAd = getArguments().getParcelable("ad");

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==binding.arrowWorkTime.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.buttonSaturdayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getSaturday(),SATURDAY);
        }else if(v.getId() == binding.buttonSundayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getSunday() , SUNDAY);
        }else if(v.getId() == binding.buttonMondayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getMonday(), MONDAY);
        }else if(v.getId() == binding.buttonTuesdayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getTuesday(),  TUESDAY);
        }else if(v.getId() == binding.buttonWednesdayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getWednesday(), WEDNESDAY);
        }else if(v.getId() == binding.buttonThursdayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getThursday(), THURSDAY);
        }else if(v.getId() == binding.buttonFridayWorkTime.getId()){
            callBottomSheet(new WorkTimeEditFragment() ,newAd.getDaysSchedule().getFriday(), FRIDAY);
        }
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment , ArrayList<WorkingTime> workingTimes, String tag){
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putParcelableArrayList("workingTimes",workingTimes);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager,fragment.getTag());
    }

    private void handleSwitches(){
        binding.switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonSaturdayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonSaturdayWorkTime.setVisibility(View.GONE);
                    showAdapterSaturday.clearList();
                }

            }
        });
        binding.switchSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.e(TAG, "onCheckedChanged: " );
                    binding.buttonSundayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonSundayWorkTime.setVisibility(View.GONE);
                    showAdapterSunday.clearList();
                }
            }
        });
        binding.switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonMondayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonMondayWorkTime.setVisibility(View.GONE);
                    showAdapterMonday.clearList();
                }
            }
        });
        binding.switchTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonTuesdayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonTuesdayWorkTime.setVisibility(View.GONE);
                    showAdapterTuesday.clearList();
                }
            }
        });
        binding.switchWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonWednesdayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonWednesdayWorkTime.setVisibility(View.GONE);
                    showAdapterWednesday.clearList();
                }
            }
        });
        binding.switchThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonThursdayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonThursdayWorkTime.setVisibility(View.GONE);
                    showAdapterThursday.clearList();
                }
            }
        });
        binding.switchFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.buttonFridayWorkTime.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonFridayWorkTime.setVisibility(View.GONE);
                    showAdapterFriday.clearList();
                }
            }
        });
    }

    private void setWorkingTimePreviewAdapter(){
        showAdapterSaturday = new WorkingTimePreviewAdapter();
        showAdapterSunday  = new WorkingTimePreviewAdapter();
        showAdapterMonday  = new WorkingTimePreviewAdapter();
        showAdapterTuesday  = new WorkingTimePreviewAdapter();
        showAdapterWednesday = new WorkingTimePreviewAdapter();
        showAdapterThursday = new WorkingTimePreviewAdapter();
        showAdapterFriday  = new WorkingTimePreviewAdapter();
        try {
            binding.recyclerSaturday.setAdapter(showAdapterSaturday);
            binding.recyclerSunday.setAdapter(showAdapterSunday);
            binding.recyclerMonday.setAdapter(showAdapterMonday);
            binding.recyclerTuesday.setAdapter(showAdapterTuesday);
            binding.recyclerWednesday.setAdapter(showAdapterWednesday);
            binding.recyclerThursday.setAdapter(showAdapterThursday);
            binding.recyclerFriday.setAdapter(showAdapterFriday);

            showAdapterSaturday.setList(newAd.getDaysSchedule().getSaturday(),SATURDAY);
            showAdapterSunday.setList(newAd.getDaysSchedule().getSunday(),SUNDAY);
            showAdapterMonday.setList(newAd.getDaysSchedule().getMonday(),MONDAY);
            showAdapterTuesday.setList(newAd.getDaysSchedule().getTuesday(),TUESDAY);
            showAdapterWednesday.setList(newAd.getDaysSchedule().getWednesday(),WEDNESDAY);
            showAdapterThursday.setList(newAd.getDaysSchedule().getThursday(),THURSDAY);
            showAdapterFriday.setList(newAd.getDaysSchedule().getFriday(),FRIDAY);

            binding.recyclerSaturday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerSunday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerMonday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerTuesday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerWednesday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerThursday.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.recyclerFriday.setLayoutManager(new LinearLayoutManager(getActivity()));
            handleSwitches();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void passedData(ArrayList<WorkingTime> list, ArrayList<String> applyToList) {
        Log.e(TAG, "onDataPass: "+applyToList);

        if(applyToList.contains(SATURDAY)){
            newAd.getDaysSchedule().setSaturday(list);
            showAdapterSaturday.setList(list,SATURDAY);
            binding.switchSaturday.setChecked(true);
            binding.buttonSaturdayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(SUNDAY)){
            Log.e(TAG, "passedData: "+"sunday" );
            newAd.getDaysSchedule().setSunday(list);
            showAdapterSunday.setList(list,SUNDAY);
            binding.switchSunday.setChecked(true);
            binding.buttonSundayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(MONDAY)){
            newAd.getDaysSchedule().setMonday(list);
            showAdapterMonday.setList(list,MONDAY);
            binding.switchMonday.setChecked(true);
            binding.buttonMondayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(TUESDAY)){
            newAd.getDaysSchedule().setTuesday(list);
            showAdapterTuesday.setList(list,TUESDAY);
            binding.switchTuesday.setChecked(true);
            binding.buttonTuesdayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(WEDNESDAY)){
            newAd.getDaysSchedule().setWednesday(list);
            showAdapterWednesday.setList(list,WEDNESDAY);
            binding.switchWednesday.setChecked(true);
            binding.buttonWednesdayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(THURSDAY)){
            newAd.getDaysSchedule().setThursday(list);
            showAdapterThursday.setList(list,THURSDAY);
            binding.switchThursday.setChecked(true);
            binding.buttonThursdayWorkTime.setText(getString(R.string.edit));
        }
        if(applyToList.contains(FRIDAY)){
            newAd.getDaysSchedule().setFriday(list);
            showAdapterFriday.setList(list,FRIDAY);
            binding.switchFriday.setChecked(true);
            binding.buttonFridayWorkTime.setText(getString(R.string.edit));

        }
    }

}