package com.example.go4lunch.repository;

import static org.mockito.Mockito.spy;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;

public class RestaurantRepositoryTest extends TestCase {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firebaseFirestore;

    @Mock
    FirebaseAuth firebaseAuth;

    RestaurantRepository restaurantRepository;
    RestaurantRepository restaurantRepositorySpy;

    @Before
    public void setUp() {
        restaurantRepository = new RestaurantRepository(firebaseAuth, firebaseFirestore);
        // Spy for changing the behavior of some methods in my RestaurantRepository
    }

    public void testGetInstance() {
    }

    public void testGetRestaurantsCollection() {
    }

    public void testCreateRestaurant() {
    }

    public void testGetRestaurantData() {
    }

    public void testGetRestaurantById() {
    }

    public void testGetUsersWhoJoinRestaurant() {
    }

    public void testGetRestaurantsList() {
    }

    public void testDeleteRestaurantFromFirestore() {
    }

    public void testLikeIncrement() {
    }

    public void testLikeDecrement() {
    }

    public void testAddUserIdToRestaurant() {
    }

    public void testDeleteUserIdToRestaurant() {
    }

    public void testGetLikeByRestaurant() {
    }
}