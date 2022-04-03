package com.example.go4lunch.ui.listview;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.databinding.FragmentListViewBinding;
import com.example.go4lunch.modelApiNearby.Result;

import java.util.List;

public class ListViewFragment extends Fragment {

    private FragmentListViewBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private RecyclerView recyclerView;
    private ListViewRecyclerViewAdapter listViewRecyclerViewAdapter;

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
        getRestaurant();
    }

    public void getRestaurant() {

        // Get Users From Random API
        Observer<List<Result>> results = new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                listViewRecyclerViewAdapter = new ListViewRecyclerViewAdapter(results, mainActivityViewModel);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(listViewRecyclerViewAdapter);
                Log.e(TAG, "onChanged: " + results);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurants().observe(this, results);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}