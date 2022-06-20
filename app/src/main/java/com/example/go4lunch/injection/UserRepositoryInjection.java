package com.example.go4lunch.injection;

import com.example.go4lunch.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepositoryInjection {
    static UserRepository sUserRepository;

    public static UserRepository userRepositoryDataSource() {
        UserRepository result = sUserRepository;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (sUserRepository == null) {
                sUserRepository = new UserRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
            }
            return sUserRepository;
        }
    }

}
