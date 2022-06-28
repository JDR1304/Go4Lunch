package com.example.go4lunch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.go4lunch.datasource.FetchRestaurantInGoogleAPI;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Geometry;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.ui.MainActivityViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityViewModelUnitTest {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();


    @Mock
    UserRepository userRepository;
    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    FetchRestaurantInGoogleAPI fetchRestaurantInGoogleAPI;

    @Mock
    private Observer<List<User>> mockObserverListUsers;

    @Mock
    private Observer<List<Restaurant>> mockObserverListRestaurants;

    @Mock
    List<Result> results;

    MainActivityViewModel mainActivityViewModel;


    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);
        restaurantRepository = mock(RestaurantRepository.class);
        fetchRestaurantInGoogleAPI = mock(FetchRestaurantInGoogleAPI.class);
        /*Attach the observer to the LiveData located in the ViewModel by using the get method
        and accessing the observeForever() method of LiveData, passing in our observer.
        The observeForever() method defaults all observation of LiveData to the given observer.*/
        mockObserverListUsers = mock(Observer.class);
        mainActivityViewModel = new MainActivityViewModel(userRepository, restaurantRepository, fetchRestaurantInGoogleAPI);
        results = mock(List.class);
    }

    @Test
    public void getLocationTest() {
        // Arrange
        MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
        mainActivityViewModel.locationLiveData = locationMutableLiveData;
        // Act
        LiveData<Location> result = mainActivityViewModel.getLocation();
        // Assert
        assertEquals(result, locationMutableLiveData);
    }

    @Test
    public void setLocationTest() {
        // Arrange
        Location locationMock = mock(Location.class);
        // Act
        mainActivityViewModel.setLocation(locationMock);
        // Assert
        assertEquals(mainActivityViewModel.locationLiveData.getValue(), locationMock);
    }

    @Test
    public void getRestaurantsTest() {
        // Arrange
        MutableLiveData<List<Result>> resultsLiveData = new MutableLiveData<>();
        resultsLiveData.postValue(results);
        Location location = mainActivityViewModel.locationLiveData.getValue();
        when(fetchRestaurantInGoogleAPI.getRestaurants(location, BuildConfig.ApiKey)).thenReturn(resultsLiveData);
        // Act
        LiveData<List<Result>> result = mainActivityViewModel.getRestaurants();
        // Assert
        assertEquals(result, resultsLiveData);
        verify(fetchRestaurantInGoogleAPI).getRestaurants(location, BuildConfig.ApiKey);

    }

    @Test
    public void getCurrentUserUid() {
        when(userRepository.getCurrentUserUID()).thenReturn("currentUID");
        assertEquals("currentUID", mainActivityViewModel.getCurrentUserUid());
        verify(userRepository).getCurrentUserUID();
    }

    @Test
    public void getCurrentUserName() {
        when(userRepository.getCurrentUserName()).thenReturn("Jerome");
        String test = mainActivityViewModel.getCurrentUserName();
        verify(userRepository).getCurrentUserName();
        assertEquals(test, "Jerome");
        assertThat(test, is("Jerome"));
        assertThat(test, not("Fabien"));
    }

    @Test
    public void getUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("123456", "jerome.diaz-rey@coppernic.fr", "Jerome Diaz Rey", null, "654321", "Villa Divina"));
        userList.add(new User("567890", "fabien.barry@coppernic.fr", "Fabien Barry", null, "098765", "Shogun"));

        // Vérification que mon LiveData fonctionne
        MutableLiveData<List<User>> users = new MutableLiveData<>();
        users.postValue(userList);
        assertEquals(users.getValue(), userList);
        // Vérification que je fais bien appel à mon mock userRepository
        when(userRepository.getUsersList()).thenReturn(users);
        assertEquals(mainActivityViewModel.getUsers(), users);
        verify(userRepository).getUsersList();

        mainActivityViewModel.getUsers().observeForever(mockObserverListUsers);
        verify(mockObserverListUsers).onChanged(userList);

    }

    @Test
    public void updatePlaceIdChoseByCurrentUserInFirestoreTest() {
        // Arrange
        String placeId = "TEST";
        // Act
        mainActivityViewModel.updatePlaceIdChoseByCurrentUserInFirestore(placeId);
        // Assert
        verify(userRepository).updatePlaceIdChoseByCurrentUserInFirestore(placeId);
    }

    @Test
    public void updateRestaurantNameChoseByCurrentUserInFirestoreTest() {
        //Arrange
        String name = "Jerome";
        // Act
        userRepository.updateRestaurantNameChoseByCurrentUserInFirestore(name);
        // Assert
        verify(userRepository).updateRestaurantNameChoseByCurrentUserInFirestore(name);
    }

    @Test
    public void getChosenRestaurantByUserFromFirestoreTest() {
        // Arrange
        String userUid = "UID user";
        // Act
        userRepository.getChosenRestaurantIdFromUser(userUid);
        // Assert
        verify(userRepository).getChosenRestaurantIdFromUser(userUid);
    }

    @Test
    public void createRestaurantInFirestoreTest() {
        Restaurant restaurant = new Restaurant("123", "Mac Donald", "rue du général De Gaulle",
                "picture", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                3, new Geometry());

        when(restaurantRepository.createRestaurant(anyString(), anyString(), anyString(),
                anyString(), anyList(), anyList(),
                anyList(), anyInt(), any(Geometry.class))).thenReturn(restaurant);

        Restaurant restaurantCreated = mainActivityViewModel.createRestaurantInFirestore(restaurant.getUid(), restaurant.getName(), restaurant.getAddress(),
                restaurant.getPictureUrl(), restaurant.getUsersWhoChoseRestaurantById(), restaurant.getUsersWhoChoseRestaurantByName(),
                restaurant.getFavoriteRestaurantUsers(), restaurant.getLikeNumber(), restaurant.getGeometry());

        assertEquals(restaurantCreated, restaurant);

        verify(restaurantRepository).createRestaurant(restaurant.getUid(), restaurant.getName(), restaurant.getAddress()
                , restaurant.getPictureUrl(), restaurant.getUsersWhoChoseRestaurantById(), restaurant.getUsersWhoChoseRestaurantByName(),
                restaurant.getFavoriteRestaurantUsers(), restaurant.getLikeNumber(), restaurant.getGeometry());
    }

    @Test
    public void getRestaurantListFromFirestoreTest() {
        // Arrange
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant("123", "Mac Donald", "rue du général De Gaulle",
                null, null, null, null,
                3, null));
        restaurantList.add(new Restaurant("456", "Villa divina", "rue de la campagne",
                null, null, null, null,
                5, null));
        // Act
        MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();
        restaurants.postValue(restaurantList);
        assertEquals(restaurants.getValue(), restaurantList);
        // Vérification que je fais bien appel à mon mock userRepository
        when(restaurantRepository.getRestaurantsList()).thenReturn(restaurants);
        assertEquals(mainActivityViewModel.getRestaurantListFromFirestore(), restaurants);
        verify(restaurantRepository).getRestaurantsList();

        mainActivityViewModel.getRestaurantListFromFirestore().observeForever(mockObserverListRestaurants);
        verify(mockObserverListRestaurants).onChanged(restaurantList);
    }

    @Test
    public void getRestaurantByIdFromFirestoreTest() {
        // Arrange
        String restaurantPlaceId = "restaurant place id";
        // Act
        restaurantRepository.getRestaurantById(restaurantPlaceId);
        // Assert
        verify(restaurantRepository).getRestaurantById(restaurantPlaceId);
    }

    @Test
    public void deleteRestaurantInFirestoreTest() {
        // Arrange
        String uid = "123456";
        // Act
        restaurantRepository.deleteRestaurantFromFirestore(uid);
        // Assert
        verify(restaurantRepository).deleteRestaurantFromFirestore(uid);
    }

    @Test
    public void likeIncrementTest() {
        // Arrange
        String uid = "123456";
        // Act
        restaurantRepository.likeIncrement(uid);
        // Assert
        verify(restaurantRepository).likeIncrement(uid);
    }

    @Test
    public void likeDecrementTest() {
        // Arrange
        String uid = "123456";
        // Act
        restaurantRepository.likeDecrement(uid);
        // Assert
        verify(restaurantRepository).likeDecrement(uid);
    }

    @Test
    public void setPredictionEstablishmentListTest() {
        // Arrange
        List<String> predictionList = new ArrayList<>();
        predictionList.add("prediction 1");
        predictionList.add("prediction 2");
        MutableLiveData<List<String>> predictions = mainActivityViewModel.establishmentPrediction;
        predictions.setValue(predictionList);
        assertEquals(predictionList, predictions.getValue());

    }

    @Test
    public void getPredictionEstablishmentList() {
        // Arrange
        MutableLiveData<List<String>> predictionLiveData = new MutableLiveData<>();
        mainActivityViewModel.establishmentPrediction = predictionLiveData;
        // Act
        LiveData<List<String>> result = mainActivityViewModel.getPredictionEstablishmentList();
        // Assert
        assertEquals(result, predictionLiveData);

    }



}


