package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 123;

    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }
   private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        // Login Button
        binding.googleSignInButton.setOnClickListener(view -> {
            startSignInActivity();
        });
    }
        private void startSignInActivity () {
            //Choose authentication providers
            List<AuthUI.IdpConfig> providers =
                    Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

            // Launch the activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false, true)
                            .setLogo(R.drawable.go4lunch)
                            .build(),
                    RC_SIGN_IN);
        }
}