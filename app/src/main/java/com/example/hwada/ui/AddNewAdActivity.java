package com.example.hwada.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.LocationCustom;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.adapter.ImagesAdapter;
import com.example.hwada.application.App;
import com.example.hwada.databinding.ActivityAddNewAdBinding;
import com.example.hwada.ui.view.map.MapsFragment;
import com.example.hwada.ui.view.ad.addNewAdd.WorkTimePreviewFragment;
import com.example.hwada.viewmodel.UserAddressViewModel;
import com.example.hwada.viewmodel.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Collections;
import java.util.List;

public class AddNewAdActivity extends AppCompatActivity implements ImagesAdapter.OnItemListener , View.OnClickListener  {
    User user ;
    ActivityAddNewAdBinding binding ;
    ImagesAdapter imagesAdapter;
    UserAddressViewModel userAddressViewModel ;
    UserViewModel userViewModel ;
    private App app;

    Ad newAd;
    private static final String TAG = "AddNewAdActivity";
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

        app = (App) getApplication();

        userAddressViewModel = ViewModelProviders.of(this).get(UserAddressViewModel.class);
        userViewModel = UserViewModel.getInstance();
        //******************
        setUserAddress();        
        setClickListener();
        
        setUserListener();
    }
    
    private void setClickListener(){
        binding.addNewImage.setOnClickListener(this);
        binding.nextButtonAddNewAd.setOnClickListener(this);
        binding.linearLayout.setOnClickListener(this);
        binding.scrollViewAddNewAdd.setOnClickListener(this);
        binding.linearlayoutInner1AddNewItem.setOnClickListener(this);
        binding.tvUserAddressAddNewAdActivity.setOnClickListener(this);
        binding.arrowAddNewAd.setOnClickListener(this);

    }
    
    private void initAd(Intent intent){
        newAd = new Ad();
        newAd.setAuthor(user);
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
                if(newAd.getImagesUri().size()==0){
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
            if(app.checkStoragePermissions()){
                pickImagesHandler();
           }else {
                app.requestStoragePermissions(this);
            }
        }else if (v.getId() == binding.nextButtonAddNewAd.getId()){
            setFieldsWarning();

            if(checkIfFieldsAreValid()){
                String price = binding.adPrice.getText().toString().trim();
                String title = binding.adTitle.getText().toString().trim();
                String description =binding.adDescription.getText().toString().trim();

                newAd.setPrice(Double.parseDouble(price));
                newAd.setTitle(title);
                newAd.setDescription(description);
                callBottomSheet(new WorkTimePreviewFragment());
            }
        }else if (v.getId() == binding.linearLayout.getId() || v.getId() == binding.scrollViewAddNewAdd.getId()
                ||v.getId() == binding.linearlayoutInner1AddNewItem.getId()){
            hideKeyboard();
        }else if (v.getId()==binding.tvUserAddressAddNewAdActivity.getId()){
           if(app.isGooglePlayServicesAvailable(this)){
               callBottomSheet(new MapsFragment());
           }else showToast(getString(R.string.googleServicesWarning));        }
    }
    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private boolean checkIfFieldsAreValid(){
        return binding.adPrice.getError()==null && binding.adTitle.getError()==null &&  binding.adDescription.getError()==null && newAd.getImagesUri().size()>0;
    }
    private void setFieldsWarning(){
        String price = binding.adPrice.getText().toString().trim();
        String title = binding.adTitle.getText().toString().trim();
        String description =binding.adDescription.getText().toString().trim();

        if(price.length()>4){
            binding.adPrice.setError(getString(R.string.toLong));
        }else if(title.length() ==0){
            binding.adTitle.setError(getString(R.string.emptyFieldWarning));
        } else if (description.length() < 50){
            if (description.length() ==0){
                binding.adDescription.setError(getString(R.string.emptyFieldWarning));
            }else  binding.adDescription.setError(getString(R.string.toShortWarning));
        }else if(newAd.getImagesUri().size()==0){
            showDialog(getString(R.string.invalidData),getString(R.string.imagesListEmptyWarning));
        }else if (price.length() == 0){
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

            if(newAd.getImagesUri().size()==0 && newAd.getImagesUri().size()+ uris.size()<=10){

                newAd.getImagesUri().addAll(uris);
                setImagesToList(newAd.getImagesUri());

            } else if(newAd.getImagesUri().size()+ uris.size()<=10){

                imagesAdapter.addItems(newAd.getImagesUri().size(),uris);

            }else if(newAd.getImagesUri().size()<10){
                int length = 10 - newAd.getImagesUri().size();
                    if(length > uris.size()) length = uris.size();

                imagesAdapter.addItems(newAd.getImagesUri().size(),uris.subList(0,length));
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

            Collections.swap(newAd.getImagesUri(),fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void pickImagesHandler(){
        if (newAd.getImagesUri().size() >= 10) {
            showDialog(getString(R.string.limitReached),getString(R.string.alertLimitReached));
        }else{
            ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
            PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                    .setMediaType(mediaType)
                    .build();
            pickMultipleMedia.launch(request);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == app.PICK_IMAGE_REQUEST) {
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
    

        private void setUserListener(){
            userViewModel.userListener(user.getUId()).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User updatedUser) {
                    user.updateUser(updatedUser);
                    if(user.getLocation()!=null) setUserAddress();
                }
            });
        }
        
        private void setUserAddress(){
            binding.tvUserAddressAddNewAdActivity.setText(user.getAddress());
            newAd.getAuthor().setLocation(user.getLocation());
            newAd.getAuthor().setAddress(user.getAddress());
            
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
}