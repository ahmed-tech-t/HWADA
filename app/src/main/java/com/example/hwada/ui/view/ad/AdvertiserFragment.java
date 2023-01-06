package com.example.hwada.ui.view.ad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.ImageSliderAdapter;
import com.example.hwada.adapter.ReviewAdapter;
import com.example.hwada.databinding.FragmentAdvertiserBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class AdvertiserFragment extends BottomSheetDialogFragment implements View.OnClickListener , ReviewAdapter.OnItemListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;

    User user;
    Ad ad;
    private static final String TAG = "AdvertiserFragment";
    ReviewAdapter adapter;

    FragmentAdvertiserBinding binding;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdvertiserBinding.inflate(inflater, container, false);
        binding.arrowAdvertiser.setOnClickListener(this);
        binding.addCommentTv.setOnClickListener(this);
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
                if(newState==BottomSheetBehavior.STATE_DRAGGING||newState==BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if(newState==BottomSheetBehavior.STATE_HIDDEN) dismiss();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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
        ad = getArguments().getParcelable("ad");
        setReviewsToRecycler();
        setToSlider();
        binding.itemDescription.setText(ad.getDescription());

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.arrowAdvertiser.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.addCommentTv.getId()){
            callReviewBottomSheet();
        }
    }

    public void setReviewsToRecycler() {
        adapter = new ReviewAdapter();
        try {
            binding.reviewRecycler.setAdapter(adapter);
            if (ad.getAdReviews()==null) ad.initAdReviewsList();
            adapter.setList(tempReviewLIst(),this);
            binding.reviewRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void getClickedUserFromComments(int position) {

    }

    private  ArrayList<AdReview> tempReviewLIst(){
        ArrayList<AdReview> adReviews = new ArrayList<>();
        String body = "this comment for test";
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));
        adReviews.add(new AdReview("date","authorId","authorName","authorImage",4,body));


        return adReviews;
    }
    private void callReviewBottomSheet(){
        ReviewFragment fragment = new ReviewFragment();
        Bundle bundle =new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("ad",ad);
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(),fragment.getTag());
    }

    private void setToSlider(){
        ImageSliderAdapter adapter = new ImageSliderAdapter(getActivity(), ad.getImagesUrl());
        binding.vp2AdvertiserFragment.setAdapter(adapter);
        binding.circleIndicator.setViewPager(binding.vp2AdvertiserFragment);
        adapter.registerAdapterDataObserver(binding.circleIndicator.getAdapterDataObserver());

    }
}