package com.example.hwada.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.ImagesAdapter;
import com.example.hwada.databinding.ActivityAddNewAdBinding;
import com.example.hwada.ui.view.MapsFragment;
import com.example.hwada.ui.view.WorkTimeEditFragment;
import com.example.hwada.ui.view.WorkTimePreviewFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AddNewAdActivity extends AppCompatActivity implements ImagesAdapter.OnItemListener , View.OnClickListener ,MapsFragment.GettingPassedData {
    User user ;
    ActivityAddNewAdBinding binding ;
    ImagesAdapter imagesAdapter;

    Ad newAd;
    private static final String TAG = "AddNewAdActivity";
    private static final int PICK_IMAGE_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAdBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // get user data
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
         initAd(intent);
        //******************
        binding.userAddressMapFragment.setText(getUserAddress(user.getLocation()));

        binding.addNewImage.setOnClickListener(this);
        binding.nextButtonAddNewAd.setOnClickListener(this);
        binding.linearLayout.setOnClickListener(this);
        binding.scrollViewAddNewAdd.setOnClickListener(this);
        binding.linearlayoutInner1AddNewItem.setOnClickListener(this);
        binding.userAddressMapFragment.setOnClickListener(this);
        binding.arrowAddNewAd.setOnClickListener(this);
    }
    private void initAd(Intent intent){
        newAd = new Ad();
        newAd.setAuthorId(user.getuId());
        newAd.setAuthorName(user.getUsername());
        newAd.setAuthorLocation(user.getLocation());
        newAd.setCategory(intent.getStringExtra("category"));
        newAd.setSubCategory(intent.getStringExtra("subCategory"));
        newAd.setSubSubCategory(intent.getStringExtra("subSubCategory"));
    }
    public void setImagesToList(List<Uri> list) {
        imagesAdapter = new ImagesAdapter();
        ItemTouchHelper itemTouchHelper =new ItemTouchHelper(simpleCallback);
        try {
            binding.recyclerAddNewAd.setAdapter(imagesAdapter);
            imagesAdapter.setList(list,this);
            binding.recyclerAddNewAd.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            itemTouchHelper.attachToRecyclerView(binding.recyclerAddNewAd);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void getItemPosition(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.add_images_bottom_sheet_dialog);
        bottomSheetDialog.show();
        TextView remove = bottomSheetDialog.findViewById(R.id.remove_bottom_sheet);
        ImageView arrow = bottomSheetDialog.findViewById(R.id.arrow_add_image_bottom_sheet);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesAdapter.removeOneItem(position);
                if(newAd.getImagesList().size()==0){
                    binding.recyclerLinearLayoutAddNewAd.setVisibility(View.GONE);
                    binding.addImageBackground.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
       if(v.getId() ==binding.arrowAddNewAd.getId()) {
            super.onBackPressed();
       } else if(v.getId() == binding.addNewImage.getId()) {
            if(checkPermissions()){
                pickImagesHandler();
           }else {
                requestPermissions();
            }
        }else if (v.getId() == binding.nextButtonAddNewAd.getId()){
            setFieldsWarning();

            if(checkIfFieldsAreValid()){
                newAd.setPrice(Double.parseDouble(binding.adPrice.getText().toString()));
                newAd.setTitle(binding.adTitle.getText().toString());
                newAd.setDescription(binding.adDescription.getText().toString());
                callBottomSheet(new WorkTimePreviewFragment());
            }
        }else if (v.getId() == binding.linearLayout.getId() || v.getId() == binding.scrollViewAddNewAdd.getId()
                ||v.getId() ==binding.linearlayoutInner1AddNewItem.getId()){
            hideKeyboard();
        }else if (v.getId()==binding.userAddressMapFragment.getId()){
            callBottomSheet(new MapsFragment());
        }
    }
    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private boolean checkIfFieldsAreValid(){
        return binding.adTitle.getError()==null &&  binding.adDescription.getError()==null && newAd.getImagesList().size()>0;
    }
    private void setFieldsWarning(){
        if(binding.adTitle.getText().length() ==0){
            binding.adTitle.setError(getString(R.string.emptyFieldWarning));
        } else if (binding.adDescription.getText().length() < 50){
            if (binding.adDescription.getText().length() ==0){
                binding.adDescription.setError(getString(R.string.emptyFieldWarning));
            }else  binding.adDescription.setError(getString(R.string.toShortWarning));
        }else if(newAd.getImagesList().size()==0){
            showDialog(getString(R.string.invalidData),getString(R.string.imagesListEmptyWarning));
        }else if (binding.adPrice.getText().length()==0){
            binding.adPrice.setText("0");
        }
    }

    private void showDialog(String title ,String body){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
    //Pick Images From Gallery
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(10), uris -> {

        if (!uris.isEmpty()) {
            binding.addImageBackground.setVisibility(View.GONE);
            binding.recyclerLinearLayoutAddNewAd.setVisibility(View.VISIBLE);

            if(newAd.getImagesList().size()==0 && newAd.getImagesList().size()+ uris.size()<=10){

                newAd.getImagesList().addAll(uris);
                setImagesToList(newAd.getImagesList());

            } else if(newAd.getImagesList().size()+ uris.size()<=10){

                imagesAdapter.addItems(newAd.getImagesList().size(),uris);

            }else if(newAd.getImagesList().size()<10){
                int length = 10 - newAd.getImagesList().size();
                    if(length > uris.size()) length = uris.size();

                imagesAdapter.addItems(newAd.getImagesList().size(),uris.subList(0,length));
            }
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    });


    //move items in recycler view
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
           int fromPosition = viewHolder.getAdapterPosition();
           int toPosition =target.getAdapterPosition();

            Collections.swap(newAd.getImagesList(),fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void pickImagesHandler(){
        if (newAd.getImagesList().size() >= 10) {
            showDialog(getString(R.string.limitReached),getString(R.string.alertLimitReached));
        }else{
            ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build();
            pickMultipleMedia.launch(request);
        }
    }

    private void requestPermissions() {

        Log.e(TAG, "requestPermissions: "+Build.VERSION.SDK_INT );
        if(Build.VERSION.SDK_INT <= 32){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }else if( Build.VERSION.SDK_INT >=33){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES
            }, PICK_IMAGE_REQUEST);
        }

    }
    private boolean checkPermissions() {
        if(Build.VERSION.SDK_INT <=32){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        }else if( Build.VERSION.SDK_INT >=33){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ;

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                pickImagesHandler();
            }
        }
    }

    public void callBottomSheet(BottomSheetDialogFragment fragment){
        Bundle bundle = new Bundle();
        bundle.putParcelable("ad", newAd);
        bundle.putParcelable("user",user);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }

    private String getUserAddress(LocationCustom location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            address = address.split(",")[0] + address.split(",")[1]+address.split(",")[2]+address.split(",")[3];

            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "loading...";
    }

    @Override
    public void newLocation(Location location) {
        LocationCustom locationCustom = new LocationCustom(location.getLatitude(),location.getLongitude());

        newAd.setAuthorLocation(locationCustom);
        binding.userAddressMapFragment.setText(getUserAddress(locationCustom));
    }

    public void callMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }
}