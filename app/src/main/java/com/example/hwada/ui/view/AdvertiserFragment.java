package com.example.hwada.ui.view;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.AdsAdapter;
import com.example.hwada.adapter.ReviewAdapter;
import com.example.hwada.ui.AdsActivity;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class AdvertiserFragment extends BottomSheetDialogFragment implements View.OnClickListener , ReviewAdapter.OnItemListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    ImageView arrow;
    User user;
    Ad ad;
    TextView addComment;
    String TAG = "AdvertiserFragment";

    ReviewAdapter adapter;
    RecyclerView reviewRecycler;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_advertiser, container, false);
        arrow = v.findViewById(R.id.arrow_advertiser);
        reviewRecycler = v.findViewById(R.id.review_recycler);
        addComment = v.findViewById(R.id.addComment_tv);
        arrow.setOnClickListener(this);
        addComment.setOnClickListener(this);
        return v;
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
    }

    private void setBottomSheet(View view){
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        LinearLayout layout = dialog.findViewById(R.id.bottom_sheet_advertiser);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels-100);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == arrow.getId()){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == addComment.getId()){
            callReviewBottomSheet();
        }
    }

    public void setReviewsToRecycler() {
        adapter = new ReviewAdapter();
        try {
            reviewRecycler.setAdapter(adapter);
            if (ad.getAdReviews()==null) ad.initAdReviewsList();
            adapter.setList(tempReviewLIst(),this);
            reviewRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                    Log.e(TAG, "onKey: " );
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

}