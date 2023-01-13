package com.example.hwada.ui.view.main;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentAccountBinding;
import com.example.hwada.ui.auth.SignUpOrLoginIn;
import com.example.hwada.ui.auth.SplashActivity;
import com.example.hwada.ui.view.EditUserFragment;
import com.example.hwada.ui.view.ImageFragment;
import com.example.hwada.viewmodel.AuthViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AccountFragment extends Fragment implements View.OnClickListener , EditUserFragment.PassedData {

   User user ;
   UserViewModel userViewModel ;
   FragmentAccountBinding binding ;
   AuthViewModel authViewModel ;
    private static final String TAG = "AccountFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentAccountBinding.inflate(inflater, container, false);
        binding.tvEditUserProfileAccountFragment.setOnClickListener(this);
        binding.userImageAccountFragment.setOnClickListener(this);
        binding.tvLogout.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
       if(v.getId() == binding.tvEditUserProfileAccountFragment.getId()){
            callBottomSheet(new EditUserFragment());
        }else if (v.getId() == binding.userImageAccountFragment.getId()){
            callBottomSheet(new ImageFragment());
        }else if(v.getId()== binding.tvMyAds.getId()){

        }else if(v.getId() == binding.tvLogout.getId()){
            authViewModel.logout();
           goToSplashActivity();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        authViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
        setDataToFields();
        setUserObserver();
    }


    private void setDataToFields(){
        binding.userNameAccountFragment.setText(user.getUsername());
        if(user.getImage()!=null){
            Picasso.get()
                    .load(user.getImage())
                    .into(binding.userImageAccountFragment);
        }
    }

    private void setUserObserver(){
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        ViewModelProviders.of(getActivity()).get(UserViewModel.class).getUser().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User u) {
                user = u;
                Log.e(TAG, "onChanged: " );
                setDataToFields();
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
    private void goToSplashActivity() {
        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void getPassedData(User u) {
        user.setUsername(u.getUsername());
        user.setGender(u.getGender());
        user.setAboutYou(u.getAboutYou());
        user.setPhone(u.getPhone());
        setDataToFields();
    }
}