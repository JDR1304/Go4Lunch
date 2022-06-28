package com.example.go4lunch.repository;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

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


}