package com.example.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.RetrieveIdRestaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.workmates.WorkmatesRecyclerViewAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantDetails extends Fragment {

    private List<Result> restaurantList;
    private List<User> usersList = new ArrayList<>();
    private Result restaurant;
    private User currentUser;
    private MainActivityViewModel mainActivityViewModel;
    private String placeId;
    private ImageView call;
    private ImageView website;
    private ImageView like;
    private ImageView star;

    private ImageView restaurantImageView;
    private TextView restaurantName;
    private TextView address;
    private FloatingActionButton restaurantBooking;
    private int setColorFloatingButton;
    private static RestaurantDetails instance;
    private final String PREFERENCES_KEY = "PREFERENCES_KEY";

    private Uri websiteUrl;
    private String phoneNumber;

    private RecyclerView recyclerView;
    private WorkmatesRecyclerViewAdapter workmatesRecyclerViewAdapter;


    public static RestaurantDetails getInstance() {
        if (instance == null)
            instance = new RestaurantDetails();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        if (getArguments() != null && getArguments().containsKey("place_id")) {
            placeId = getArguments().getString("place_id");
        } else {
            getRestaurantPlaceId();
        }
        getRestaurantList();
        getUsers();
        getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRestaurant(view);// Avec la liste et l'id j'identifie le resto et je créé la vue

        //displayRecyclerView();
        //setFloatingButton(); // je mets le bouton vers si l'id correspond a celui reservé par l'user
        restaurantBooked();
        getPlacePhoneNumberAndWebsite(placeId);
        getWebsite();
        callRestaurant();
        getLike();
    }

    private void getRestaurantPlaceId() {
        /*if (RestaurantDetailsArgs.fromBundle(getArguments()).getPlaceId().equals("from drawer")) {
            placeId = mainActivityViewModel.getRestaurantBooking();
            setColorFloatingButton = 1;
        } else {
            placeId = RestaurantDetailsArgs.fromBundle(getArguments()).getPlaceId();
            setColorFloatingButton = 0;
        }*/
        placeId = RestaurantDetailsArgs.fromBundle(getArguments()).getPlaceId();
    }

    private void getRestaurantList() {
        restaurantList = mainActivityViewModel.getRestaurants().getValue();
    }

    public Result getRestaurantById(String placeId) {
        for (int i = 0; i < restaurantList.size(); i++) {
            if (placeId.equals(restaurantList.get(i).getPlaceId())) {
                return restaurantList.get(i);
            }
        }
        return null;
    }

    private void initRestaurant(@NonNull View view) {
        restaurant = getRestaurantById(placeId);
        restaurantImageView = view.findViewById(R.id.restaurant_details_image_view);
        restaurantName = view.findViewById(R.id.restaurant_details_name);
        address = view.findViewById(R.id.restaurant_details_address);
        restaurantBooking = view.findViewById(R.id.check_fab);
        call = view.findViewById(R.id.image_view_phone);
        website = view.findViewById(R.id.image_view_earth);
        like = view.findViewById(R.id.image_view_star);
        star = view.findViewById(R.id.restaurant_details_star);
        getRestaurantPicture(restaurant);
        restaurantName.setText(restaurant.getName());
        address.setText(restaurant.getVicinity());
        recyclerView = view.findViewById(R.id.restaurant_details_recyclerView);
    }

    public void getButtonGreen() {
        restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
    }

    public void getButtonWhite() {
        restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
    }

    public void setFloatingButton() {
        if (setColorFloatingButton == 1) {
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (setColorFloatingButton == 0 /*&& currentUser.getRestaurantPlaceId().equals(placeId)*/) {
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }
        /*Log.e(TAG, "setFloatingButton: " + getCurrentUser().getRestaurantPlaceId());
        if (getCurrentUser().getRestaurantPlaceId() == null || getCurrentUser().getRestaurantPlaceId() != placeId) {
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }*/

    }

    /*public void displayRecyclerView() {
        List<User> usersListForRecyclerView = new ArrayList<>();
        Observer<List<User>> users = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getRestaurantPlaceId() == placeId) {
                        usersListForRecyclerView.add(users.get(i));
                    }
                }
                if (usersListForRecyclerView != null) {
                    workmatesRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter(usersListForRecyclerView, mainActivityViewModel, new RetrieveIdRestaurant() {
                        @Override
                        public void onClickItem(String placeId) {
                            Log.e(TAG, "onClickItem: " + placeId);
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(workmatesRecyclerViewAdapter);
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(getActivity(), users);
    }*/

    /*private List<User> getUsers(String placeId){
        Observer<List<User>> users = new Observer<List<User>> () {
            @Override
            public void onChanged(@Nullable List<User> users) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getRestaurantPlaceId() == placeId) {
                        usersList.add(users.get(i));
                    }
                }
                if (usersList!=null) {
                    workmatesRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter(usersList, mainActivityViewModel, new RetrieveIdRestaurant() {
                        @Override
                        public void onClickItem(String placeId) {
                            Log.e(TAG, "onClickItem: " + placeId);
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(workmatesRecyclerViewAdapter);
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(getActivity(),users);
        return usersList;
    }*/

    public List<User> getUsers() {
        Observer<List<User>> users = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                for (int i = 0; i < users.size(); i++) {
                    Log.e(TAG, "onChanged: in getUsers" + users.get(i).getName());
                    usersList.add(users.get(i));
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(getActivity(), users);
        return usersList;
    }

    public void getCurrentUser() {
        // récupération du current user et verification pour set FAB
        Observer<List<User>> users = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                Log.e(TAG, "getCurrentUser: " + users);
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUid().equals(mainActivityViewModel.getCurrentUserUid())
                            && users.get(i).getRestaurantPlaceId() == null) {
                        getButtonWhite();
                        break;
                    } else if (users.get(i).getUid().equals(mainActivityViewModel.getCurrentUserUid()) &&
                            users.get(i).getRestaurantPlaceId().equals(placeId)) {
                        getButtonGreen();
                        break;
                    }
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(getActivity(), users);
    }

    private void restaurantBooked() {
        restaurantBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantBooking.getBackgroundTintList().equals(ColorStateList.valueOf(Color.WHITE))) {
                    restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    if (placeId != null) {
                        mainActivityViewModel.deleteRestaurant(placeId);
                    }
                    mainActivityViewModel.updateRestaurantPlaceId(placeId);
                    mainActivityViewModel.createRestaurant(placeId, 0);
                    Log.e(TAG, "onClick: in if " + placeId);
                    //mainActivityViewModel.setRestaurantBooking(placeId);
                    //setSharedPreferences(placeId);
                    // incrémenter le coworker number pour le restaurant
                } else {
                    restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mainActivityViewModel.updateRestaurantPlaceId(null);
                    mainActivityViewModel.deleteRestaurant(placeId);
                    Log.e(TAG, "onClick: in else " + placeId);
                    //mainActivityViewModel.setRestaurantBooking(null);
                    //setSharedPreferences(null);
                }
                //getUsers(placeId);
                Log.e(TAG, "onClick: in getUsers " + placeId);
            }
        });
    }


    private void setSharedPreferences(String id) {
        //La création d'un objet de type sharedPreferences pour stocker des données privées (primitives) sous forme clé/valeur.
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("preferences", 0);
        //La création d'un éditeur pour valider les données
        SharedPreferences.Editor editor = preferences.edit();
        //Ajout des données
        editor.putString(PREFERENCES_KEY, id);
        //Validation des données dans le shared Préferences
        editor.commit();
        editor.apply();
    }


    private void getRestaurantPicture(Result restaurant) {
        if (restaurant.getPhotos() != null) {
            String reference = restaurant.getPhotos().get(0).getPhotoReference();
            String pictureSize = Integer.toString(restaurant.getPhotos().get(0).getWidth());
            Glide.with(this.restaurantImageView.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key=" + mainActivityViewModel.apiKey)
                    .into(this.restaurantImageView);
        } else {
            Glide.with(this.restaurantImageView.getContext())
                    .load(R.drawable.go4lunch)
                    .into(this.restaurantImageView);
        }
    }

    private void getPlacePhoneNumberAndWebsite(String restaurantPlaceId) {
        // Initialize Places.
        Places.initialize(getActivity(), mainActivityViewModel.apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());
        // Define a Place ID.
        final String placeId = restaurantPlaceId;

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUrl = place.getWebsiteUri();

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                final int statusCode = apiException.getStatusCode();

            }
        });
    }

    private void callRestaurant() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneNumber);
                dialIntent.setData(data);
                startActivity(dialIntent);

            }
        });

    }

    private void getWebsite() {
        website.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (websiteUrl != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, websiteUrl);
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(getContext(), "No website accessible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getLike() {
        like.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                if (i == 0) {
                    star.setVisibility(View.VISIBLE);
                    mainActivityViewModel.likeIncrement(placeId);
                    i++;
                } else {
                    star.setVisibility(View.GONE);
                    mainActivityViewModel.likeDecrement(placeId);
                    i--;
                }
            }
        });
    }

}