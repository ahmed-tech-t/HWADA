package com.example.hwada.ui.view.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.ChatAdapter;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.FragmentChatBinding;
import com.example.hwada.ui.ChatActivity;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.viewmodel.ChatViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment implements ChatAdapter.OnItemListener {

    FragmentChatBinding binding ;
    ChatViewModel chatViewModel ;
    User user;
    ArrayList<Chat> chatList;
    private long latestTimestamp = 0;

    private static final String TAG = "ChatFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = getArguments().getParcelable("user");

        chatViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
                .get(ChatViewModel.class);



        chatListener();
    }

    private void setRecycler(){
        ChatAdapter adapter = new ChatAdapter(getContext());
        adapter.setList(user.getUId(),chatList,this);
        binding.recyclerChatFragment.setAdapter(adapter);
        if(chatList.size()==0)binding.recyclerChatFragment.setBackgroundResource(R.drawable.empty_page);
        binding.recyclerChatFragment.setLayoutManager( new LinearLayoutManager(getContext()));
    }


    @Override
    public void getItemPosition(int position) {
        callChatActivity(position);
    }

    @Override
    public void pressedImagePosition(int pos) {
        callImageDialog(pos);
    }

    public void callImageDialog(int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("image", chatList.get(pos).getAd().getImagesUrl().get(0));
        ImageMiniDialogFragment fragment = new ImageMiniDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

    public void callChatActivity(int pos){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("chat",chatList.get(pos));
        startActivity(intent);
    }


    private void chatListener(){
        chatViewModel.chatListener(user.getUId()).observe(getActivity(), new Observer<ArrayList<Chat>>() {
            @Override
            public void onChanged(ArrayList<Chat> chats) {
                binding.loadingChatFragment.setVisibility(View.GONE);
                chatList = chats;
                setRecycler();
            }
        });
    }



}