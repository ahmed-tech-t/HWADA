package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.Model.User;
import com.example.hwada.repository.ChatRepo;

import java.util.ArrayList;

public class ChatViewModel extends AndroidViewModel {

    ChatRepo repo ;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repo = new ChatRepo();
    }


    public LiveData<Chat> addNewChat(String fromUserId , Chat chat){
        return repo.addNewChat(fromUserId,chat);
    }

    public LiveData<ArrayList<Chat>> chatListener(){
        return repo.chatListMutableLiveData;
    }

    public void startChatListener(String userId){
        repo.startChatListener(userId);
    }
    public void removeChatListener(){
        repo.removeChatListener();
    }
    public void deleteChat(String userId,String chatId){
        repo.deleteChat(userId,chatId);
    }
}
