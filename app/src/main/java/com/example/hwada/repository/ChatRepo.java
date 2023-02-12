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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
        String chatId = getChatId(senderId,chat.getReceiverId(),chat.getAd());
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
                                String receiverId = chat.getReceiverId();
                                DocumentReference docRefSender = getChatRef(senderId).document(chatId);
                                DocumentReference docRefReceiver = getChatRef(chat.getReceiverId()).document(chatId);

                                transaction.set(docRefSender,chat);
                                chat.setReceiverId(senderId);
                                transaction.set(docRefReceiver,chat);
                                chat.setReceiverId(receiverId);

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
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Chat> chatArrayList = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    if(documents.isEmpty()){
                        Log.e(TAG, "onComplete: empty" );
                        mutableLiveData.setValue(new ArrayList<>());
                        return;
                    }

                    for (DocumentSnapshot document : documents) {

                        Chat chat = document.toObject(Chat.class);

                        getAdDocRef(chat.getAd()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot adSnapshot = task.getResult();
                                    Ad ad = adSnapshot.toObject(Ad.class);
                                    chat.setAd(ad);
                                    chatArrayList.add(chat);
                                    if(chatArrayList.size() == documents.size()){
                                        mutableLiveData.setValue(chatArrayList);
                                        return;
                                    }
                                }
                            }
                        });

                    }
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return mutableLiveData;
    }


    private DocumentReference getAdDocRef(Ad ad){
        DocumentReference adDocRef;
        if (ad.getCategory().equals(DbHandler.FREELANCE)){
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(ad.getCategory())
                    .collection(ad.getCategory())
                    .document(ad.getSubCategory())
                    .collection(ad.getSubSubCategory()).document(ad.getId());
        }else {
            adDocRef = rootRef.collection(DbHandler.adCollection)
                    .document(ad.getCategory())
                    .collection(ad.getSubCategory()).document(ad.getId());
        }
        return adDocRef;
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

    public MutableLiveData<ArrayList<Chat>> chatListener(String userId){
        MutableLiveData<ArrayList<Chat>> mutableLiveData = new MutableLiveData<>();
        getChatRef(userId)
                .orderBy("lastMessage.timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        ArrayList<Chat> chats = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Chat chat = doc.toObject(Chat.class);
                                     chats.add(chat);
                        }
                        mutableLiveData.setValue(chats);
                    }

                });
        return mutableLiveData ;
    }

    public MutableLiveData<User> getReceiverInfo(String id){
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();
        DocumentReference docRef = rootRef.collection(DbHandler.userCollection).document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                User user = snapshot.toObject(User.class);
                mutableLiveData.setValue(user);
            }
        });

        return mutableLiveData;
    }


}
