  package com.tyctak.canalmap;

import android.util.DisplayMetrics;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.libraries.Bootstrap;
//import com.tyctak.canalmap.libraries.Library_DB;
import com.tyctak.map.libraries.XP_Library_DB;

  public class Global {
    private static Global instance = new Global();

//    private static Library_DB LIBDB;
    private float densityMultiplier = 0;
    private String TAG = "Global";

    private String deviceId;
    private String path;

    public static Global getInstance() {
        return instance;
    }

    public void initialise(String databaseName, String appStyle, String appName, String appCode, String pDeviceId, String pPath) {
        deviceId = pDeviceId;
        path = pPath;

        Bootstrap.getInstance().initialise(databaseName, appStyle, appName, appCode, deviceId, path, MyApp.getContext());
    }

    public XP_Library_DB getDb() {
//        if (Bootstrap.getInstance().getDatabase().isValid()) {
//            Log.d(TAG, "database is null");
//
//        }

        return Bootstrap.getInstance().getDatabase();
    }

//    public boolean DatOnSdCard() {
//        Library_FS LIBFS = new Library_FS();
//        XP_Library_FS XPLIBFS = new XP_Library_FS();
//        return XPLIBFS.isFileExists(LIBFS.getExternalPath(MyApp.getContext()), XP_Library_DB.DATABASE_NAME);
//    }

//    public static Library_DB getLIBDB() {
//        if (LIBDB == null) LIBDB = new Library_DB(MyApp.getContext());
//        return LIBDB;
//    }

    public float getDensityMultiplier() {
        if (densityMultiplier == 0) {
            DisplayMetrics metrics = MyApp.getContext().getResources().getDisplayMetrics();
            densityMultiplier = (float) (metrics.density);
        }

        return densityMultiplier;
    }

    public boolean getMyPoi(String entityGuid) {
        boolean retval = false;

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.IsAdministrator) {
            retval = true;
        } else {
            retval = entityGuid.equals(Global.getInstance().getDb().getMyEntityGuid());
        }

        return retval;
    }

    public final static String k = "YTM3Y";
}