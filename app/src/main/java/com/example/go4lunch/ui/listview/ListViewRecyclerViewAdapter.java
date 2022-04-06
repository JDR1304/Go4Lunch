package com.example.go4lunch.ui.listview;




import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.RestaurantDetails;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private static final String RESTAURANT_ID_KEY = "RESTAURANT_ID_KEY";
    List<Result> restaurantList;
    MainActivityViewModel mainActivityViewModel;
    int numberOfCoworker = 4;
    int i = 40;


    public ListViewRecyclerViewAdapter(List<Result> restaurantList, MainActivityViewModel viewModel) {
        this.restaurantList = restaurantList;
        this.mainActivityViewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRecyclerViewAdapter.ViewHolder holder, int position) {
        Result restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.distance.setText(Integer.toString(getDistance(restaurant)) + "m");
        holder.address.setText(restaurant.getVicinity());
        getOpenHours(holder, restaurant);
        getRestaurantPicture(holder, restaurant);

        if  (i>0 && i<30) {
            holder.star1.setVisibility(View.VISIBLE);
        }
        else if (i>=30 && i<60) {
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
        }

        else if (i>=60 && i<100) {
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.VISIBLE);
        }

        if (numberOfCoworker>1){
            holder.coworkerImage.setVisibility(View.VISIBLE);
            holder.coworkerNumber.setVisibility(View.VISIBLE);
            holder.coworkerNumber.setText("("+Integer.toString(numberOfCoworker)+")");

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)v.getContext();
                Intent intent = new Intent(mainActivity, RestaurantDetails.class);
                Bundle param = new Bundle();
                param.putString(RESTAURANT_ID_KEY, restaurant.getPlaceId());
                Log.e(TAG, "onClick: "+param );
                intent.putExtras(param);
                mainActivity.startActivity(intent);

            }
        });
    }

    private void getRestaurantPicture(@NonNull ViewHolder holder, Result restaurant) {
        if (restaurant.getPhotos() != null) {
            String reference = restaurant.getPhotos().get(0).getPhotoReference();
            String pictureSize = Integer.toString(restaurant.getPhotos().get(0).getWidth());
            Glide.with(holder.restaurantImageView.getContext())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key=AIzaSyD6pekqGKHnG9bm4jbb21ges37dv2UgH5w")
                    .into(holder.restaurantImageView);
        } else {
            Glide.with(holder.restaurantImageView.getContext())
                    .load(R.drawable.go4lunch)
                    .into(holder.restaurantImageView);
        }
    }

    private void getOpenHours(@NonNull ViewHolder holder, Result restaurant) {
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow() == false) {
                holder.openHours.setText("Close");
            } else {
                holder.openHours.setText("Open");
            }
        } else {
            holder.openHours.setText("No info");
        }
    }

    private int getDistance(@NonNull Result restaurant) {
        Location currentLocation = new Location("");
        Location restaurantLocation = new Location("");
        currentLocation.setLatitude(mainActivityViewModel.getLocation().getValue().getLatitude());
        currentLocation.setLongitude(mainActivityViewModel.getLocation().getValue().getLongitude());
        restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());
        restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
        int distance = (int) currentLocation.distanceTo(restaurantLocation);
        return distance;
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView restaurantImageView;
        private TextView restaurantName;
        private TextView distance;
        private TextView address;
        private TextView openHours;
        private ImageView coworkerImage;
        private TextView coworkerNumber;
        private ImageView star1;
        private ImageView star2;
        private ImageView star3;

        public ViewHolder(View view) {
            super(view);
            restaurantImageView = view.findViewById(R.id.restaurant_imageView);
            restaurantName = view.findViewById(R.id.restaurant_details_name);
            distance = view.findViewById(R.id.distance);
            address = view.findViewById(R.id.address);
            openHours = view.findViewById(R.id.open_hours);
            coworkerImage = view.findViewById(R.id.listview_coworker);
            coworkerNumber = view.findViewById(R.id.listview_coworker_number);
            star1 = view.findViewById(R.id.listview_star1);
            star2 = view.findViewById(R.id.listview_star2);
            star3 = view.findViewById(R.id.listview_star3);



        }
    }

}
