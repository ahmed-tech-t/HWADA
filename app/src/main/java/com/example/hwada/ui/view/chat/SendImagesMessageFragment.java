package com.example.hwada.ui.view.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.hwada.Model.Message;
import com.example.hwada.R;
import com.example.hwada.adapter.ImagesIndicatorAdapter;
import com.example.hwada.adapter.ImagesFullDialogAdapter;
import com.example.hwada.adapter.MessagesImageWithCaptionAdapter;
import com.example.hwada.databinding.FragmentSendImagesMessageBinding;

import java.util.ArrayList;

public class SendImagesMessageFragment extends DialogFragment implements MessagesImageWithCaptionAdapter.OnItemListener , View.OnClickListener , ImagesIndicatorAdapter.OnItemListener {


    FragmentSendImagesMessageBinding binding ;
    ArrayList<Message> messages;
    ArrayList<String> imagesUri;
    private ArrayList<String> textList;
    SendMessage mListener ;
    String receiverName ;
    ImagesIndicatorAdapter imagesIndicatorAdapter;
    MessagesImageWithCaptionAdapter messagesImageWithCaptionAdapter;

    private static final String TAG = "SendImagesMessageFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSendImagesMessageBinding.inflate(inflater, container, false);

        setListeners();
        return binding.getRoot();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getArguments() != null;
        messages = getArguments().getParcelableArrayList("messages");
        imagesUri = getArguments().getStringArrayList("imagesUri");
        receiverName =  getArguments().getString("receiverName");
        binding.receiverNameSendImagesMessageFragment.setText(receiverName);
        setVp2();
        setRecycler();
        vp2Listener();
        setTextListener();
    }

    private void setVp2(){
        messagesImageWithCaptionAdapter = new MessagesImageWithCaptionAdapter();
        messagesImageWithCaptionAdapter.setList(imagesUri,getContext(),this);
        binding.vp2SendImagesMessagesFragment.setAdapter(messagesImageWithCaptionAdapter);
    }

    private void setRecycler() {
        imagesIndicatorAdapter = new ImagesIndicatorAdapter();
        imagesIndicatorAdapter.setList(imagesUri,getContext(),this);
        binding.recyclerSendImagesMessagesFragment.setAdapter(imagesIndicatorAdapter);
        binding.recyclerSendImagesMessagesFragment.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerSendImagesMessagesFragment);
    }
    private void setListeners(){
        binding.simSendMessageImageFragment.setOnClickListener(this);
        binding.deleteSendImagesMessagesFragment.setOnClickListener(this);
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View v) {
        if(v.getId() == binding.simSendMessageImageFragment.getId()){
            mListener.getSendMessage(messages);
            dismiss();
        }else if(v.getId() == binding.deleteSendImagesMessagesFragment.getId()){
            int pos = binding.vp2SendImagesMessagesFragment.getCurrentItem();
            messages.remove(pos);
            messagesImageWithCaptionAdapter.removeOneItem(pos);
            imagesIndicatorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialogFragmentTheme);
    }

    @Override
    public void getDeletedItemPosition(int pos) {

    }

    @Override
    public void getItemPosition(int position) {
        binding.recyclerSendImagesMessagesFragment.scrollToPosition(position);
        binding.vp2SendImagesMessagesFragment.setCurrentItem(position);
    }

    private void vp2Listener(){
        binding.vp2SendImagesMessagesFragment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //hide delete button
                if(messages.size()<=1) binding.llDeleteSendImagesMessagesFragment.setVisibility(View.GONE);

                if(messages.get(position).getBody()!=null) binding.etBodyImageWithCaptionHolder.setText(messages.get(position).getBody());
                else binding.etBodyImageWithCaptionHolder.setText("");
               imagesIndicatorAdapter.setSelectedPosition(position);
            }
        });
    }

    private void setTextListener(){
        binding.etBodyImageWithCaptionHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String body = s.toString().trim();
                messages.get(binding.vp2SendImagesMessagesFragment.getCurrentItem()).setBody(body);
            }
        });
    }


    public interface SendMessage {
        void getSendMessage(ArrayList<Message> messages);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDataPassListener");
        }
    }
}