package com.example.hwada.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentEditUserBinding;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class EditUserFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    FragmentEditUserBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    User user;
    User tempUser ;
    Dialog saveDialog;
    UserViewModel userViewModel ;
    PassedData mListener;
    private static final String TAG = "EditUserFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditUserBinding.inflate(inflater, container, false);
        binding.arrowEditUser.setOnClickListener(this);
        binding.userImageEditUserFragment.setOnClickListener(this);
        binding.saveButtonAddNewAd.setOnClickListener(this);
        tempUser = new User();
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
        user = getArguments().getParcelable("user");
        binding.userNameEditUserFragment.setText(user.getUsername());
        binding.aboutUEditUserFragment.setText(user.getAboutYou());
        setDataToSpinner(user.getGender());
        spinnerListener();
        binding.phoneEditUserFragment.setText(user.getPhone());
        if(user.getImage()!=null){
            Picasso.get()
                    .load(user.getImage())
                    .into(binding.userImageEditUserFragment);
        }
        userViewModel = UserViewModel.getInstance();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.arrowEditUser.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.userImageEditUserFragment.getId()){
           // callBottomSheet(new ImageFragment());
        }else if(v.getId() == binding.saveButtonAddNewAd.getId()){
            if(fieldsNotEmpty()){
                tempUser.setPhone(binding.phoneEditUserFragment.getText().toString());
                tempUser.setUsername(binding.userNameEditUserFragment.getText().toString());
                tempUser.setAboutYou(binding.aboutUEditUserFragment.getText().toString());
                setSavingDialog();
                updateUser();
            }
        }
    }

    private void updateUser(){
        userViewModel.updateUser(tempUser);
        userViewModel.updateUserLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User u) {
                user.setUsername(u.getUsername());
                user.setGender(u.getGender());
                user.setAboutYou(u.getAboutYou());
                user.setPhone(u.getPhone());

                mListener.getPassedData(user);
                if(saveDialog.isShowing()) saveDialog.dismiss();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment ){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager,fragment.getTag());
    }

    private  void spinnerListener(){
        binding.spinnerGenderEditUserFragment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                tempUser.setGender(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
   private void setDataToSpinner(String gender){
       if(gender != null){
           if(gender.equals("Female")){
               binding.spinnerGenderEditUserFragment.setSelection(1);
           }else binding.spinnerGenderEditUserFragment.setSelection(0);
       }else  binding.spinnerGenderEditUserFragment.setSelection(0);
   }

   private boolean fieldsNotEmpty(){
       if (TextUtils.isEmpty(binding.userNameEditUserFragment.getText().toString())){
           binding.userNameEditUserFragment.setError("Invalid Username");
       } else if(binding.aboutUEditUserFragment.getText().length()<40){
            binding.aboutUEditUserFragment.setError(getString(R.string.toShortWarning));
       }else  if (TextUtils.isEmpty(binding.phoneEditUserFragment.getText().toString())
               || !Patterns.PHONE.matcher(binding.phoneEditUserFragment.getText().toString()).matches()){
           binding.phoneEditUserFragment.setError("Invalid Phone");
       }

       if(binding.userNameEditUserFragment.getError()==null
            && binding.aboutUEditUserFragment.getError()==null
            && binding.phoneEditUserFragment.getError()==null){
            return true;
       }
       return false;
   }

    public void setSavingDialog() {
        if (saveDialog != null && saveDialog.isShowing()) return;
        saveDialog = new Dialog(getContext());
        saveDialog.setContentView(R.layout.dialog_saving_data_layout);
        Window window = saveDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        saveDialog.setCanceledOnTouchOutside(false);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.setCancelable(false);
        saveDialog.show();
    }

    public interface PassedData{
        public void getPassedData(User user);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PassedData) getParentFragment();
    }
}