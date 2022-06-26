package com.example.go4lunch.repository;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.modelApiNearby.Geometry;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRepositoryTest extends TestCase {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firebaseFirestore;

    RestaurantRepository restaurantRepository;
    RestaurantRepository restaurantRepositorySpy;

    @Before
    public void setUp() {
        restaurantRepository = new RestaurantRepository(firebaseFirestore);
        // Spy for changing the behavior of some methods in my RestaurantRepository
        restaurantRepositorySpy = spy(restaurantRepository);
    }

    @Test
    public void testGetRestaurantsCollection() {
        CollectionReference collectionRef = mock(CollectionReference.class);
        when(firebaseFirestore.collection(any())).thenReturn(collectionRef);
        assertEquals(restaurantRepository.getRestaurantsCollection(), collectionRef);
        verify(firebaseFirestore).collection(anyString());
    }

    @Test
    public void testCreateRestaurant() {
        // Arrange
        Restaurant restaurant = new Restaurant("123", "Mac Donald", "rue du général De Gaulle",
                "picture", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                3, new Geometry());
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        doReturn(collectionRefMock).when(restaurantRepositorySpy).getRestaurantsCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(taskMock);

        when(restaurantRepositorySpy.createRestaurant(anyString(), anyString(), anyString(),
                anyString(), anyList(), anyList(),
                anyList(), anyInt(), any(Geometry.class))).thenReturn(restaurant);

        Restaurant restaurantCreated = restaurantRepository.createRestaurant("123", "Mac Donald", "rue du général De Gaulle",
                "picture", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                3, new Geometry());
        /*Task taskDataMock = mock(Task.class);
        doReturn(taskDataMock).when(restaurantRepositorySpy).getRestaurantData(restaurant.getUid());*/
        // Act
        assertEquals(restaurant, restaurantCreated );

    }

    @Test
    public void testGetRestaurantData() {
        // Arrange
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        String uid = "UID";
        // doReturn allows to return something without checking
        doReturn(collectionRefMock).when(restaurantRepositorySpy).getRestaurantsCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(taskMock);
        // Act
        Task<DocumentSnapshot> result = restaurantRepositorySpy.getRestaurantData(uid);
        // Assert
        assertEquals(result, taskMock);
        InOrder orderVerifier = inOrder(restaurantRepositorySpy, collectionRefMock, documentReferenceMock);
        orderVerifier.verify(restaurantRepositorySpy).getRestaurantsCollection();
        orderVerifier.verify(collectionRefMock).document(uid);
        orderVerifier.verify(documentReferenceMock).get();
    }

    public void testGetRestaurantById() {
    }

    public void testGetUsersWhoJoinRestaurant() {
    }

    public void testGetRestaurantsList() {
    }

    @Test
    public void testDeleteRestaurantFromFirestore() {
        // Arrange
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        String uid = "UID";
        // doReturn allows to return something without checking
        doReturn(collectionRefMock).when(restaurantRepositorySpy).getRestaurantsCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.delete()).thenReturn(taskMock);
        // Act
        restaurantRepositorySpy.deleteRestaurantFromFirestore(uid);
        // Assert
        //assertEquals(result, taskMock);
        InOrder orderVerifier = inOrder(restaurantRepositorySpy, collectionRefMock, documentReferenceMock);
        orderVerifier.verify(restaurantRepositorySpy).getRestaurantsCollection();
        orderVerifier.verify(collectionRefMock).document(uid);
        orderVerifier.verify(documentReferenceMock).delete();
    }
    @Test
    public void testLikeIncrement() {
        String uid = "UID";
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference likeIncrementMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        when(collectionRefMock.document(any())).thenReturn(likeIncrementMock);
        when(likeIncrementMock.update(any())).thenReturn(taskMock);
        restaurantRepository.likeIncrement(uid);
        // Atomically increment the like number.
        //likeIncrement.update(LIKE_NUMBER, FieldValue.increment(1));
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