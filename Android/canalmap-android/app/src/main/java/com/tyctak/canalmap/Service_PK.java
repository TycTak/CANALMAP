package com.tyctak.canalmap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tyctak.map.libraries.XP_Library_PK;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Service_PK extends Service {

    private final String TAG = "Service_PK";

    private final int INITIAL_DELAY_SECONDS = 5; //30
    private final int REPEATING_DELAY_SECONDS = 10; //120

    ScheduledExecutorService scheduledExecutorService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public synchronized void run() {
//                Library LIB = new Library();
//                boolean tileDownloadRunning = LIB.isServiceRunning(Service_MD.class);
//
//                if (!tileDownloadRunning) {
                Log.d(TAG, "Service_PK START");

                XP_Library_PK LIBPK = new XP_Library_PK();
                LIBPK.executeSend();

                Log.d(TAG, "Service_PK END");
//                }
            }
        }, INITIAL_DELAY_SECONDS, REPEATING_DELAY_SECONDS, TimeUnit.SECONDS);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService != null) scheduledExecutorService.shutdown();
    }
}