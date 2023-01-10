package com.example.hwada.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.WorkingTimeEditPeriodAdapter;
import com.example.hwada.databinding.FragmentWorkTimeEditBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WorkTimeEditFragment extends BottomSheetDialogFragment implements WorkingTimeEditPeriodAdapter.OnItemListener , View.OnClickListener {

    WorkingTimeEditPeriodAdapter adapter ;
    int hour ,minutes;
    String tag ;
    ArrayList<WorkingTime> workingTimeList ;
    private FragmentWorkTimeEditBinding binding;
    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;
    ArrayList<String> applyTo ;
    GettingPassedData mListener;
    String SATURDAY = "saturday" ,SUNDAY ="sunday" ,MONDAY ="monday",TUESDAY ="tuesday"
            ,WEDNESDAY="wednesday",THURSDAY="thursday",FRIDAY="friday";
    String TAG ="WorkTimeEditFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkTimeEditBinding.inflate(inflater, container, false);

        binding.buttonAddWorkTimeEdit.setOnClickListener(this);
        binding.buttonSaveWorkTimeEdit.setOnClickListener(this);
        binding.arrowEditWorkTime.setOnClickListener(this);

        binding.checkboxSaturday.setOnClickListener(this);

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
        LinearLayout layout = dialog.findViewById(R.id.bottom_sheet_work_time_edit);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        workingTimeList = getArguments().getParcelableArrayList("workingTimes");
        tag = getArguments().getString("tag");
        applyTo = new ArrayList<>();
        setWorkTimeToAdapter();
        setTitle();
        checkBoxListener();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setWorkTimeToAdapter() {
        adapter = new WorkingTimeEditPeriodAdapter();
        try {
            binding.recyclerWorkTimeEdit.setAdapter(adapter);

            adapter.setList(workingTimeList,this);

            binding.recyclerWorkTimeEdit.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void fromTimeListener(int pos,  Button button) {
        getTimeFromPickerAndSetItToButton(pos , button,"from");
    }

    @Override
    public void toTimeListener(int pos, Button button) {
        getTimeFromPickerAndSetItToButton(pos , button,"to");
    }

    @Override
    public void removeTimeListener(int pos) {
        adapter.removeOneItem(pos);
        if(adapter.getItemCount()<3) binding.buttonAddWorkTimeEdit.setVisibility(View.VISIBLE);
    }

    private void getTimeFromPickerAndSetItToButton(int pos , Button button,String tag){
        button.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
            }
        }, 1000); // Re-enable the button after 1 second

        fireTimePickupDialog(getString(R.string.from),pos,button,tag);
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==binding.arrowEditWorkTime.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else if (v.getId() == binding.buttonAddWorkTimeEdit.getId()){
            WorkingTime w = new WorkingTime();
            adapter.addItem(w);
            if(adapter.getItemCount()>=3) binding.buttonAddWorkTimeEdit.setVisibility(View.GONE);
        }else if(v.getId() == binding.buttonSaveWorkTimeEdit.getId()){
            if(workingTimeList.size()==0) showDialog(getString(R.string.failed),getString(R.string.periodError));
            else if (!dataValidated()) showDialog(getString(R.string.failed),getString(R.string.pickTimeEmptyError));
            else if(applyTo.size()==0) showDialog(getString(R.string.failed),getString(R.string.checkBoxError));
            else {
                if(mListener!=null) {
                                      mListener.passedData(workingTimeList, applyTo);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    dismiss();
                }
            }
        }
    }

    private boolean dataValidated(){
        boolean validated = true ;
        for (WorkingTime w : workingTimeList) {
            if(w.getTo().length()==0||w.getFrom().length()==0) return false;
        }
        return validated;
    }
    private void setTitle(){

        if(tag.equals(SATURDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.saturday));
            binding.checkboxSaturday.setChecked(true);
            applyTo.add(SATURDAY);
        }else if(tag.equals(SUNDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.sunday));
            binding.checkboxSunday.setChecked(true);
            applyTo.add(SUNDAY);
        }else if(tag.equals(MONDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.monday));
            binding.checkboxMonday.setChecked(true);
            applyTo.add(MONDAY);
        }else if(tag.equals(TUESDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.tuesday));
            binding.checkboxTuesday.setChecked(true);
            applyTo.add(TUESDAY);
        }else if(tag.equals(WEDNESDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.wednesday));
            binding.checkboxWednesday.setChecked(true);
            applyTo.add(WEDNESDAY);
        }else if(tag.equals(THURSDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.thursday));
            binding.checkboxThursday.setChecked(true);
            applyTo.add(THURSDAY);
        }else if(tag.equals(FRIDAY)) {
            binding.tvDayWorkTimeEdit.setText(getString(R.string.friday));
            binding.checkboxFriday.setChecked(true);
            applyTo.add(FRIDAY);
        }
    }

    private void fireTimePickupDialog(String title ,int pos,Button button ,String tag){

        TimePickerDialog.OnTimeSetListener onItemListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String formattedTime = sdf.format(calendar.getTime());

                if(tag.equals("from")) workingTimeList.get(pos).setFrom(formattedTime);
                else if(tag.equals("to")) workingTimeList.get(pos).setTo(formattedTime);
                button.setText(formattedTime);

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext() , onItemListener ,hour, minutes ,false);
        timePickerDialog.setTitle(title);
        timePickerDialog.show();

    }

    private void checkBoxListener(){
        binding.checkboxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(SATURDAY);
                }else {
                    applyTo.remove(SATURDAY);
                }
            }
        });
        binding.checkboxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(SUNDAY);
                }else {
                    applyTo.remove(SUNDAY);
                }
            }
        });

        binding.checkboxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(MONDAY);
                }else {
                    applyTo.remove(MONDAY);
                }
            }
        });

        binding.checkboxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(TUESDAY);
                }else {
                    applyTo.remove(TUESDAY);
                }
            }
        });

        binding.checkboxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(WEDNESDAY);
                }else {
                    applyTo.remove(WEDNESDAY);
                }
            }
        });

        binding.checkboxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(THURSDAY);
                }else {
                    applyTo.remove(THURSDAY);
                }
            }
        });

        binding.checkboxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    applyTo.add(FRIDAY);
                }else {
                    applyTo.remove(FRIDAY);
                }
            }
        });
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



    public interface GettingPassedData{
        void passedData(ArrayList<WorkingTime>w,ArrayList<String>s);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (GettingPassedData) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDataPassListener");
        }
    }

}