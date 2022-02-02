package com.example.go4lunch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.NavHeaderDrawerMainBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.repository.UserRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int RC_SIGN_IN = 123;
    private AppBarConfiguration mAppBarConfiguration;

    //Drawer Variable
    private ImageView headerDrawerImage;
    private TextView headerDrawerName;
    private TextView headerDrawerEmail;


    private UserManager userManager = UserManager.getInstance();
    FirebaseUser user;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.bottomNavigation.toolbar);

        //drawerBindingView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfUserLogged();
        //uiDrawerNavigation();
        //uiBottomNavigation();

    }
    private void checkIfUserLogged(){
        // Login/Profile Button
            if(userManager.isCurrentUserLogged()){
                user = userManager.getCurrentUser();
                drawerBindingView();
                uiDrawerNavigation();
                uiBottomNavigation();

                setProfilUserData(user);
                if(user.getPhotoUrl() != null){
                    setProfilePicture(user.getPhotoUrl());
                }
            }else{
                startSignInActivity();
            }

    }

    private void drawerBindingView() {
        View headerView = binding.navViewDrawer.getHeaderView(0);
        NavHeaderDrawerMainBinding headerBinding = NavHeaderDrawerMainBinding .bind(headerView);
        headerDrawerImage = headerBinding.imageViewHeaderDrawer;
        headerDrawerName = headerBinding.textViewHeaderDrawerName;
        headerDrawerEmail = headerBinding.textViewHeaderDrawerEmail;
    }

    private void setProfilUserData(FirebaseUser user){
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        headerDrawerName.setText(username);
        headerDrawerEmail.setText(email);
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(headerDrawerImage);
    }

    private void uiDrawerNavigation() {

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navViewDrawer;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_lunch, R.id.nav_settings, R.id.nav_logout,R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates)
                .setOpenableLayout(drawer)
                .build();
        NavController navDrawerController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navDrawerController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navDrawerController);

    }

    private void uiBottomNavigation() {
        NavController navBottomController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(binding.bottomNavigation.navView, navBottomController);
    }

    private void startSignInActivity() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);

    }

    // Show Snack Bar with a message
    private void showSnackBar(String message) {
        Snackbar.make(binding.bottomNavigation.container, message, Snackbar.LENGTH_SHORT).show();
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
               userManager.createUser();
            } else {
                // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }

    /*
    Methods for the drawer
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}