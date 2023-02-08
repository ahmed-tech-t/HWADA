package com.example.hwada.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Chat;
import com.example.hwada.repository.ChatRepo;

import java.util.ArrayList;

public class ChatViewModel extends ViewModel {

    ChatRepo repo ;

    public ChatViewModel() {
         repo = new ChatRepo();
    }

    public LiveData<Boolean> addNewChat(String fromUserId , Chat chat){
        return repo.addNewChat(fromUserId,chat);
    }

    public LiveData<ArrayList<Chat>> getAllChats(String userId){
        return  repo.getALlChats(userId);
    }
}
