package com.example.hwada.ui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AdvertiserFragment extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;

    RecyclerView commentRecycler;
    User user;

    AdsViewModel viewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_advertiser, container, false);
        commentRecycler = v.findViewById(R.id.commentRecycler);
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        LinearLayout layout = dialog.findViewById(R.id.bottom_sheet_advertiser);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels-100);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        viewModel = ViewModelProviders.of(getActivity()).get(AdsViewModel.class);

    }
}