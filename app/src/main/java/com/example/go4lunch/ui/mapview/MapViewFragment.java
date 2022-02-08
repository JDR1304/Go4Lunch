package com.example.go4lunch.ui.mapview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.databinding.FragmentMapViewBinding;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private MapViewViewModel mapViewViewModel;
    private FragmentMapViewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewViewModel =
                new ViewModelProvider(this).get(MapViewViewModel.class);

        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMapView;
        mapViewViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
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