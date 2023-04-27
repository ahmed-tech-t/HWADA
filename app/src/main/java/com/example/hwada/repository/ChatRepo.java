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
import com.google.firebase.firestore.ListenerRegistration;
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

    private FirebaseAuth auth;
    private static final String TAG = "ChatRepo";

    ListenerRegistration listenerRegistration;
    public MutableLiveData<ArrayList<Chat>> chatListMutableLiveData;

    public ChatRepo() {
        chatListMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Chat> addNewChat(String senderId, Chat chat) {
        MutableLiveData<Chat> mutableLiveData = new MutableLiveData<>();
        String chatId = getChatId(senderId, chat.getReceiver().getUId(), chat.getAd());
        chat.setId(chatId);

        CollectionRef.Companion.getChatRef(rootRef, senderId).document(chatId).get().addOnCompleteListener(task -> {
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
                            DocumentReference docRefSender = CollectionRef.Companion.getChatRef(rootRef, senderId).document(chatId);
                            DocumentReference docRefReceiver = CollectionRef.Companion.getChatRef(rootRef, chat.getReceiver().getUId()).document(chatId);

                            transaction.set(docRefSender, chat);
                            chat.getReceiver().setUId(senderId);
                            transaction.set(docRefReceiver, chat);
                            chat.getReceiver().setUId(receiverId);
                            return null;
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Object>() {
                        @Override
                        public void onComplete(@NonNull Task<Object> task) {
                            if (task.isSuccessful()) {
                                mutableLiveData.setValue(chat);
                            }
                        }
                    });
                }
            } else {
                // Handle error
            }
        });
        return mutableLiveData;
    }


    private String getChatId(String senderId, String receiverId, Ad ad) {
        if (ad.getAuthorId().equals(senderId)) {
            return ad.getId() + receiverId;
        }
        return ad.getId() + senderId;
    }


    private DocumentReference userDocumentRef(String id) {
        return rootRef.collection(DbHandler.userCollection).document(id);
    }

    /**
     * chat listener
     **/
//    public MutableLiveData<ArrayList<Chat>> chatListener(String userId) {
//        listenerRegistration = CollectionRef.Companion.getChatRef(rootRef, userId)
//                .orderBy("lastMessage.timeStamp", Query.Direction.ASCENDING)
//                .addSnapshotListener((value, e) -> {
//                    if (e != null) {
//                        Log.e(TAG, "failed.", e);
//                        chatListMutableLiveData.setValue(new ArrayList<>());
//                        return;
//                    }
//                    assert value != null;
//                    if (value.isEmpty()) {
//                        Log.e(TAG, "QuerySnapshot is empty.");
//                        chatListMutableLiveData.setValue(new ArrayList<>());
//                        return;
//                    }
//                    ArrayList<Chat> chats = new ArrayList<>();
//                    for (DocumentChange change : value.getDocumentChanges()) {
//                        Chat chat = change.getDocument().toObject(Chat.class);
//                        getAdAndReceiver(chats, value.getDocumentChanges().size(), chat);
//                    }
//                });
//        return chatListMutableLiveData;
//    }
    private void getAdAndReceiver(ArrayList<Chat> chats, int size, Chat chat) {
        Task<DocumentSnapshot> task1 = CollectionRef.Companion.getAdColRef(rootRef, chat.getAd()).document(chat.getAd().getId()).get();
        Task<DocumentSnapshot> task2 = userDocumentRef(chat.getReceiver().getUId()).get();

        Tasks.whenAllComplete(task1, task2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task1.getResult() != null && task2.getResult() != null) {
                    chat.setAd(task1.getResult().toObject(Ad.class));
                    chat.setReceiver(task2.getResult().toObject(User.class));
                    chats.add(chat);
                }
                if (size == chats.size()) {
                    chatListMutableLiveData.setValue(chats);
                }
            } else {
                Log.d(TAG, "onComplete: " + task.getException());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    EventListener<QuerySnapshot> listener = (value, e) -> {
        if (e != null) {
            Log.e(TAG, "failed.", e);
            chatListMutableLiveData.setValue(new ArrayList<>());
            return;
        }
        assert value != null;
        if (value.isEmpty()) {
            Log.e(TAG, "QuerySnapshot is empty.");
            chatListMutableLiveData.setValue(new ArrayList<>());
            return;
        }
        ArrayList<Chat> chats = new ArrayList<>();
        for (DocumentChange change : value.getDocumentChanges()) {
            Chat chat = change.getDocument().toObject(Chat.class);
            getAdAndReceiver(chats, value.getDocumentChanges().size(), chat);

        }
    };

    public void startChatListener(String userId) {
        Log.d(TAG, "startChatListener: ");
        listenerRegistration = CollectionRef.Companion.getChatRef(rootRef, userId).orderBy("lastMessage.timeStamp", Query.Direction.ASCENDING).addSnapshotListener(listener);
    }
    public void removeChatListener(){
        listenerRegistration.remove();
    }
    public void deleteChat(String userId, String chatId) {
        Log.d(TAG, "deleteChat: remove listener");
        CollectionRef.Companion.getChatRef(rootRef, userId).document(chatId).collection(DbHandler.messagesCollection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                rootRef.runTransaction(transaction -> {
                    DocumentReference chatDocRef = CollectionRef.Companion.getChatRef(rootRef, userId).document(chatId);

                    Map<String, Object> data = new HashMap<>();
                    data.put("lastMessage", null);
                    transaction.update(chatDocRef, data);

                    for (DocumentSnapshot message : documents) {
                        transaction.delete(message.getReference());
                    }
                    return null;
                });
            }
        });
    }
}
