package com.example.hwada.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Message;
import com.example.hwada.repository.MessageRepo;

import java.util.ArrayList;

public class MessageViewModel extends AndroidViewModel {
     MessageRepo repo;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        repo = new MessageRepo(application);
    }


    public LiveData<Message>sendMessage(Message message, Ad ad){
         return repo.sendMessage(message,ad);
     }

     public LiveData<ArrayList<Message>>getAllMessages(String userId,String chatId){
         return repo.getAllMessages(userId,chatId);
     }

     public Message getMessageId(Message message,Ad ad){
         return repo.getMessageId(message , ad);
     }

    public MutableLiveData<ArrayList<Message>> messagesListener(String userId, String chatId){
        return repo.messagesListener(userId,chatId);
    }
    public void setMessagesStatusToSeen(Message message , String chatId){
        repo.setMessagesStatusToSeen(message,chatId);
    }
}
