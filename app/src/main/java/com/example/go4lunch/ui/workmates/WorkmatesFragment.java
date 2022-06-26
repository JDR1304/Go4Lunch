package com.example.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.ui.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.RetrieveIdRestaurant;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.User;
import java.util.List;



public class WorkmatesFragment extends Fragment {

    private MainActivityViewModel mainActivityViewModel;
    private FragmentWorkmatesBinding binding;
    private RecyclerView recyclerView;
    private WorkmatesRecyclerViewAdapter workmatesRecyclerViewAdapter;
    private WorkmatesFragmentDirections.ActionNavigationWorkmatesToRestaurantDetails action;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.fragmentWorkmatesRecyclerview;
        //mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        configureViewModel();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Create the observer which updates the UI.
        Observer<List<User>> users = new Observer<List<User>> () {
            @Override
            public void onChanged(@Nullable List<User> users) {
                // Update the UI, in this case, a TextView.
                workmatesRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter(users,getContext(), mainActivityViewModel, new RetrieveIdRestaurant() {
                    @Override
                    public void onClickItem(String placeId) {
                        action = WorkmatesFragmentDirections.actionNavigationWorkmatesToRestaurantDetails();
                        action.setPlaceId(placeId);
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main).navigate(action);
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(workmatesRecyclerViewAdapter);
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(this, users);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        this.mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}