package com.example.hwada.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ChatRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    private FirebaseAuth auth ;
    private static final String TAG = "ChatRepo";

    public MutableLiveData<Boolean> addNewChat(String senderId ,Chat chat){
        MutableLiveData <Boolean> mutableLiveData = new MutableLiveData<>();

        getChatRef(senderId).document(chat.getAd().getId()+chat.getAd().getAuthorId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mutableLiveData.setValue(true);
                    } else {
                        chat.setReceiverId(chat.getAd().getAuthorId());
                        DocumentReference docRefSender = getChatRef(senderId).document(chat.getAd().getId()+chat.getReceiverId());
                        docRefSender.set(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mutableLiveData.setValue(true);
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
        getChatRef(userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Chat> chatArrayList =new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        Ad ad  = document.get("ad",Ad.class);
                        String date = document.getString("date");
                        Message lastMessage = document.get("lastMessage",Message.class);
                        getAdDocRef(ad).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              if (task.isSuccessful()){
                                  DocumentSnapshot snapshot = task.getResult();

                                  ad.setAuthorName(snapshot.getString("authorName"));
                                  ad.setTitle( snapshot.getString("title"));
                                  ad.setImagesUrl((List<String>) snapshot.get("imagesUrl"));
                                  chatArrayList.add(new Chat(ad,date,lastMessage));

                                  if(chatArrayList.size() == documents.size()){
                                      mutableLiveData.setValue(chatArrayList);
                                  }
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
    private CollectionReference getChatRef(String id){
        return rootRef.collection(DbHandler.userCollection).document(id).collection(DbHandler.chatCollection);
    }
}
