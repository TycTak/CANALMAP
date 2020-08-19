package com.tyctak.canalmap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class Service_Closing extends Service {

    private final String TAG = "Service_Closing";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

//        Log.d(TAG, "onTaskRemoved START");
//
//        try {
////            XP_Library_WS XPLIBWS = new XP_Library_WS();
////            XPLIBWS.cancelAsyncCalls();
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG,"#2 " + Global.getInstance().getDb());
//
//                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();
//
//                    if (mySettings.SendPosition) {
//                        Log.d(TAG, "Runnable.onTaskRemoved SENDING POSITION START " + mySettings.SendPosition);
//                        mySettings.SendPosition = false;
//                        Global.getInstance().getDb().writeStopSendPosition(Global.getInstance().getDb().getMyEntityGuid());
//
//                        Log.d(TAG, "Runnable.onTaskRemoved SENDING POSITION STOP");
//                    }
//                }
//            });
//
//            thread.start();
//            thread.join();
//
//            Thread sendThread = new Thread(new Runnable() {
//                @Override
//                public synchronized void run() {
//                    XP_Library_PK LIBPK = new XP_Library_PK();
//                    LIBPK.executeSend();
//                }
//            });
//
//            sendThread.start();
//            sendThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        stopService(new Intent(MyApp.getContext(), Service_PK.class));
        stopService(new Intent(MyApp.getContext(), Service_WS.class));
        stopService(new Intent(MyApp.getContext(), Service_GP.class));
        stopService(new Intent(MyApp.getContext(), Service_MD.class));

        Log.d(TAG, "onTaskRemoved END");

        stopSelf();
    }
}