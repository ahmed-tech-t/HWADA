package com.example.hwada.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.hwada.Model.DebugModel;
import com.example.hwada.application.App;
import com.example.hwada.ui.MainActivity;
import com.example.hwada.Model.User;
import com.example.hwada.viewmodel.DebugViewModel;
import com.example.hwada.viewmodel.SplashViewModel;

import org.checkerframework.checker.units.qual.A;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SplashActivity extends AppCompatActivity {
    SplashViewModel splashViewModel;
    DebugViewModel debugViewModel;
    App app;
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debugViewModel = ViewModelProviders.of(this).get(DebugViewModel.class);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        app =(App)getApplication();
        checkIfUserIsAuthenticated();
    }


    private void checkIfUserIsAuthenticated() {
        try {
            splashViewModel.checkIfUserIsAuthenticated();
            splashViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
                if (!user.isAuthenticated) {
                    goToAuthInActivity();
                    finish();
                } else {
                    getUserFromDatabase(user.getUId());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            app.reportError(e,this);
        }
    }
    
    private void getUserFromDatabase(String uid) {
          try {
              splashViewModel.setUid(uid);
              splashViewModel.userLiveData.observe(this, user -> {
                  goToMainActivity(user);
                  finish();
              });
          }catch (Exception e){
              app.reportError(e,this);
          }
    }
    private void goToMainActivity(User user) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
    private void goToAuthInActivity() {
        Intent intent = new Intent(SplashActivity.this, SignUpOrLoginIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}