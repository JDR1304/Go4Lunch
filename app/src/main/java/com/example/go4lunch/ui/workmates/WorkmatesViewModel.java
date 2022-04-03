package com.example.go4lunch.ui.workmates;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;
import java.util.List;

public class WorkmatesViewModel extends ViewModel  {

    private UserRepository userRepository = UserRepository.getInstance();

    public WorkmatesViewModel (){
    }
    // Why return LiveData because I don't want change the data outside the viewModel
    public LiveData<List<User>> getUsers() {
        return userRepository.getUsersList();


    }

}