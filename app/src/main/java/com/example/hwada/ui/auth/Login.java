package com.example.hwada.ui.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.hwada.ui.MainActivity;
import com.example.hwada.Model.User;
import com.example.hwada.R;
import com.example.hwada.databinding.ActivityLoginBinding;
import com.example.hwada.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener {
    AuthViewModel viewModel;

    //google
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    //request code
    private int googleReqCode = 1000;

    ActivityLoginBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //google Authentication
        initGoogleSignInClient();

        //set listener
        binding.btLogin.setOnClickListener(this);
        binding.btGoogle.setOnClickListener(this);
        binding.loginMainConstraintLayout.setOnClickListener(this);
        binding.loginMainLinearLayout.setOnClickListener(this);
        binding.tvLogin.setOnClickListener(this);

        //get viewModel ref
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AuthViewModel.class);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == binding.btLogin.getId() && fieldsNotEmpty()){
            String email = binding.etEmail.getText().toString();
            String pass = binding.etPass.getText().toString();
            loginWithEmail(email , pass);
        }else if(v.getId()== binding.btGoogle.getId()){
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, googleReqCode);
        }else if (v.getId()==binding.loginMainLinearLayout.getId()){
            hideKeyBoard(v);
        }else if (v.getId()==binding.loginMainConstraintLayout.getId()){
            hideKeyBoard(v);
        }else if (v.getId()==binding.tvLogin.getId()){
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
            finish();
        }
    }
    private void loginWithEmail(String email ,String pass){
            viewModel.loginWithEmail(email,pass);
            viewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
                goToSplashActivity(authenticatedUser);
        });
    }
    private boolean fieldsNotEmpty() {
        boolean validate = false;
        if (TextUtils.isEmpty(binding.etEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches())
            binding.etEmail.setError("Invalid Email");
        if (binding.etPass.getText().toString().length() < 6) binding.etPass.setError("To Short");
        if (binding.etEmail.getError() == null && binding.etPass.getError() == null ) {
            validate = true;
        }
        return validate;
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
            goToSplashActivity(authenticatedUser);
        });
    }
    private void goToSplashActivity(User user) {
        Intent intent = new Intent(Login.this, SplashActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public void hideKeyBoard(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}