package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hwada.Model.User;
import com.example.hwada.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        user = (User) intent.getParcelableExtra("user");
        String tagFragment = intent.getStringExtra("tag");
        fragmentManager = getSupportFragmentManager();

        handleFragmentsCall(tagFragment);
    }

    private void handleFragmentsCall(String tag){
        switch (tag){
            case "worker" :
                callFragment(new WorkerCategoryFragment(),"worker");
                break;
            case "freelance" :
              callMainFreelanceFragment();
              break;
        }
    }
    private void callFragment(Fragment fragment, String tag){
      try {
          Bundle bundle = new Bundle();
          bundle.putParcelable("user", user);
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
        //Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent);
        finish();
    }

    public void callRideFragment(){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.car_image_background);
        binding.rightImage.setImageResource(R.drawable.bus_image_background);
        callFragment(new RideFragment(),"ride");
    }
    public void callDeliveryFragment(){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.car_image_background);
        binding.rightImage.setImageResource(R.drawable.cycle_image_background);
        callFragment(new DeliveryFragment(),"delivery");
    }
    public void callMainFreelanceFragment(){
        binding.topIcons.setVisibility(View.VISIBLE);
        binding.leftImage.setImageResource(R.drawable.nurse_image_background);
        binding.rightImage.setImageResource(R.drawable.maid_image_background);
        callFragment(new FreelanceCategoryFragment(),"freelance");
    }
    public void callAdsActivity(String category,String subCategory){
        Intent intent = new Intent(this, AdsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category",category);
        intent.putExtra("subCategory",subCategory);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.category_fragment_container);
        if(fragment instanceof FreelanceCategoryFragment || fragment instanceof WorkerCategoryFragment){
            callMainActivity();
        }else if (fragment instanceof RideFragment || fragment instanceof DeliveryFragment){
            callMainFreelanceFragment();
        }else super.onBackPressed();
    }
}