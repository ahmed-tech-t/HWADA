package com.example.hwada.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hwada.Model.User;
import com.example.hwada.ui.view.main.FavoritesFragment;

import java.util.ArrayList;

public class TabLayoutAdapter extends FragmentStateAdapter {
   String[] category;
   User user;
   String TAG ="TabLayoutAdapter";
    public TabLayoutAdapter(Fragment fragment ,String [] category ,User user ) {
        super(fragment);
        this.category= category;
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        Log.e(TAG, "createFragment: "+position );
        args.putParcelable("user",user);
        args.putString("category",category[position]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
