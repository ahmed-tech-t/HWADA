package com.example.hwada.ui.view.images;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.hwada.R;
import com.example.hwada.databinding.FragmentImageBinding;
import com.example.hwada.util.GlideImageLoader;
import com.example.hwada.util.ProgressDrawable;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;


public class ImageMiniDialogFragment extends DialogFragment implements View.OnClickListener  {

    FragmentImageBinding binding ;
    String imagePath;
    private static final String TAG = "ImageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        imagePath = getArguments().getString("image");
        /*Picasso.get()
                .load(imagePath)
                .placeholder(new ProgressDrawable(getContext()))
                .into(binding.userImageFragment);

        */

        RequestOptions options = new RequestOptions()
                .priority(Priority.HIGH);

        new GlideImageLoader(binding.userImageFragment,new ProgressBar(getContext())).load(imagePath,options);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }

    @Override
    public void onClick(View v) {

    }
}