package com.example.go4lunch.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.listview.ListViewRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RestaurantDetails extends AppCompatActivity {

    private static final String RESTAURANT_ID_KEY = "RESTAURANT_ID_KEY";
    List<Result> restaurantList;
    Result restaurant;
    MainActivityViewModel mainActivityViewModel;
    String restaurantPlaceId;

    private ImageView restaurantImageView;
    private TextView restaurantName;
    private TextView address;
    private FloatingActionButton restaurantBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details);
        this.getSupportActionBar().hide();
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        restaurantList = mainActivityViewModel.getRestaurants().getValue();

        Bundle param = getIntent().getExtras();
        if (param != null)
            restaurantPlaceId = param.getString(RESTAURANT_ID_KEY);
        restaurant = getRestaurantById(restaurantPlaceId);
        restaurantImageView = findViewById(R.id.restaurant_details_image_view);
        restaurantName = findViewById(R.id.restaurant_details_name);
        address = findViewById(R.id.restaurant_details_address);
        restaurantBooking = findViewById(R.id.check_fab);
        getRestaurantPicture(restaurant);
        restaurantName.setText(restaurant.getName());
        address.setText(restaurant.getVicinity());

        restaurantBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //restaurantBooking.setBackgroundColor(Color.parseColor("#337536"));
                restaurantBooking.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#337536")));

            }
        });

    }

    public Result getRestaurantById(String placeId) {
        for (int i = 0; i < restaurantList.size(); i++) {
            Log.e(TAG, "getRestaurantById: " + restaurantList.get(i).getPlaceId());
            if (placeId.equals(restaurantList.get(i).getPlaceId())) {
                Log.e(TAG, "getRestaurantById: dans le if " + restaurantList.get(i));
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