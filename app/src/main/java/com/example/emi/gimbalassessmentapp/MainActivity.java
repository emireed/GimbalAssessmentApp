package com.example.emi.gimbalassessmentapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class MainActivity extends AppCompatActivity {

    // For obtaining location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private SettingsClient mSettingsClient;

    // For Geofences
    private GeofencingClient mGeofencingClient;
    private Geofence ROWGeofence;
    private static final String ROW_GEOFENCE_ID = "ROW geofence for Gimbal";

    // Static values about ROW Geofence
    private static final double ROW_LAT = 34.0339128;
    private static final double ROW_LON = -118.2418602;
    private static final float ROW_RAD = 21;

    // Static values about

    // Permission request IDs
    final static int REQUEST_LOCATION_UPDATE = 0;
    final static int REQUEST_GEOFENCE_ADD = 100;

    TextView textView;
    private Boolean mUpdatingLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the layout
        textView = (TextView) findViewById(R.id.location_text);

        // Setup Clients
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);


        // Setup Geofence
        createGeofence();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(REQUEST_GEOFENCE_ADD);
        } else {
            textView.setText("You will be notified when you arrive at The ROW");
            addGeofence();
        }

        // Setup location services
        setupLocationCallback();
        setupLocationRequest();
        createLocationSettingsRequest();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(REQUEST_LOCATION_UPDATE);
        } else {
            textView.setText("You will be notified when you arrive at The ROW");
            startUpdatingLocation();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if have permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // If no, request
            requestPermissions(REQUEST_GEOFENCE_ADD);
            requestPermissions(REQUEST_LOCATION_UPDATE);
        } else {
            // If yes and not already updating, start updating location
            textView.setText("You will be notified when you arrive at The ROW");
            if (!mUpdatingLocation) {
                addGeofence();
                startUpdatingLocation();
            }
        }

    }

    /**
     * This function sets up the location callback that will keep tracking the phones location
     */
    private void setupLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Print the location on the screen
                textView.setText(locationResult.toString());
            }
        };
    }

    /**
     * This function initializes the LocationRequest
     */
    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); // Ask every 10 seconds
        mLocationRequest.setFastestInterval(5000); // Receive at most every 5 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Prefer accuracy
    }

    /**
     * This function creates the LocationSettingsRequest with our LocationRequest
     */
    private void createLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * This function begins tracking the user's position
     */
    private void startUpdatingLocation() {

        // Check if permission granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Begin tracking using our LocationCallback
            mUpdatingLocation = true;
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper());
        }
    }

    /**
     * This function initializes the geofence around The ROW in DTLA
     */
    private void createGeofence() {
        ROWGeofence = new Geofence.Builder()
                .setCircularRegion(ROW_LAT, ROW_LON, ROW_RAD)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setRequestId(ROW_GEOFENCE_ID)
                .setTransitionTypes(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        Log.d("location", "Created geofence");
    }

    /**
     * This function creates the ENTER GeofencingRequest
     * @return GeofencingRequest
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(ROWGeofence);
        Log.d("location", "geofencing request");
        return builder.build();
    }

    /**
     * This function adds our geofence around The ROW to the GeofencingClient
     */
    private void addGeofence() {
        // Check if we have permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Add the geofence
            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());
            Log.d("location", "added geofence");
        }

    }

    /**
     * This function creates a PendingIntent based on our GeofenceTransitionsIntentService
     * @return PendingIntent
     */
    private PendingIntent getGeofencePendingIntent() {
        Log.d("location", "create pending intent");
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This function displays rationale if necessary, and requests missing permissions
     * @param iD
     */
    private void requestPermissions(int iD) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            textView.setText("Do not have permission to access location");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, iD);
        }
    }

    /**
     * This function adds the geofence or starts tracking the location if permission is granted
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GEOFENCE_ADD: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        textView.setText("You will be notified when you arrive at The ROW");
                        addGeofence();
                    }
                }
            }
            case REQUEST_LOCATION_UPDATE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        textView.setText("You will be notified when you arrive at The ROW");
                        startUpdatingLocation();
                    }
                }
            }
        }
    }
}