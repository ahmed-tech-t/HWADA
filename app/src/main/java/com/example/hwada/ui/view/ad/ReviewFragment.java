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
import com.example.hwada.databinding.FragmentReviewBinding;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.viewmodel.AdsViewModel;
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

    Dialog saveDialog ;
    private GettingPassedData mListener;

    AdsViewModel adsViewModel;
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


    private void showKeyboard(){
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
                adReview.setAuthorId(user.getuId());
                adReview.setAuthorName(user.getUsername());
                adReview.setAuthorImage(user.getImage());
                adReview.setDate(getCurrentDate());
                binding.reviewSubmitIm.setVisibility(View.GONE);
                binding.reviewEt.setFocusable(false);
                binding.reviewEt.setClickable(false);
                binding.reviewRating.setClickable(false);
                binding.reviewRating.setFocusable(false);
                setSavingDialog();
                saveReview();
                //TODO send it to database
                hideKeyboard();
            }
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
        user = getArguments().getParcelable("user");
        ad = getArguments().getParcelable("ad");
        Glide.with(getActivity()).load(user.getImage()).into(binding.userImageReview);

        adsViewModel = ViewModelProviders.of(this).get(AdsViewModel.class);
        adReview = new AdReview();
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy , h:mm a");
        return  sdf.format(date);
    }
    private void saveReview(){
        adsViewModel.addReview(ad ,adReview);
        adsViewModel.liveDataAddReview.observe(this, new Observer<AdReview>() {
            @Override
            public void onChanged(AdReview review) {
                if(saveDialog.isShowing())saveDialog.dismiss();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mListener.getAd(review);
            }
        });
    }

    public interface GettingPassedData{
        void getAd(AdReview review);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ReviewFragment.GettingPassedData) getParentFragment();
    }

    public void setSavingDialog() {
        if (saveDialog != null && saveDialog.isShowing()) return;
        saveDialog = new Dialog(getContext());
        saveDialog.setContentView(R.layout.dialog_adding_new_review);
        Window window = saveDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        saveDialog.setCanceledOnTouchOutside(false);
        saveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        saveDialog.setCancelable(false);
        saveDialog.show();
    }
}