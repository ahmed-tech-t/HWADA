package com.example.hwada.ui.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.WorkingTimeEditDaysAdapter;
import com.example.hwada.databinding.FragmentWorkTimePreviewBinding;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class WorkTimePreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener , WorkTimeEditFragment.GettingPassedData , WorkingTimeEditDaysAdapter.OnItemListener  {

    BottomSheetBehavior bottomSheetBehavior ;
    BottomSheetDialog dialog ;
    Dialog saveDialog;
    User user;
    String SATURDAY = "saturday" ,SUNDAY ="sunday" ,MONDAY ="monday",TUESDAY ="tuesday"
            ,WEDNESDAY="wednesday",THURSDAY="thursday",FRIDAY="friday";
    
    WorkingTimeEditDaysAdapter adapter ;
    String DAY_TAG ;
    private static final String TAG = "WorkTimePreviewFragment";
    ArrayList<Boolean> switches = new ArrayList<>(Arrays.asList(false,false,false,false,false,false,false));
    Ad newAd ;
    AdsViewModel adsViewModel = AdsViewModel.getInstance();
    FragmentWorkTimePreviewBinding binding ;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding = FragmentWorkTimePreviewBinding.inflate(inflater, container, false);
        newAd = new Ad();

        binding.arrowWorkTime.setOnClickListener(this);
        binding.saveButtonAddNewAd.setOnClickListener(this);
        setWorkingTimeMainAdapter();
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
        newAd = getArguments().getParcelable("ad");
        user = getArguments().getParcelable("user");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.arrowWorkTime.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (v.getId() ==binding.saveButtonAddNewAd.getId()){
            if(dataValidated()){
                newAd.setDate(getCurrentDate());
                adsViewModel.addNewAd(newAd);
                goToMainActivity();
            }
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


    private void setWorkingTimeMainAdapter(){
        adapter = new WorkingTimeEditDaysAdapter();
        try {
            binding.recyclerWorkingTimePreview.setAdapter(adapter);
            adapter.setList(daysListHeader(),DAY_TAG,getContext(),this);
            binding.recyclerWorkingTimePreview.setLayoutManager(new LinearLayoutManager(getActivity()));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<String> daysListHeader(){
        ArrayList<String> temp =new ArrayList<>();
        temp.add(getString(R.string.saturday));
        temp.add(getString(R.string.sunday));
        temp.add(getString(R.string.monday));
        temp.add(getString(R.string.tuesday));
        temp.add(getString(R.string.wednesday));
        temp.add(getString(R.string.thursday));
        temp.add(getString(R.string.friday));
        return temp;
    }
    private ArrayList<String> daysListTag(){
        ArrayList<String> temp =new ArrayList<>();
        temp.add(SATURDAY);
        temp.add(SUNDAY);
        temp.add(MONDAY);
        temp.add(TUESDAY);
        temp.add(WEDNESDAY);
        temp.add(THURSDAY);
        temp.add(FRIDAY);
        return temp;
    }

    @Override
    public void passedData(ArrayList<WorkingTime> list, ArrayList<String> applyToList) {
        for (int i = 0 ; i<applyToList.size();i++){
            int pos = daysListTag().indexOf(applyToList.get(i));
            newAd.getDaysSchedule().set(pos,list);
            adapter.updateRecycler(list,pos);
        }
    }

    @Override
    public void rightButtonClicked(int position,String day) {
        callBottomSheet(new WorkTimeEditFragment(),newAd.getDaysSchedule().get(position),day);
    }

    @Override
    public void switchClicked(boolean isChecked, int pos) {
        if(!isChecked){
            newAd.getDaysSchedule().remove(pos);
            switches.set(pos,false);
        }else switches.set(pos,true);

    }


    private boolean dataValidated(){
        for (int i = 0; i < switches.size(); i++) {
            if(switches.get(i) == true ){
                if(newAd.getDaysSchedule().get(i).size()==0){
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
        intent.putExtra("user", user);
        startActivity(intent);
        getActivity().finish();
    }
    public void setSavingDialog() {
        if (saveDialog != null && saveDialog.isShowing()) return;
        saveDialog = new Dialog(getContext());
        saveDialog.setContentView(R.layout.dialog_adding_new_add);
        Window window = saveDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        saveDialog.setCanceledOnTouchOutside(false);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.setCancelable(false);
        saveDialog.show();
    }
    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy , h:mm a");
        return  sdf.format(date);
    }

}