package com.example.hwada.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.ActivityCategoryBinding;
import com.example.hwada.databinding.ActivityMainBinding;
import com.example.hwada.ui.view.category.DeliveryFragment;
import com.example.hwada.ui.view.category.FreelanceCategoryFragment;
import com.example.hwada.ui.view.category.RideFragment;
import com.example.hwada.ui.view.category.WorkerCategoryFragment;

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
        handleFragmentsCall(tagFragment);
    }

    private void handleFragmentsCall(String tag){
        switch (tag){
            case "food" :
                //TODO
                break;
            case "worker" :
                callFragment(new WorkerCategoryFragment(),"worker");
                break;
            case "freelance" :
                binding.topIcons.setVisibility(View.VISIBLE);
                binding.leftImage.setImageResource(R.drawable.nurse_image_background);
                binding.rightImage.setImageResource(R.drawable.maid_image_background);
                callFragment(new FreelanceCategoryFragment(),"freelance");
                break;
            case "handcraft":
                //TODO
                break;
        }
    }
    private void callFragment(Fragment fragment, String tag){
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        fragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.to_up,R.anim.to_down);
        //set Animation

        fragmentTransaction.replace(R.id.category_fragment_container, fragment,tag);

        //TODO on back pressed from fragment
        fragmentTransaction.commit();
    }
    private void callMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user",user);
        Bundle b = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent,b);
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

    @Override
    public void onBackPressed() {
      callMainActivity();
    }
}