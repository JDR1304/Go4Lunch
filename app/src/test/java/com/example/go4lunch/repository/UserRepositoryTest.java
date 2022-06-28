package com.example.go4lunch.repository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;


import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import kotlin.jvm.internal.Lambda;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest extends TestCase {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firebaseFirestore;

    @Mock
    FirebaseAuth firebaseAuth;

    UserRepository userRepository;
    UserRepository userRepositorySpy;

    @Before
    public void setUp() {
        userRepository = new UserRepository(firebaseAuth, firebaseFirestore);
        // Spy for changing the behavior of some methods in my userRepository
        userRepositorySpy = spy(userRepository);
    }

    @Test
    public void testGetCurrentUser() {
        // Arrange
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        // Assert
        assertEquals(userRepository.getCurrentUser(), firebaseUser);
        // Verify
        verify(firebaseAuth).getCurrentUser();


    }

    @Test
    public void testIsCurrentUserLogged() {
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        assertFalse(userRepository.isCurrentUserLogged());
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        assertTrue(userRepository.isCurrentUserLogged());
        verify(firebaseAuth, times(2)).getCurrentUser();

    }

    @Test
    public void testSignOut() {
    }



    @Test
    public void testGetUsersCollection() {
        CollectionReference collectionRef = mock(CollectionReference.class);
        when(firebaseFirestore.collection(anyString())).thenReturn(collectionRef);
        assertEquals(userRepository.getUsersCollection(), collectionRef);
        verify(firebaseFirestore).collection(anyString());
    }
    @Test
    public void testCreateUser() {
        // Arrange
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        doReturn(firebaseUser).when(userRepositorySpy).getCurrentUser();
        when(firebaseUser.getUid()).thenReturn("UID");
        when(firebaseUser.getEmail()).thenReturn("jerome.diaz@gmail.com");
        when(firebaseUser.getDisplayName()).thenReturn("jerome Diaz");
        when(firebaseUser.getPhotoUrl()).thenReturn(Uri.parse("https://picsum.photos/200"));
        Task taskMock = mock(Task.class);
        doReturn(taskMock).when(userRepositorySpy).getUserData();
        // Act
        userRepositorySpy.createUser();
        // Assert
        verify(firebaseUser).getUid();


    }

    @Test
    public void testGetUserData() {
        // Arrange
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        String uid = "UID";
        // doReturn allows to return something without checking
        doReturn(uid).when(userRepositorySpy).getCurrentUserUID();

        doReturn(collectionRefMock).when(userRepositorySpy).getUsersCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(taskMock);
        // Act
        Task<DocumentSnapshot> result = userRepositorySpy.getUserData();
        // Assert
        assertEquals(result, taskMock);
        InOrder orderVerifier = inOrder(userRepositorySpy, collectionRefMock, documentReferenceMock);
        orderVerifier.verify(userRepositorySpy).getCurrentUserUID();
        orderVerifier.verify(userRepositorySpy).getUsersCollection();
        orderVerifier.verify(collectionRefMock).document(uid);
        orderVerifier.verify(documentReferenceMock).get();
    }

    @Test
    public void testGetCurrentUserUID() {
        // Arrange
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(firebaseUser.getUid()).thenReturn("UID");
        // Act
        when(userRepositorySpy.getCurrentUser()).thenReturn(firebaseUser);
        String uid = firebaseUser.getUid();
        // Assert
        assertEquals(uid, userRepository.getCurrentUserUID());
        verify(userRepositorySpy).getCurrentUser();

    }

    @Test
    public void testGetCurrentUserName() {
        // Need: FirebaseUser and what to answer with the method firebaseUser.getDisplayName()
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(firebaseUser.getDisplayName()).thenReturn("Jerome Diaz");
        String name = firebaseUser.getDisplayName();
        // Act
        when(userRepositorySpy.getCurrentUser()).thenReturn(firebaseUser);
        // Assert
        assertEquals(name, userRepository.getCurrentUserName());
        verify(userRepositorySpy).getCurrentUser();
    }

    @Test
    public void testUpdatePlaceIdChoseByCurrentUserInFirestore() {
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        String uid = "UID";
        String RESTAURANT_PLACE_ID = "restaurantPlaceId";
        String place_Id = "placeId";
        // doReturn allows to return something without checking
        doReturn(uid).when(userRepositorySpy).getCurrentUserUID();
        doReturn(collectionRefMock).when(userRepositorySpy).getUsersCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.update(RESTAURANT_PLACE_ID, place_Id)).thenReturn(taskMock);
        // Act
        Task <Void> result = userRepositorySpy.updatePlaceIdChoseByCurrentUserInFirestore("placeId");
        // Assert
        assertEquals(result, taskMock);
        InOrder orderVerifier = inOrder(userRepositorySpy, collectionRefMock, documentReferenceMock);
        orderVerifier.verify(userRepositorySpy).getCurrentUserUID();
        orderVerifier.verify(userRepositorySpy).getUsersCollection();
        orderVerifier.verify(collectionRefMock).document(uid);
        orderVerifier.verify(documentReferenceMock).update(anyString(), anyString());


    }

    @Test
    public void testUpdateRestaurantNameChoseByCurrentUserInFirestore() {
        CollectionReference collectionRefMock = mock(CollectionReference.class);
        DocumentReference documentReferenceMock = mock(DocumentReference.class);
        Task taskMock = mock(Task.class);
        String uid = "UID";
        String RESTAURANT_NAME = "restaurantName";
        String name = "Jerome Diaz";
        // doReturn allows to return something without checking
        doReturn(uid).when(userRepositorySpy).getCurrentUserUID();
        doReturn(collectionRefMock).when(userRepositorySpy).getUsersCollection();
        when(collectionRefMock.document(any())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.update(RESTAURANT_NAME, name)).thenReturn(taskMock);
        // Act
        Task <Void> result = userRepositorySpy.updateRestaurantNameChoseByCurrentUserInFirestore("Jerome Diaz");
        // Assert
        assertEquals(result, taskMock);
        InOrder orderVerifier = inOrder(userRepositorySpy, collectionRefMock, documentReferenceMock);
        orderVerifier.verify(userRepositorySpy).getCurrentUserUID();
        orderVerifier.verify(userRepositorySpy).getUsersCollection();
        orderVerifier.verify(collectionRefMock).document(uid);
        orderVerifier.verify(documentReferenceMock).update(anyString(), anyString());
    }


    @Test
    public void testGetChosenRestaurantIdFromUser() {
    }

}