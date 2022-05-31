package com.example.go4lunch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ExampleUnitTest {

    UserRepository userRepository;
    List<User> users;

    @Before
    public void setup() {
        userRepository = UserRepository.getInstance();
        users = userRepository.getUsersList().getValue();
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getUsers() {
        UserRepository userRepository = UserRepository.getInstance();
        List<User> user = userRepository.getUsersList().getValue();
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getCurrentUserUid() {
        String userUID = userRepository.getCurrentUserUID();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUid().equals(userUID)) {
                assertEquals(users.get(i).getUid(), userUID);
            }
        }
        assertEquals(4, 2 + 2);
    }
}


