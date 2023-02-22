package com.example.hwada.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hwada.Model.Ad;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class AddNewAdActivity extends AppCompatActivity implements ImagesAdapter.OnItemListener , View.OnClickListener  {
    User user ;
    ActivityAddNewAdBinding binding ;
    ImagesAdapter imagesAdapter;
    UserAddressViewModel userAddressViewModel ;
    UserViewModel userViewModel ;
    private App app;
    String mode ;
    Ad ad;
    private static final String TAG = "AddNewAdActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewAdBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        app = (App) getApplication();
        userAddressViewModel = ViewModelProviders.of(this).get(UserAddressViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        getDataFromIntent();
        setAdAddress(false);


        if(mode.equals(getString(R.string.editModeVal))){
            ad.setImagesUri(new ArrayList<>(ad.getImagesUrl()));
            setRecycler();
            setDataToView();
        }
        setClickListener();
        setUserListener();

    }



    private void getDataFromIntent(){
        Intent intent = getIntent();
        mode =  intent.getStringExtra(getString(R.string.modeVal));
        user = (User) intent.getParcelableExtra(getString(R.string.userVal));

        if(mode!=null){
            if(mode.equals(getString(R.string.newModeVal))){
                initAd(intent);
            }else if(mode.equals(getString(R.string.editModeVal))){
                ad = intent.getParcelableExtra(getString(R.string.adVal));
                Log.d(TAG, "getDataFromIntent: daysSchedule "+ad.getDaysSchedule());
            }else finish();
        }else finish();
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
        ad = new Ad();
        ad.setAuthorId(user.getUId());
        ad.setAuthorAddress(user.getAddress());
        ad.setAuthorLocation(user.getLocation());
        ad.setCategory(intent.getStringExtra(getString(R.string.categoryVal)));
        ad.setSubCategory(intent.getStringExtra(getString(R.string.subCategoryVal)));
        ad.setSubSubCategory(intent.getStringExtra(getString(R.string.subSubCategoryVal)));
    }

    private void showRecycler(){
        binding.addImageBackground.setVisibility(View.GONE);
        binding.recyclerLinearLayoutAddNewAd.setVisibility(View.VISIBLE);
    }
    public void setRecycler() {
        showRecycler();
        imagesAdapter = new ImagesAdapter();
        try {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

            imagesAdapter.setList(ad.getImagesUri(),this);
            binding.recyclerAddNewAd.setAdapter(imagesAdapter);
            binding.recyclerAddNewAd.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            itemTouchHelper.attachToRecyclerView(binding.recyclerAddNewAd);

        }catch (Exception e){
            e.getMessage();
        }
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(ad.getImagesUri(),fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return true;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void setDataToView(){
        binding.adDescription.setText(ad.getDescription());
        binding.adTitle.setText(ad.getTitle());
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String formattedValue = decimalFormat.format(ad.getPrice());
        binding.adPrice.setText(formattedValue);
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
                if(ad.getImagesUri().size()==0){
                    binding.recyclerLinearLayoutAddNewAd.setVisibility(View.GONE);
                    binding.addImageBackground.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
       if(v.getId() == binding.arrowAddNewAd.getId()) {
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

                ad.setPrice(Double.parseDouble(price));
                ad.setTitle(title);
                ad.setDescription(description);
                callBottomSheet(new WorkTimePreviewFragment());
            }
        }else if (v.getId() == binding.linearLayout.getId()
                || v.getId() == binding.scrollViewAddNewAdd.getId()
                ||v.getId() == binding.linearlayoutInner1AddNewItem.getId()){
            hideKeyboard();
        }else if (v.getId()==binding.tvUserAddressAddNewAdActivity.getId()){
           if(app.isGooglePlayServicesAvailable(this)){
               callBottomSheet(new MapsFragment());
           }else showToast(getString(R.string.googleServicesWarning));
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
        return binding.adPrice.getError()==null && binding.adTitle.getError()==null &&  binding.adDescription.getError()==null && ad.getImagesUri().size()>0;
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
        }else if(ad.getImagesUri().size()==0){
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
   private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(10), uris -> {

        if (!uris.isEmpty()) {
            ArrayList<String> urisList = (ArrayList<String>) uris.stream().map(Uri::toString).collect(Collectors.toList());
            binding.addImageBackground.setVisibility(View.GONE);
            binding.recyclerLinearLayoutAddNewAd.setVisibility(View.VISIBLE);

            if(ad.getImagesUri().size() == 0 && ad.getImagesUri().size()+ urisList.size()<=10){

                ad.getImagesUri().addAll(urisList);
                setRecycler();

            } else if(ad.getImagesUri().size()+ urisList.size()<=10){

                imagesAdapter.addItems(ad.getImagesUri().size(),urisList);

            }else if(ad.getImagesUri().size()<10){
                int length = 10 - ad.getImagesUri().size();
                    if(length > urisList.size()) length = urisList.size();

                imagesAdapter.addItems(ad.getImagesUri().size(),urisList.subList(0,length));
            }
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
   });


    private void pickImagesHandler(){
        if (ad.getImagesUri().size() >= 10) {
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
        bundle.putParcelable(getString(R.string.adVal), ad);
        bundle.putParcelable(getString(R.string.userVal),user);
        bundle.putString(getString(R.string.modeVal),mode);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),fragment.getTag());
    }
    

        private void setUserListener(){
            userViewModel.userListener(user.getUId()).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User updatedUser) {
                    user.updateUser(updatedUser);
                    if(user.getLocation()!=null) setAdAddress(true);
                }
            });
        }
        
        private void setAdAddress(boolean isNewLocation){
            if(mode.equals(getString(R.string.newModeVal))||isNewLocation){
                binding.tvUserAddressAddNewAdActivity.setText(user.getAddress());
                ad.setAuthorLocation(user.getLocation());
                ad.setAuthorAddress(user.getAddress());
            }else if(mode.equals(getString(R.string.editModeVal))){
                binding.tvUserAddressAddNewAdActivity.setText(ad.getAuthorAddress());
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