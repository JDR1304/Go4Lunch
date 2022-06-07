package com.example.go4lunch;

import static android.content.ContentValues.TAG;

import android.app.Notification;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.repository.RestaurantRepository;
import com.example.go4lunch.repository.UserRepository;

import java.util.concurrent.TimeUnit;


public class UploadWorker extends Worker {

    private static String WORKER_ID = "WORKER_ID";
    private final String CHANNEL_ID = "CHANNEL_ID";
    private final int NOTIFICATION_ID = 100;
    private UserRepository userRepository = UserRepository.getInstance();
    private RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();
    private Restaurant restaurant;


    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (userRepository.getChosenRestaurantIdFromUser(userRepository.getCurrentUserUID())!= null) {
            notification(getRestaurant());
            Log.e(TAG, "doWork: active the worker " + getRestaurant());
        }
        return Result.success();
    }

    public void notification(Restaurant restaurant) {
        if (restaurant!=null) {
            Notification builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications)
                    .setContentTitle(restaurant.getName())
                    .setContentText(restaurant.getAddress())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText((restaurant.getAddress()+"\n"+getUsersWhoJoinRestaurant(restaurant))))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NOTIFICATION_ID, builder);
        }
    }

    public static void scheduleWorker(WorkManager workManager) {
        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .build();

        workManager.enqueueUniqueWork(WORKER_ID, ExistingWorkPolicy.REPLACE, uploadWorkRequest);
    }

    public static long getTimeDuration() {
        long hours;
        long minutes;
        long delay;

        Time time = new Time();
        time.setToNow();
        Log.e(TAG, "getTimeDuration: " + time.hour + ":" + time.minute);
        if (time.hour < 12) {
            hours = 11 - time.hour;
        } else {
            hours = 24 - time.hour + 11;
        }
        minutes = 60 - time.minute;
        delay = (hours * 60) + minutes;
        Log.e(TAG, "getTimeDuration: " + delay);
        return delay;
    }

    public Restaurant getRestaurant(){
        if (restaurantRepository.getRestaurantsList().getValue()!= null) {
            String restaurantId = userRepository.getChosenRestaurantIdFromUser(userRepository.getCurrentUserUID()).getValue();
            for (int i = 0; i<restaurantRepository.getRestaurantsList().getValue().size();i++){
                if (restaurantRepository.getRestaurantsList().getValue().get(i).getUid().equals(restaurantId)) {
                    restaurant = restaurantRepository.getRestaurantsList().getValue().get(i);
                    return restaurant;
                }
            }
        }
        return null;
    }

    public StringBuilder getUsersWhoJoinRestaurant(Restaurant restaurant){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<restaurant.getUsersWhoChoseRestaurantByName().size(); i++){
                stringBuilder.append(restaurant.getUsersWhoChoseRestaurantByName().get(i)+"\n");
            }
       return stringBuilder;
    }
}
