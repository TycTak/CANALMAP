package com.tyctak.map.libraries;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._MySettings;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;

import java.util.logging.Logger;

public class Bootstrap {
    private static final Logger LOGGER = Logger.getLogger("Bootstrap");
    private static Bootstrap instance = new Bootstrap();
    private SQLighterDb dbWriteOnly;
    private SQLighterDb dbReadOnly;
    private _MySettings mySettings = null;
    private _Entity myEntitySettings;
    private static XP_Library_DB XPLIBDB;
    private String deviceId = null;
    private Object context = null;
    private String dbPath = null;
    private String appName = null;
    private String appCode = null;
    private String appStyle = null;
    public final static String k = "YTM3Y";

    private Bootstrap() {}

    public void initialise(String databaseName, String appStyle, String appName, String appCode, String deviceId, String path, Object ctx) {
        LOGGER.info(String.format("initialise %s", deviceId));

        this.deviceId = deviceId;
        this.dbPath = path;
        this.context = ctx;
        this.appName = appName;
        this.appCode = appCode;
        this.appStyle = appStyle;

        XPLIBDB = new XP_Library_DB(databaseName, dbPath, context);
    }

    public SQLighterDb getDbReadOnly() {
        return dbReadOnly;
    }

    public void setDbReadOnly(SQLighterDb db) {
//        if (db == null && this.dbReadOnly != null) this.dbReadOnly.close();
        this.dbReadOnly = db;
    }

    public SQLighterDb getDbWriteOnly() {
        return dbReadOnly;
    }

    public void setDbWriteOnly(SQLighterDb db) {
//        if (db == null && this.dbWriteOnly != null) this.dbWriteOnly.close();
        this.dbWriteOnly = db;
    }

    public void getDbWriteOnly(SQLighterDb db) {
        this.dbWriteOnly = db;
    }

    private Object _lock = new Object();

    public _MySettings getMySettings() {
        if (mySettings == null) {
            synchronized (_lock) {
                setMySettings(XPLIBDB.createMySettings());
            }
        }

        return mySettings;
    }

    public void setMySettings(_MySettings mySettings) { this.mySettings = mySettings; }

    public _Entity getMyEntitySettings() {
        if (myEntitySettings == null) {
            synchronized (_lock) {
                setMyEntitySettings(XPLIBDB.createMyEntitySettings());
            }
        }

        return myEntitySettings;
    }

    public void setMyEntitySettings(_Entity myEntitySettings) { this.myEntitySettings = myEntitySettings; }

    public XP_Library_DB getDatabase() {
        return XPLIBDB;
    }

    public String getDeviceId() { return deviceId; }

    public String getAppName() { return appName; }

    public String getAppCode() { return appCode; }

    public String getAppStyle() { return appStyle; }

    public static String getPassword() {
        return k + XP_Library_SC.MyInitQ + Bootstrap.getInstance().getDatabase().getSystem().FtpPassword + XP_Library_DB.ASQ;
    }

    public static Bootstrap getInstance() {
        return instance;
    }
}