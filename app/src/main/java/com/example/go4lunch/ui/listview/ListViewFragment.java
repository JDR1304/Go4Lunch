package com.example.go4lunch.ui.listview;

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

import com.example.go4lunch.databinding.FragmentListViewBinding;

public class ListViewFragment extends Fragment {

    private ListViewViewModel listViewViewModel;
    private FragmentListViewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewViewModel=
                new ViewModelProvider(this).get(ListViewViewModel.class);

        binding = FragmentListViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textListView;
        listViewViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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