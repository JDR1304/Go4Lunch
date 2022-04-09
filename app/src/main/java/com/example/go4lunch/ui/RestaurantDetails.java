package com.example.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.modelApiNearby.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

public class RestaurantDetails extends Fragment {

    private List<Result> restaurantList;
    private Result restaurant;
    private MainActivityViewModel mainActivityViewModel;
    private String restaurantPlaceId;

    private ImageView restaurantImageView;
    private TextView restaurantName;
    private TextView address;
    private FloatingActionButton restaurantBooking;
    private static RestaurantDetails instance;
    private final String PREFERENCES_KEY = "PREFERENCES_KEY";

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
        getRestaurantPicture(restaurant);
        restaurantName.setText(restaurant.getName());
        address.setText(restaurant.getVicinity());
    }

    private void getRestaurantList() {
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                restaurantList = results;
                Log.e(TAG, "onChanged: in restaurantDetail" + results.size());
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(getActivity(), results);
    }

    public Result getRestaurantById(String placeId) {
        //Log.e(TAG, "getRestaurantById: "+placeId );
        for (int i = 0; i < restaurantList.size(); i++) {
//            Log.e(TAG, "getRestaurantById: "+restaurantList.size() );
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
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key=AIzaSyD6pekqGKHnG9bm4jbb21ges37dv2UgH5w")
                    .into(this.restaurantImageView);
        } else {
            Glide.with(this.restaurantImageView.getContext())
                    .load(R.drawable.go4lunch)
                    .into(this.restaurantImageView);
        }
    }

}