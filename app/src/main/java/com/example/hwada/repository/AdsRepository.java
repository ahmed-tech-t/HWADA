package com.example.hwada.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.database.DbHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdsRepository {
    private Application application ;
    MutableLiveData<ArrayList<Ad>> workerAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> homeFoodAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> handcraftAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> freeLanceAdsMutableLiveData;
    MutableLiveData<ArrayList<Ad>> favAdsMutableLiveData;

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private DocumentReference addRef;
    //Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static final String TAG = "AdsRepository";

//********************************
    public MutableLiveData<Boolean>addNewAdd(Ad newAd){
        MutableLiveData<Boolean> addNewAdSuccess = new MutableLiveData<>();
        addNewAdSuccess.setValue(true);

        if (newAd.getCategory().equals("freelance")){
            addRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getCategory())
                    .document(newAd.getSubCategory())
                    .collection(newAd.getSubSubCategory())
                    .document();
        }else {
            addRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getSubCategory())
                    .document();
        }

        newAd.setId(addRef.getId());
        Log.e(TAG, "addNewAdd: "+ addRef.getId());
        StorageReference imageRef = storageRef.child("images").child(newAd.getAuthorId()).child(newAd.getCategory()).child(newAd.getId());
        List<Uri> downloadUrls = new ArrayList<>();

        for (Uri uri :newAd.getImagesList()){
            File file= new File(uri.getPath());

            UploadTask uploadTask = imageRef.child(file.getName()).putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                            url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrls.add(uri);
                                    if (downloadUrls.size() == newAd.getImagesList().size()) {
                                        newAd.getImagesList().clear();
                                        newAd.setImagesList(downloadUrls);
                                        uploadAd(newAd,addNewAdSuccess);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        return addNewAdSuccess;
    }
    private void uploadAd(Ad newAd , MutableLiveData<Boolean>addNewAdSuccess){
        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // add new Add
                transaction.set(addRef,newAd);
                if (newAd.getCategory().equals("freelance")){
                    addRef = rootRef.collection(DbHandler.userCollection)
                            .document(newAd.getAuthorId())
                            .collection("myAds")
                            .document(newAd.getCategory())
                            .collection(newAd.getCategory())
                            .document(newAd.getSubCategory())
                            .collection(newAd.getSubSubCategory())
                            .document(newAd.getId());
                }else {
                    addRef = rootRef.collection(DbHandler.userCollection)
                            .document(newAd.getAuthorId())
                            .collection("myAds")
                            .document(newAd.getCategory())
                            .collection(newAd.getSubCategory())
                            .document(newAd.getId());
                }
                transaction.set(addRef,newAd);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    addNewAdSuccess.setValue(true);
                }else {
                    addNewAdSuccess.setValue(false);
                    Log.e(TAG, "onComplete: "+task.getException().getMessage());
                    task.getException().printStackTrace();
                }
            }
        });

    }
//********************************



    public MutableLiveData<Boolean>addAds(Ad newAd){
        MutableLiveData<Boolean> addNewAdSuccess = new MutableLiveData<>();

        rootRef.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // add new Add
                transaction.set(addRef,newAd);
                if (newAd.getCategory().equals("freelance")){
                    addRef = rootRef.collection(DbHandler.userCollection)
                            .document(newAd.getAuthorId())
                            .collection("myAds")
                            .document(newAd.getCategory())
                            .collection(newAd.getCategory())
                            .document(newAd.getSubCategory())
                            .collection(newAd.getSubSubCategory())
                            .document(newAd.getId());
                }else {
                    addRef = rootRef.collection(DbHandler.userCollection)
                            .document(newAd.getAuthorId())
                            .collection("myAds")
                            .document(newAd.getCategory())
                            .collection(newAd.getSubCategory())
                            .document(newAd.getId());
                }
                transaction.set(addRef,newAd);
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task<Object> task) {
                if(task.isSuccessful()){
                    addNewAdSuccess.setValue(true);
                }else {
                    addNewAdSuccess.setValue(false);
                    Log.e(TAG, "onComplete: "+task.getException().getMessage());
                    task.getException().printStackTrace();
                }
            }
        });

        return addNewAdSuccess;
    }



    public MutableLiveData<Boolean>updateImages(Ad newAd){
        MutableLiveData<Boolean> updateImagesSuccess = new MutableLiveData<>();
        if (newAd.getCategory().equals("freelance")||newAd.getCategory().equals("worker")){
            addRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory())
                    .collection(newAd.getSubCategory())
                    .document(newAd.getSubSubCategory());
        }else {
            addRef = rootRef.collection(DbHandler.adCollection)
                    .document(newAd.getCategory());
        }
        Map<String,Object> data = new HashMap<>();
        data.put("imagesList",newAd.getImagesList());

        addRef.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateImagesSuccess.setValue(true);
                }else updateImagesSuccess.setValue(false);
            }
        });
        return updateImagesSuccess;
    }

    public MutableLiveData<ArrayList<Ad>> getAllWorkersAds(){
        ArrayList<Ad> list = new ArrayList<>();
        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));


        workerAdsMutableLiveData.setValue(list);
        return  workerAdsMutableLiveData;
    }
    public MutableLiveData<ArrayList<Ad>> getAllFreelanceAds(String subCategory){
        ArrayList<Ad> list = new ArrayList<>();

        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));


        freeLanceAdsMutableLiveData.setValue(list);
        return  freeLanceAdsMutableLiveData;
    }


    public MutableLiveData<ArrayList<Ad>> getAllHomeFoodAds(){
        ArrayList<Ad> list = new ArrayList<>();
        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));


        homeFoodAdsMutableLiveData.setValue(list);
        return  homeFoodAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ad>> getFavAds(User user){
        ArrayList<Ad> list = new ArrayList<>();

        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));


        favAdsMutableLiveData.setValue(list);
        return  favAdsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Ad>> getAllHandcraftAds(){
        ArrayList<Ad> list = new ArrayList<>();
        LocationCustom authorLocation =null;
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));
        list.add(new Ad("authorId","authorName",authorLocation,"title","description",10,"date","category","subCategory","subSubCategory"));

        handcraftAdsMutableLiveData.setValue(list);
        return  handcraftAdsMutableLiveData;
    }


    public AdsRepository(Application application){
        this.application = application;
        workerAdsMutableLiveData = new MutableLiveData<>();
        homeFoodAdsMutableLiveData =new MutableLiveData<>();
        favAdsMutableLiveData =new MutableLiveData<>();
        handcraftAdsMutableLiveData =new MutableLiveData<>();
        freeLanceAdsMutableLiveData =new MutableLiveData<>();
    }

}
