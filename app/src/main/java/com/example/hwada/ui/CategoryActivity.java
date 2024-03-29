package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hwada.Model.User;
import com.example.hwada.R;

import com.example.hwada.application.App;
import com.example.hwada.database.DbHandler;
import com.example.hwada.databinding.ActivityCategoryBinding;
import com.example.hwada.ui.view.category.freelance.DeliveryFragment;
import com.example.hwada.ui.view.category.freelance.FreelanceCategoryFragment;
import com.example.hwada.ui.view.category.freelance.RideFragment;
import com.example.hwada.ui.view.category.worker.WorkerCategoryFragment;

public class CategoryActivity extends AppCompatActivity {

    ActivityCategoryBinding binding ;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    User user;
    private App app;

    String target;

    private static final String TAG = "CategoryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        target = intent.getStringExtra("target");
        String tagFragment = intent.getStringExtra("tag");

        app = (App) getApplication();

        fragmentManager = getSupportFragmentManager();
        handleFragmentsCall(tagFragment);
    }

    private void handleFragmentsCall(String tag){
        switch (tag){
            case DbHandler.WORKER :
                callFragment(new WorkerCategoryFragment(), DbHandler.WORKER,target);
                break;
            case DbHandler.FREELANCE :
              callMainFreelanceFragment();
              break;
        }
    }
    private void callFragment(Fragment fragment, String tag,String target){
      try {
          Bundle bundle = new Bundle();
          bundle.putParcelable("user", user);
          bundle.putString("target",target);
          fragment.setArguments(bundle);
          //set Animation
          fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(R.anim.to_up, R.anim.to_down);
          fragmentTransaction.replace(R.id.category_fragment_container, fragment, tag);
          fragmentTransaction .commit();

          //TODO on back pressed from fragment
      }catch (Exception e){
          e.getMessage();
          e.printStackTrace();
      }
    }
    public void callMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    public void callRideFragment(String target){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.car_image_background);
        binding.rightImage.setImageResource(R.drawable.bus_image_background);
        RideFragment fragment =new RideFragment();
        Bundle bundle = new Bundle();
        bundle.putString("target",target);
        fragment.setArguments(bundle);
        callFragment(fragment,DbHandler.RIDE,target);
    }
    public void callDeliveryFragment(String target){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.car_image_background);
        binding.rightImage.setImageResource(R.drawable.cycle_image_background);
        DeliveryFragment fragment =new DeliveryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("target",target);
        fragment.setArguments(bundle);
        callFragment(fragment,DbHandler.DELIVERY,target);
    }
    public void callMainFreelanceFragment(){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.nurse_image_background);
        binding.rightImage.setImageResource(R.drawable.maid_image_background);
        callFragment(new FreelanceCategoryFragment(),DbHandler.FREELANCE,target);
    }
    public void callAdsActivity(String category,String subCategory ,String subSubCategory){
        Intent intent = new Intent(this, AdsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category",category);
        intent.putExtra("subCategory",subCategory);
        intent.putExtra("subSubCategory",subSubCategory);

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.category_fragment_container);
        if(fragment instanceof FreelanceCategoryFragment || fragment instanceof WorkerCategoryFragment){
            super.onBackPressed();
        }else if (fragment instanceof RideFragment || fragment instanceof DeliveryFragment){
            callMainFreelanceFragment();
        }else {
            super.onBackPressed();
        }
    }

    public void callAddNewAdActivity(String category ,String subCategory , String subSubCategory){
        try {
            Intent intent = new Intent(this, AddNewAdActivity.class);
            intent.putExtra(getString(R.string.userVal),user);
            intent.putExtra(getString(R.string.modeVal),getString(R.string.newModeVal));
            intent.putExtra(getString(R.string.categoryVal),category);
            intent.putExtra(getString(R.string.subCategoryVal),subCategory);
            intent.putExtra(getString(R.string.subSubCategoryVal),subSubCategory);
            startActivity(intent);
        }catch (Exception e){
            app.reportError(e,this);
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


}