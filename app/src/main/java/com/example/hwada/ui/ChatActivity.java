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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
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
import android.view.View;
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
import com.example.hwada.ui.view.chat.SendImagesMessageFragment;
import com.example.hwada.ui.view.images.ImagesFullDialogFragment;
import com.example.hwada.viewmodel.ChatViewModel;
import com.example.hwada.viewmodel.MessageViewModel;
import com.example.hwada.viewmodel.UserViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends AppCompatActivity implements View.OnClickListener , SendImagesMessageFragment.SendMessage , MessageAdapter.OnItemListener{
    ArrayList<Message> messagesList;

    ActivityChatBinding binding ;
    User user;
    UserViewModel userViewModel ;
    ChatViewModel chatViewModel;
    private App app;
    MessageAdapter adapter ;
    private String mCurrentPhotoPath;
    Chat chat;
    int REQUEST_IMAGE_CAPTURE = 4 ;
    Ad ad;

    User receiverInfo;
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
        user =  intent.getParcelableExtra("user");
        chat = intent.getParcelableExtra("chat");

        ad = chat.getAd();

        app = (App) getApplication();

        setSendImageWhenLanguageIsArabic();

        userViewModel =  UserViewModel.getInstance();
        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        adapter = new MessageAdapter(this,chat.getId(),messageViewModel);
        messagesList = new ArrayList<>();

        getReceiverInfo();
        setListeners();
        getUserStatus();
        setDataToFields();
        getAllMessages();

    }

    private void getReceiverInfo() {
        chatViewModel.getReceiverInfo(chat.getReceiverId()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                receiverInfo = user;
                binding.usernameChatActivity.setText(receiverInfo.getUsername());
            }
        });
    }


    private void messagesObserver(){
        messageViewModel.messagesListener(user.getUId(),chat.getId()).observe(this, new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(ArrayList<Message> messages) {
                if (messages != null) {
                    for (Message m : messages) {
                        int index = messagesList.indexOf(m);
                        if (index != -1) {
                                messagesList.set(index, m);
                                adapter.notifyDataSetChanged();
                        } else {
                            messagesList.add(m);
                            adapter.notifyItemInserted(messagesList.size()-1);
                        }
                    }
                    binding.recyclerChatActivity.smoothScrollToPosition(messagesList.size() - 1);
                }
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
    }
    private void setDataToFields(){
        binding.tvAdTitleChatActivity.setText(ad.getTitle());
        Glide.with(this).load(ad.getImagesUrl().get(0)).into(binding.simAdChatActivity);
    }

    private void getUserStatus(){
        userViewModel.getUserStatus(ad.getAuthorId()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setUserStatusToView(s);
            }
        });
    }
    private void setUserStatusToView(String status){
        if(status.equals(DbHandler.ONLINE)){
            binding.simUserStatus.setBackgroundColor(getResources().getColor(R.color.green));
        }else binding.simUserStatus.setBackgroundColor(Color.RED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.setUserOnline(user.getUId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.setUserOffline(user.getUId());
    }

    private void setRecycler(){
        adapter.setList(messagesList ,user.getUId(),this);
        binding.recyclerChatActivity.setAdapter(adapter);
        binding.recyclerChatActivity.setLayoutManager(new LinearLayoutManager(this));

        binding.recyclerChatActivity.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerChatActivity.setNestedScrollingEnabled(false);
        binding.recyclerChatActivity.setHasFixedSize(true);

        messagesObserver();
    }

    private void getAllMessages(){
        messageViewModel.getAllMessages(user.getUId(),chat.getId()).observe(this, new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(ArrayList<Message> messages) {
                messagesList = messages;
                setRecycler();
            }
        });
    }

    private void sendMessage(String body){
        String s = body.trim();

        Message newMessage = new Message(app.getCurrentDate(),s,user.getUId(),chat.getReceiverId());

        saveMessage(messageViewModel.getMessageId(newMessage,ad));
    }
    private void saveMessage(Message newMessage){
        messageViewModel.sendMessage(newMessage,ad).observe(this, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                if(message!=null){
                    changeMessageStatusToSend(message);
                }
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
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == app.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use camera functionality.",
                        Toast.LENGTH_SHORT).show();
            }
        } if (requestCode == app.PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImagesHandler();
            } else {
                Toast.makeText(this, "Storage permission is required to to access images.",
                        Toast.LENGTH_SHORT).show();
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

                tempMessages.add(new Message(app.getCurrentDate(),uri,user.getUId(),chat.getReceiverId()));

                //TODO
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
                  tempMessages.add(new Message(app.getCurrentDate(),uri,user.getUId(),chat.getReceiverId()));
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
            bundle.putString("receiverName",receiverInfo.getUsername());
            fragment.setArguments(bundle);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    @Override
    public void getSendMessage(ArrayList<Message> messages) {
        for (int i = 0;i<messages.size();i++) {
            saveMessage(messageViewModel.getMessageId(messages.get(i),ad));
        }
    }

    private void setSendImageWhenLanguageIsArabic(){
        Locale locale = Resources.getSystem().getConfiguration().locale;
        if (locale.getLanguage().equals("ar")) {
            binding.simSendMessageChatActivity.setImageResource(R.drawable.send_ar_icon);
        }
    }

    @Override
    public void getImagePosition(int position) {
        String path ="";
        if(messagesList.get(position).getUrl()!=null)path = messagesList.get(position).getUrl();
        else if(messagesList.get(position).getUri()!=null) path = String.valueOf(messagesList.get(position).getUri());
        if(path.length()>0){
            ArrayList<String> url = new ArrayList<>();
            url.add(path);
            callImagesFullDialogFragment(url,0);
        }
    }

    private void callImagesFullDialogFragment(ArrayList<String> url,int pos){
        ImagesFullDialogFragment fragment = new ImagesFullDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imagesUrl", url);
        bundle.putInt("pos",pos);
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }
    private void changeMessageStatusToSend(Message message){
        for (int i = 0; i < messagesList.size(); i++) {
            if(messagesList.get(i).getId().equals(message.getId())){
                messagesList.get(i).setSent(true);
                adapter.notifyDataSetChanged();
            }
        }
    }
}