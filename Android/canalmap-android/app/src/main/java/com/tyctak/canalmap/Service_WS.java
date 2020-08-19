package com.tyctak.canalmap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.libraries.XP_Library_FS;
import com.tyctak.map.libraries.XP_Library_WS;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service_WS extends Service {

    private final String TAG = "Service_WS";

    private final int INITIAL_DELAY_SECONDS = 20;
    private final int REPEATING_DELAY_SECONDS = 60;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        Service_WS getService() {
            return Service_WS.this;
        }
    }

    private final XP_Library_WS XPLIBWS = new XP_Library_WS();
    ScheduledExecutorService scheduledExecutorService = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isNetworkAvailable = XPLIBWS.isNetworkAvailable();
//                    Global.setNetworkAvailable(isNetworkAvailable);

                    if (isNetworkAvailable) {
                        XPLIBWS.getRoles();
                        XPLIBWS.getRoutes();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    String guid = Global.getInstance().getDb().getMyEntityGuid();

                    if (!Global.getInstance().getDb().getImported()) {
                        try {
                            XP_Library_FS XPLIBFS = new XP_Library_FS();

                            FileInputStream fileInputStream = MyApp.getContext().getAssets().openFd("import.zip").createInputStream();
                            ArrayList<String> importFile = XPLIBFS.readImportFile(fileInputStream);

                            if (importFile != null) {
                                for (String value : importFile) {
                                    XPLIBWS.handlerJSON("poilocations", 0l, value);
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        Global.getInstance().getDb().writeImported();
                    }

                    boolean changesFound = XPLIBWS.getSubCategories();

                    if (changesFound) {
                        ArrayList<String> routes = Global.getInstance().getDb().getRoutesWithPoisOnServer();

                        for (String route : routes) {
                            String[] items = route.split(";");
                            Long serverDate = Long.parseLong(items[1]);
                            Long currentDate = Long.parseLong(items[2]);

                            do {
                                serverDate = XPLIBWS.synchroniseServer(guid, "poilocations", items[0], serverDate, currentDate);
                            } while (!serverDate.equals(currentDate));
                        }

                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                        if (mySettings.LastServerDate < mySettings.CurrentServerDate) {
                            Long serverDate = mySettings.LastServerDate;
                            Long currentDate = mySettings.CurrentServerDate;

                            do {
                                serverDate = XPLIBWS.synchroniseServer(guid, "entities", "entities", serverDate, currentDate);
                            } while (!serverDate.equals(currentDate));
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, INITIAL_DELAY_SECONDS, REPEATING_DELAY_SECONDS, TimeUnit.SECONDS);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService != null) scheduledExecutorService.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}