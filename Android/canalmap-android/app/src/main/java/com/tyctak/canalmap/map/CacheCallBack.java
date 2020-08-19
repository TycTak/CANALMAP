package com.tyctak.canalmap.map;

import android.content.Context;
import android.util.Log;

public class CacheCallBack implements LocalDownloadTiles.CacheManagerCallback {

    private Integer totalTiles = 0;
    private String TAG = "CacheCallBack";
    private Context context;

    public CacheCallBack(Context ctx) {
        context = ctx;
    }

    @Override
    public void onTaskComplete() {
        Log.d(TAG, "onTaskComplete");
    }

    @Override
    public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
        Log.d(TAG, String.format("updateProgress %s %s %s %s", progress, currentZoomLevel, zoomMin, zoomMax));
        Log.d(TAG, String.format("Percentage %s", (progress / totalTiles) * 100));
    }

    @Override
    public void downloadStarted() {
        Log.d(TAG, "downloadStarted");
    }

    @Override
    public void setPossibleTilesInArea(int total) {
        totalTiles = total;
        Log.d(TAG, String.format("setPossibleTilesInArea %s", total));
    }

    @Override
    public void onTaskFailed(int errors) {
        Log.d(TAG, String.format("onTaskFailed", errors));
    }
}