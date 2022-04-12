package com.example.go4lunch.ui;


import static android.content.ContentValues.TAG;

import android.content.Context;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.example.go4lunch.modelApiNearby.Result;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.DataInput;
import java.util.Arrays;
import java.util.List;

public class RestaurantDetails extends Fragment {

    private List<Result> restaurantList;
    private Result restaurant;
    private MainActivityViewModel mainActivityViewModel;
    private String restaurantPlaceId;
    private ImageView call;
    private ImageView website;
    private ImageView like;
    private ImageView star;

    private ImageView restaurantImageView;
    private TextView restaurantName;
    private TextView address;
    private FloatingActionButton restaurantBooking;
    private static RestaurantDetails instance;
    private final String PREFERENCES_KEY = "PREFERENCES_KEY";
    private Uri websiteUrl;
    private String phoneNumber;


    public static RestaurantDetails getInstance() {
        if(instance== null)
        instance= new RestaurantDetails();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            restaurantPlaceId = RestaurantDetailsArgs.fromBundle(getArguments()).getPlaceId();
        }
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getRestaurantList();
        initRestaurant(view);
        getPlacePhoneNumberAndWebsite(restaurantPlaceId);
        getWebsite();
        callRestaurant();
        getLike();

        if (restaurantPlaceId.equals(mainActivityViewModel.getRestaurantBooking().getValue())){
            restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        }


        restaurantBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantBooking.getBackgroundTintList().equals(ColorStateList.valueOf(Color.WHITE))) {
                    restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    mainActivityViewModel.setRestaurantBooking(restaurantPlaceId);
                    setSharedPreferences(restaurantPlaceId);
                    // incrémenter le coworker number pour le restaurant
                } else {
                    restaurantBooking.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mainActivityViewModel.setRestaurantBooking(null);
                    setSharedPreferences(null);
                }

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


    private void initRestaurant(@NonNull View view) {
        restaurant = getRestaurantById(restaurantPlaceId);
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

    private void getRestaurantPicture(Result restaurant) {
        if (restaurant.getPhotos() != null) {
            String reference = restaurant.getPhotos().get(0).getPhotoReference();
            String pictureSize = Integer.toString(restaurant.getPhotos().get(0).getWidth());
            Glide.with(this.restaurantImageView.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key="+mainActivityViewModel.apiKey)
                    .into(this.restaurantImageView);
        } else {
            Glide.with(this.restaurantImageView.getContext())
                    .load(R.drawable.go4lunch)
                    .into(this.restaurantImageView);
        }
    }

    private void getPlacePhoneNumberAndWebsite(String restaurantPlaceId){
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

    private void callRestaurant(){
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:"+phoneNumber);
                dialIntent.setData(data);
                startActivity(dialIntent);

            }
        });

    }

    private void getWebsite(){
        website.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (websiteUrl != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, websiteUrl);
                    startActivity(browserIntent);
                }
                else{
                    Toast.makeText(getContext(), "No website accessible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getLike(){

        like.setOnClickListener(new View.OnClickListener() {
            int i = 0;
            @Override
            public void onClick(View v) {
                if (i==0) {
                    star.setVisibility(View.VISIBLE);
                    i++;
                }else {
                    star.setVisibility(View.GONE);
                    i--;
                }
            }
        });
    }
}