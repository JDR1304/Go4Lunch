package com.example.go4lunch;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;


import android.Manifest;
import android.annotation.SuppressLint;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.NavHeaderDrawerMainBinding;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.repository.UserRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private final int locationRequestCode = 1000;
    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private String placeIdBookedByUser;


    //Prediction List
    MutableLiveData<List<String>> liveDataPredictionEstablishmentList;

    //Toolbar custom
    private Toolbar toolbar;

    //Drawer Variable
    private ImageView headerDrawerImage;
    private TextView headerDrawerName;
    private TextView headerDrawerEmail;

    //Tie DrawerLayout and actionBar
    private ActionBarDrawerToggle toggle;

    //Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    //Config for all setting related to FusedLocationProviderClient
    private LocationRequest locationRequest;

    //private UserManager userManager = UserManager.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private FirebaseUser user;



    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        updateGps();
        configureToolbar();
        getRestaurantBookedByUser();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getRestaurantBookedByUser();
        checkIfUserLogged();
        updateGps();

    }

    private void checkIfUserLogged() {
        // Login/Profile Button
        if (userRepository.isCurrentUserLogged()) {
            user = userRepository.getCurrentUser();

            drawerBindingView();
            uiDrawerNavigation();
            uiBottomNavigation();

            setProfileUserDataInTheHeader(user);
            if (user.getPhotoUrl() != null) {
                setProfilePictureInTheHeader(user.getPhotoUrl());
            }
        } else {
            startSignInActivity();
        }
    }

    private void setProfileUserDataInTheHeader(FirebaseUser user) {
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        headerDrawerName.setText(username);
        headerDrawerEmail.setText(email);
    }

    private void setProfilePictureInTheHeader(Uri profilePictureUrl) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(headerDrawerImage);
    }

    //Configure Toolbar custom without Title
    private void configureToolbar() {
        this.toolbar = binding.bottomNavigation.toolbarCustom;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void drawerBindingView() {
        View headerView = binding.navViewDrawer.getHeaderView(0);
        NavHeaderDrawerMainBinding headerBinding = NavHeaderDrawerMainBinding.bind(headerView);
        headerDrawerImage = headerBinding.imageViewHeaderDrawer;
        headerDrawerName = headerBinding.textViewHeaderDrawerName;
        headerDrawerEmail = headerBinding.textViewHeaderDrawerEmail;
    }

    private void uiDrawerNavigation() {
        //Tie Toolbar custom and the drawerLayout
        DrawerLayout drawer = binding.drawerLayout;
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map_view, R.id.navigation_workmates, R.id.navigation_list_view)
                .setOpenableLayout(drawer)
                .build();

        NavController navDrawerController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navDrawerController, mAppBarConfiguration);


        //Bind NavGraph with Navigation Drawer
        NavigationView navigationView = binding.navViewDrawer;
        NavigationUI.setupWithNavController(navigationView, navDrawerController);

        // code ajouter pour la gestion de mon menu
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_logout:
                    userRepository.signOut(this).addOnSuccessListener(aVoid -> {
                        startSignInActivity();
                    });
                    return true;
                case R.id.restaurant_details:
                    if (placeIdBookedByUser == null) {
                        Toast.makeText(getApplicationContext(), "No restaurant chosen", Toast.LENGTH_LONG).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("place_id", placeIdBookedByUser);
                        navDrawerController.navigate(menuItem.getItemId(), bundle);

                    }
                    //This is for closing the drawer after acting on it
                    drawer.closeDrawer(GravityCompat.START);

                    return true;

                case R.id.nav_settings:
                    Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                    navDrawerController.navigate(R.id.nav_settings);
                    //This is for closing the drawer after acting on it
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

            }
            //This is for maintaining the behavior of the Navigation view
            NavigationUI.onNavDestinationSelected(menuItem, navDrawerController);

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
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
                userRepository.createUser();
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

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                getRestaurantByName(newText);
                //doSearch(newText);
                Log.e(TAG, "onQueryTextChange: " + newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "queryTextSubmit ", Toast.LENGTH_LONG).show();


                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        getRestaurantBookedByUser();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
//-------------------------------------------------------------------------------------------------------------------------------

    public void updateGps() {

        getLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Get the last known location. In some rare situations, this can be null.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            @SuppressLint("MissingPermission")
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(this, location -> {
                if (location != null) {
                    mainActivityViewModel.setLocation(location);
                    Log.e(TAG, "updateGps: " + location);

                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        }

    }

    private void getLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGps();
                } else {
                    Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getRestaurantBookedByUser() {
        if (mainActivityViewModel.getChosenRestaurantByUserFromFirestore(mainActivityViewModel.getCurrentUserUid()) != null) {
            Observer<String> placeIdBookByUser = new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    placeIdBookedByUser = s;
                }
            };
            mainActivityViewModel.getChosenRestaurantByUserFromFirestore(mainActivityViewModel.getCurrentUserUid()).observe(this, placeIdBookByUser);
        }
    }

    public void getRestaurantByName(String query) {

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.API_KEY);
        }
        List<String> predictionList = new ArrayList<>();

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        //Current location
        Location location = mainActivityViewModel.getLocation().getValue();
        // Create a RectangularBounds object.
        //get establishment around 1500 meters
        LatLng southWest = new LatLng(location.getLatitude() - 0.015, location.getLongitude() - 0.0145);
        LatLng northEast = new LatLng(location.getLatitude() + 0.015, location.getLongitude() + 0.0145);
        RectangularBounds bounds = RectangularBounds.newInstance(southWest, northEast);
        Log.e(TAG, "getRestaurantByName: rectangle sud est" + southWest + " nord est" + northEast);

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            predictionList.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                predictionList.add(prediction.getPrimaryText(null).toString());
            }
            mainActivityViewModel.setPredictionEstablishmentList(predictionList);
            Log.e(TAG, "getRestaurantByName: at the end " + predictionList);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }
}