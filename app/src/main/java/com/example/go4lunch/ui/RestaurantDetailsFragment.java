package com.example.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.RetrieveIdRestaurant;
import com.example.go4lunch.UploadWorker;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.modelApiNearby.Result;
import com.example.go4lunch.ui.workmates.WorkmatesRecyclerViewAdapter;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantDetailsFragment extends Fragment {

    private MainActivityViewModel mainActivityViewModel;
    private final String PLACE_ID = "place_id";
    private String placeId;

    private Restaurant restaurant;
    private User currentUser;
    private List<User> userList;

    // Attributes for API
    private Result restaurantResultFromApi;
    private Restaurant restaurantFromApi;

    //Attributes for FireStore
    private String pictureUrl;
    private Restaurant restaurantDetailsFromFirestore;
    private String userRestaurantIdChosen;
    private String currentUserUid;


    // Fragment's Views
    private ImageView call;
    private ImageView website;
    private ImageView like;
    private ImageView star;
    private ImageView restaurantImageView;
    private TextView restaurantName;
    private TextView address;
    private FloatingActionButton floatingActionButtonToBookRestaurant;

    // Get Website and Phone
    private Uri websiteUrl;
    private String phoneNumber;

    // RecyclerView
    private RecyclerView recyclerView;
    private WorkmatesRecyclerViewAdapter workmatesRecyclerViewAdapter;

    private List <String> usersWhoJoinRestaurantByName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(PLACE_ID)) {
            placeId = getArguments().getString(PLACE_ID);
        }
        mainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        currentUserUid = mainActivityViewModel.getCurrentUserUid();
        getRestaurantFromFirestore(placeId);
        checkIfCurrentUserChoseRestaurant(placeId);
        displayUsersWhoJoinRestaurant(placeId);
        //getCurrentUser(currentUserUid);

        /*
        Je récupere l'argument place_Id qui me permet d'identifier le restaurant que je souhaite afficher
        Pour rappel les points d'entrées sont les quatre suivants:
                    - Depuis le drawer en cliquant sur Lunch affiche un resto si le current user en à choisit un.
                      Le Floating bouton doit être en vert.
                    - Depuis MapView en cliquant sur un marker.
                    - Depuis ListView en cliquant sur un item de la liste
                    - Depuis WorkmateList en cliquant sur un User de la liste
         */


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // J'initialise la vue
        initRestaurant(view);
        displayRestaurantFromApi();
        //-----------------------------------------------

        // Manage la reservation du current user
        restaurantBooked();

        //La partie ci-dessous fonctionne
        getPlacePhoneNumberAndWebsite(placeId);
        getWebsite();
        callRestaurant();
        getLike();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //--------------------------------init View from api-------------------------------------------------------

    // initialisation de la vue
    private void initRestaurant(@NonNull View view) {
        restaurantImageView = view.findViewById(R.id.restaurant_details_image_view);
        restaurantName = view.findViewById(R.id.restaurant_details_name);
        address = view.findViewById(R.id.restaurant_details_address);
        floatingActionButtonToBookRestaurant = view.findViewById(R.id.check_fab);
        call = view.findViewById(R.id.image_view_phone);
        website = view.findViewById(R.id.image_view_earth);
        like = view.findViewById(R.id.image_view_star);
        star = view.findViewById(R.id.restaurant_details_star);
        recyclerView = view.findViewById(R.id.restaurant_details_recyclerView);
    }

    public void displayRestaurantFromApi() {
        List<Result> restaurantList = mainActivityViewModel.getRestaurants().getValue();
        for (int i = 0; i < restaurantList.size(); i++) {
            if (placeId.equals(restaurantList.get(i).getPlaceId())) {
                restaurantResultFromApi = restaurantList.get(i);
                restaurantName.setText(restaurantResultFromApi.getName());
                address.setText(restaurantResultFromApi.getVicinity());
                String reference = restaurantResultFromApi.getPhotos().get(0).getPhotoReference();
                String pictureSize = Integer.toString(restaurantResultFromApi.getPhotos().get(0).getWidth());

                if (reference != null) {
                    Glide.with(this.restaurantImageView.getContext())
                            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key=" + BuildConfig.API_KEY)
                            .into(this.restaurantImageView);
                } else {
                    Glide.with(this.restaurantImageView.getContext())
                            .load(R.drawable.go4lunch)
                            .into(this.restaurantImageView);
                }

            }
        }
    }

    public void checkIfCurrentUserChoseRestaurant(String placeId) {
        Observer<String> RestaurantIdCurrentUserChose = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userRestaurantIdChosen = s;
                if (userRestaurantIdChosen != null && userRestaurantIdChosen.equals(placeId)) {
                    getButtonGreen();
                } else {
                    getButtonWhite();
                }
            }
        };
        mainActivityViewModel.getChosenRestaurantByUserFromFirestore(currentUserUid).observe(this, RestaurantIdCurrentUserChose);
    }

    public void displayUsersWhoJoinRestaurant(String placeId) {
        List<User> usersWhoChoseRestaurantForRecyclerView = new ArrayList<>();
        Observer<List<User>> users = new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                usersWhoChoseRestaurantForRecyclerView.clear();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getRestaurantPlaceId() != null && users.get(i).getRestaurantPlaceId().equals(placeId)) {
                        usersWhoChoseRestaurantForRecyclerView.add(users.get(i));
                    }
                }
                if (usersWhoChoseRestaurantForRecyclerView != null) {
                    workmatesRecyclerViewAdapter = new WorkmatesRecyclerViewAdapter
                            (usersWhoChoseRestaurantForRecyclerView, mainActivityViewModel, new RetrieveIdRestaurant() {
                                @Override
                                public void onClickItem(String placeId) {
                                    Log.e(TAG, "onClickItem: " + placeId);
                                }
                            });
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(workmatesRecyclerViewAdapter);
                }
            }

        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getUsers().observe(this, users);
    }

    public void getRestaurantFromFirestore(String placeId) {
        // Si la collection n'est pas null, je fais l'observe de la liste des restaurants
        Observer<List<Restaurant>> restaurants = new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (restaurants != null) {
                    for (int i = 0; i < restaurants.size(); i++) {
                        if (restaurants.get(i).getUid().equals(placeId)) {
                            // je récupère les infos du restaurant sur firestore et je les mets dans restaurantByIdFromFirestore
                            //restaurantDetailsFromFirestore = restaurants.get(i);
                            restaurant = restaurants.get(i);

                        }
                    }
                }
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mainActivityViewModel.getRestaurantListFromFirestore().observe(this, restaurants);
    }

    private Restaurant changeResultInRestaurant(Result restaurantResult) {
        List<String> usersWhoChoseRestaurantFromFirestoreById = new ArrayList<>();
        List<String> usersWhoChoseRestaurantFromFirestoreByName = new ArrayList<>();
        List<String> favoriteRestaurantUsersFromFirestore = new ArrayList<>();
        int likeNumber = 0;
        usersWhoChoseRestaurantFromFirestoreById.add(currentUserUid);
        String reference = restaurantResult.getPhotos().get(0).getPhotoReference();
        String pictureSize = Integer.toString(restaurantResult.getPhotos().get(0).getWidth());
        pictureUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + pictureSize + "&photo_reference=" + reference + "&key=" + BuildConfig.API_KEY;
        Restaurant restaurant = new Restaurant(restaurantResult.getPlaceId(), restaurantResult.getName(),
                restaurantResult.getVicinity(), pictureUrl, usersWhoChoseRestaurantFromFirestoreById,usersWhoChoseRestaurantFromFirestoreByName,
                favoriteRestaurantUsersFromFirestore, likeNumber, restaurantResult.getGeometry());
        return restaurant;
    }

    public void getButtonGreen() {
        floatingActionButtonToBookRestaurant.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

    }

    public void getButtonWhite() {
        floatingActionButtonToBookRestaurant.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
    }

    private void restaurantBooked() {
        floatingActionButtonToBookRestaurant.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //Autre variable qui correspond à une donnée dans le if!!!

                //if (floatingActionButtonToBookRestaurant.getBackgroundTintList().equals(ColorStateList.valueOf(Color.WHITE))) {
                if (userRestaurantIdChosen == null || !userRestaurantIdChosen.equals(placeId)) {
                    //For the Drawer
                    getButtonGreen();
                    mainActivityViewModel.getNotification(getActivity());
                    deleteOldRestaurantIdChosen(userRestaurantIdChosen);

                    // if restaurant exists in Firestore add current user to getUsersWhoChoseRestaurant list
                    if (restaurant != null /*&& restaurantDetailsFromFirestore.getUid().equals(placeId)*/) {
                        restaurant.getUsersWhoChoseRestaurantById().add(currentUserUid);


                    }
                    // if restaurant doesn't exist in Firestore I create a new one
                    else {
                        restaurant = changeResultInRestaurant(restaurantResultFromApi);
                        mainActivityViewModel.createRestaurantInFirestore(placeId, restaurant.getName(), restaurant.getAddress(), pictureUrl,
                                restaurant.getUsersWhoChoseRestaurantById(),restaurant.getUsersWhoChoseRestaurantByName(), restaurant.getFavoriteRestaurantUsers(), restaurant.getLikeNumber(), restaurant.getGeometry());
                    }
                    restaurant.getUsersWhoChoseRestaurantByName().add(mainActivityViewModel.getCurrentUserName());
                    mainActivityViewModel.updatePlaceIdChoseByCurrentUserInFirestore(placeId);
                    mainActivityViewModel.updateRestaurantNameChoseByCurrentUserInFirestore(restaurant.getName());
                    userRestaurantIdChosen = placeId;
                } else {
                    getButtonWhite();
                    mainActivityViewModel.cancelNotification(getActivity());
                    if (restaurant.getLikeNumber() > 0 || restaurant.getUsersWhoChoseRestaurantById().size() > 1) {
                        restaurant.getUsersWhoChoseRestaurantById().remove(currentUserUid);
                        restaurant.getUsersWhoChoseRestaurantByName().remove(mainActivityViewModel.getCurrentUserName());
                        mainActivityViewModel.updatePlaceIdChoseByCurrentUserInFirestore(null);
                        mainActivityViewModel.updateRestaurantNameChoseByCurrentUserInFirestore(null);
                        userRestaurantIdChosen = null;
                    } else {
                        mainActivityViewModel.deleteRestaurantInFirestore(placeId);
                        mainActivityViewModel.updatePlaceIdChoseByCurrentUserInFirestore(null);
                        mainActivityViewModel.updateRestaurantNameChoseByCurrentUserInFirestore(null);
                        userRestaurantIdChosen = null;
                        restaurant.getUid().equals(null);
                        restaurant = null;
                    }
                }
            }
        });
    }

    // //Si le current user à déja choisit un resto je supprime ca présence dans la liste des resto
    // //si il y a d'autres personnes qui mangent dans ce resto ou qu'il y a un like
    // //sinon je supprime le resto de firestore
    //

    public void deleteOldRestaurantIdChosen(String userRestaurantIdChosen) {
        if (userRestaurantIdChosen != null) {
            Observer<Restaurant> restaurantByIdFromFirestore = new Observer<Restaurant>() {
                @Override
                public void onChanged(Restaurant restaurant) {
                    if (restaurant.getLikeNumber() > 0 || restaurant.getUsersWhoChoseRestaurantById().size() > 1) {
                        restaurant.getUsersWhoChoseRestaurantById().remove(currentUserUid);
                    } else {
                        mainActivityViewModel.deleteRestaurantInFirestore(userRestaurantIdChosen);
                    }
                }
            };
            mainActivityViewModel.getRestaurantByIdFromFirestore(userRestaurantIdChosen).observe(this, restaurantByIdFromFirestore);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void getPlacePhoneNumberAndWebsite(String restaurantPlaceId) {
        // Initialize Places.
        Places.initialize(getActivity(), BuildConfig.API_KEY);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getContext());
        // Define a Place ID.
        final String placeId = restaurantPlaceId;

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUrl = place.getWebsiteUri();

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                final int statusCode = apiException.getStatusCode();

            }
        });
    }

    private void callRestaurant() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phoneNumber);
                dialIntent.setData(data);
                startActivity(dialIntent);

            }
        });

    }

    private void getWebsite() {
        website.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (websiteUrl != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, websiteUrl);
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(getContext(), "No website accessible", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getLike() {
        like.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                if (i == 0) {
                    star.setVisibility(View.VISIBLE);
                    mainActivityViewModel.likeIncrement(placeId);
                    i++;
                } else {
                    star.setVisibility(View.GONE);
                    mainActivityViewModel.likeDecrement(placeId);
                    i--;
                }
            }
        });
    }


}




