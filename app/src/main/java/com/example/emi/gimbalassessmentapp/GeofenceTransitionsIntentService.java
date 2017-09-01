package com.example.emi.gimbalassessmentapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emi on 8/30/17.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String NAME = "Geofencing";
    private String ROW_GEOFENCE_ID;
    private String OUTER_GEOFENCE_ID;

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

        // Initialize values
        ROW_GEOFENCE_ID = getResources().getString(R.string.rowGeofenceID);
        OUTER_GEOFENCE_ID = getResources().getString(R.string.outerGeofenceID);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // For each transition, we check which geofence and the type of transition
        List<String> geofenceIDList = toIDs(geofencingEvent.getTriggeringGeofences());
        for (String geofenceID : geofenceIDList) {

            if (geofenceID.equals(ROW_GEOFENCE_ID)) {

                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    sendNotification();
                    updateUX(true);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    updateUX(false);
                }

            } else if (geofenceID.equals(OUTER_GEOFENCE_ID)) {

                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    updateSpeed(true);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    updateSpeed(false);
                }
            }
        }

    }

    /**
     * This function creates and deploys a notification
     */
    private void sendNotification() {
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

    /**
     * This function calls the InROWReceiver to update the UX
     * @param inROW
     */
    private void updateUX(Boolean inROW) {
        Intent receiverIntent = new Intent(getString(R.string.rowGeofenceIntent));
        receiverIntent.putExtra("inROW", inROW);
        LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
    }

    /**
     * This function calls the InROWReceiver to update the location update speed and priority
     * @param inOuter
     */
    private void updateSpeed(Boolean inOuter) {
        Intent receiverIntent = new Intent(getString(R.string.rowGeofenceIntent));
        receiverIntent.putExtra("inOuter", inOuter);
        LocalBroadcastManager.getInstance(this).sendBroadcast(receiverIntent);
    }

    /**
     * This function changes the list of Geofences into a list of their IDs
     * @param geofenceList
     * @return geofenceIDList
     */
    private List<String> toIDs(List<Geofence> geofenceList) {
        List<String> geofenceIDList = new ArrayList<>();

        for (Geofence geofence : geofenceList) {
            geofenceIDList.add(geofence.getRequestId());
        }

        return geofenceIDList;
    }
}
