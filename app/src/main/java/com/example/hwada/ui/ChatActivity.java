package com.example.hwada.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hwada.Model.Ad;
import com.example.hwada.Model.Chat;
import com.example.hwada.Model.Message;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.MessageAdapter;
import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.ActivityChatBinding;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.ui.view.ad.AdvertiserFragment;
import com.example.hwada.ui.view.chat.SendImagesMessageFragment;
import com.example.hwada.ui.view.images.ImagesFullDialogFragment;
import com.example.hwada.viewmodel.AdsViewModel;
import com.example.hwada.viewmodel.ChatViewModel;
import com.example.hwada.viewmodel.MessageViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.firebase.Timestamp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends AppCompatActivity implements View.OnClickListener , SendImagesMessageFragment.SendMessage , MessageAdapter.OnItemListener {
    ArrayList<Message> messagesList;

    ActivityChatBinding binding ;
    User user;
    UserViewModel userViewModel ;
    ChatViewModel chatViewModel;
    private App app;
    MessageAdapter adapter ;
    AdvertiserFragment advertiserFragment ;

    private String mCurrentPhotoPath;
    Chat chat;
    int REQUEST_IMAGE_CAPTURE = 4 ;
    Ad ad;

    MessageViewModel messageViewModel;

    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // get user data
        Intent intent = getIntent();
        user =  intent.getParcelableExtra(getString(R.string.userVal));
        chat = intent.getParcelableExtra(getString(R.string.chatVal));
        binding.shimmerChatActivity.startShimmer();
        ad = chat.getAd();


        app = (App) getApplication();
        setSendImageWhenLanguageIsArabic();
        advertiserFragment = new AdvertiserFragment();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        chatViewModel = new ViewModelProvider (this).get(ChatViewModel.class);

        adapter = new MessageAdapter(this,chat.getId(),messageViewModel);
        messagesList = new ArrayList<>();

        setRecycler();
        setListeners();
        setDataToFields();
        getAllMessages();
        setReceiverListener();

    }

    private void messagesObserver(){
        messageViewModel.messagesListener(user.getUId(),chat.getId()).observe(this, messages -> {
            for (Message message: messages) {
                int pos = messagesList.indexOf(message);
                if(pos==-1){
                    adapter.addItem(message);
                }else {
                    adapter.updateItem(pos,message);
                }
                binding.recyclerChatActivity.smoothScrollToPosition(messagesList.size() - 1);
            }
        });
    }

    private void setListeners(){
        binding.simSendMessageChatActivity.setOnClickListener(this);
        binding.etBodyMessageChatActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    binding.cameraChatActivity.setVisibility(View.GONE);
                }
                if(s.length()==0)binding.cameraChatActivity.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.cameraChatActivity.setOnClickListener(this);
        binding.mediaChatActivity.setOnClickListener(this);
        binding.llUserChatActivity.setOnClickListener(this);
        binding.llAdChatActivity.setOnClickListener(this);
    }
    private void scrollRecycleViewToBottom(){
        //scroll recycle to the bottom
        binding.recyclerChatActivity.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                binding.recyclerChatActivity.getLayoutManager().smoothScrollToPosition(binding.recyclerChatActivity, null, messagesList.size());
            }
        });
    }
    private void setDataToFields(){
        binding.tvAdTitleChatActivity.setText(ad.getTitle());
        Glide.with(this).load(ad.getImagesUrl().get(0)).into(binding.simAdChatActivity);
        binding.tvAdTitleChatActivity.setSelected(true);
        binding.tvReceiverLastSeenChatActivity.setSelected(true);
    }

    private void setReceiverListener(){
        userViewModel.userListener(chat.getReceiver().getUId()).observe(this, u -> {
            chat.setReceiver(u);
            setUserStatusToView(u.getStatus());
            binding.tvReceiverNameChatActivity.setText(chat.getReceiver().getUsername());
            Glide.with(getApplication()).load(u.getImage()).into(binding.simReceiverImageChatActivity);
            app.setCurrentChatId(u.getUId());
        });
    }
    @SuppressLint("SetTextI18n")
    private void setUserStatusToView(String status){
        if(status.equals(DbHandler.ONLINE)){
            binding.tvReceiverLastSeenChatActivity.setText(getText(R.string.onlineVal));
        }else {
            binding.tvReceiverLastSeenChatActivity.setText(getString(R.string.lastSeen)+" "+handleTime(chat.getReceiver().getLastSeen()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setUserOnline(user.getUId(),this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline(user.getUId(),this);
        app.setCurrentChatId("");
    }

    private void setRecycler(){
        adapter.setList(messagesList ,user.getUId(),this);
        binding.recyclerChatActivity.setAdapter(adapter);
        binding.recyclerChatActivity.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerChatActivity.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerChatActivity.setNestedScrollingEnabled(false);
        binding.recyclerChatActivity.setHasFixedSize(true);
        scrollRecycleViewToBottom();
        messagesObserver();
    }

    private void getAllMessages(){
        messageViewModel.getAllMessages(user.getUId(),chat.getId()).observe(this, messages -> {
            messagesList = messages;
            binding.shimmerChatActivity.setVisibility(View.GONE);
            binding.shimmerChatActivity.stopShimmer();
            binding.recyclerChatActivity.setVisibility(View.VISIBLE);
            setRecycler();
        });
    }

    private void sendMessage(String body){
        String s = body.trim();

        Message newMessage = new Message(app.getCurrentDate(),s,user.getUId(),chat.getReceiver().getUId());

        sendMessage(messageViewModel.getMessageId(newMessage,ad));
    }
    private void sendMessage(Message newMessage){
        messageViewModel.sendMessage(newMessage,ad).observe(this, message -> {
            if(message!=null){
                int pos = messagesList.indexOf(message);
                if(pos!=-1) adapter.updateItem(pos,message);
            }
        });
        adapter.addItem(newMessage);
        binding.recyclerChatActivity.smoothScrollToPosition(messagesList.size()-1);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == binding.simSendMessageChatActivity.getId() ){
            String s = binding.etBodyMessageChatActivity.getText().toString().trim();
            if(s.length()>0){
                sendMessage(binding.etBodyMessageChatActivity.getText().toString());
                binding.etBodyMessageChatActivity.setText("");
            }
        }else if (v.getId()== binding.cameraChatActivity.getId()){
            if(app.checkCameraPermission(this)){
                openCamera();
            }
        }else if(v.getId() == binding.mediaChatActivity.getId()){
            if(app.checkStoragePermissions()){
                pickImagesHandler();
            }else app.requestStoragePermissions(this);
        }else if(v.getId()==binding.llUserChatActivity.getId()){
            callUserProfileActivity();
        }else if(v.getId()==binding.llAdChatActivity.getId()){
            callAdvertiserFragment();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == app.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showToast(getString(R.string.cameraPermissionWarning));
            }
        } if (requestCode == app.PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //pickImagesHandler();
            } else {
                showToast(getString(R.string.storagePermissionWarning));
            }
        }
    }


    private Toast mCurrentToast;
    public void showToast(String message) {
        if (mCurrentToast == null) {
            mCurrentToast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT);
            mCurrentToast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mCurrentToast.addCallback(new Toast.Callback() {
                    @Override
                    public void onToastShown() {
                        super.onToastShown();
                    }

                    @Override
                    public void onToastHidden() {
                        super.onToastHidden();
                        mCurrentToast = null;
                    }
                });
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                Uri photoURI = createImageFile();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
        }
    }

    private Uri createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return FileProvider.getUriForFile(this,
                "com.example.hwada.fileprovider",
                image);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri tempUri = Uri.parse(mCurrentPhotoPath);
            File file = new File(tempUri.getPath());
            Uri uri = Uri.fromFile(file);
            if(uri != null){
                ArrayList<Message>tempMessages = new ArrayList<>();

                ArrayList<String> list  = new ArrayList<>();
                list.add(uri.toString());

                tempMessages.add(new Message(app.getCurrentDate(),uri,user.getUId(),chat.getReceiver().getUId()));

                callSendImagesFragment(tempMessages,list);
            }
            mCurrentPhotoPath = null;
        }
    }


    private void pickImagesHandler(){

            ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build();
            pickMultipleMedia.launch(request);
    }

    //Pick Images From Gallery
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(10), uris -> {
      if(uris != null && uris.size()>0 ){
          ArrayList<Message>tempMessages = new ArrayList<>();
          try {
              for (Uri uri: uris) {
                  tempMessages.add(new Message(app.getCurrentDate(),uri,user.getUId(),chat.getReceiver().getUId()));
              }

              ArrayList<String> uriList = new ArrayList<>(uris.size());
              uriList.addAll(uris.stream().map(Uri::toString).collect(Collectors.toList()));
              callSendImagesFragment(tempMessages,uriList);
          }catch (Exception e){
              e.printStackTrace();
          }
      }
    });


    private void callSendImagesFragment(ArrayList<Message> messages,ArrayList<String>uris){
        SendImagesMessageFragment fragment = new SendImagesMessageFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("messages", messages);
            bundle.putStringArrayList("imagesUri",uris);
            bundle.putString("receiverName",chat.getReceiver().getUsername());
            fragment.setArguments(bundle);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    @Override
    public void getSendMessage(ArrayList<Message> messages) {
        for (int i = 0;i<messages.size();i++) {
            sendMessage(messageViewModel.getMessageId(messages.get(i),ad));
        }
    }

    private void setSendImageWhenLanguageIsArabic(){
        Locale locale = Resources.getSystem().getConfiguration().locale;
        if (locale.getLanguage().equals("ar")) {
            binding.simSendMessageChatActivity.setImageResource(R.drawable.send_ar_icon);
        }
    }

    @Override
    public void getImagePosition(String uri , int pos) {
        Message message = messagesList.get(pos);
        if(message.getUrl()!=null){
            if(app.checkStoragePermissions()){
                ArrayList<String> uris = new ArrayList<>();
                uris.add(uri);
                callImagesFullDialogFragment(uris,0);
            }else app.requestStoragePermissions(this);
        }
    }

    private void callImagesFullDialogFragment(ArrayList<String> url,int pos){
        ImagesFullDialogFragment fragment = new ImagesFullDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(getString(R.string.imagesUrlVal), url);
        bundle.putInt(getString(R.string.posVal),pos);
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }
    private void callUserProfileActivity(){
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(getString(R.string.receiver),chat.getReceiver());
        intent.putExtra(getString(R.string.userVal),user);
    startActivity(intent);
    }
    public String handleTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        String dateString = app.getDateFromTimeStamp(timestamp);
        try {
            Calendar today = Calendar.getInstance();
            Calendar inputDate = Calendar.getInstance();
            inputDate.setTime(date);

            if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return getString(R.string.today)+ dateString.split(",")[1]+ dateString.split(",")[3] ;
            }
            else if (inputDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && inputDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                return getString(R.string.yesterday)+ dateString.split(",")[1] + dateString.split(",")[3];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString.split(",")[0];
    }
    private void callAdvertiserFragment(){
        if(!advertiserFragment.isAdded()){
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.userVal), user);
            bundle.putParcelable(getString(R.string.adVal),chat.getAd());
            advertiserFragment.setArguments(bundle);
            advertiserFragment.show(getSupportFragmentManager(),advertiserFragment.getTag());
        }

    }
}