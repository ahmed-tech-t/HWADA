package com.example.hwada.ui.view.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
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
import com.example.hwada.ui.view.EditUserFragment;
import com.example.hwada.ui.view.images.ImageMiniDialogFragment;
import com.example.hwada.viewmodel.ChatViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.internal.Sleeper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatFragment extends Fragment implements View.OnClickListener, ChatAdapter.OnItemListener {

    FragmentChatBinding binding;
    ChatViewModel chatViewModel;
    User user;
    ArrayList<Chat> chatList;
    ChatAdapter adapter;
    ArrayList<Chat> selectedList;


    private static final String TAG = "ChatFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        chatList = new ArrayList<>();
        selectedList = new ArrayList<>();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        user = getArguments().getParcelable(getString(R.string.userVal));

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        binding.deleteChatFragment.setOnClickListener(this);
        if (getActivity() != null) {
            adapter = new ChatAdapter(getContext());
            setRecycler();
            chatListener();
        }
    }

    private void setRecycler() {
        adapter.setList(user.getUId(), chatList, this);
        binding.recyclerChatFragment.setAdapter(adapter);
        binding.recyclerChatFragment.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void getItemPosition(int position) {
        callChatActivity(position);
    }

    @Override
    public void pressedImagePosition(int pos) {
        callImageDialog(pos);
    }

    @Override
    public void getSelectedChatsToDelete(ArrayList<Chat> chats) {
        selectedList = chats;
        if (!chats.isEmpty()) {
            binding.arrowImage.setVisibility(View.GONE);
            binding.deleteChatFragment.setVisibility(View.VISIBLE);
        } else {
            binding.arrowImage.setVisibility(View.VISIBLE);
            binding.deleteChatFragment.setVisibility(View.GONE);
        }
    }

    public void callImageDialog(int pos) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.imageVal), chatList.get(pos).getAd().getImagesUrl().get(0));
        ImageMiniDialogFragment fragment = new ImageMiniDialogFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.show(fragmentManager, fragment.getTag());
    }

    public void callChatActivity(int pos) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(getString(R.string.userVal), user);
        intent.putExtra(getString(R.string.chatVal), chatList.get(pos));
        startActivity(intent);
    }

    private void chatListener() {
        chatViewModel.startChatListener(user.getUId());
        chatViewModel.chatListener().observe(requireActivity(), chats -> {
            binding.loadingChatFragment.setVisibility(View.GONE);

            if (chats.isEmpty()) {
                adapter.clearList();
                binding.recyclerChatFragment.setBackgroundResource(R.drawable.empty_page);
                Log.d(TAG, "chatListener: empty");
            } else {
                binding.recyclerChatFragment.setBackgroundResource(R.color.white);
                Log.d(TAG, "chatListener: not empty");
                chats = rearrangeList(chats);
                for (Chat chat : chats) {
                    int pos = chatList.indexOf(chat);
                    if (pos == -1) {
                        if (chat.getLastMessage() != null) {
                            adapter.addItem(chat);
                        }
                    } else {
                        if (chatList.get(pos).getLastMessage().isNewMessage(chat.getLastMessage())) {
                            adapter.updateChatWithNewMessage(chat, pos);
                        } else {
                            adapter.updateChat(chat, pos);
                        }
                    }
                }
            }
        });
    }


    private ArrayList<Chat> rearrangeList(ArrayList<Chat> chats) {
        return chats.stream().sorted(Comparator.comparing(chat -> chat.getLastMessage().getTimeStamp())).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.deleteChatFragment.getId()) {
            showDialog(getString(R.string.warning), getString(R.string.deleteChatsDialogBody));
        }
    }

    private void showDialog(String title, String body) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteSelectedChats())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                })
                .show();
    }

    private void deleteSelectedChats() {
        chatViewModel.removeChatListener();
        for (Chat chat : selectedList) {
            Log.d(TAG+" ChatTest", "deleteSelectedChats: "+chat.getId());
            adapter.removeChat(chat);
            chatViewModel.deleteChat(user.getUId(), chat.getId());
        }
        new Handler().postDelayed(() -> {
            chatViewModel.startChatListener(user.getUId());
        }, 2000);
    }

}