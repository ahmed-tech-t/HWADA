package com.example.hwada.ui.view.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentAccountBinding;
import com.example.hwada.ui.MyAdsActivity;
import com.example.hwada.ui.auth.SplashActivity;
import com.example.hwada.ui.view.EditUserFragment;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.viewmodel.AuthViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class AccountFragment extends Fragment implements View.OnClickListener {

    User user;
    UserViewModel userViewModel;
    FragmentAccountBinding binding;
    AuthViewModel authViewModel;
    private static final String TAG = "AccountFragment";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        binding.tvEditUserProfileAccountFragment.setOnClickListener(this);
        binding.userImageAccountFragment.setOnClickListener(this);
        binding.tvLogout.setOnClickListener(this);
        binding.tvMyAds.setOnClickListener(this);
        setArrowDirections();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.tvEditUserProfileAccountFragment.getId()) {
            callBottomSheet(new EditUserFragment());
        } else if (v.getId() == binding.userImageAccountFragment.getId()) {
          callImageDialog();
        } else if (v.getId() == binding.tvMyAds.getId()) {
            Log.e(TAG, "onClick: ");
            callMyAdsActivity();
        } else if (v.getId() == binding.tvLogout.getId()) {
            authViewModel.logout();
            goToSplashActivity();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        authViewModel = ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
        userViewModel = UserViewModel.getInstance();
        setDataToFields();
        setUserListener();
    }


    private void setDataToFields() {
        binding.userNameAccountFragment.setText(user.getUsername());
        if (user.getImage() != null) {
            Picasso.get()
                    .load(user.getImage())
                    .into(binding.userImageAccountFragment);
        }
    }



    public void callBottomSheet(BottomSheetDialogFragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }
    public void callImageDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("image", user.getImage());
        ImageMiniDialogFragment fragment = new ImageMiniDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }


    private void goToSplashActivity() {
        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }



    public void callMyAdsActivity() {
        try {
            Intent intent = new Intent(getContext(), MyAdsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserListener(){
        userViewModel.userListener(user.getUId()).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User updatedUser) {
                user.updateUser(updatedUser);
                setDataToFields();
            }
        });
    }

    private void setArrowDirections() {

        Locale locale = Resources.getSystem().getConfiguration().locale;
        if (locale.getLanguage().equals("ar")) {
            Log.e(TAG, "setArrowDirections: " );
            binding.tvMyAds.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, R.drawable.my_ads_icon, 0);
            binding.joinMemberShipAccountFragment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, R.drawable.blue_membership_icon, 0);
            binding.helpSupportAccountFragment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, R.drawable.help_support_icon, 0);
            binding.cancelSubscriptionAccountFragment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_left, 0, R.drawable.cancel_sub_icon, 0);

        }else{
            binding.tvMyAds.setCompoundDrawablesWithIntrinsicBounds( R.drawable.my_ads_icon, 0,R.drawable.arrow_right, 0);
            binding.joinMemberShipAccountFragment.setCompoundDrawablesWithIntrinsicBounds( R.drawable.blue_membership_icon, 0,R.drawable.arrow_right, 0);
            binding.helpSupportAccountFragment.setCompoundDrawablesWithIntrinsicBounds( R.drawable.help_support_icon, 0,R.drawable.arrow_right, 0);
            binding.cancelSubscriptionAccountFragment.setCompoundDrawablesWithIntrinsicBounds( R.drawable.cancel_sub_icon, 0,R.drawable.arrow_right, 0);
    }
}
}