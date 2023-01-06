package com.example.hwada.repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.hwada.Model.Ad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ImagesRepository {
    private static final String TAG = "ImagesRepository";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

   MutableLiveData<List<Uri>> mutableLiveDataImagesList;
    Application application;

   public ImagesRepository (Application application){
       this.application = application;
   }


   public MutableLiveData<List<Uri>> uploadImages( Ad ad){
       List<Uri> imagesList =new ArrayList<>();
       mutableLiveDataImagesList = new MutableLiveData<>();

       StorageReference imagesRef = storageRef.child("images").child(ad.getAuthorId()).child(ad.getCategory()).child(ad.getId());

       for (Uri file : ad.getImagesList()) {
           new Thread(new Runnable() {
               @Override
               public void run() {

                   UploadTask uploadTask = imagesRef.putFile(file);
                   uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                           // Calculate progress percentage
                           double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                           // Display percentage in progress bar
                       }
                   });

                   uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           if (taskSnapshot.getMetadata() != null) {
                               if (taskSnapshot.getMetadata().getReference() != null) {
                                   Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                                   url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           imagesList.add(uri);
                                       }
                                   });
                               }
                           }
                       }
                   });

                   uploadTask.addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception exception) {
                           Log.e(TAG, "onFailure: failed to upload images" );
                       }
                   });
               }
           }).start();
       }
       mutableLiveDataImagesList.setValue(imagesList);
       return mutableLiveDataImagesList;
   }
}
