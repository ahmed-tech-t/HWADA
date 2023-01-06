package com.example.hwada.ui.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hwada.ui.MainActivity;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.ActivitySignUpOrLoginInBinding;
import com.example.hwada.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpOrLoginIn extends AppCompatActivity implements View.OnClickListener {

    AuthViewModel viewModel;

    //google
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    //request code
    private int googleReqCode = 1000;

  ActivitySignUpOrLoginInBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set layout
        binding = ActivitySignUpOrLoginInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //google Authentication
        initGoogleSignInClient();

        binding.btSignUp.setOnClickListener(this);
        binding.btGoogle.setOnClickListener(this);
        binding.btLogin.setOnClickListener(this);

        //get viewModel ref
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AuthViewModel.class);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==binding.btLogin.getId()){
            Intent intent = new Intent(SignUpOrLoginIn.this, Login.class);
            startActivity(intent);
        }else if (v.getId() == binding.btSignUp.getId()){
            Intent intent = new Intent(SignUpOrLoginIn.this, SignUp.class);
            startActivity(intent);
        }else if (v.getId() == binding.btGoogle.getId()) {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, googleReqCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == googleReqCode){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "startActivityForResult: "+e.getMessage() );
                e.printStackTrace();
            }
        }
    }


    private void initGoogleSignInClient() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientId))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        viewModel.signInWithGoogle(googleAuthCredential);
        viewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew()) {
                createNewUser(authenticatedUser);
            } else {
                goToSplashActivity(authenticatedUser);
            }
        });
    }
    private void createNewUser(User authenticatedUser) {
        viewModel.createUser(authenticatedUser);
        viewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated()) {
                goToSplashActivity(user);
            }else Log.e(TAG, "createNewUser: failed" );
        });
    }
    private void goToSplashActivity(User user) {
        Intent intent = new Intent(SignUpOrLoginIn.this, SplashActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

}