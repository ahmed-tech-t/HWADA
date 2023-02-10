package com.example.hwada.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MessageRepo {

    Application application ;
    private static final String TAG = "MessageRepo";
    
    MutableLiveData<Message> sendMessage;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

    //Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    App app ;
    public MessageRepo(Application application){
        this.application = application;
        sendMessage = new MutableLiveData<>();
        app = (App) application.getApplicationContext();
    }

    public MutableLiveData<Message> sendMessage (Message message,Ad ad){

        String chatId = getChatId(message,ad);

        DocumentReference docRef = getSenderMessagesColRef(message,chatId).document(message.getId());

        if(message.getUri()!=null){
            uploadImages(docRef,message ,chatId);
        }else uploadMessage(docRef,chatId,message);

        return sendMessage;
    }

    private void uploadImages(DocumentReference docRef ,Message message , String chatId) {

        StorageReference imageRef = storageRef.child("chatImages").child(chatId).child(message.getId());
        File file= new File(message.getUri().getPath());
        UploadTask uploadTask = imageRef.child(file.getName()).putFile(message.getUri());
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                        url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                message.setUrl(String.valueOf(uri));
                                message.setUri(null);
                                uploadMessage(docRef,chatId,message);
                            }
                        });
                    }
                }
            }
        });
    }

    private void uploadMessage(DocumentReference docSenderRef ,String chatId, Message message) {
        DocumentReference chatSenderDocRef = getChatCocRef(message.getSenderId(),chatId);
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

               transaction.set(docSenderRef,message);
               //update last message in chat collection
               Map<String,Object> data = new HashMap<>();
               data.put("lastMessage",message);
               transaction.update(chatSenderDocRef,data);

               DocumentReference decReceiverRef = getReceiverMessagesColRef(message,chatId).document(message.getId());

               transaction.set(decReceiverRef,message);

               //update last message in chat collection
               data.put("lastMessage",message);
               transaction.update(getChatCocRef(message.getReceiverId(),chatId),data);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    changeMessageStatusToSent(message,chatId);
                    sendMessage.setValue(message);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }



    public MutableLiveData<ArrayList<Message>>getAllMessages(String userId , String chatId){
        MutableLiveData<ArrayList<Message>> mutableLiveData = new MutableLiveData<>();
        getUserMessagesColRef(userId , chatId)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Message> messages = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                    if(documents.isEmpty()) mutableLiveData.setValue(new ArrayList<>());
                    for (DocumentSnapshot document : documents) {
                        messages.add(document.toObject(Message.class));
                    }
                    mutableLiveData.setValue(messages);
                }
            }
        });
        return mutableLiveData;
    }

    public Message getMessageId(Message message,Ad ad){
        String chatId = getChatId(message,ad);
        DocumentReference docRef = getSenderMessagesColRef(message,chatId).document();
        message.setId(docRef.getId());
        return  message ;
    }

    private CollectionReference getSenderMessagesColRef(Message message,String chatId){
            return rootRef.collection(DbHandler.userCollection)
                    .document(message.getSenderId())
                    .collection(DbHandler.chatCollection)
                    .document(chatId)
                    .collection(DbHandler.messagesCollection);
    }

    private DocumentReference getChatCocRef(String userId,String chatId){
        return rootRef.collection(DbHandler.userCollection)
                .document(userId)
                .collection(DbHandler.chatCollection)
                .document(chatId);
    }
    private CollectionReference getReceiverMessagesColRef(Message message,String chatId){
        return rootRef.collection(DbHandler.userCollection)
                .document(message.getReceiverId())
                .collection(DbHandler.chatCollection)
                .document(chatId)
                .collection(DbHandler.messagesCollection);
    }

    private CollectionReference getUserMessagesColRef(String userId,String chatId){
        return rootRef.collection(DbHandler.userCollection)
                .document(userId)
                .collection(DbHandler.chatCollection)
                .document(chatId)
                .collection(DbHandler.messagesCollection);
    }

    private String getChatId(Message message, Ad ad){
        if(ad.getAuthorId().equals(message.getSenderId())){
            return ad.getId()+message.getReceiverId();
        }
        return ad.getId()+message.getSenderId();
    }

    private String getChatId(String senderId , String receiverId, Ad ad){
        if(ad.getAuthorId().equals(senderId)){
            return ad.getId()+receiverId;
        }
        return ad.getId()+senderId;
    }

    public MutableLiveData<ArrayList<Message>> messagesListener(String userId,String chatId){
        MutableLiveData<ArrayList<Message>> mutableLiveData = new MutableLiveData<>();
        getUserMessagesColRef(userId,chatId)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                Log.e(TAG, "onEvent: " );
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    ArrayList<Message> messagesList = new ArrayList<>();
                    Message message = change.getDocument().toObject(Message.class) ;
                    if(!message.isSent()&&message.getSenderId().equals(userId)){
                        return;
                    }
                    messagesList.add(message);
                    mutableLiveData.postValue(messagesList);
                }
            }
        });
        return mutableLiveData;
    }
    private void changeMessageStatusToSent(Message message , String chatId){
        DocumentReference  docSenderRef = getSenderMessagesColRef(message,chatId).document(message.getId());
        DocumentReference chatSenderDocRef = getChatCocRef(message.getSenderId(),chatId);

        DocumentReference  docReceiverRef = getReceiverMessagesColRef(message,chatId).document(message.getId());
        DocumentReference chatReceiverDocRef = getChatCocRef(message.getReceiverId(),chatId);

        rootRef.runTransaction(new Transaction.Function<Object>() {

            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object>messageData = new HashMap<>();
                messageData.put("sent",true);
                transaction.update(docSenderRef,messageData);
                transaction.update(docReceiverRef,messageData);

                Map<String,Object>chatData = new HashMap<>();
                chatData.put("lastMessage.sent",true);
                transaction.update(chatSenderDocRef,chatData);
                transaction.update(chatReceiverDocRef,chatData);

                return null;
            }
        });
    }

    public void setMessagesStatusToSeen(Message message , String chatId){
        DocumentReference  docSenderRef = getSenderMessagesColRef(message,chatId).document(message.getId());
        DocumentReference chatSenderDocRef = getChatCocRef(message.getSenderId(),chatId);

        DocumentReference  docReceiverRef = getReceiverMessagesColRef(message,chatId).document(message.getId());
        DocumentReference chatReceiverDocRef = getChatCocRef(message.getReceiverId(),chatId);
        rootRef.runTransaction(new Transaction.Function<Object>() {


            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object>messageData = new HashMap<>();
                messageData.put("seen",true);
                transaction.update(docSenderRef,messageData);
                transaction.update(docReceiverRef,messageData);

                Map<String,Object>chatData = new HashMap<>();
                chatData.put("lastMessage.seen",true);
                transaction.update(chatSenderDocRef,chatData);
                transaction.update(chatReceiverDocRef,chatData);
                return null;
            }
        });
    }
}
