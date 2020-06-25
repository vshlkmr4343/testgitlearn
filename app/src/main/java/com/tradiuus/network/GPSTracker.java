package com.tradiuus.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.tradiuus.R;
import com.tradiuus.TradiuusApp;
import com.tradiuus.models.Technician;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class GPSTracker extends Service {
    private static String TAG = GPSTracker.class.getName();

    private Context mContext;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 7 * 1000; // 7 second

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = GPSTracker.this;
        getLocation();
        Log.e(TAG, "GPSTracker onCreate -> " + System.currentTimeMillis());

        //For creating the Foreground Service
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? getNotificationChannel(notificationManager) : "";
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        // .setPriority(PRIORITY_MIN)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .build();
                startForeground(110, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String getNotificationChannel(NotificationManager notificationManager) {
        String channelId = "com.tradiuus.network.gpstracker";
        String channelName = getResources().getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "GPSTracker onDestroy -> " + System.currentTimeMillis());
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        Log.e(TAG, "GPSTracker onStartCommand -> " + System.currentTimeMillis());
        getLocation();
        return START_STICKY;
    }

    public void getLocation() {
        try {
            if (mFusedLocationClient == null) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            }
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);
                mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } catch (Exception e) {
            Log.e(TAG, "getLocation: " + e.getMessage());
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        private boolean firstTimeLoad = true;

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            try {
                Log.e(TAG, "GPSTracker onLocationResult -> " + System.currentTimeMillis());
                for (Location location : locationResult.getLocations()) {
                    updateGPSCoordinates(location);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }
    };

    public void updateGPSCoordinates(Location location) {
        try {
            if (location != null) {
                Technician mTechnician = ((TradiuusApp) mContext.getApplicationContext()).mTechnician;
                if (mTechnician != null) {
                    updateLocation(mTechnician.getTechnician_id(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateGPSCoordinates: " + e.getMessage());
        }
    }

    public void updateLocation(String technician_id, String lat, String lng) {
        try {
            if (!NetworkController.isNetworkAvailable(this.mContext)) {
                return;
            }

            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("api_key", NetworkController.API_KEY)
                        .add("technician_id", technician_id)
                        .add("latitude", lat)
                        .add("longitude", lng)
                        .build();
                NetworkController.postBase(NetworkController.NetworkRoutes.TECHNICIAN_UPDATE_LOCATION, formBody, new NetworkResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            Log.e(TAG, "GPSTracker result: " + response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "error onSuccess: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(JSONObject error) {
                        Log.e(TAG, "onFailure: " + error.toString());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "updateLocation network call: " + e.getLocalizedMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateLocation main: " + e.getMessage());
        }
    }

}