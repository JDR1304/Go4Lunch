package com.example.go4lunch.ui.listview;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.RetrieveIdRestaurant;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.modelApiNearby.Result;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.go4lunch.ui.RestaurantDetailsArgs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListViewFragment extends Fragment {


    private FragmentListViewBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;
    private ListViewRecyclerViewAdapter listViewRecyclerViewAdapter;
    private ListViewFragmentDirections.ActionNavigationListViewToNavigationRestaurantDetails action;
    private List<Result> restaurants = new ArrayList<>();
    private List<Result> predictionsList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allow to go into onCreateOptionsMenu
        setHasOptionsMenu(true);
        // Manage my custom toolbar in my ListFragment
        toolbar = getActivity().findViewById(R.id.toolbar_custom);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem filterButton = menu.findItem(R.id.filter);
        filterButton.setVisible(true);
        toolbar.setTitle(null);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentListviewRecyclerview;
       // mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        configureViewModel();
        getToolbar();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        getPredictionEstablishment();
        getRestaurants();
        getRestaurantFromFirestore();

    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        this.mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getRestaurants() {
        // Get Users From Random API
        restaurants.clear();
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i).getRating() == null) {
                        results.get(i).setRating(0.0);
                    }
                    restaurants.add(results.get(i));
                }
                //restaurants.addAll(results);
                listViewRecyclerViewAdapter = new ListViewRecyclerViewAdapter(restaurants, mainActivityViewModel, new RetrieveIdRestaurant() {

                    @Override
                    public void onClickItem(String placeId) {
                        action = ListViewFragmentDirections.actionNavigationListViewToNavigationRestaurantDetails();
                        action.setPlaceId(placeId);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(action);
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(listViewRecyclerViewAdapter);
            }

        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(this, results);
    }

    public void getPredictionEstablishment() {
        Observer<List<String>> establishments = new Observer<List<String>>() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(List<String> predictions) {
                Log.e(TAG, "onChanged: MapFragment" + predictions);
                if (predictions.size() > 0) {
                    predictionsList.clear();
                    for (int i = 0; i < predictions.size(); i++) {
                        for (int j = 0; j < restaurants.size(); j++) {
                            if (predictions.get(i).equals(restaurants.get(j).getName())) {
                                predictionsList.add(restaurants.get(j));
                                break;
                            }
                        }
                    }
                    if (predictionsList.size() > 0) {
                        restaurants.clear();
                        restaurants.addAll(predictionsList);
                        //recyclerView.setAdapter(listViewRecyclerViewAdapter);
                        listViewRecyclerViewAdapter.notifyDataSetChanged();
                    }
                } else {
                    restaurants.clear();
                    getRestaurants();
                }

            }
        };
        mainActivityViewModel.getPredictionEstablishmentList().observe(this, establishments);
    }

    public void getRestaurantFromFirestore() {
        Observer<List<Restaurant>> restaurants = new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
            }
        };
        mainActivityViewModel.getRestaurantListFromFirestore().observe(this, restaurants);
    }

    public void getToolbar() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sort_by_distance:
                        sortRestaurantByDistance();
                        return true;

                    case R.id.sort_by_stars:
                        sortRestaurantByStars();
                        return true;

                }
                return false;
            }
        });
    }

    public void sortRestaurantByDistance() {
        List<Integer> distance = new ArrayList<>();
        // je récupère les distances des restaurants dans une liste nommée distance
        for (int i = 0; i < restaurants.size(); i++) {
            distance.add(getDistance(restaurants.get(i)));
            Log.e(TAG, "sortRestaurantByDistance: " + getDistance(restaurants.get(i)));
        }
        // je tris la liste distance du plus prés au plus loin
        Collections.sort(distance);
        Log.e(TAG, "sortRestaurantByDistance sorted: " + distance);
        List<Result> restaurantsSorted = new ArrayList<>();
        // je crée une troisième listes dans laquelle je mets les restaurants triés grace à la liste des distance.
        for (int i = 0; i < distance.size(); i++) {
            for (int j = 0; j < restaurants.size(); j++) {
                if (distance.get(i).equals(getDistance(restaurants.get(j)))) {
                    restaurantsSorted.add(restaurants.get(j));
                }
            }
        }
        restaurants.clear();
        restaurants.addAll(restaurantsSorted);
        listViewRecyclerViewAdapter.notifyDataSetChanged();
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

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortRestaurantByStars() {
        restaurants.sort(Comparator.comparing(Result::getRating));
        Collections.reverse(restaurants);
        listViewRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}