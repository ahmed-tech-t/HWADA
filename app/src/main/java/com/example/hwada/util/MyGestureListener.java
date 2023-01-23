package com.example.hwada.util;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.material.tabs.TabLayout;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final TabLayout tabLayout;

    public MyGestureListener(TabLayout tabLayout){
    this.tabLayout = tabLayout;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    public void onSwipeRight() {
        int currentTab = tabLayout.getSelectedTabPosition();
        if (currentTab > 0) {
            tabLayout.getTabAt(currentTab - 1).select();
        }
    }

    public void onSwipeLeft() {
        int currentTab = tabLayout.getSelectedTabPosition();
        if (currentTab < tabLayout.getTabCount() - 1) {
            tabLayout.getTabAt(currentTab + 1).select();
        }
    }
}
