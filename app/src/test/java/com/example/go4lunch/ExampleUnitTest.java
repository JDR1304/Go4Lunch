package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ExampleUnitTest {


    MainActivityViewModel mainActivityViewModelMock = mock(MainActivityViewModel.class);
    List<User> users = new ArrayList<>();
    MutableLiveData<List<User>> listLiveData;


    @Before
    public void setup() {
        if (listLiveData == null) {
            listLiveData = new MutableLiveData<>();
        }
        users.add(new User("123456789", "jerome.diaz-rey@coppernic.fr", "jerome Diaz",
                null, "restaurantPlaceId", "Villa divina"));

        //listLiveData.postValue(users);

    }

    @Test
    public void getUsers() {
        when(mainActivityViewModelMock.getUsers()).thenReturn(listLiveData);
        List<User> userList = mainActivityViewModelMock.getUsers().getValue();
        assertEquals(userList, listLiveData.getValue());
    }

    @Test
    public void getCurrentUserUid() {
        when(mainActivityViewModelMock.getCurrentUserUid()).thenReturn("currentUID");
        assertEquals(mainActivityViewModelMock.getCurrentUserUid(), "currentUID");

    }

    @Test
    public void getCurrentUserName() {
        when(mainActivityViewModelMock.getCurrentUserName()).thenReturn("Jerome");
        assertEquals(mainActivityViewModelMock.getCurrentUserName(), "Jerome");

    }
}


