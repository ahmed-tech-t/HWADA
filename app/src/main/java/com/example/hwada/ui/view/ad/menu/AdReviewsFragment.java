package com.example.hwada.ui.view.ad.menu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.ReviewAdapter;
import com.example.hwada.databinding.FragmentAdReviewsBinding;
import com.example.hwada.ui.view.ad.ReviewFragment;

import java.util.ArrayList;


public class AdReviewsFragment extends Fragment implements ReviewAdapter.OnItemListener, View.OnClickListener,ReviewFragment.GettingPassedData {

    FragmentAdReviewsBinding binding ;
    ReviewAdapter adapter;
    Ad ad ;
    User user ;
    private static final String TAG = "AdReviewsFragment";
    //debounce mechanism
    private static final long DEBOUNCE_DELAY_MILLIS = 500;
    private boolean debouncing = false;
    private Runnable debounceRunnable;
    private Handler debounceHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdReviewsBinding.inflate(inflater, container, false);
        binding.tvAddCommentAdReviewFragment.setOnClickListener(this);
        debounceHandler = new Handler();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ad = getArguments().getParcelable("ad");
        user =getArguments().getParcelable("user");
        if(userMadeReview()){
            binding.linearLayoutCommentBox.setVisibility(View.GONE);
        }else binding.linearLayoutCommentBox.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(user.getImage()).into(binding.userImageAdReview);

        binding.tvReviewsAdReviewsFragment.setText(getString(R.string.reviews)+"("+ad.getAdReviews().size()+")");
        setReviewsToRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(userMadeReview()){
            binding.linearLayoutCommentBox.setVisibility(View.GONE);
        }else binding.linearLayoutCommentBox.setVisibility(View.VISIBLE);
    }

    public void setReviewsToRecycler() {
        adapter = new ReviewAdapter();
        try {
            binding.reviewRecyclerAdReviewFragment.setAdapter(adapter);
            if (ad.getAdReviews()==null) ad.initAdReviewsList();
            adapter.setList(ad.getAdReviews(),getContext(),this);
            binding.reviewRecyclerAdReviewFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void callReviewBottomSheet(){
        ReviewFragment fragment = new ReviewFragment();
        Bundle bundle =new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("ad",ad);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),fragment.getTag());
    }
    @Override
    public void getClickedUserFromComments(int position) {

    }

    @Override
    public void menuFromComments(int position) {
        Log.e(TAG, "menuFromComments: " );
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.tvAddCommentAdReviewFragment.getId()){
            if (debouncing) {
                // Remove the previous runnable
                debounceHandler.removeCallbacks(debounceRunnable);
            } else {
                // This is the first click, so open the item
                debouncing = true;
                callReviewBottomSheet();
            }
            // Start a new timer
            debounceRunnable = () -> debouncing = false;
            debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY_MILLIS);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        debounceHandler.removeCallbacks(debounceRunnable);
    }

    @Override
    public void getAd(AdReview review) {
        binding.linearLayoutCommentBox.setVisibility(View.GONE);
        adapter.addItem(review);
        binding.tvReviewsAdReviewsFragment.setText(getString(R.string.reviews)+"("+ad.getAdReviews().size()+")");
        Log.e(TAG, "getAd: "+ad.getId() );
    }

    private boolean userMadeReview(){
        for (AdReview a: ad.getAdReviews()) {
            if(a.getAuthorId().equals(user.getuId())){
                return true;
            }
        }
        return false;
    }
}