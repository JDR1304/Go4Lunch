package com.example.go4lunch.ui.listview;


import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.ui.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.RetrieveIdRestaurant;
import com.example.go4lunch.modelApiNearby.Result;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder>  {


    private List<Result> restaurantList;
    private MainActivityViewModel mainActivityViewModel;
    private double rating;
    private RetrieveIdRestaurant listener;


    public ListViewRecyclerViewAdapter(List<Result> restaurantList, MainActivityViewModel viewModel, RetrieveIdRestaurant listener) {
        this.restaurantList = restaurantList;
        this.mainActivityViewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.distance.setText(Integer.toString(getDistance(restaurant)) + "m");
        holder.address.setText(restaurant.getVicinity());
        getOpenHours(holder, restaurant);
        getRestaurantPicture(holder, restaurant);
        if (getRating(restaurant)!=null) {
            rating = getRating(restaurant);
        }
        if (rating >=0 && rating < 3) {
            holder.star1.setVisibility(View.GONE);
            holder.star2.setVisibility(View.GONE);
            holder.star3.setVisibility(View.GONE);
        } else if (rating >=3 && rating < 4) {
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.GONE);
            holder.star3.setVisibility(View.GONE);
        } else if (rating >= 4 && rating < 4.5) {
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.GONE);
        } else if (rating >= 4.5 && rating <= 5) {
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(restaurant.getPlaceId());
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

    private Double getRating(Result restaurant) {
        return restaurant.getRating();
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
