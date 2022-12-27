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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hwada.Model.Ad;
import com.example.hwada.Model.User;
import com.example.hwada.Model.WorkingTime;
import com.example.hwada.R;
import com.example.hwada.adapter.ImagesAdapter;
import com.example.hwada.adapter.WorkingTimeAdapter;
import com.example.hwada.databinding.ActivityAddNewAdBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Collections;
import java.util.List;

public class AddNewAdActivity extends AppCompatActivity implements ImagesAdapter.OnItemListener , View.OnClickListener , WorkingTimeAdapter.OnItemListener {
    User user ;
    ActivityAddNewAdBinding binding ;
    String category ,subCategory ,subSubCategory;
    ImagesAdapter imagesAdapter;

    WorkingTimeAdapter workingTimeAdapterForSaturday ;
    WorkingTimeAdapter workingTimeAdapterForSunday ;
    WorkingTimeAdapter workingTimeAdapterForMonday ;
    WorkingTimeAdapter workingTimeAdapterForTuesday ;
    WorkingTimeAdapter workingTimeAdapterForWednesday ;
    WorkingTimeAdapter workingTimeAdapterForThursday ;
    WorkingTimeAdapter workingTimeAdapterForFriday ;

    Ad newAd;
    String TAG ="AddNewAdActivity";
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
        newAd = new Ad();
        newAd.setAuthorId(user.getuId());
        newAd.setAuthorName(user.getUsername());
        newAd.setCategory(intent.getStringExtra("category"));
        newAd.setSubCategory(intent.getStringExtra("subCategory"));
        newAd.setSubSubCategory(intent.getStringExtra("subSubCategory"));

        //******************
        binding.addNewImage.setOnClickListener(this);

        setWorkTimeToAdapter();
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
        if(v.getId() == binding.addNewImage.getId()) {
            if(checkPermissions()){
                pickImagesHandler();
           }else {
                requestPermissions();
            }
        }
    }

    //Pick Images From Galary
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
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.limitReached))
                    .setMessage(getString(R.string.alertLimitReached))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }else{
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
    }
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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



    public void setWorkTimeToAdapter() {
         workingTimeAdapterForSaturday =new WorkingTimeAdapter();
         workingTimeAdapterForSunday  =new WorkingTimeAdapter();
         workingTimeAdapterForMonday  =new WorkingTimeAdapter();
         workingTimeAdapterForTuesday  =new WorkingTimeAdapter();
         workingTimeAdapterForWednesday  =new WorkingTimeAdapter();
         workingTimeAdapterForThursday =new WorkingTimeAdapter();
         workingTimeAdapterForFriday  =new WorkingTimeAdapter();
        try {
            binding.recyclerSaturday.setAdapter(workingTimeAdapterForSaturday);
            binding.recyclerSunday.setAdapter(workingTimeAdapterForSunday);
            binding.recyclerMonday.setAdapter(workingTimeAdapterForMonday);
            binding.recyclerTuesday.setAdapter(workingTimeAdapterForTuesday);
            binding.recyclerWednesday.setAdapter(workingTimeAdapterForWednesday);
            binding.recyclerThursday.setAdapter(workingTimeAdapterForThursday);
            binding.recyclerFriday.setAdapter(workingTimeAdapterForFriday);

            workingTimeAdapterForSaturday.setList(newAd.getDaysSchedule().getSaturday(),this);
            workingTimeAdapterForSunday.setList(newAd.getDaysSchedule().getSunday(),this);
            workingTimeAdapterForMonday.setList(newAd.getDaysSchedule().getMonday(),this);
            workingTimeAdapterForTuesday.setList(newAd.getDaysSchedule().getTuesday(),this);
            workingTimeAdapterForWednesday.setList(newAd.getDaysSchedule().getWednesday(),this);
            workingTimeAdapterForThursday.setList(newAd.getDaysSchedule().getThursday(),this);
            workingTimeAdapterForFriday.setList(newAd.getDaysSchedule().getFriday(),this);

            binding.recyclerSaturday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerSunday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerMonday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerTuesday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerWednesday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerThursday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            binding.recyclerFriday.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            handleSwitches();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void handleSwitches(){
        WorkingTime tempWorkingTime = new WorkingTime();
        binding.switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForSaturday.addItem(tempWorkingTime);
                }else workingTimeAdapterForSaturday.clearList();
            }
        });
        binding.switchSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForSunday.addItem(tempWorkingTime);
                }else workingTimeAdapterForSunday.clearList();
            }
        });
        binding.switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForMonday.addItem(tempWorkingTime);
                }else workingTimeAdapterForMonday.clearList();
            }
        });
        binding.switchTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForTuesday.addItem(tempWorkingTime);
                }else workingTimeAdapterForTuesday.clearList();
            }
        });
        binding.switchWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForWednesday.addItem(tempWorkingTime);
                }else workingTimeAdapterForWednesday.clearList();
            }
        });
        binding.switchThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForThursday.addItem(tempWorkingTime);
                }else workingTimeAdapterForThursday.clearList();
            }
        });
        binding.switchFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workingTimeAdapterForFriday.addItem(tempWorkingTime);
                }else workingTimeAdapterForFriday.clearList();
            }
        });
    }

    @Override
    public void fromTimeListener(int pos) {

    }

    @Override
    public void toTimeListener(int pos) {

    }

    @Override
    public void addTimeListener(int pos) {

    }

    @Override
    public void removeTimeListener(int pos) {

    }
}