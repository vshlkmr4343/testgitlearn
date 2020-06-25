package com.tradiuus.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.tradiuus.R;

public class LocationHandler {

    private static LocationHandler mLocationHandler = null;

    private LocationManager locationManager;
    private Boolean locationServiceBoolean = false;
    private int providerType = 0;
    private AlertDialog alert;

    public static LocationHandler getInstance(Context context) {
        if (mLocationHandler == null)
            mLocationHandler = new LocationHandler();

        mLocationHandler.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsEnabled = mLocationHandler.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkIsEnabled = mLocationHandler.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (networkIsEnabled == true && gpsIsEnabled == true) {
            mLocationHandler.locationServiceBoolean = true;
            mLocationHandler.providerType = 1;
        } else if (networkIsEnabled != true && gpsIsEnabled == true) {
            mLocationHandler.locationServiceBoolean = true;
            mLocationHandler.providerType = 2;
        } else if (networkIsEnabled == true && gpsIsEnabled != true) {
            mLocationHandler.locationServiceBoolean = true;
            mLocationHandler.providerType = 1;
        }
        return mLocationHandler;
    }


    public Boolean isLocationServiceAvailable() {
        return locationServiceBoolean;
    }

    public int getProviderType() {
        return providerType;
    }

    public void createLocationServiceError(final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(
                "You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
        alert = builder.create();
        alert.show();
    }

}
