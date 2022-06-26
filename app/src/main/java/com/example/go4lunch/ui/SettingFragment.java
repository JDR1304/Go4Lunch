package com.example.go4lunch.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;

public class SettingFragment extends Fragment {

    private TextView notification;
    private Switch notificationSwitch;
    private MainActivityViewModel mainActivityViewModel;

    private SharedPreferences sharedPreferences;
    private boolean switchState;
    private SharedPreferences.Editor editor;

    private Toolbar toolbar;


    public SettingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        sharedPreferences = getActivity().getSharedPreferences("SAVE_SWITCH_STATE", MODE_PRIVATE);
        switchState = sharedPreferences.getBoolean("SAVE",true);
        editor = sharedPreferences.edit();
        initialisation(view);
        setNotificationSwitch();
        return view;

    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        this.mainActivityViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainActivityViewModel.class);
    }

    public void initialisation(View view) {
        notification = view.findViewById(R.id.notification);
        notificationSwitch = view.findViewById(R.id.notification_switch);
        notificationSwitch.setChecked(switchState);
    }

    public void setNotificationSwitch() {

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    editor.putBoolean("SAVE", true);
                    editor.apply();
                    editor.commit();
                    mainActivityViewModel.getNotification(getActivity());
                    // Par le view model je dois activer la notification
                    Toast.makeText(getActivity(), "Settings switch on", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("SAVE", false);
                    editor.apply();
                    editor.commit();
                    mainActivityViewModel.cancelNotification();
                    // Par le view model je dois désactiver la notification
                    Toast.makeText(getActivity(), "Settings switch off", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}