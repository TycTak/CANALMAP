package com.tyctak.canalmap;

import android.content.Intent;
import android.util.Log;

public class Worker_DownloadTiles implements Runnable {
    private String TAG = "Worker_DownloadTiles";

    private String routeGuid;
    private DownloadTilesListener listener;

    public interface DownloadTilesListener {
        void onSuccess();
        void onFailed();
    }

    public Worker_DownloadTiles(String pRouteGuid, DownloadTilesListener pListener) {
        routeGuid = pRouteGuid;
        listener = pListener;
    }

    @Override
    public void run() {
        Log.d("Activity_Routes", "#3");
        Global.getInstance().getDb().writeUpdateRoute(routeGuid);
        Log.d("Activity_Routes", "#4");

        if (Global.getInstance().getDb().downloadTiles(routeGuid, false)) {
            Log.d("Activity_Routes", "#5");
            Global.getInstance().getDb().writeUpdateRoute(routeGuid);
            Log.d("Activity_Routes", "#6");

            Activity_Main.setRedrawMap(true);

            Log.d("Activity_Routes", "#7");

            if (listener != null) listener.onSuccess();

            MyApp.getContext().startService(new Intent(MyApp.getContext(), Service_MD.class));
        } else {
            if (listener != null) listener.onFailed();
        }
    }
}
