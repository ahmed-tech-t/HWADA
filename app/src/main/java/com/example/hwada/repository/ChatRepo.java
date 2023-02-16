package com.example.hwada.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.Model.User;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private FirebaseAuth auth ;
    private static final String TAG = "ChatRepo";

    Application application ;
    App app;
    public ChatRepo(Application application){
        this.application = application;
        app = (App) application.getApplicationContext();
    }
    public MutableLiveData<Chat> addNewChat(String senderId ,Chat chat){
        MutableLiveData <Chat> mutableLiveData = new MutableLiveData<>();
        String chatId = getChatId(senderId,chat.getReceiver().getUId(),chat.getAd());
        chat.setId(chatId);

        getChatRef(senderId).document(chatId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mutableLiveData.setValue(chat);
                    } else {

                        rootRef.runTransaction(new Transaction.Function<Object>() {
                            @Nullable
                            @Override
                            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                String receiverId = chat.getReceiver().getUId();
                                DocumentReference docRefSender = getChatRef(senderId).document(chatId);
                                DocumentReference docRefReceiver = getChatRef(chat.getReceiver().getUId()).document(chatId);

                                transaction.set(docRefSender,chat);
                                chat.getReceiver().setUId(senderId);
                                transaction.set(docRefReceiver,chat);
                                chat.getReceiver().setUId(receiverId);
                                return null;
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Object>() {
                            @Override
                            public void onComplete(@NonNull Task<Object> task) {
                                if(task.isSuccessful()){
                                    mutableLiveData.setValue(chat);
                                }
                            }
                        });
                    }
                } else {
                    // Handle error
                }
            }
        });
        return mutableLiveData;
    }
    
    public MutableLiveData<ArrayList<Chat>> getALlChats(String userId){
        MutableLiveData<ArrayList<Chat>> mutableLiveData = new MutableLiveData<>();
        getChatRef(userId)
                .orderBy("lastMessage.timeStamp", Query.Direction.DESCENDING)
                .get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<List<Chat>>>() {
                    @Override
                    public Task<List<Chat>> then(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Task<Chat>> chatTasks = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            return Tasks.forResult(new ArrayList<Chat>());
                        }
                        for (DocumentSnapshot document : task.getResult()) {
                            Chat chat = document.toObject(Chat.class);
                            Log.e(TAG, "then: chat" );
                            Task<DocumentSnapshot> userTask = userDocumentRef(chat.getReceiver().getUId()).get();
                            Task<DocumentSnapshot> adTask = getAdColRef(chat.getAd()).document(chat.getAd().getId()).get();
                            Task<Chat> chatTask = Tasks.whenAllSuccess(userTask, adTask).continueWith(new Continuation<List<Object>, Chat>() {
                                @Override
                                public Chat then(@NonNull Task<List<Object>> task) {
                                    List<Object> results = task.getResult();
                                    // Update the chat with the user information
                                    DocumentSnapshot userDoc = (DocumentSnapshot) results.get(0);
                                    if (userDoc.exists()) {
                                        chat.setReceiver(userDoc.toObject(User.class));
                                    }
                                    DocumentSnapshot adDoc = (DocumentSnapshot) results.get(1);
                                    if (adDoc.exists()) {
                                        chat.setAd(adDoc.toObject(Ad.class));
                                    }
                                    return chat;
                                }
                            });

                            chatTasks.add(chatTask);
                        }
                        return Tasks.whenAllSuccess(chatTasks);
                    }
                }).continueWith(new Continuation<List<Chat>, Object>() {
                    @Override
                    public Object then(@NonNull Task<List<Chat>> task) throws Exception {
                       mutableLiveData.setValue((ArrayList<Chat>) task.getResult());
                        return null;
                    }
                });
        return mutableLiveData;
    }

    private String getChatId(String senderId , String receiverId, Ad ad){
        if(ad.getAuthorId().equals(senderId)){
            return ad.getId()+receiverId;
        }
        return ad.getId()+senderId;
    }

    private CollectionReference getChatRef(String id){
        return rootRef.collection(DbHandler.userCollection).document(id).collection(DbHandler.chatCollection);
    }
    private DocumentReference userDocumentRef(String id){
        return rootRef.collection(DbHandler.userCollection).document(id);
    }
    public MutableLiveData<ArrayList<Chat>> chatListener(String userId){
        MutableLiveData<ArrayList<Chat>> mutableLiveData = new MutableLiveData<>();
        getChatRef(userId)
                .orderBy("lastMessage.timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        Log.e(TAG, "onEvent: start function" );
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            mutableLiveData.setValue(new ArrayList<>());
                            return;
                        }
                        if (value.isEmpty()) {
                            Log.e(TAG, "QuerySnapshot is empty.");
                            mutableLiveData.setValue(new ArrayList<>());
                            return;
                        }
                        ArrayList<Chat> chats = new ArrayList<>();
                        for (DocumentChange change : value.getDocumentChanges()) {
                            Chat chat = change.getDocument().toObject(Chat.class);
                            getAdColRef(chat.getAd()).document(chat.getAd().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        chat.setAd(task.getResult().toObject(Ad.class));
                                            userDocumentRef(chat.getReceiver().getUId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()){
                                                          chat.setReceiver(task.getResult().toObject(User.class));
                                                          chats.add(chat);
                                                          if(value.getDocumentChanges().size() == chats.size()) {

                                                            mutableLiveData.setValue(chats);

                                                          }
                                                      }else{
                                                         // Log.e(TAG, "onComplete: failed to load chat");
                                                          mutableLiveData.setValue(new ArrayList<>());
                                                      }
                                                }
                                            });
                                    }else {
                                        Log.e(TAG, "onComplete: failed to load chat");
                                        mutableLiveData.setValue(new ArrayList<>());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                });
        return mutableLiveData ;
    }

    private CollectionReference getAdColRef(Ad ad){
        if (ad.getCategory().equals(DbHandler.FREELANCE)){
            return getAdColRef(ad.getCategory(),ad.getSubCategory(),ad.getSubSubCategory());
        }else {
            return getAdColRef(ad.getCategory(),ad.getSubCategory());
        }
    }
    private CollectionReference getAdColRef(String category , String subCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(subCategory);
        return adColRef;
    }
    private CollectionReference getAdColRef(String category , String subCategory , String subSubCategory){
        CollectionReference adColRef;
        adColRef = rootRef.collection(DbHandler.adCollection)
                .document(category)
                .collection(category)
                .document(subCategory)
                .collection(subSubCategory);
        return adColRef;
    }
}
