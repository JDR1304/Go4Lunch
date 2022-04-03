package com.example.go4lunch.ui.mapview;


import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.listview.ListViewRecyclerViewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapViewFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MainActivityViewModel mainActivityViewModel;
    private static final int DEFAULT_ZOOM = 15;
    private Marker marker;
    private Location location;


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
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        // Add a marker at current place and move the camera
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, DEFAULT_ZOOM));
        getRestaurant();
        markerClickListener();
    }

    public void getRestaurant() {

        // Get Users From Random API
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                results = mainActivityViewModel.getRestaurants().getValue();
                LatLng restaurantPosition;
                for (int i = 0; i<results.size(); i++){
                    restaurantPosition = new LatLng(results.get(i).getGeometry().getLocation().getLat(), results.get(i).getGeometry().getLocation().getLng());
                    marker = mMap.addMarker(new MarkerOptions().position(restaurantPosition).title(results.get(i).getName()));
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(this, results);
    }

    public void markerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(getActivity(), "marker ", Toast.LENGTH_SHORT).show();
                return true;
            }

        });
    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        marker.remove();
    }
}

