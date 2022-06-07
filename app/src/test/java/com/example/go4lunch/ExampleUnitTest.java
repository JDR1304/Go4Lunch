package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    UserRepository userRepository = mock(UserRepository.class);
    @Mock
    RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
    @Mock
    FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI = mock(FetchRestaurantInGoogleAPI.class);

    MainActivityViewModel mainActivityViewModel = new MainActivityViewModel(userRepository, restaurantRepository, fetchRestaurantInGoogleAPI);


    List<User> users = new ArrayList<>();
    MutableLiveData<List<User>> listLiveData;


    @Before
    public void setup() {

       /* if (listLiveData == null) {
            listLiveData = new MutableLiveData<>();
        }
        users.add(new User("123456789", "jerome.diaz-rey@coppernic.fr", "jerome Diaz",
                null, "restaurantPlaceId", "Villa divina"));*/

        //listLiveData.postValue(users);

    }

 /*   @Test
    public void getUsers() {
        when(userRepository.getUsers()).thenReturn(listLiveData);
        List<User> userList = mainActivityViewModelMock.getUsers().getValue();
        assertEquals(userList, listLiveData.getValue());
    }*/

    @Test
    public void getCurrentUserUid() {
        when(userRepository.getCurrentUserUID()).thenReturn("currentUID");
        verify(userRepository).getCurrentUserUID();
        assertEquals(mainActivityViewModel.getCurrentUserUid(), "currentUID");

    }

    @Test
    public void getCurrentUserName() {
        when(userRepository.getCurrentUserName()).thenReturn("Jerome");
        //verify(userRepository).getCurrentUserName();
        assertEquals(userRepository.getCurrentUserUID(), "Jerome");

    }
}


