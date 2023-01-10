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
import com.example.hwada.databinding.ActivitySignUpBinding;
import com.example.hwada.viewmodel.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    AuthViewModel viewModel;
    //google
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    //request code
    private int googleReqCode = 1000;

    ActivitySignUpBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set layout
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //google Authentication
        initGoogleSignInClient();

        //set listener
        binding.btSignUp.setOnClickListener(this);
        binding.btGoogle.setOnClickListener(this);
        binding.signUpMainConstraintLayout.setOnClickListener(this);
        binding.signUpMainLinearLayout.setOnClickListener(this);
        binding.tvSignIn.setOnClickListener(this);

        //get viewModel ref
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(AuthViewModel.class);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.btSignUp.getId() && fieldsNotEmpty()) {
            signUpWithEmail();
        } else if (v.getId() == binding.btGoogle.getId()) {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, googleReqCode);
        } else if (v.getId()==binding.signUpMainLinearLayout.getId()){
            hideKeyBoard(v);
        }else if (v.getId()==binding.signUpMainConstraintLayout.getId()){
            hideKeyBoard(v);
        }else if (v.getId()==binding.tvSignIn.getId()){
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
            finish();
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


    private boolean fieldsNotEmpty() {
        boolean validate = false;
        if (TextUtils.isEmpty(binding.etUsername.getText().toString())) binding.etUsername.setError("Invalid Username");
        if (TextUtils.isEmpty(binding.etEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches())
            binding.etEmail.setError("Invalid Email");
        if (TextUtils.isEmpty(binding.etPhone.getText().toString()) || !Patterns.PHONE.matcher(binding.etPhone.getText().toString()).matches())
            binding.etPhone.setError("Invalid Phone");
        if (binding.etPass.getText().toString().length() < 6) binding.etPass.setError("To Short");
        if (!binding.etConfPass.getText().toString().equals(binding.etConfPass.getText().toString()))
            binding.etConfPass.setError("Confirm password must be like 'password'");

        if (binding.etUsername.getError() == null && binding.etEmail.getError() == null &&
                binding.etPhone.getError() == null && binding.etPass.getError() == null && binding.etConfPass.getError() == null) {
            validate = true;
        }
        return validate;
    }

    private void signUpWithEmail() {
        String email = binding.etEmail.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String username = binding.etUsername.getText().toString();
        String password = binding.etPass.getText().toString();

        User userModel = new User(username, email, phone);
        Log.e(TAG, "signUpWithEmail: "+userModel.getEmail() );
        viewModel.signUpWithEmail(userModel, password);
        viewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if (authenticatedUser.isNew()) {
                createNewUser(authenticatedUser);
            } else {
                goToSplashActivity(authenticatedUser);
            }
        });
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
        Intent intent = new Intent(SignUp.this, SplashActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public void hideKeyBoard(View v){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}