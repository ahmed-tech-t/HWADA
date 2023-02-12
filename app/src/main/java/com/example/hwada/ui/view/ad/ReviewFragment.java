package com.example.hwada.ui.view.ad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.MyReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.application.App;
import com.example.hwada.databinding.FragmentReviewBinding;
import com.example.hwada.repository.ReviewRepo;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.ReviewViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    Ad ad ;
    AdReview adReview ;
    User user;
    String tag;
    Dialog saveDialog ;
    private GettingPassedData mListener;
    int PASSED_POS ;
    App app;
    ReviewViewModel reviewViewModel ;

    FragmentReviewBinding binding;
    String TAG ="ReviewFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        binding.arrowReview.setOnClickListener(this);
        binding.reviewSubmitIm.setOnClickListener(this);

        app =  (App) getContext().getApplicationContext();
        return binding.getRoot();

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

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
        ratingBarListener();

    }

    private void setBottomSheet(View view){
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_DRAGGING){
                    hideKeyboard();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideKeyboard();
            }
        });
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


    private void hideKeyboard(){
        try {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        }catch (Exception e){
            e.printStackTrace();
        }
   }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.arrowReview.getId()){
            hideKeyboard();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == binding.reviewSubmitIm.getId()){
            if(adReview.getRating()>0){

                adReview.setBody(binding.reviewEt.getText().toString());
                if(tag =="add"){
                    adReview.setAuthorId(user.getUId());
                    adReview.setAuthorName(user.getUsername());
                    adReview.setAuthorImage(user.getImage());
                    adReview.setTimeStamp(app.getCurrentDate());
                }

                binding.reviewSubmitIm.setVisibility(View.GONE);
                binding.reviewEt.setFocusable(false);
                binding.reviewEt.setClickable(false);
                binding.reviewRating.setClickable(false);
                binding.reviewRating.setFocusable(false);

                if(tag == "add"){
                    saveReview();
                    setSavingDialog("add");
                }else if(tag == "edit"){
                    setSavingDialog("edit");
                    editReview();
                }
                hideKeyboard();
            }
        }
    }

    private void editReview() {
        reviewViewModel.editReview(ad,adReview).observe(this, new Observer<AdReview>() {
            @Override
            public void onChanged(AdReview review) {
                if(saveDialog.isShowing())saveDialog.dismiss();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.getUpdatedReview(review,PASSED_POS);
            }
        });
    }
    private void setDataToFields(){
        if(tag == "edit" && adReview != null ){
            binding.reviewRating.setRating(adReview.getRating());
            binding.reviewEt.setText(adReview.getBody());
        }
    }
    private void ratingBarListener(){
        binding.reviewRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                adReview.setRating(rating);
                if(rating > 0) {
                    binding.reviewSubmitIm.setVisibility(View.VISIBLE);
                }else binding.reviewSubmitIm.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tag = getArguments().getString("tag");
        user = getArguments().getParcelable("user");
        ad = getArguments().getParcelable("ad");

        if(tag == "add"){
            adReview = new AdReview();
        }else if(tag == "edit" ){
            PASSED_POS = getArguments().getInt("pos");
            adReview = ad.getAdReviews().get(PASSED_POS);
            setDataToFields();
        }

        Glide.with(getActivity()).load(user.getImage()).into(binding.userImageReview);
        reviewViewModel =ViewModelProviders.of(this).get(ReviewViewModel.class);
    }

    private void saveReview(){
        reviewViewModel.addReview(user,ad ,adReview);
        reviewViewModel.adReviewLiveData.observe(this, new Observer<AdReview>() {
            @Override
            public void onChanged(AdReview review) {
                if(saveDialog.isShowing())saveDialog.dismiss();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.getAddedReview(review);
            }
        });
    }

    public interface GettingPassedData{
        void getAddedReview(AdReview review );
        void getUpdatedReview(AdReview review ,int pos);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ReviewFragment.GettingPassedData) getParentFragment();
    }

    public void setSavingDialog(String tag) {
        if (saveDialog != null && saveDialog.isShowing()) return;
        saveDialog = new Dialog(getContext());
        if(tag=="add") saveDialog.setContentView(R.layout.dialog_adding_new_review);
        else if(tag =="edit")saveDialog.setContentView(R.layout.dialog_updating_review);
        Window window = saveDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        saveDialog.setCanceledOnTouchOutside(false);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.setCancelable(false);
        saveDialog.show();
    }
}