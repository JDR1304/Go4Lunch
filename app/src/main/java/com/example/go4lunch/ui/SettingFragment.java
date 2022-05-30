package com.example.go4lunch.ui;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;

public class SettingFragment extends Fragment {

    private TextView notification;
    private Switch notificationSwitch;
    private MainActivityViewModel mainActivityViewModel;

    public SettingFragment() {
        // Required empty public constructor
    }
   /*
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance() {

        return ;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initialisation(view);
        setNotificationSwitch();
        return view;

    }

    public void initialisation(View view){
        notification= view.findViewById(R.id.notification);
        notificationSwitch = view.findViewById(R.id.notification_switch);
    }

    public void setNotificationSwitch(){
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked== true) {
                    mainActivityViewModel.getNotification(getActivity());
                    // Par le view model je dois activer la notification
                    Toast.makeText(getActivity(), "Settings switch on", Toast.LENGTH_SHORT).show();
                }else
                    mainActivityViewModel.cancelNotification(getActivity());
                    // Par le view model je dois désactiver la notification
                    Toast.makeText(getActivity(), "Settings switch off", Toast.LENGTH_SHORT).show();
            }
        });


    }
}