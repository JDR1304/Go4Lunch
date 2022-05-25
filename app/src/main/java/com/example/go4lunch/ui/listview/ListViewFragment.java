package com.example.go4lunch.ui.listview;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.RetrieveIdRestaurant;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.modelApiNearby.Result;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.example.go4lunch.ui.RestaurantDetailsArgs;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {


    private FragmentListViewBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;
    private ListViewRecyclerViewAdapter listViewRecyclerViewAdapter;
    private ListViewFragmentDirections.ActionNavigationListViewToNavigationRestaurantDetails action;
    private List <Result> restaurants = new ArrayList<>();
    private List <Result> predictionsList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentListviewRecyclerview;
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPredictionEstablishment();
        getRestaurants();
    }

    public void getRestaurants() {

        // Get Users From Random API
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                restaurants.addAll(results);
                listViewRecyclerViewAdapter = new ListViewRecyclerViewAdapter(restaurants, mainActivityViewModel, new RetrieveIdRestaurant() {

                    @Override
                    public void onClickItem(String placeId) {
                        action = ListViewFragmentDirections.actionNavigationListViewToNavigationRestaurantDetails();
                        action.setPlaceId(placeId);
                        Navigation.findNavController(getActivity(),R.id.nav_host_fragment_content_main).navigate(action);
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(listViewRecyclerViewAdapter);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(this, results);
    }

    public void getPredictionEstablishment (){
        Observer <List<String>> establishments = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> predictions) {
                Log.e(TAG, "onChanged: MapFragment" + predictions);
                if (predictions.size() > 0) {
                    predictionsList.clear();
                    for (int i = 0; i<predictions.size(); i++){
                        for (int j =0; j<restaurants.size(); j++){
                            if (predictions.get(i).equals(restaurants.get(j).getName())){
                                predictionsList.add(restaurants.get(j));
                                break;
                            }
                        }
                    }
                    if (predictionsList.size()>0) {
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}