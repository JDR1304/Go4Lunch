package com.example.go4lunch.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunch.injection.UserRepositoryInjection;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest extends TestCase {

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Mock
    FirebaseFirestore firebaseFirestore;

    @Mock
    FirebaseUser firebaseUser;

    @Mock
    FirebaseAuth firebaseAuth;


    UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository = new UserRepository(firebaseAuth,firebaseFirestore);
    }


    public void testGetInstance() {
    }

    @Test
    public void testGetCurrentUser() {
        FirebaseUser firebaseUser = mock(FirebaseUser.class);
        when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);
        assertNotNull(userRepository.getCurrentUser());
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

    public void testSignOut() {
    }

    public void testDeleteUser() {
    }

    public void testGetUsersCollection() {
    }

    public void testCreateUser() {
    }

    public void testGetUserData() {
    }

    @Test
    public void testGetCurrentUserUID() {
        //String uid = firebaseUser.getUid();
        //when((firebaseAuth.getCurrentUser())).thenReturn(firebaseUser);
        when(userRepository.getCurrentUser()).thenReturn(firebaseUser);
        assertEquals("Current UID", userRepository.getCurrentUserUID());
        verify(userRepository.getCurrentUserUID());
    }

    public void testGetCurrentUserName() {
    }

    public void testUpdatePlaceIdChoseByCurrentUserInFirestore() {
    }

    public void testUpdateRestaurantNameChoseByCurrentUserInFirestore() {
    }

    public void testUpdateUsername() {
    }

    public void testGetUsersList() {
    }

    public void testGetChosenRestaurantNameFromUser() {
    }

    public void testGetChosenRestaurantIdFromUser() {
    }

    public void testDeleteUserFromFirestore() {
    }
}