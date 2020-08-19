package com.tyctak.canalmap;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._MySettings;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.Locality;

public class Service_GP extends Service implements LocationListener {

    private final String TAG = "Service_GP";
    private final int maxZeroAngleSeconds = 120;
    private final Integer ACCURACY_LIMIT = 50;

    private int zeroAngleSeconds = 0;
    private Locality currentLocality;
    private LocationManager locationManager;
    private int Accuracy = 0;
    private int updateGpsInterval;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        zeroAngleSeconds = 0;
        selectProvider();

        Log.d(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");

        if (isStarted()) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    private Boolean isStarted() {
        return (locationManager != null);
    }

    private void selectProvider() {
        Log.d(TAG, "selectProvider");

        try {
            if (!isStarted())
                locationManager = (LocationManager) MyApp.getContext().getSystemService(Context.LOCATION_SERVICE);

            boolean checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (checkGPS || checkNetwork) {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                Library_UI LIBUI = new Library_UI();
                updateGpsInterval = LIBUI.getUpdateGpsInterval(mySettings.UpdateGps);

                if (checkNetwork && !checkGPS) {
                    initialiseGps(LocationManager.NETWORK_PROVIDER, updateGpsInterval, Global.getInstance().getDb().getSystem().DistanceGps);
                } else if (checkGPS) {
                    initialiseGps(LocationManager.GPS_PROVIDER, updateGpsInterval, Global.getInstance().getDb().getSystem().DistanceGps);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialiseGps(String provider, Integer intervalGps, Integer distanceGps) {
        Log.d(TAG, "initialiseGps");

        try {
            if (isStarted()) {
                locationManager.requestLocationUpdates(provider, intervalGps, (float)distanceGps, this);
                Location latestLocation = locationManager.getLastKnownLocation(provider);
                Locality latestLocality = new Locality(provider, Global.getInstance().getDb().roundDown(latestLocation.getLatitude()), Global.getInstance().getDb().roundDown(latestLocation.getLongitude()), latestLocation.getSpeed(), latestLocation.getBearing(), latestLocation.getAccuracy(), true);

                if (checkLocationAccuracy(latestLocality, currentLocality)) {
                    currentLocality = latestLocality; // new Locality(Global.getInstance().getDb().roundDown(latestLocation.getLongitude()), Global.getInstance().getDb().roundDown(latestLocation.getLongitude()), latestLocation.getSpeed(), latestLocation.getBearing(), latestLocation.getAccuracy());
//                    currentLocality.setLongitude(Global.getInstance().getDb().roundDown(latestLocation.getLongitude()));
//                    currentLocality.setLatitude(Global.getInstance().getDb().roundDown(latestLocation.getLatitude()));

                    _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();

                    Global.getInstance().getDb().writeEntityPosition(currentLocality, myEntitySettings.EntityGuid, _Entity.enmStatus.NotMoving);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private boolean checkLocationAccuracy(Locality latestLocation, Locality previousLocation) {
        boolean isMoreAccurate = false;

        if (latestLocation == null && previousLocation == null) {
            isMoreAccurate = false;
        } else if (previousLocation == null) {
            isMoreAccurate = true;
        } else if ((latestLocation.getAccuracy() < (previousLocation.getAccuracy() * 1.5) || latestLocation.getAccuracy() < ACCURACY_LIMIT) && latestLocation.getProvider().equals(previousLocation.getProvider())) {
            isMoreAccurate = true;
        } else if ((latestLocation.getAccuracy() < (previousLocation.getAccuracy() * 2) || latestLocation.getAccuracy() < ACCURACY_LIMIT) && !latestLocation.getProvider().equals(previousLocation.getProvider())) {
            isMoreAccurate = true;
        }

        if (latestLocation != null) {
            Accuracy = (int)latestLocation.getAccuracy();
        } else {
            Accuracy = 0;
        }

        return isMoreAccurate;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        Library_UI LIBUI = new Library_UI();
        int updateGps = LIBUI.getUpdateGpsInterval(mySettings.UpdateGps);

        Locality latestLocality = new Locality(location.getProvider(), Global.getInstance().getDb().roundDown(location.getLatitude()), Global.getInstance().getDb().roundDown(location.getLongitude()), location.getSpeed(), location.getBearing(), location.getAccuracy(), true);

        if (checkLocationAccuracy(latestLocality, currentLocality)) {
            currentLocality = latestLocality;

            _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();
            Locality lastLocality = new Locality(location.getProvider(), Global.getInstance().getDb().roundDown(myEntitySettings.Latitude), Global.getInstance().getDb().roundDown(myEntitySettings.Longitude), 0, 0, 0, true);

            int bearing = (int) lastLocality.bearingTo(currentLocality);

            if (bearing == 0) {
                zeroAngleSeconds += (updateGpsInterval / 1000);
            } else {
                zeroAngleSeconds = 0;
            }

            currentLocality.setZeroAngleFixed((zeroAngleSeconds >= maxZeroAngleSeconds));

            Global.getInstance().getDb().writeEntityPosition(currentLocality, myEntitySettings.EntityGuid, _Entity.enmStatus.Moving);
        }

        if (updateGps != updateGpsInterval) {
            updateGpsInterval = updateGps;
            zeroAngleSeconds = 0;

            if (isStarted()) {
                locationManager.removeUpdates(this);
            }

            selectProvider();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }
}