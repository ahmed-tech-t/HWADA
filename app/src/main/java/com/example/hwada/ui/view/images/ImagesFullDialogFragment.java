package com.example.hwada.ui.view.images;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.hwada.adapter.ImagesIndicatorAdapter;
import com.example.hwada.adapter.ImagesFullDialogAdapter;
import com.example.hwada.databinding.FragmentImagesFullDialogBinding;

import java.util.ArrayList;
import java.util.Objects;


public class ImagesFullDialogFragment extends DialogFragment implements ImagesIndicatorAdapter.OnItemListener , View.OnClickListener {

    FragmentImagesFullDialogBinding binding ;
    ArrayList<String> imagesUrl;
    ImagesIndicatorAdapter imagesIndicatorAdapter ;
    int currentPos =-1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImagesFullDialogBinding.inflate(inflater, container, false);
        binding.simCancelImagesFullDialog.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(true);
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(true);
        assert getArguments() != null;
        imagesUrl = getArguments().getStringArrayList("imagesUrl");
        currentPos = getArguments().getInt("pos");
        setVp2();
        setRecycler();
        vp2Listener();
        if(currentPos !=-1) binding.vp2ImagesFullDialogFragment.setCurrentItem(currentPos);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }
    private void setVp2(){
        ImagesFullDialogAdapter adapter = new ImagesFullDialogAdapter();
        adapter.setList(imagesUrl,getContext());
        binding.vp2ImagesFullDialogFragment.setAdapter(adapter);
    }
    private void setRecycler() {
        imagesIndicatorAdapter = new ImagesIndicatorAdapter();
        imagesIndicatorAdapter.setList(imagesUrl,getContext(),this);
        binding.recyclerImagesFullDialogFragment.setAdapter(imagesIndicatorAdapter);
        binding.recyclerImagesFullDialogFragment.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerImagesFullDialogFragment.setHasFixedSize(true);
        binding.recyclerImagesFullDialogFragment.setNestedScrollingEnabled(false);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerImagesFullDialogFragment);
    }

    private void vp2Listener(){
        binding.vp2ImagesFullDialogFragment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                imagesIndicatorAdapter.setSelectedPosition(position);
            }
        });
    }

    @Override
    public void getItemPosition(int position) {
        binding.recyclerImagesFullDialogFragment.scrollToPosition(position);
        binding.vp2ImagesFullDialogFragment.setCurrentItem(position);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == binding.simCancelImagesFullDialog.getId()){
            dismiss();
        }
    }
}