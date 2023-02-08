package com.example.hwada.ui.view.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwada.Model.Chat;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.ChatAdapter;
import com.example.hwada.databinding.FragmentChatBinding;
import com.example.hwada.ui.ChatActivity;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatFragment extends Fragment implements ChatAdapter.OnItemListener {

    FragmentChatBinding binding ;
    ChatViewModel chatViewModel ;
    User user;
    ArrayList<Chat> chatList;
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
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        chatObserver();
    }

    private void setRecycler(){
        ChatAdapter adapter = new ChatAdapter();
        adapter.setList(chatList,getContext(),this);
        binding.recyclerChatFragment.setAdapter(adapter);
        binding.recyclerChatFragment.setLayoutManager( new LinearLayoutManager(getContext()));
    }

    private void chatObserver(){
        chatViewModel.getAllChats(user.getUId()).observe(getActivity(), new Observer<ArrayList<Chat>>() {
            @Override
            public void onChanged(ArrayList<Chat> chats) {
                Collections.sort(chats, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat o1, Chat o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                Collections.reverse(chats);
                chatList = chats;
                binding.loadingChatFragment.setVisibility(View.GONE);

                if (chatList.size()>0)binding.recyclerChatFragment.setBackgroundColor(Color.WHITE);
                else binding.recyclerChatFragment.setBackgroundResource(R.drawable.empty_page);
                setRecycler();
            }
        });
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
        intent.putExtra("ad",chatList.get(pos).getAd());
        startActivity(intent);
    }
}