package com.example.hwada.ui.view;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Toast;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentEditUserBinding;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class EditUserFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    FragmentEditUserBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    User user;
    User tempUser ;
    App app ;
    UserViewModel userViewModel ;
    int PICK_IMAGE = 4 ;
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
        assert getArguments() != null;
        user = getArguments().getParcelable("user");
        setDataToFields();
        spinnerListener();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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

    private void setDataToFields(){
        binding.userNameEditUserFragment.setText(user.getUsername());
        binding.aboutUEditUserFragment.setText(user.getAboutYou());
        binding.phoneEditUserFragment.setText(user.getPhone());

        setDataToSpinner(user.getGender());
        if(user.getImage()!=null){
            Picasso.get()
                    .load(user.getImage())
                    .into(binding.userImageEditUserFragment);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.arrowEditUser.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.userImageEditUserFragment.getId()){
            if(app.checkStoragePermissions()) pickImageFromGallery();
            else app.requestStoragePermissions(getContext());
        }else if(v.getId() == binding.saveButtonAddNewAd.getId()){
            if(fieldsNotEmpty()){
                String phone = binding.phoneEditUserFragment.getText().toString();
                String userName = binding.userNameEditUserFragment.getText().toString();
                String aboutYou = binding.aboutUEditUserFragment.getText().toString();
                app.hideKeyboardFrom(getContext(), v);
                tempUser.setUId(user.getUId());
                tempUser.setPhone(phone.trim());
                tempUser.setUsername(userName.trim());
                tempUser.setAboutYou(aboutYou.trim());
                updateUser();
            }
        }
    }

    private void updateUser(){
        userViewModel.updateUser(tempUser);
        showToast(getString(R.string.updateProfileMessage));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // get the selected image URI
            Uri uri = data.getData();
            Picasso.get().load(uri).into(binding.userImageEditUserFragment);
            tempUser.setImage(uri.toString());
        }
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
       } else if(binding.aboutUEditUserFragment.getText().length()<10){
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

    private Toast mCurrentToast;
    public void showToast(String message) {
        if (mCurrentToast == null) {
            mCurrentToast = Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT);
            mCurrentToast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mCurrentToast.addCallback(new Toast.Callback() {
                    @Override
                    public void onToastShown() {
                        super.onToastShown();
                    }

                    @Override
                    public void onToastHidden() {
                        super.onToastHidden();
                        mCurrentToast = null;
                    }
                });
            }
        }
    }

}