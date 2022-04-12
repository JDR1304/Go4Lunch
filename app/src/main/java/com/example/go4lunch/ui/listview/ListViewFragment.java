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
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.RestaurantDetailsArgs;

import java.util.List;

public class ListViewFragment extends Fragment {

    interface RetrieveIdRestaurant {
        void onClickItem(String placeId);
    }

    private FragmentListViewBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;
    private ListViewRecyclerViewAdapter listViewRecyclerViewAdapter;
    private ListViewFragmentDirections.ActionNavigationListViewToNavigationRestaurantDetails action;
    private Context context = getContext();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentListviewRecyclerview;
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        getRestaurants();
    }

    public void getRestaurants() {

        // Get Users From Random API
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                listViewRecyclerViewAdapter = new ListViewRecyclerViewAdapter(results, mainActivityViewModel, new RetrieveIdRestaurant() {

                    @Override
                    public void onClickItem(String placeId) {
                        Result restaurant = getRestaurantId(results, placeId);
                        action = ListViewFragmentDirections.actionNavigationListViewToNavigationRestaurantDetails(placeId);
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

    private Result getRestaurantId(List<Result> restaurants, String placeId) {
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getPlaceId().equals(placeId)) {
                return restaurants.get(i);
            }
        }
        return null;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}