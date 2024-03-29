package com.example.hwada.ui.view.ad.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.Report;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsGridAdapter;
import com.example.hwada.adapter.ReviewAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentAdReviewsBinding;
import com.example.hwada.ui.UserProfileActivity;
import com.example.hwada.ui.view.ad.ReviewFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.ReportViewModel;
import com.example.hwada.viewmodel.ReviewViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Objects;


public class AdReviewsFragment extends Fragment implements ReviewAdapter.OnItemListener, View.OnClickListener,ReviewFragment.GettingPassedData  {

    FragmentAdReviewsBinding binding ;
    ReviewAdapter adapter;
    Ad ad ;
    User user ;
    App app ;

    UserViewModel userViewModel ;
    ReportViewModel reportViewModel ;
    ReviewViewModel reviewViewModel ;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        ad = getArguments().getParcelable("ad");
        user =getArguments().getParcelable("user");
        app = (App) getContext().getApplicationContext();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.shimmerReviews.startShimmer();

        reportViewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        reviewViewModel =ViewModelProviders.of(this).get(ReviewViewModel.class);
        getALlReviews();
        setViewIfAdIsOpen();
    }

    private void setViewIfAdIsOpen() {
        String[] days = getResources().getStringArray(R.array.daysVal);
        boolean adIsOpen = ad.isOpen(app.getTime(),days,app.getDayIndex());

        if(!adIsOpen ||!ad.isActive()){
            binding.constraintLayoutAdReviewsFragment.setAlpha(0.5f);
        }else binding.constraintLayoutAdReviewsFragment.setAlpha(1f);
    }
    @Override
    public void onResume() {
        super.onResume();
       // initReviewBox();
     }

    public void setRecycler() {
        closeShimmer();
        binding.adRatingAdReviewsFragment.setRating(ad.getRating());

        adapter = new ReviewAdapter(getActivity());
        try {
            binding.reviewRecyclerAdReviewFragment.setAdapter(adapter);
            if (ad.getAdReviews()==null) ad.initAdReviewsList();
            adapter.setList(ad.getAdReviews(),this);
            if(!ad.getAdReviews().isEmpty())binding.reviewRecyclerAdReviewFragment.setVisibility(View.VISIBLE);
            binding.reviewRecyclerAdReviewFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.reviewRecyclerAdReviewFragment.setHasFixedSize(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getALlReviews(){
        if (getActivity()==null) Log.d(TAG, "getALlReviews: null");
        reviewViewModel.getAdReviews(ad).observe(getActivity(), new Observer<ArrayList<AdReview>>() {
            @Override
            public void onChanged(ArrayList<AdReview> adReviews) {
                ad.setAdReviews(adReviews);
                if(getActivity()!=null){
                    setRecycler();
                    initReviewBox();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initReviewBox(){
        binding.tvReviewsAdReviewsFragment.setText(getString(R.string.reviews)+"("+ad.getAdReviews().size()+")");

        Glide.with(getActivity()).load(user.getImage()).into(binding.userImageAdReview);
        if(userMadeReview()){
            binding.linearLayoutCommentBox.setVisibility(View.GONE);
        }else binding.linearLayoutCommentBox.setVisibility(View.VISIBLE);
    }
    private void callReviewBottomSheet(String tag){
        ReviewFragment fragment = new ReviewFragment();
        Bundle bundle =new Bundle();
        bundle.putParcelable("user",user);
        bundle.putParcelable("ad",ad);
        bundle.putString("tag",tag);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),fragment.getTag());
    }

    private void callReviewBottomSheet(String tag,int pos){
        ReviewFragment fragment = new ReviewFragment();
        Bundle bundle =new Bundle();
        bundle.putParcelable("user", user);
        bundle.putParcelable("ad", ad);
        bundle.putString("tag", tag);
        bundle.putInt("pos", pos);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),fragment.getTag());
    }
    @Override
    public void getClickedUserFromComments(int position) {
        userViewModel.getUserById(ad.getAdReviews().get(position).getAuthorId()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User u) {
                callUserProfileActivity(u);
            }
        });
    }

    @Override
    public void menuFromComments(int position,ImageView menu) {
        popupMenu(position,menu);
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
                callReviewBottomSheet("add");
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


    private boolean userMadeReview(){
        for (AdReview a: ad.getAdReviews()) {
            if(a.getAuthorId().equals(user.getUId())){
                return true;
            }
        }
        return false;
    }
    private void popupMenu(int pos,ImageView imageView){
        PopupMenu popup = new PopupMenu(getActivity(),imageView);
        //Inflating the Popup using xml file

        if(isUserReview(pos))popup.getMenuInflater().inflate(R.menu.edit_review_menu, popup.getMenu());
        else popup.getMenuInflater().inflate(R.menu.report_review_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.delete_review_menu){
                    deleteReview(ad.getAdReviews().get(pos),pos);
                }else if((item.getItemId()==R.id.edit_review_menu)){
                    callReviewBottomSheet("edit",pos);
                }else if((item.getItemId()==R.id.report_review_menu)){
                    Report r = new Report(ad.getAdReviews().get(pos).getId(), DbHandler.Review,ad.getCategory(),ad.getSubCategory(),ad.getSubSubCategory());
                    reportViewModel.addReport(r);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    private boolean isUserReview(int pos){
        return user.getUId().equals(ad.getAdReviews().get(pos).getAuthorId());
    }

    private void deleteReview(AdReview review,int pos){
        reviewViewModel.deleteReview(user,ad,review).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if(success){
                    adapter.removeOneItem(pos);
                    binding.linearLayoutCommentBox.setVisibility(View.VISIBLE);
                    binding.tvReviewsAdReviewsFragment.setText(getString(R.string.reviews)+"("+ad.getAdReviews().size()+")");
                    if(adapter.getItemCount()==0) binding.reviewRecyclerAdReviewFragment.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void getAddedReview(AdReview review) {
        binding.linearLayoutCommentBox.setVisibility(View.GONE);
        binding.reviewRecyclerAdReviewFragment.setVisibility(View.VISIBLE);
        adapter.addItem(review);
        binding.tvReviewsAdReviewsFragment.setText(getString(R.string.reviews)+"("+ad.getAdReviews().size()+")");
    }

    @Override
    public void getUpdatedReview(AdReview review, int pos) {
        adapter.updateItem(review,pos);
    }

    private void closeShimmer(){
        binding.shimmerReviews.setVisibility(View.GONE);
        binding.shimmerReviews.stopShimmer();
    }

    private void callUserProfileActivity(User u){
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("receiver",u);
        startActivity(intent);
    }
}
