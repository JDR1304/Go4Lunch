package com.example.go4lunch.ui.mapview;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.modelApiNearby.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapViewFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MainActivityViewModel mainActivityViewModel;
    private static final int DEFAULT_ZOOM = 14;
    private Location location;
    private String placeId;
    private List <Result> restaurants = new ArrayList<>();
    private Marker marker;
    private MapViewFragmentDirections.ActionNavigationMapViewToNavigationRestaurantDetails action;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View viewMap = inflater.inflate(R.layout.fragment_map_view, container, false);
        return viewMap;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.getLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                MapViewFragment.this.location = location;
                mapFragment.getMapAsync(MapViewFragment.this);

            }
        });
        /*getRestaurant();
        getRestaurantFromFirestore();
        getPredictionEstablishment();*/

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getRestaurant();
        getPredictionEstablishment();
        getRestaurantFromFirestore();
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        // Add a marker at current place and move the camera
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentPosition).title("Current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));


        LatLng southWest = new LatLng(location.getLatitude() - 0.015, location.getLongitude()-0.0145);
        LatLng northEast = new LatLng(location.getLatitude() + 0.015, location.getLongitude() + 0.0145);

        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(southWest));
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(northEast));

    }

    public void getRestaurant() {
        // Get Users From Random API
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                restaurants.addAll(results);
                LatLng restaurantPosition;
                for (int i = 0; i < results.size(); i++) {
                    restaurantPosition = new LatLng(results.get(i).getGeometry().getLocation().getLat(), results.get(i).getGeometry().getLocation().getLng());
                    mMap.addMarker(new MarkerOptions().position(restaurantPosition).title(results.get(i).getName()));
                }
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg0) {
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).getName().equals(arg0.getTitle())) {
                                placeId = results.get(i).getPlaceId();
                            }

                        }
                        action = MapViewFragmentDirections.actionNavigationMapViewToNavigationRestaurantDetails();
                        action.setPlaceId(placeId);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(action);

                        return true;
                    }

                });
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(this, results);
    }

    public void getRestaurantFromFirestore() {
        Observer<List<Restaurant>> restaurants = new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
                if (restaurantList != null) {
                    for (int i = 0; i < restaurantList.size(); i++) {
                        LatLng restaurantPosition = new LatLng(restaurantList.get(i).getGeometry().getLocation().getLat(), restaurantList.get(i).getGeometry().getLocation().getLng());
                        restaurantList.get(i).getGeometry();
                        if (mMap != null) {
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .position(restaurantPosition).title(restaurantList.get(i).getName()));
                        }
                    }

                }
            }
        };
        mainActivityViewModel.getRestaurantListFromFirestore().observe(this, restaurants);
    }

    public void getPredictionEstablishment() {
        Observer<List<String>> establishments = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> predictions) {
                Log.e(TAG, "onChanged: MapFragment" + predictions);
                if (predictions.size() > 0) {
                    mMap.clear();
                    for (int i = 0; i<predictions.size(); i++){
                        for (int j =0; j<restaurants.size(); j++){
                            if (predictions.get(i).equals(restaurants.get(j).getName())){
                                LatLng restaurantPosition = new LatLng(restaurants.get(j).getGeometry().getLocation().getLat(), restaurants.get(j).getGeometry().getLocation().getLng());
                                mMap.addMarker(new MarkerOptions()
                                        .position(restaurantPosition).title(restaurants.get(j).getName()));
                                break;
                            }
                        }
                    }

                } else {
                    getRestaurant();
                }

            }
        };
        mainActivityViewModel.getPredictionEstablishmentList().observe(this, establishments);
    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getRestaurant();
        getPredictionEstablishment();
        getRestaurantFromFirestore();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //marker.remove();
    }
}

