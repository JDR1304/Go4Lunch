package com.example.go4lunch.ui.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.User;

import java.util.Arrays;
import java.util.List;


public class WorkmatesFragment extends Fragment {

    private WorkmatesViewModel workmatesViewModel;
    private FragmentWorkmatesBinding binding;
    private RecyclerView recyclerView;
    private WorkmatesRecyclerViewAdapter workmatesRecyclerViewAdapter;
    private UserManager userManager = UserManager.getInstance();

    //Map <User> users = (List<User>) userManager.getUsersCollection().get();
    public static List<User> DUMMY_USER = Arrays.asList(
            new User("1", "jerome.diazrey@gmail.com", "Jerome", "https://i.pravatar.cc/150?u=a042581f4e29026704a"),
            new User("2", "fabien.barry@gmail.com", "Fabien", "https://i.pravatar.cc/150?u=a042581f4e29026704e"),
            new User("3", "rachel.dauphin@gmail.com", "Rachel", "https://i.pravatar.cc/150?u=a042581f4e29026704d"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        workmatesViewModel = new ViewModelProvider(this).get(WorkmatesViewModel.class);

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.fragmentWorkmatesRecyclerview;
        workmatesRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter(DUMMY_USER);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(workmatesRecyclerViewAdapter);
        workmatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}