package com.example.emi.gimbalassessmentapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by emi on 8/30/17.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String NAME = "Geofencing";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GeofenceTransitionsIntentService() {
        super(NAME);
    }


    /**
     * On a geofence transition, function sends a notification if it's GEOFENCE_TRANSITION_ENTER
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("location", "Handling geofence intent");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            sendNotification();
        }
    }

    /**
     * This function creates and deploys a notification
     */
    private void sendNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set all of the notification's values
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH) // Make the notif display onscreen
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(Color.BLUE)
                .setContentTitle("The ROW in DTLA")
                .setContentText("You have entered The ROW in DTLA")
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }
}
