package com.example.hwada.ui.view.ad;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.AdReview;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    RatingBar ratingBar ;
    EditText review ;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog dialog ;
    ImageView arrow ;
    Ad ad ;
    CircleImageView submit_im ;
    AdReview adReview ;
    User user;
    String TAG ="ReviewFragment";
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        arrow = v.findViewById(R.id.arrow_review);
        submit_im = v.findViewById(R.id.review_submit_im);
        arrow.setOnClickListener(this);
        submit_im.setOnClickListener(this);
        review = v.findViewById(R.id.review_et);
        ratingBar =v.findViewById(R.id.review_rating);

        review.requestFocus();
        showKeyboard();
        return v;
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
        bottomSheetBehavior = BottomSheetBehavior.from((View)view.getParent());
        bottomSheetListener();
        ratingBarListener();
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
        if(v.getId()==arrow.getId()){
            hideKeyboard();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(v.getId() == submit_im.getId()){
            if(adReview.getRating()>0){
                adReview.setBody(review.getText().toString());
                adReview.setAuthorId(user.getuId());
                adReview.setAuthorName(user.getUsername());
                adReview.setAuthorImage(user.getImage());
                adReview.setDate(getCurrentDate());

                //TODO send it to database
                hideKeyboard();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    private void ratingBarListener(){
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                adReview.setRating(rating);
                if(rating > 0) {
                    submit_im.setVisibility(View.VISIBLE);
                }else submit_im.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getArguments().getParcelable("user");
        ad = getArguments().getParcelable("ad");

        adReview = new AdReview();
    }
    private String getCurrentDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh/mm/ss");
        return sdf.format(new Date());
    }

    private void bottomSheetListener(){
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                hideKeyboard();
            }
        });
    }
}