package com.tyctak.map.libraries;

import com.tyctak.map.entities._Association;
import com.tyctak.map.entities._Country;
import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._Incoming;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Poi;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.map.entities._Route;
import com.tyctak.map.entities._ShortLocation;
import com.tyctak.map.entities._System;
import com.tyctak.map.entities._Tile;
import com.vals.a2ios.sqlighter.impl.SQLighterDbImpl;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

//import sun.rmi.runtime.Log;

import static com.tyctak.map.entities._MySettings.enmFilter.favourite;

public class XP_Library_DB {

    private final String TAG = "XP_Library_DB";
    private static final Logger LOGGER = Logger.getLogger( "XP_Library_DB" );

    public final static String ASQ = "hMjMyNjIw";
    public final static int DATABASE_VERSION = 12;
    private static _System system;
    private static double margin = 0.001;
    public static String splitter = "^";
    private static String _dbPath;
    private static Object _context;
    private static String _databaseName;

    private final String TABLE_MYSETTINGS = "mysettings";
    private final String TABLE_ENTITIES = "entities";
    private final String TABLE_ROUTES = "routes";
    private final String TABLE_POIS = "pois";
    private final String TABLE_POILOCATIONS = "poilocations";
    private final String TABLE_BINARIES = "binaries";
    private final String TABLE_TILES = "tiles";
    private final String TABLE_CROSSCHECK = "crosscheck";
    private final String TABLE_INCOMING = "incoming";
    private final String TABLE_ENTITYMETA = "entitymeta";
    private final String TABLE_SYSTEM = "system";
    private final String TABLE_IMAGES = "images";

    public enum enmBinaryType {
        PoiLocation,
        Entity
    }

    public enum enmFilterPoi {
        Reset,
        JustItems,
        Created,
        New,
        Tick
    }

    public enum enmSecurityFeatures {
        isPublisher,
        isReviewer,
        isAdministrator,
        PublisherLongitude,
        PublisherLatitude,
        PublisherRadius,
        ReviewerLongitude,
        ReviewerLatitude,
        ReviewerRadius,
        isSecurityPremium
    }

    public String getPremiumGuid() {
        return "kl1z8r4._asa.1kks733_s2";
    }

    public String getAllRouteGuid() {
        return "qkis2.1_asa.1kks733_s3";
    }

    public static String getHistoricRouteGuids() {
        return "'qkis2.1_asa.1kks733', 'jlasqw_2992.ssd991', 'llqa1_112.ssaasa_112', 'lqwos2.ss256_ss.2a', 'aqiw23s_112.ss243s12', 'f23ssx.aas.qw1_a22', 'dklqow_1122_sskks.ss', 'pqaas_112xk.aal11', 'ssa229.aas123_9wo231', 'ha2.xzz2343.s_s29', 'jg2wq_23.dd.121_a12', 'h23.ss_1.xz_123zs2x', 'kwe889fs.12_2212.ssd', 'hgsw221.xa1_sa_sa.a1', 'kssw2332.a21_qo1221', 'jq1qo.ss_qaoq_aas.1', 'ak.1221.65xsxs.jd_12s', 'hdk2.112.a_a2lds64', 'daqhd.s1_sklq12sd2', 'us1ls2.sk21_aqq.a23', 'ls11.12sa_asq.23dsw', 'jsw122l.ss_211_12.2', 'vw2m.q876.sk8s_s55'";
    }

    public _System getSystem() {
        if (system == null) system = new _System();
        return system;
    }

    public String getDatabaseName() {
        return _databaseName;
    }

    private SQLighterDb getDb(String databaseName, String dbPath, Object context) {
        SQLighterDbImpl db = null;

        try {
            db = new SQLighterDbImpl();

            db.setDbPath(dbPath);
            db.setDbName(databaseName);

            db.setOverwriteDb(false);
            db.setContext(context);

            db.deployDbOnce();
            db.openIfClosed();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return db;
    }

    public void openDb() {
        Bootstrap.getInstance().setDbReadOnly(getDb(_databaseName, _dbPath, _context));
        Bootstrap.getInstance().setDbWriteOnly(getDb(_databaseName, _dbPath, _context));
    }

    public void closeDb() {
        Bootstrap.getInstance().setDbReadOnly(null);
        Bootstrap.getInstance().setDbWriteOnly(null);
    }

    public XP_Library_DB(String databaseName, String dbPath, Object context) {
        try {
            this._dbPath = dbPath;
            this._context = context;
            this._databaseName = databaseName;

            Bootstrap.getInstance().setDbReadOnly(getDb(_databaseName, dbPath, context));
            Bootstrap.getInstance().setDbWriteOnly(getDb(_databaseName, dbPath, context));

            writePragma();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getDatabaseVersion() {
        String sql = String.format("SELECT version FROM %s", TABLE_SYSTEM);
        int currentVersion = getSingleInt(sql);
        return currentVersion;
    }

    public boolean getAdminRequest() {
        String sql = String.format("SELECT adminrequest FROM %s", TABLE_SYSTEM);
        boolean retval = (getSingleInt(sql) != 0);
        return retval;
    }

    public boolean getPremiumRequest() {
        String sql = String.format("SELECT premiumrequest FROM %s", TABLE_SYSTEM);
        boolean retval = (getSingleInt(sql) != 0);
        return retval;
    }

    public boolean writeAdminRequest(boolean value) {
        String sql = String.format("UPDATE %s SET adminrequest = %s", TABLE_SYSTEM, gb(value));
        boolean retval = write(sql);

        return retval;
    }

    public boolean writePremiumRequest(boolean value) {
        String sql = String.format("UPDATE %s SET premiumrequest = %s", TABLE_SYSTEM, gb(value));
        boolean retval = write(sql);

        return retval;
    }

    public int getClassVersion() {
        return DATABASE_VERSION;
    }

    public void upgrade(InputStream inputStream) {
        if (isUpdgradeRequired()) {
            XP_Library_FS XPLIBFS = new XP_Library_FS();
            ArrayList<String> fileContents = XPLIBFS.readTextFile(inputStream);

            if (fileContents.size() > 0) {
                ArrayList<String> sqlScript = getReleaseScript(fileContents);

                if (sqlScript.size() > 0) {
                    upgradeDatabase(sqlScript);
                }
            }
        }
    }

    private boolean isUpdgradeRequired() {
        int currentVersion = getDatabaseVersion();
        LOGGER.info(String.format("SQL Result %s", currentVersion));

        return (currentVersion < DATABASE_VERSION);
    }

    private void upgradeDatabase(ArrayList<String> sqlScript) {
        for (String sql: sqlScript) {
            try {
                write(sql);
            } catch (Exception ex) {
                LOGGER.info(String.format("ERROR: %s", sql));
                ex.printStackTrace();
            }
        }
    }

    private ArrayList<String> getReleaseScript(ArrayList<String> fileContents) {
        ArrayList<String> sqlScript = new ArrayList<>();

        int databaseVersion = getDatabaseVersion();
        int classVersion = getClassVersion();

        boolean started = false;
        String upgradeKey = "--UPGRADE " + classVersion;
        String upgradeFromKey = "--UPGRADE " + databaseVersion + "-" + classVersion;

        for (String line : fileContents) {
            if (line.toUpperCase().equals(upgradeKey) || line.toUpperCase().equals(upgradeFromKey)) {
                started = true;
            } else if (line.toUpperCase().startsWith("--Upgrade ")) {
                started = false;
            } else if (started) {
                sqlScript.add(line);
            }
        }

        return sqlScript;
    }

    public boolean writeExternalId(int rowId) {
        String sql = String.format("UPDATE %s SET externalid = %s WHERE id = %s", TABLE_POILOCATIONS, rowId, rowId);
        return write(sql);
    }

    public Point getMapTileFromCoordinates(final double latitude, final double longitude, final int zoom) {
        final int y = (int) Math.floor((1 - Math.log(Math.tan(latitude * Math.PI / 180) + 1 / Math.cos(latitude * Math.PI / 180)) / Math.PI) / 2 * (1 << zoom));
        final int x = (int) Math.floor((longitude + 180) / 360 * (1 << zoom));
        return new Point(x, y);
    }

    public String getRoutes(int zoom, double latitude, double longitude) {
        String retval = "";

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        Point point = XPLIBCM.getMapTileFromCoordinates(latitude, longitude, zoom);
        int key = ((zoom << zoom) + point.x << zoom) + point.y;

        ArrayList<String> routes = getListCrossCheckedString(key);
        retval = XPLIBCM.concatenateString(",", routes, false);

        return retval;
    }

    public boolean writeBinary(int id, int type, byte[] image) {
        boolean retval = false;

        if (isExistBinary(id, type)) {
            String sql = String.format("UPDATE %s SET binary = %s WHERE id = %s AND type = %s", TABLE_BINARIES, gh(image), id, type);
            retval = write(sql);
        } else if (image != null) {
            String sql = String.format("INSERT INTO %s (binary, id, type) VALUES (%s, %s, %s)", TABLE_BINARIES, gh(image), id, type);
            retval = write(sql);
        }

        return retval;
    }

    public boolean writePoiLocationImage(int id, byte[] image) {
        boolean retval = false;

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.nowAsLong();

        String sql = String.format("UPDATE %s SET updated = %s, status = %s, reviewedfeedback = %s WHERE id = %s", TABLE_POILOCATIONS, now, _PoiLocation.enmStatus.Updated.ordinal(), _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), id);
        retval = write(sql);

        writeBinary(id, enmBinaryType.PoiLocation.ordinal(), image);

        writeCurrentLocalDate(now);

        return retval;
    }

    public boolean deletePoiLocation(int id, int shared) {
        boolean retval = false;

        Long now = XP_Library_CM.getDate(XP_Library_CM.now());

        String sql;

        if (shared > 0) {
            sql = String.format("UPDATE %s SET deleted = %s, updated = %s, status = %s, reviewedfeedback = %s, shared = %s WHERE id = %s", TABLE_POILOCATIONS, gb(true), now, _PoiLocation.enmStatus.Deleted.ordinal(), _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), -1, id);
        } else {
            sql = String.format("UPDATE %s SET deleted = %s, updated = %s, status = %s, reviewedfeedback = %s WHERE id = %s", TABLE_POILOCATIONS, gb(true), now, _PoiLocation.enmStatus.Deleted.ordinal(), _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), id);
        }

        retval = write(sql);
        if (retval) writeCurrentLocalDate(now);

        return retval;
    }

    private long getSingleLong(String sql) {
        long retval = 0;

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null && rs.hasNext()) {
            if (rs.getLong(0) != null) retval = rs.getLong(0);
        }

        return retval;
    }

    private int getSingleInt(String sql) {
        int retval = 0;

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null && rs.hasNext()) {
            if (rs.getInt(0) != null) retval = rs.getInt(0);
        }

        return retval;
    }

    private String getSingleString(String sql) {
        String retval = null;

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null && rs.hasNext()) {
            if (rs.getString(0) != null) retval = rs.getString(0);
        }

        return retval;
    }

    private ArrayList<String> getArrayString(String sql) {
        ArrayList<String> retval = new ArrayList<>();

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                if (rs.getString(0) != null) retval.add(rs.getString(0));
            }
        }

        return retval;
    }

    private SQLighterRs getRecordSet(String sql) {
        SQLighterRs rs = null;

        try {
            long start = System.currentTimeMillis();

            SQLighterDb db = Bootstrap.getInstance().getDbReadOnly();
            rs = db.executeSelect(sql);

            LOGGER.info(String.format("SQL RS (%s) -> %s", timeTaken(start), sql));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;
    }

    public boolean writePragma() {
        boolean retval = false;

        try {
             Bootstrap.getInstance().getDbWriteOnly().executeChange("PRAGMA synchronous = NORMAL");
             SQLighterRs rs = Bootstrap.getInstance().getDbWriteOnly().executeSelect("PRAGMA synchronous");
             retval = (rs.hasNext() && rs.getString(0).equals("1"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean writeWithPrefix(String prefix, String sql) {
        boolean retval = false;

        try {
            long start = System.currentTimeMillis();;

            SQLighterDb db = Bootstrap.getInstance().getDbWriteOnly();
            retval = (db.executeChange(sql) > 0);

            LOGGER.info(String.format("%s (%s) -> %s", prefix, timeTaken(start), sql));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean writeMultipleStatements(ArrayList<String> sqlStatements) {
        boolean retval = false;

        for (String sql : sqlStatements) {
            long start = System.currentTimeMillis();
            retval = write(sql);
            LOGGER.info(String.format("%s (%s) -> %s", "SQL MS", timeTaken(start), sql));
            if (!retval) break;
        }

//        try {
//            SQLighterDb db = Bootstrap.getInstance().getDbWriteOnly();
//            db.beginTransaction();
//
//            for (String sql : sqlStatements) {
//                long start = System.currentTimeMillis();
//                retval = (db.executeChange(sql) > 0);
//                LOGGER.info(String.format("%s (%s) -> %s", "SQL MS", timeTaken(start), sql));
//                if (!retval) break;
//            }
//
//            db.commitTransaction();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return retval;
    }

    public boolean write(String sql) {
        return writeWithPrefix("SQL WR", sql);
    }

    private long timeTaken(long start) {
        return (start != 0 ? (System.currentTimeMillis() - start) : -2);
    }

    public int getWaitingToSend() {
        String sql = String.format("SELECT COUNT(*) FROM incoming WHERE completed = 0", TABLE_INCOMING);
        return getSingleInt(sql);
    }

    public int getEntityCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s", TABLE_ENTITIES);
        return getSingleInt(sql);
    }

    public int getEntityActiveCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE longitude <> 0", TABLE_ENTITIES);
        return getSingleInt(sql);
    }

    private int getLastRowId() {
        String sql = String.format("SELECT last_insert_rowid()");
        return getSingleInt(sql);
    }

    public int getTilesLeft() {
        String sql = String.format("SELECT COUNT(*) FROM tiles WHERE (downloaded = 0 OR downloaded = -1) AND waterwayguid <> 'ALL' LIMIT 1;");
        return getSingleInt(sql);
    }

    public String getPoiDescriptionByName(String name) {
        String sql = String.format("SELECT description FROM %s WHERE name = '%s' LIMIT 1", TABLE_POIS, name);
        return getSingleString(sql);
    }

    public String getRouteGuid() {
        String sql = String.format("SELECT waterwayguid FROM %s WHERE purchased = 1 AND paused = 0 AND tilesdownloaded < totaltiles LIMIT 1", TABLE_ROUTES);
        return getSingleString(sql);
    }

    public String getSessionPassword() {
        _MySettings mySettings = getMySettings();
        return mySettings.SessionPassword;
    }

//    public String getMyInitQ() {
//        _MySettings mySettings = getMySettings();
//        return mySettings.InitQ;
//    }

    public String getMyEncryptedEntityGuid() {
        _MySettings mySettings = getMySettings();

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        if (XPLIBCM.isBlank(mySettings.EncryptedEntityGuid)) throw new RuntimeException();

        return mySettings.EncryptedEntityGuid;
    }

    public String getMyEntityGuid() {
        _MySettings mySettings = getMySettings();

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        if (XPLIBCM.isBlank(mySettings.EntityGuid)) throw new RuntimeException();

        return mySettings.EntityGuid;
    }

    public ArrayList<String> getPoisNameUnfiltered() {
        String sql = String.format("SELECT name FROM %s WHERE filter = 0 AND area IN ('symbols', 'markers')", TABLE_POIS);
        return getArrayString(sql);
    }

    public ArrayList<String> getPoisNameFiltered() {
        String sql = String.format("SELECT name FROM %s WHERE filter = 1 AND area IN ('symbols', 'markers')", TABLE_POIS);
        return getArrayString(sql);
    }

    public ArrayList<String> getListCrossCheckedString(int key) {
        String sql = String.format("SELECT DISTINCT p2.waterwayguid FROM crosscheck p1 INNER JOIN routes p2 ON p2.waterwayid = p1.waterwayid WHERE p1.key = %s", key);
        return getArrayString(sql);
    }

    public ArrayList<String> getAllPois() {
        String sql = String.format("SELECT name FROM %s WHERE area IN ('symbols', 'markers') AND scope = 0 ORDER BY area DESC, category, description", TABLE_POIS);
        return getArrayString(sql);
    }

    public boolean getEntityFavourite(String entityGuid) {
        String sql = String.format("SELECT favourite FROM %s WHERE entityguid = '%s'", TABLE_ENTITYMETA, entityGuid);
        int favourite = getSingleInt(sql);
        return (favourite != 0);
    }

    public boolean getVacuum() {
        String sql = String.format("SELECT vacuum FROM %s", TABLE_MYSETTINGS);
        int vacuum = getSingleInt(sql);
        return (vacuum != 0);
    }

    public boolean getPaused() {
        _MySettings mySettings = getMySettings();
        return (mySettings.Paused);
    }

    public boolean getPurchased() {
        String sql = String.format("SELECT purchased FROM %s WHERE purchased <> 0 LIMIT 1", TABLE_ROUTES);
        int purchased = getSingleInt(sql);
        return (purchased != 0);
    }

    public boolean getIsRoutePurchased(int key) {
        String sql = String.format("SELECT 1 FROM crosscheck p1 INNER JOIN %s p2 ON p1.waterwayid = p2.waterwayid WHERE p2.purchased <> 0 AND p1.key = %s", TABLE_ROUTES, key);
        int exist = getSingleInt(sql);
        return (exist != 0);
    }

    public boolean getHasTile(int key) {
        String sql = String.format("SELECT 1 FROM %s WHERE key = %s", TABLE_TILES, key);
        int exist = getSingleInt(sql);
        return (exist != 0);
    }

    public boolean getIsDownloading() {
        String sql = String.format("SELECT 1 FROM %s WHERE purchased = 1 AND totaltiles > tilesdownloaded AND paused = 0 AND empty = 0 LIMIT 1", TABLE_ROUTES);
        int exist = getSingleInt(sql);
        return (exist != 0);
    }

    public boolean getImported() {
        _MySettings mySettings = getMySettings();
        return (mySettings.Imported);
    }

    private boolean isExistBinary(int id, int type) {
        String sql = String.format("SELECT 1 FROM %s WHERE id = %s AND type = %s LIMIT 1", TABLE_BINARIES, id, type);
        int exist = getSingleInt(sql);
        return (exist != 0);
    }

    public boolean getPurchased(String routeGuid) {
        String sql;

        if (routeGuid.equals(getAllRouteGuid())) {
            sql = String.format("SELECT purchased FROM %s LIMIT 1", TABLE_ROUTES);
        } else {
            sql = String.format("SELECT purchased FROM %s WHERE waterwayguid = '%s'", TABLE_ROUTES, routeGuid);
        }

        int purchased = getSingleInt(sql);
        return (purchased != 0);
    }

    public boolean getStopped(String waterwayGuid) {
        String sql = String.format("SELECT paused, empty FROM %s WHERE waterwayguid = '%s'", TABLE_ROUTES, waterwayGuid);
        SQLighterRs rs = getRecordSet(sql);

        boolean retval = (rs != null && rs.hasNext() && !(rs.getInt(0) == 0));
        if (rs != null) rs.close();

        return retval;
    }

    public _Route getRoute(String routeGuid, int routeId) {
        _Route route = null;

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        String sql;

        if (routeId == 0 && !XPLIBCM.isBlank(routeGuid)) {
            sql = String.format("SELECT waterwayid AS _id, waterwayguid, waterwayname, price, type, map, totaltiles, tilesdownloaded, percentage, empty, myversion, currentversion, purchased, availability, description, filename, paused, lastserverdate, currentserverdate, mbs FROM %s p1 WHERE waterwayguid = '%s'", TABLE_ROUTES, routeGuid);
        } else {
            sql = String.format("SELECT waterwayid AS _id, waterwayguid, waterwayname, price, type, map, totaltiles, tilesdownloaded, percentage, empty, myversion, currentversion, purchased, availability, description, filename, paused, lastserverdate, currentserverdate, mbs FROM %s p1 WHERE waterwayid = %s", TABLE_ROUTES, routeId);
        }

        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            route =  new _Route();
            route.RouteId = rs.getInt(0);
            route.RouteGuid = rs.getString(1);
            route.RouteName = rs.getString(2);
            route.Price = rs.getString(3);
            route.Type = rs.getString(4);
            route.Map = rs.getBlob(5);
            route.TotalTiles = rs.getInt(6);
            route.TilesDownloaded = rs.getInt(7);
            route.Percentage = rs.getInt(8);
            route.Empty = (rs.getInt(9) != 0);
            route.MyVersion = rs.getInt(10);
            route.CurrentVersion = rs.getInt(11);
            route.Purchased = (rs.getInt(12) != 0);
            route.Availability = rs.getString(13);
            route.Description = rs.getString(14);
            route.FileName = rs.getString(15);
            route.Paused = (rs.getInt(16) > 0);
            route.LastServerDate = rs.getLong(17);
            route.CurrentServerDate = rs.getLong(18);
            route.Mbs = rs.getInt(19);
        }

        rs.close();

        return route;
    }

    public _PoiLocation getPoiLocation(int id) {
        _PoiLocation poi = null;

        String sql = String.format("SELECT p1.id, p1.area, name, latitude, longitude, scope, entityguid, b1.binary AS image, null, action, message, created, updated, deleted, twitter, facebook, startdate, enddate, title, locked, CASE WHEN message IS NULL OR message = '' THEN 0 ELSE 1 END AS ismessage, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, route, shared, category FROM %s p1 LEFT OUTER JOIN %s b1 ON b1.id = p1.id AND b1.type = 0 WHERE p1.id = %s", TABLE_POILOCATIONS, TABLE_BINARIES, id);
        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            poi = new _PoiLocation();

            poi.Id = rs.getInt(0);
            poi.Area = rs.getString(1);
            poi.Name = rs.getString(2);
            poi.Latitude = rs.getDouble(3);
            poi.Longitude = rs.getDouble(4);
            poi.Scope = rs.getInt(5);
            poi.EntityGuid = rs.getString(6);
            poi.Image = rs.getBlob(7);
            //            poi.Marker = cursor.getBlob(8);
            poi.Action = rs.getString(9);
            poi.Message = rs.getString(10);
            poi.IsTwitter = (rs.getInt(14) != 0);
            poi.IsTwitter = (rs.getInt(15) != 0);
            poi.IsLocked = (rs.getInt(19) != 0);
            poi.StartDate = rs.getLong(16);
            poi.EndDate = rs.getLong(17);
            poi.Title = rs.getString(18);
            poi.Shared = rs.getInt(23);
            poi.IsMessage = (rs.getInt(20) != 0);
            poi.IsImage = (rs.getInt(21) != 0);
            poi.Route = rs.getString(22);
            long created = rs.getLong(11);
            long updated = rs.getLong(12);
            boolean deleted = (rs.getInt(13) != 0);
            poi.Category = (rs.isNull(24) ? "" : rs.getString(24));

            poi.Status = (deleted ? _PoiLocation.enmStatus.Deleted : (updated == created ? _PoiLocation.enmStatus.Created : _PoiLocation.enmStatus.Updated));
        }

        rs.close();

        return poi;
    }

    public _PoiLocation getPoiLocationComplete(int id) {
        _PoiLocation poi = null;

        String sql = String.format("SELECT p1.id, p1.area, p1.name, p1.latitude, p1.longitude, p1.scope, p1.entityguid, b1.binary AS image, null, p1.action, p1.message, p1.created, p1.updated, p1.deleted, p1.twitter, p1.facebook, p1.startdate, p1.enddate, p1.title, p1.locked, CASE WHEN p1.message IS NULL OR p1.message = '' THEN 0 ELSE 1 END AS ismessage, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, p1.route, p1.shared, p2.level07, p2.level08, p2.level09, p2.level10, p2.level11, p2.level12, p2.level13, p2.level14, p2.level15, p2.level16, p2.level17, p1.externalid, p1.feedback, p1.reviewedfeedback, p1.lastchecksum, p1.checksum, p1.category FROM %s p1 INNER JOIN %s p2 ON p1.name = p2.name LEFT OUTER JOIN %s b1 ON b1.id = p1.id AND b1.type = 0 WHERE p1.id = %s", TABLE_POILOCATIONS, TABLE_POIS, TABLE_BINARIES, id);
        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            poi = new _PoiLocation();

            poi.Id = rs.getInt(0);
            poi.Area = rs.getString(1);
            poi.Name = rs.getString(2);
            poi.Latitude = rs.getDouble(3);
            poi.Longitude = rs.getDouble(4);
            poi.Scope = rs.getInt(5);
            poi.EntityGuid = rs.getString(6);
            poi.Image = rs.getBlob(7);
//            poi.Marker = rs.getBlob(8);
            poi.Action = rs.getString(9);
            poi.Message = rs.getString(10);
            poi.IsTwitter = (rs.getInt(14) != 0);
            poi.IsTwitter = (rs.getInt(15) != 0);
            poi.IsLocked = (rs.getInt(19) != 0);
            poi.StartDate = rs.getLong(16);
            poi.EndDate = rs.getLong(17);
            poi.Title = rs.getString(18);
            poi.Shared = rs.getInt(23);
            poi.IsMessage = (rs.getInt(20) != 0);
            poi.IsImage = (rs.getInt(21) != 0);
            poi.Route = rs.getString(22);
            poi.Created = rs.getLong(11);
            poi.Updated = rs.getLong(12);
            poi.Deleted = (rs.getInt(13) != 0);
            poi.Level07 = rs.getInt(24);
            poi.Level08 = rs.getInt(25);
            poi.Level09 = rs.getInt(26);
            poi.Level10 = rs.getInt(27);
            poi.Level11 = rs.getInt(28);
            poi.Level12 = rs.getInt(29);
            poi.Level13 = rs.getInt(30);
            poi.Level14 = rs.getInt(31);
            poi.Level15 = rs.getInt(32);
            poi.Level16 = rs.getInt(33);
            poi.Level17 = rs.getInt(34);
            poi.ExternalId = rs.getInt(35);
            poi.Feedback = rs.getString(36);
            poi.ReviewedFeedback = _PoiLocation.enmReviewedFeedback.values()[rs.getInt(37)];
            poi.LastCheckSum = rs.getLong(38);
            poi.CheckSum = rs.getLong(39);

            poi.Status = (poi.Deleted ? _PoiLocation.enmStatus.Deleted : (poi.Updated == poi.Created ? _PoiLocation.enmStatus.Created : _PoiLocation.enmStatus.Updated));
        }

        rs.close();

        return poi;
    }

    public ArrayList<String> getProfileIcons() {
        ArrayList<String> names = new ArrayList<String>();

        String sql = String.format("SELECT imagename FROM %s WHERE imagecategory = 'mysettings'", TABLE_IMAGES);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            names.add(rs.getString(0));
        }

        return names;
    }

    public ArrayList<_Entity> getEntities(boolean isFavourite, boolean isShopping, boolean isSocialMedia, String filter) {
        String filterSql = "";
        XP_Library_CM LIBCM = new XP_Library_CM();

        if (!LIBCM.isBlank(filter)) {
            filterSql = " AND (b1.people LIKE '%" + filter + "%' OR b1.entityname LIKE '%" + filter + "%')";
        }

        if (isFavourite) {
            filterSql += " AND e1.favourite <> 0";
        }

        if (isShopping) {
            filterSql += " AND b1.entitytype = 0";
        }

        if (isSocialMedia) {
            filterSql += " AND (IFNULL(b1.facebook, '') <> '' OR IFNULL(b1.twitter, '') <> '' OR IFNULL(b1.instagram, '') <> '' OR IFNULL(b1.youtube, '') <> '' OR IFNULL(b1.patreon, '') <> '')";
        }

        ArrayList<_Entity> entities = new ArrayList<>();

        String sql = String.format("SELECT b1.entityguid, b1.entityname, b1.avatarmarker, b1.status, b1.longitude, b1.latitude, b1.distance, b1.facebook, b1.twitter, b1.instagram, e1.favourite, b1.entitytype, b1.youtube, b1.website, b1.lastmoved FROM %s b1 LEFT OUTER JOIN entitymeta e1 ON e1.entityguid = b1.entityguid WHERE b1.entityname <> '' AND b1.entityname IS NOT NULL %s AND isactive = 1 ORDER BY b1.entityname", TABLE_ENTITIES, filterSql);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Entity entity = new _Entity();

            entity.EntityGuid = rs.getString(0);
            entity.EntityName = rs.getString(1);
            entity.AvatarMarker = rs.getBlob(2);
            entity.Status = _Entity.enmStatus.values()[rs.getInt(3)];
            entity.Longitude = rs.getDouble(4);
            entity.Latitude = rs.getDouble(5);
            entity.Distance = rs.getDouble(6);
            entity.Facebook = rs.getString(7);
            entity.Twitter = rs.getString(8);
            entity.Instagram = rs.getString(9);
            entity.Favourite = (rs.isNull(10) ? false : rs.getInt(10) != 0);
            entity.EntityType = rs.getInt(11);
            entity.YouTube = rs.getString(12);
            entity.Website = rs.getString(13);
            entity.LastMoved = rs.getLong(14);

            entities.add(entity);
        }

        rs.close();

        return entities;
    }

    public ArrayList<_Route> getAllRoutes() {
        ArrayList<_Route> routes = new ArrayList<>();

        String sql = String.format("SELECT waterwayid, waterwayguid, waterwayname, price, type, totaltiles, tilesdownloaded, empty, myversion, currentversion, purchased, availability, paused, description FROM %s ORDER BY price DESC, waterwayname ASC", TABLE_ROUTES);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Route route = new _Route();

            route.RouteId = rs.getInt(0);
            route.RouteGuid = rs.getString(1);
            route.RouteName = rs.getString(2);
            route.Price = rs.getString(3);
            route.Type = rs.getString(4);
            route.TotalTiles = rs.getInt(5);
            route.TilesDownloaded = rs.getInt(6);
            route.Empty = (rs.getInt(7) != 0);
            route.MyVersion = rs.getInt(8);
            route.CurrentVersion = rs.getInt(9);
            route.Purchased = (rs.getInt(10) != 0);
            route.Availability = rs.getString(11);
            route.Paused = (rs.getInt(12) != 0);
            route.Description = rs.getString(13);

            routes.add(route);
        }

        rs.close();

        return routes;
    }

    public _Route getRoute(String routeGuid) {
        _Route route = null;

        String sql = String.format("SELECT waterwayid AS _id, waterwayguid, waterwayname, price, type, map, totaltiles, tilesdownloaded, percentage, empty, myversion, currentversion, purchased, availability, description, filename, paused, lastserverdate, currentserverdate, mbs FROM %s p1 WHERE waterwayguid = '%s' ORDER BY waterwayname ASC", TABLE_ROUTES, routeGuid);
        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            route =  new _Route();
            route.RouteId = rs.getInt(0);
            route.RouteGuid = rs.getString(1);
            route.RouteName = rs.getString(2);
            route.Price = rs.getString(3);
            route.Type = rs.getString(4);
            route.Map = rs.getBlob(5);
            route.TotalTiles = rs.getInt(6);
            route.TilesDownloaded = rs.getInt(7);
            route.Percentage = rs.getInt(8);
            route.Empty = (rs.getInt(9) != 0);
            route.MyVersion = rs.getInt(10);
            route.CurrentVersion = rs.getInt(11);
            route.Purchased = (rs.getInt(12) != 0);
            route.Availability = rs.getString(13);
            route.Description = rs.getString(14);
            route.FileName = rs.getString(15);
            route.Paused = (rs.getInt(16) > 0);
            route.LastServerDate = rs.getLong(17);
            route.CurrentServerDate = rs.getLong(18);
            route.Mbs = rs.getInt(19);
        }

        rs.close();

        return route;
    }

    public _Entity getEntity(String entityGuid) {
        _Entity entity = null;

        String sql = String.format("SELECT b1.entityguid, b1.entityname, b1.people, b1.ishireboat, b1.avatar, b1.icon, b1.status, b1.longitude, b1.latitude, b1.locks, b1.distance, b1.avatarmarker, b1.direction, b1.avatarchecked, b1.publish, b1.changed, b1.zeroanglefixed, b1.iconname, b1.lastchecksum, b1.checksum, b1.phone, b1.tradingname, b1.facebook, b1.twitter, b1.instagram, b1.website, b1.tracker2, b1.entitytype, b1.lastmoved, b1.strength, b1.reference, b1.email, b1.metadata1, b1.metadata2, b1.youtube, e1.favourite, b1.patreon, b1.isactive, b1.updated FROM %s b1 LEFT OUTER JOIN %s e1 ON e1.entityguid = b1.entityguid WHERE b1.entityguid = '%s'", TABLE_ENTITIES, TABLE_ENTITYMETA, entityGuid);
        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            if (rs.hasNext()) {
                entity = new _Entity();
                entity.EntityGuid = rs.getString(0);
                entity.EntityName = rs.getString(1);
                entity.Description = rs.getString(2);
                entity.IsHireBoat = (rs.getInt(3) != null ? (rs.getInt(3) != 0) : false);
                entity.Avatar = rs.getBlob(4);
                entity.Icon = rs.getInt(5);
                entity.Status = _Entity.enmStatus.values()[rs.getInt(6)];
                entity.Longitude = rs.getDouble(7);
                entity.Latitude = rs.getDouble(8);
                entity.Tracker1 = rs.getInt(9);
                entity.Distance = rs.getDouble(10);
                entity.AvatarMarker = rs.getBlob(11);
                entity.Direction = rs.getInt(12);
                entity.AvatarChecked = (rs.getInt(13) != 0);
                entity.Publish = (rs.getInt(14) != 0);
                entity.ZeroAngleFixed = (rs.getInt(16) != 0);
                entity.IconName = (rs.getString(17));
                entity.LastCheckSum = rs.getLong(18);
                entity.CheckSum = rs.getLong(19);
                entity.Phone = rs.getString(20);
                entity.TradingName = rs.getString(21);
                entity.Facebook = rs.getString(22);
                entity.Twitter = rs.getString(23);
                entity.Instagram = rs.getString(24);
                entity.Website = rs.getString(25);
                entity.Tracker2 = rs.getInt(26);
                entity.EntityType = rs.getInt(27);
                entity.LastMoved = rs.getLong(28);
                entity.Strength = rs.getInt(29);
                entity.Reference = rs.getString(30);
                entity.Email = rs.getString(31);
                entity.MetaData1 = rs.getString(32);
                entity.MetaData2 = rs.getString(33);
                entity.YouTube = rs.getString(34);
                entity.Favourite = (rs.getInt(35) != null ? (rs.getInt(35) != 0) : false);
                entity.Patreon = rs.getString(36);
                entity.IsActive = (rs.getInt(37) != null ? (rs.getInt(37) != 0) : false);
                entity.Updated = rs.getLong(38);
            }

            rs.close();
        }

        return entity;
    }

    public ArrayList<_Poi> getPoisRecent(String area, String myEntityGuid) {
        ArrayList<_Poi> pois = new ArrayList<>();

        String sql = String.format("SELECT DISTINCT p1.area, p1.name, p2.description, 0 AS orderidx, p2.action, p2.filter FROM %s p1 INNER JOIN %s p2 ON p1.name = p2.name WHERE p2.scope = 0 AND p1.area = '%s' AND (deleted = 0 OR deleted IS NULL) AND entityguid = '%s' ORDER BY updated DESC LIMIT 10", TABLE_POILOCATIONS, TABLE_POIS, area, myEntityGuid);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Poi poi = new _Poi();

            poi.Area = rs.getString(0);
            poi.Name = rs.getString(1);
            poi.Description = rs.getString(2);
            poi.OrderIdx = rs.getInt(3);
            poi.Action = rs.getString(4);
            poi.Filter = (rs.getInt(5) != 0);

            pois.add(poi);
        }

        rs.close();

        return pois;
    }

    public _Tile getTile(int key) {
        _Tile tile = new _Tile();

        String sql = String.format("SELECT key, tile, waterwayguid, downloaded, tileurl FROM %s WHERE key = %s AND type = 1 ORDER BY tile DESC, waterwayguid DESC", TABLE_TILES, key);
        SQLighterRs rs1 = getRecordSet(sql);

        if (rs1.hasNext()) {
            tile.Tile = rs1.getBlob(1);
            tile.Downloaded = (rs1.getInt(3) > 0);
            tile.RouteGuid = rs1.getString(2);
            tile.TileUrl = rs1.getString(4);
            tile.Priority = 0;

            rs1.close();
        } else {
            rs1.close();

            sql = String.format("SELECT key FROM %s WHERE key = %s LIMIT 1", TABLE_CROSSCHECK, key);
            SQLighterRs rs2 = getRecordSet(sql);

            if (rs2.hasNext()) {
                tile.Priority = -1;
            } else {
                tile.Priority = -2;
            }

            rs2.close();
        }

        return tile;
    }

    public ArrayList<_Route> getListCrossCheckedRoutes(int key) {
        ArrayList<_Route> routes = new ArrayList<>();

        String sql = String.format("SELECT waterwayid FROM %s WHERE key = %s", TABLE_CROSSCHECK, key);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            int waterwayId = rs.getInt(0);
            _Route route = getRoute(null, waterwayId);
            routes.add(route);
        }

        rs.close();

        return routes;
    }

    public HashMap<String, _Association> getPois(String where) {
        HashMap<String, _Association> retval =  new HashMap<String, _Association>();

        String sql = String.format("SELECT category, name, description, orderidx, action FROM %s %s", TABLE_POIS, (where.isEmpty() ? "" : " WHERE " + where));
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Association association = new _Association();
            association.Name = rs.getString(1);
            retval.put(association.Name, association);
        }

        rs.close();

        return retval;
    }

    public ArrayList<_Country> getCountries() {
        ArrayList<_Country> countries = new ArrayList<>();

        String sql = "SELECT countrycode, country, prefix FROM countries ORDER BY country;";
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Country country = new _Country();

            country.CountryCode = rs.getString(0);
            country.CountryName = rs.getString(1);
            country.Prefix = rs.getString(2);

            countries.add(country);
        }

        rs.close();

        return countries;
    }

    public ArrayList<_Poi> getPois(String area, String category) {
        ArrayList<_Poi> pois = new ArrayList<>();

        String sql = String.format("SELECT category, name, description, orderidx, action, filter FROM %s WHERE area = '%s' AND category = '%s' ORDER BY orderidx, name", TABLE_POIS, area, category);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _Poi poi = new _Poi();

            poi.Area = rs.getString(0);
            poi.Name = rs.getString(1);
            poi.Description = rs.getString(2);
            poi.OrderIdx = rs.getInt(3);
            poi.Action = rs.getString(4);
            poi.Filter = (rs.getInt(5) != 0);

            pois.add(poi);
        }

        rs.close();

        return pois;
    }

    public ArrayList<_PoiLocation> getPoiImageMessage(ArrayList<Integer> ids) {
        ArrayList<_PoiLocation> poilocations = new ArrayList<>();

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        String where = XPLIBCM.concatenateInteger(",", ids);

        String sql = String.format("SELECT b1.binary AS image, p1.message, p2.description AS title, shared, reviewedfeedback, entityguid, CASE WHEN p1.message IS NULL OR p1.message = '' THEN 0 ELSE 1 END AS ismessage, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, p1.name, p1.id, p1.area, p2.category, p1.longitude, p1.latitude FROM %s p1 LEFT OUTER JOIN pois p2 ON p2.name = p1.name LEFT OUTER JOIN %s b1 ON b1.id = p1.id AND b1.type = 0 WHERE p1.id IN (%s)", TABLE_POILOCATIONS, TABLE_BINARIES, where);
        SQLighterRs rs = getRecordSet(sql);

        while (rs.hasNext()) {
            _PoiLocation poi = new _PoiLocation();
            poi.Image = rs.getBlob(0);
            poi.Message = rs.getString(1);
            poi.Title = rs.getString(2);
            poi.Shared = rs.getInt(3);
            poi.ReviewedFeedback = _PoiLocation.enmReviewedFeedback.values()[rs.getInt(4)];
            poi.EntityGuid = rs.getString(5);
            poi.IsMessage = (rs.getInt(6) != 0);
            poi.IsImage = (rs.getInt(7) != 0);
            poi.Name = rs.getString(8);
            poi.Id = rs.getInt(9);
            poi.Area = rs.getString(10);
            poi.Longitude = rs.getDouble(12);
            poi.Latitude = rs.getDouble(13);
            poi.Category = (rs.isNull(11) ? "" : rs.getString(11));

            poilocations.add(poi);
        }

        rs.close();

        return poilocations;
    }

    public int getMinPoiLocationZoomLevel(int id) {
        int retval = 0;

        String sql = String.format("SELECT p2.level07, p2.level08, p2.level09, p2.level10, p2.level11, p2.level12, p2.level13, p2.level14, p2.level15, p2.level16, p2.level17 FROM %s p1 INNER JOIN %s p2 ON p1.name = p2.name WHERE p1.id = %s", TABLE_POILOCATIONS, TABLE_POIS, id);
        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            boolean level07 = (rs.getInt(0) != 0);
            boolean level08 = (rs.getInt(1) != 0);
            boolean level09 = (rs.getInt(2) != 0);
            boolean level10 = (rs.getInt(3) != 0);
            boolean level11 = (rs.getInt(4) != 0);
            boolean level12 = (rs.getInt(5) != 0);
            boolean level13 = (rs.getInt(6) != 0);
            boolean level14 = (rs.getInt(7) != 0);
            boolean level15 = (rs.getInt(8) != 0);
            boolean level16 = (rs.getInt(9) != 0);
            boolean level17 = (rs.getInt(10) != 0);

            if (level07) {
                retval = 7;
            } else if (level08) {
                retval = 8;
            } else if (level09) {
                retval = 9;
            } else if (level10) {
                retval = 10;
            } else if (level11) {
                retval = 11;
            } else if (level12) {
                retval = 12;
            } else if (level13) {
                retval = 13;
            } else if (level14) {
                retval = 14;
            } else if (level15) {
                retval = 15;
            } else if (level16) {
                retval = 16;
            } else if (level17) {
                retval = 17;
            }
        }

        rs.close();

        return retval;
    }

    public boolean writeEntityCheckSum(String entityGuid, long checkSum, long lastCheckSum) {
        String sql = String.format("UPDATE %s SET checksum = %s, lastchecksum = %s WHERE entityguid = '%s'", TABLE_ENTITIES, checkSum, lastCheckSum, entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writePoiLocationCheckSum(int id, String entityGuid, long checkSum, long lastCheckSum) {
        String sql = String.format("UPDATE %s SET checksum = %s, lastchecksum = %s WHERE id = %s AND entityguid = '%s'", TABLE_POILOCATIONS, checkSum, lastCheckSum, id, entityGuid);
        return write(sql);
    }

    public boolean writeCurrentLocalDate(long now) {
        String sql = String.format("UPDATE %s SET currentlocaldate = %s", TABLE_MYSETTINGS, now);

        boolean retval = write(sql);
        if (retval) getMySettings().CurrentLocalDate = now;

        return retval;
    }

    public boolean writePrivatePublished(int id) {
        String sql = String.format("UPDATE %s SET shared = 0 WHERE id = %s", TABLE_POILOCATIONS, id);
        return write(sql);
    }

    public boolean writeTotalTiles(String entityGuid, int totalTiles) {
        String sql = String.format("UPDATE %s SET totaltiles = %s WHERE waterwayguid = '%s'", TABLE_ROUTES, totalTiles, entityGuid);
        return write(sql);
    }

    public boolean writeResetAllTiles() {
        String sql = String.format("UPDATE %s SET downloaded = 0 WHERE downloaded = -1", TABLE_TILES);
        return write(sql);
    }

    public boolean writeChangedEntity(String entityGuid, boolean changed) {
        String sql = String.format("UPDATE %s SET changed = %s WHERE entityguid = '%s'", TABLE_ENTITIES, gb(changed), entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writeDeleteTile(String routeGuid, int key) {
        String sql = String.format("DELETE FROM %s WHERE key = %s AND waterwayguid = '%s'", TABLE_TILES, key, routeGuid);
        return write(sql);
    }

    public boolean resetEntities() {
        String myEntityGuid = getMyEntityGuid();

        String sql = String.format("DELETE FROM %s WHERE entityguid <> '%s'", TABLE_ENTITIES, myEntityGuid);
        boolean retval = write(sql);
        if (retval && myEntityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();

        if (retval) {
            sql = String.format("UPDATE %s SET lastserverdate = 0, currentserverdate = 0", TABLE_MYSETTINGS);
            retval = write(sql);
        }

        refreshMySettings();

        return retval;
    }

    public boolean writeTogglePaused(String routeGuid) {
        String sql = String.format("UPDATE %s SET paused = CASE paused WHEN 1 THEN 0 ELSE 1 END WHERE waterwayguid = '%s'", TABLE_ROUTES, routeGuid);
        return write(sql);
    }

    public boolean writeRouteLastServerDate(String routeGuid, long currentServerDate) {
        String sql = String.format("UPDATE %s SET lastserverdate = %s WHERE waterwayguid = '%s'", TABLE_ROUTES, currentServerDate, routeGuid);
        return writeWithPrefix("SQL SC", sql);
    }

    public boolean writeEntityLastServerDate(long currentServerDate) {
        String sql = String.format("UPDATE %s SET lastserverdate = %s", TABLE_MYSETTINGS, currentServerDate);

        boolean retval = writeWithPrefix("SQL SC", sql);
        if (retval) getMySettings().CurrentServerDate = currentServerDate;

        return retval;
    }

    public String writeSessionPassword(String sessionPassword) {
        String sql = String.format("UPDATE %s SET password = %s", TABLE_MYSETTINGS, gs(sessionPassword));

        boolean retval = write(sql);
        String newPassword = null;

        if (retval) {
            if (retval) getMySettings().SessionPassword = sessionPassword;
            newPassword = getSessionPassword();
        }

        return newPassword;
    }

    public boolean writeMyEntitySearch(boolean favourite, boolean shopping, boolean socialMedia) {
        String sql = String.format("UPDATE %s SET favouritesearch = %s, shoppingsearch = %s, socialmediasearch = %s", TABLE_MYSETTINGS, gb(favourite), gb(shopping), gb(socialMedia));
        return write(sql);
    }

    public boolean writeImported() {
        String sql = String.format("UPDATE %s SET imported = 1", TABLE_MYSETTINGS);

        boolean retval = write(sql);

        if (retval) {
            getMySettings().Imported = true;
            retval = getImported();
        }

        return retval;
    }

    public boolean writeEntityPublish(String entityGuid) {
        String sql = String.format("UPDATE %s SET publish = 0 WHERE entityguid = '%s'", TABLE_ENTITIES, entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writeFilter(int value) {
        String sql = String.format("UPDATE %s SET filter = %s", TABLE_MYSETTINGS, value);

        boolean retval = write(sql);
        if (retval) getMySettings().Filter = _MySettings.enmFilter.values()[value];

        return retval;
    }

    public boolean writePacketCompleted(int id) {
        String sql = String.format("UPDATE %s SET completed = 1 WHERE id = %s", TABLE_INCOMING, id);
        return write(sql);
    }

    public boolean writeMyEntityIsActive(String entityGuid, boolean isActive) {
        String sql = String.format("UPDATE %s SET isactive = 0 WHERE entityguid = '%s'", TABLE_ENTITIES, gb(isActive), entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writePoiLocationPublished(int id, int published) {
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.getDate(XPLIBCM.now());

        String sql = String.format("UPDATE %s SET shared = %s, updated = %s, status = %s, reviewedfeedback = %s WHERE id = %s", TABLE_POILOCATIONS, published, now, _PoiLocation.enmStatus.Updated.ordinal(), _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), id);
        boolean retval = write(sql);

        if (retval) writeCurrentLocalDate(now);

        return retval;
    }

    public boolean writePoiLocationText(int id, String text) {
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.getDate(XPLIBCM.now());

        String sql = String.format("UPDATE %s SET message = %s, updated = %s, status = %s, reviewedfeedback = %s WHERE id = %s", TABLE_POILOCATIONS, gs(text), now, _PoiLocation.enmStatus.Updated.ordinal(), _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), id);
        boolean retval = write(sql);

        if (retval) writeCurrentLocalDate(now);

        return retval;
    }

    public boolean writeAvatarMarker(String entityGuid, byte[] avatarMarker) {
        String sql = String.format("UPDATE %s SET avatarmarker = %s, changed = true WHERE entityguid = '%s'", TABLE_ENTITIES, gh(avatarMarker), entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writeMySettingsRestore(long lastLocalDate, long currentLocalDate, int batchId) {
        String sql = String.format("UPDATE %s SET lastlocaldate = %s, currentlocaldate = %s, batchid = %s", TABLE_MYSETTINGS, lastLocalDate, currentLocalDate, batchId);

        boolean retval = write(sql);
        if (retval) {
            getMySettings().LastLocalDate = lastLocalDate;
            getMySettings().CurrentLocalDate = currentLocalDate;
        }

        return retval;
    }

    public int getMyBatchId(boolean increment) {
        String sql = String.format("SELECT batchid FROM %s", TABLE_MYSETTINGS);
        int batchid = getSingleInt(sql);

        if (batchid >= 0) {
            batchid += 1;

            sql = String.format("UPDATE %s SET batchid = %s", TABLE_MYSETTINGS, batchid);
            if (!write(sql)) batchid = 0;

            refreshMySettings();
        }

        return batchid;
    }

    public long[] getMySettingsBackup() {
        long[] retval = new long[] { 0, 0, 0 };

        String sql = String.format("SELECT lastlocaldate, currentlocaldate, batchid FROM %s", TABLE_MYSETTINGS);
        SQLighterRs rs = getRecordSet(sql);

        if (rs.hasNext()) {
            retval[0] = rs.getLong(0);
            retval[1] = rs.getLong(1);
            retval[2] = rs.getInt(2);
        }

        rs.close();

        return retval;
    }

    public boolean insertMySettings() {
        _MySettings mySettings = new _MySettings();
        long lastLocalDate = new Date().getTime();

        String sql = String.format("INSERT INTO %s (stoppedtrigger, isdebug, filterpoi, favouritesearch, shoppingsearch, socialmediasearch, lastzoomlevel, centrelatitude, centrelongitude, centremyboat, entityguid, updategps, sendposition, filter, lastlocaldate) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, '%s', %s, %s, %s, %s)", TABLE_MYSETTINGS, mySettings.StoppedTrigger, gb(mySettings.IsDebug), gb(mySettings.FilterPoi), gb(mySettings.Favourite), gb(mySettings.Shopping), gb(mySettings.SocialMedia), mySettings.LastZoomLevel, mySettings.CentreLatitude, mySettings.CentreLongitude, mySettings.CentreMyEntity.ordinal(), mySettings.EntityGuid, mySettings.UpdateGps, gb(mySettings.SendPosition), mySettings.Filter.ordinal(), lastLocalDate);
        boolean retval = write(sql);

        refreshMySettings();

        return retval;
    }

    public boolean writeCentreLocation(Double longitude, Double latitude) {
        String sql = String.format("UPDATE %s SET centrelongitude = %s, centrelatitude = %s", TABLE_MYSETTINGS, longitude, latitude);

        boolean retval = write(sql);
        if (retval) {
            getMySettings().CentreLongitude = longitude;
            getMySettings().CentreLatitude = latitude;
        }

        return retval;
    }

    public boolean writePremium(long date) {
        String sql = String.format("UPDATE %s SET premiumcontent = %s, filter = %s, filterpoi = 0, sendposition = 0", TABLE_MYSETTINGS, date, _MySettings.enmFilter.none.ordinal());
        boolean retval = write(sql);

        if (retval) {
            sql = String.format("UPDATE %s SET filter = 1", TABLE_POIS);
            retval = write(sql);
        }

        refreshMySettings();

        return retval;
    }

    public boolean writeMarkerSymbol(String name, boolean filter) {
        String sql = String.format("UPDATE %s SET filter = %s WHERE name = '%s'", TABLE_POIS, gb(filter), name);
        return write(sql);
    }

    public boolean writeGpsTiming(int updateGpsTiming) {
        String sql = String.format("UPDATE %s SET updategps = %s", TABLE_MYSETTINGS, updateGpsTiming);

        boolean retval = write(sql);
        if (retval) getMySettings().UpdateGps = updateGpsTiming;

        return retval;
    }

    public boolean writeSecurityFeatures(boolean isSecurityPremium, boolean isPublisher, boolean isReviewer, boolean isAdministrator,
                                         double publisherLongitude, double publisherLatitude, int publisherRadius,
                                         double reviewerLongitude, double reviewerLatitude, int reviewerRadius) {

        XP_Library_SC XPLIBSC = new XP_Library_SC();
        String securityFeatures = XPLIBSC.getSecurityFeatures(isSecurityPremium, isPublisher, isReviewer, isAdministrator, publisherLongitude, publisherLatitude, publisherRadius, reviewerLongitude, reviewerLatitude, reviewerRadius);

        String sql = String.format("UPDATE %s SET securityfeatures = '%s'", TABLE_MYSETTINGS, securityFeatures);
        refreshMySettings();

        return write(sql);
    }

    public _MySettings getMySettings() {
//        if (Bootstrap.getInstance().getMySettings() == null) {
//            Bootstrap.getInstance().setMySettings(createMySettings());
//        }
        return Bootstrap.getInstance().getMySettings();
    }

    private void refreshMySettings() {
        Bootstrap.getInstance().setMySettings(null);
    }

    public _Entity getMyEntitySettings() {
//        if (Bootstrap.getInstance().getMyEntitySettings() != null) {
//            return Bootstrap.getInstance().getMyEntitySettings();
//        } else {
//            Bootstrap.getInstance().setMyEntitySettings(createMyEntitySettings());
//        }

        return Bootstrap.getInstance().getMyEntitySettings();
    }

    public void refreshMyEntitySettings() {
        Bootstrap.getInstance().setMyEntitySettings(null);
    }

    public synchronized _Entity createMyEntitySettings() {
        String myEntityGuid = getMyEntityGuid();

        _Entity myEntity = getEntity(myEntityGuid);

        if (myEntity == null) {
            myEntity = new _Entity();
            myEntity.EntityGuid = myEntityGuid;

            if (writeEntity(null, myEntity)) {
                myEntity = getEntity(myEntityGuid);
            }
        }

        return myEntity;
    }

    public synchronized _MySettings createMySettings() {
        _MySettings mySettings =  new _MySettings();

        String sql = String.format("SELECT lastzoomlevel, centrelongitude, centrelatitude, entityguid, isdebug, centremyboat, contactserver, updategps, accuracy, waterwaydate, searchdate, boatdate, connectdate, symboldate, itemdate, offset, paused, stoppedtrigger, lastserverdate, currentserverdate, sendposition, filter, filterpoi, favouritesearch, shoppingsearch, socialmediasearch, premiumcontent, entitymoved, imported, initq, password, lastlocaldate, currentlocaldate, securityfeatures FROM %s", TABLE_MYSETTINGS);
        SQLighterRs rs = getRecordSet(sql);

        if (rs != null && rs.hasNext()) {
            mySettings.LastZoomLevel = rs.getInt(0);
            mySettings.CentreLongitude = rs.getDouble(1);
            mySettings.CentreLatitude = rs.getDouble(2);
            mySettings.EntityGuid = rs.getString(3);
            mySettings.IsDebug = (rs.isNull(4) ? false : rs.getInt(4) != 0);
            mySettings.CentreMyEntity = _MySettings.enmCentreMyEntity.values()[rs.getInt(5)];
            mySettings.UpdateGps = rs.getInt(7);
            mySettings.Accuracy = (rs.getInt(8) != null ? rs.getInt(8) : 0);
            mySettings.StoppedTrigger = (rs.getInt(17) != null ? rs.getLong(17) : 0);
            mySettings.LastServerDate = rs.getLong(18);
            mySettings.CurrentServerDate = rs.getLong(19);
            mySettings.SendPosition = (rs.getInt(20) != 0);
            mySettings.Filter =  _MySettings.enmFilter.values()[rs.getInt(21)];
            mySettings.FilterPoi = (rs.getInt(22) != 0);
            mySettings.Favourite = (rs.getInt(23) != 0);
            mySettings.Shopping = (rs.getInt(24) != 0);
            mySettings.SocialMedia = (rs.getInt(25) != 0);
            mySettings.PremiumContent = rs.getLong(26);
            mySettings.Paused = (rs.isNull(16) ? false : (rs.getInt(16) != 0));
            mySettings.Imported = (rs.isNull(28) ? false : (rs.getInt(28) != 0));
            mySettings.InitQ = rs.getString(29);
            mySettings.SessionPassword = rs.getString(30);
            mySettings.LastLocalDate = rs.getLong(31);
            mySettings.CurrentLocalDate = rs.getLong(32);
            mySettings.EncryptedEntityGuid = getEncryptedEntityGuid(mySettings.EntityGuid);

            XP_Library_CM XPLIBCM = new XP_Library_CM();
            mySettings.IsPremium = (mySettings.PremiumContent > XPLIBCM.nowAsLong());
            mySettings.EntityMoved = _MySettings.enmEntityMoved.values()[rs.getInt(27)];

            XP_Library_SC XPLIBSC = new XP_Library_SC();

            String securityFeatures = XPLIBSC.Decrypt(rs.getString(33));
            String[] split = securityFeatures.split("\\" + splitter);

            if (split.length > 1) { // && (Bootstrap.getInstance().getMySettings().SecurityFeatures == null || Bootstrap.getInstance().getMySettings().SecurityFeatures != securityFeatures)) {
                mySettings.SecurityFeatures = securityFeatures;

                mySettings.IsPublisher = Boolean.valueOf(split[enmSecurityFeatures.isPublisher.ordinal()]);
                mySettings.IsReviewer = Boolean.valueOf(split[enmSecurityFeatures.isReviewer.ordinal()]);
                mySettings.IsAdministrator = Boolean.valueOf(split[enmSecurityFeatures.isAdministrator.ordinal()]);
                mySettings.PublisherLongitude = Double.valueOf("0" + split[enmSecurityFeatures.PublisherLongitude.ordinal()]);
                mySettings.PublisherLatitude = Double.valueOf("0" + split[enmSecurityFeatures.PublisherLatitude.ordinal()]);
                mySettings.PublisherRadius = Integer.valueOf("0" + split[enmSecurityFeatures.PublisherRadius.ordinal()]);
                mySettings.ReviewerLongitude = Double.valueOf("0" + split[enmSecurityFeatures.ReviewerLongitude.ordinal()]);
                mySettings.ReviewerLatitude = Double.valueOf("0" + split[enmSecurityFeatures.ReviewerLatitude.ordinal()]);
                mySettings.ReviewerRadius = Integer.valueOf("0" + split[enmSecurityFeatures.ReviewerRadius.ordinal()]);
                mySettings.IsSecurityPremium = Boolean.valueOf(split[enmSecurityFeatures.isSecurityPremium.ordinal()]);
            }

            rs.close();
        } else {
            if (rs != null) rs.close();

            if (insertMySettings()) {
                mySettings = createMySettings();
            }
        }

        return mySettings;
    }

    private String getEncryptedEntityGuid(String entityGuid) {
        XP_Library_SC LIBSC = new XP_Library_SC();
        String temp = LIBSC.Encrypt(entityGuid);
        byte[] encodedBytes = Base64.encode(temp.getBytes(), Base64.DEFAULT + Base64.NO_WRAP);
        return new String(encodedBytes);
    }

    public boolean writeFilterPoi(Boolean filterPoi) {
        String sql = String.format("UPDATE %s SET filterpoi = %s", TABLE_MYSETTINGS, gb(filterPoi));

        boolean retval = write(sql);
        if (retval) getMySettings().FilterPoi = filterPoi;

        return retval;
    }

    public boolean writePaused(Boolean paused) {
        String sql = String.format("UPDATE %s SET paused = %s", TABLE_MYSETTINGS, gb(paused));

        boolean retval = write(sql);

        if (retval) {
            getMySettings().Paused = paused;

            if (!paused) {
                sql = String.format("UPDATE %s SET paused = %s WHERE purchased = 1", TABLE_ROUTES, gb(paused));
                retval = write(sql);
            }
        }

        return retval;
    }

    public boolean writeZoomLevel(int zoomLevel) {
        String sql = String.format("UPDATE %s SET lastzoomlevel = %s", TABLE_MYSETTINGS, zoomLevel);

        boolean retval = write(sql);
        if (retval) getMySettings().LastZoomLevel = zoomLevel;

        return retval;
    }

    public boolean writeCentreMyEntity(String entityGuid, _MySettings.enmCentreMyEntity centreMyEntity) {
        String sql = String.format("UPDATE %s SET centremyboat = %s", TABLE_MYSETTINGS, centreMyEntity.ordinal());
        boolean retval = write(sql);

        if (centreMyEntity == _MySettings.enmCentreMyEntity.inactive && retval) {
            sql = String.format("UPDATE %s SET longitude = %s, latitude = %s, direction = %s, zeroanglefixed = %s, status = %s WHERE entityguid = '%s'", TABLE_ENTITIES, 0, 0, 0, 0, _Entity.enmStatus.NotMoving.ordinal(), entityGuid);
            retval = write(sql);
            if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        }

        if (retval) getMySettings().CentreMyEntity = centreMyEntity;

        return retval;
    }

    public boolean writeVacuum(boolean on) {
        String sql = String.format("UPDATE %s SET vacuum = %s", TABLE_MYSETTINGS, gb(on));
        return write(sql);
    }

    public boolean cleanDatabase() {
        String sql = String.format("UPDATE %s SET vacuum = 0", TABLE_MYSETTINGS);
        boolean retval = write(sql);

        if (retval) {
            sql = String.format("vacuum");
            retval = write(sql);
        }

        refreshMySettings();

        return retval;
    }

    public boolean writeEntityGuid(String currentEntityGuid, String newEntityGuid) {
        String sql = String.format("UPDATE %s SET entityguid = '%s' WHERE entityguid = '%s'", TABLE_ENTITIES, newEntityGuid, currentEntityGuid);
        boolean retval = write(sql);
        if (retval && currentEntityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();

        if (retval) {
            sql = String.format("UPDATE %s SET entityguid = '%s' WHERE entityguid = '%s'", TABLE_POILOCATIONS, newEntityGuid, currentEntityGuid);
            retval = write(sql);

            if (retval) {
                sql = String.format("UPDATE %s SET entityguid = '%s'", TABLE_MYSETTINGS, newEntityGuid);
                retval = write(sql);
                refreshMySettings();
            }
        }

        return retval;
    }

    public boolean writeEntityStatus(String entityGuid, _Entity.enmStatus status) {
        String sql = String.format("UPDATE %s SET changed = 1, status = %s WHERE entityguid = '%s'", TABLE_ENTITIES, status.ordinal(), entityGuid);
        boolean retval = write(sql);
        if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writeCategoryRoute(String routeGuid, long currentServerDate) {
        String sql = String.format("SELECT currentserverdate FROM %s WHERE waterwayguid = '%s' AND currentserverdate < %s", TABLE_ROUTES, routeGuid, currentServerDate);
        boolean retval = (getSingleInt(sql) != 0);

        if (retval) {
            sql = String.format("UPDATE %s SET currentserverdate = %s WHERE waterwayguid = '%s' AND currentserverdate < %s", TABLE_ROUTES, currentServerDate, routeGuid, currentServerDate);
            retval = write(sql);
        }

        return retval;
    }

    public boolean writeCategoryEntity(long currentServerDate) {
        String sql = String.format("SELECT currentserverdate FROM %s WHERE currentserverdate < %s", TABLE_ROUTES, currentServerDate);
        boolean retval = (getSingleInt(sql) != 0);

        if (retval) {
            sql = String.format("UPDATE %s SET currentserverdate = %s WHERE currentserverdate < %s", TABLE_MYSETTINGS, currentServerDate, currentServerDate);
            retval = write(sql);
        }

        refreshMySettings();

        return retval;
    }

    public boolean writeUpdateRoute(String routeGuid) {
        String sql = String.format("UPDATE %s SET empty = 0, paused = 0, tilesdownloaded = 0, percentage = 0, myversion = currentversion WHERE waterwayguid = '%s'", TABLE_ROUTES, routeGuid);
        boolean retval = write(sql);

        if (retval) {
            sql = String.format("UPDATE %s SET downloaded = 0, priority = 0 WHERE waterwayguid = '%s'", TABLE_TILES, routeGuid);
            retval = write(sql);
        }

        return retval;
    }

    public ArrayList<String> getRoutesWithPoisOnServer() {
        ArrayList<String> routes = new ArrayList<>();

        String sql = String.format("SELECT waterwayid, waterwayguid, lastserverdate, currentserverdate FROM %s WHERE lastserverdate < currentserverdate", TABLE_ROUTES);
        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                routes.add(String.format("%s;%s;%s", rs.getString(1), rs.getLong(2), rs.getLong(3)));
            }

            rs.close();
        }

        return routes;
    }

    public boolean initialiseLocalDate(long localDate) {
        String sql;

        sql = String.format("UPDATE %s SET entitymoved = %s, lastlocaldate = %s", TABLE_MYSETTINGS, localDate);

        boolean retval = write(sql);
        refreshMySettings();

        return retval;
    }

    public boolean writeUpdateLocalDate(long localDate) {
        String sql;

        if (localDate > 0) {
            sql = String.format("UPDATE %s SET entitymoved = %s, lastlocaldate = %s", TABLE_MYSETTINGS, _MySettings.enmEntityMoved.Sent.ordinal(), localDate);
        } else {
            sql = String.format("UPDATE %s SET entitymoved = %s", TABLE_MYSETTINGS, _MySettings.enmEntityMoved.Sent.ordinal());
        }

        boolean retval = write(sql);
        refreshMySettings();

        return retval;
    }

    public boolean writeSendPosition(boolean sendPosition) {
        String sql = String.format("UPDATE %s SET sendposition = %s, entitymoved = %s", TABLE_MYSETTINGS, gb(sendPosition), (sendPosition ? _MySettings.enmEntityMoved.PositionNotSent.ordinal() : _MySettings.enmEntityMoved.StoppedNotSent.ordinal()));

        boolean retval = write(sql);
        if (retval) {
            getMySettings().SendPosition = sendPosition;
            getMySettings().EntityMoved = (sendPosition ? _MySettings.enmEntityMoved.PositionNotSent : _MySettings.enmEntityMoved.StoppedNotSent);
        }

        return retval;
    }

    public boolean writeStopSendPosition(String entityGuid) {
        String sql = String.format("UPDATE %s SET sendposition = %s, entitymoved = %s", TABLE_MYSETTINGS, 0, _MySettings.enmEntityMoved.StoppedNotSent.ordinal());

        boolean retval = write(sql);

        if (retval) {
            getMySettings().SendPosition = false;
            getMySettings().EntityMoved = _MySettings.enmEntityMoved.StoppedNotSent;

            sql = String.format("UPDATE %s SET longitude = 0, latitude = 0 WHERE entityguid = '%s'", TABLE_ENTITIES, entityGuid);
            retval = write(sql);
            if (retval && entityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        }

        return retval;
    }

    public _Incoming insertPacket(String fileName, String packet) {
        _Incoming incoming = null;

        String sql = String.format("INSERT INTO %s (filename, packet) VALUES ('%s', '%s')", TABLE_INCOMING, fileName, packet);
        boolean retval = write(sql);

        if (retval) {
            int rowId = getLastRowId();

            incoming = new _Incoming();
            incoming.Id = rowId;
            incoming.FileName = fileName;
            incoming.Completed = false;
        }

        return incoming;
    }

    public _Incoming updatePacket(String fileName, String packet) {
        _Incoming incoming = null;

        String sql = String.format("SELECT id FROM %s WHERE filename = '%s'", TABLE_INCOMING, fileName);
        int rowId = getSingleInt(sql);

        if (rowId > 0) {
            sql = String.format("UPDATE %s SET packet = '%s' WHERE id = %s", TABLE_INCOMING, packet, rowId);
            boolean retval = write(sql);

            if (retval) {
                incoming = new _Incoming();
                incoming.Id = rowId;
                incoming.FileName = fileName;
                incoming.Completed = false;
            }
        }

        return incoming;
    }

    public class getIncomingInfo {
        public ArrayList<_Incoming> incomings;
        public boolean isMore = false;
    }

    public long[] getSendPosition(int timeBuffer) {
        long[] retval = new long[] { 0, 0, 0, 0 };

        _MySettings mySettings = getMySettings();
        retval[0] = (mySettings.SendPosition ? 1 : 0);
        retval[1] = mySettings.EntityMoved.ordinal();

        if (mySettings.LastLocalDate < (mySettings.CurrentLocalDate - timeBuffer)) {
            retval[2] = mySettings.LastLocalDate;
        } else {
            retval[2] = -1;
        }

        retval[3] = mySettings.PremiumContent;

        return retval;
    }

    public boolean clearMyPois() {
        String myEntityGuid = getMyEntityGuid();

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.nowAsLong();

        String sql = String.format("DELETE FROM %s WHERE entityguid = '%s' AND shared = 0", TABLE_POILOCATIONS, myEntityGuid);
        boolean retval = (getSingleInt(sql) != 0);

        if (retval) {
            sql = String.format("UPDATE %s SET deleted = 1, status = 2, updated = %s WHERE entityguid = '%s' AND shared = 1", TABLE_POILOCATIONS, now, myEntityGuid);
            retval = (getSingleInt(sql) != 0);
        }

        return retval;
    }

    public boolean checkTilesDownloaded(int routeId) {
        boolean retval = true;

        String sql = String.format("SELECT COUNT(*) FROM %s WHERE waterwayid = %s AND downloaded = 1", TABLE_TILES, routeId);
        int tilesDownloaded = getSingleInt(sql);

        if (tilesDownloaded > 0) {
            sql = String.format("UPDATE %s SET tilesdownloaded = %s, percentage = %s WHERE waterwayid = %s", TABLE_ROUTES, tilesDownloaded, (tilesDownloaded / 11000 * 100), routeId);
//            sql = String.format("UPDATE %s SET tilesdownloaded = %s, percentage = CAST(%s / CAST(totaltiles AS FLOAT) * 100 AS INT) WHERE waterwayguid = '%s'", TABLE_ROUTES, tilesDownloaded, tilesDownloaded, routeGuid);
            retval = write(sql);
        }

        return retval;
    }

    public String getRetrievedTileSQL(int key, int routeId, byte[] tile) {
        return String.format("UPDATE %s SET downloaded = 1, tile = %s, priority = 0 WHERE key = %s AND waterwayid = '%s'", TABLE_TILES, gh(tile), key, routeId);
    }

    public boolean writeRetrievedTile(int key, int routeId, byte[] tile) {
        String sql = getRetrievedTileSQL(key, routeId, tile);

//        checkTilesDownloaded(routeId);

        return write(sql);
    }

    public StringBuilder getIncomingPacket(int id) {
        StringBuilder retval = new StringBuilder();
        int maxSize = 2000000;

        String sql = String.format("SELECT length(packet) FROM incoming WHERE id = %s", id);
        int packetLength = getSingleInt(sql);

        if (packetLength > 0) {
            int position = 0;
            XP_Library_CM LIBCM = new XP_Library_CM();

            sql = String.format("SELECT SUBSTR(packet, %s, %s) FROM %s WHERE id = %s", position, maxSize, TABLE_INCOMING, id);
            String temp = getSingleString(sql);

            while (!LIBCM.isBlank(temp)){
                retval.append(temp);
                position += maxSize;
                sql = String.format("SELECT SUBSTR(packet, %s, %s) FROM %s WHERE id = %s", position, maxSize, TABLE_INCOMING, id);
                temp = getSingleString(sql);
            }
        }

        return retval;
    }

    public getIncomingInfo getIncoming() {
        getIncomingInfo info = new getIncomingInfo();

        info.incomings = new ArrayList<>();
        int batch = 10;

        String sql = String.format("SELECT id, filename, completed FROM %s WHERE completed = 0 ORDER BY id LIMIT %s", TABLE_INCOMING, (batch + 1));
        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while ((info.incomings.size() < batch) && rs.hasNext()) {
                _Incoming incoming = new _Incoming();
                incoming.Id = rs.getInt(0);
                incoming.FileName = rs.getString(1);
                incoming.Completed = (rs.getInt(2) != 0);
                info.incomings.add(incoming);
            }

            info.isMore = rs.hasNext();

            rs.close();
        }

        return info;
    }

//    public boolean getEntityExists(String entityGuid) {
//        String sql = String.format("SELECT 1 FROM %s WHERE entityguid = '%s'", TABLE_ENTITIES, entityGuid);
//        return (getSingleInt(sql) != 0);
//    }

    public ArrayList<_ShortLocation> getPoiLocations(double north, double west, double south, double east, int level, String poiWhereStatement, String myEntityGuid) {
        ArrayList<_ShortLocation> markers = new ArrayList<>();

        north += margin;
        south -= margin;
        west -= margin;
        east += margin;

        String sql = String.format("SELECT p1.id, p1.name, latitude, longitude, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, CASE WHEN message IS NULL OR message = '' THEN 0 ELSE 1 END AS ismessage, updated FROM %s p1 LEFT OUTER JOIN %s b1 ON b1.id = p1.id AND b1.type = 0 INNER JOIN %s p2 ON p2.name = p1.name WHERE (deleted = 0 OR deleted IS NULL) AND latitude > %s AND latitude < %s AND longitude > %s AND longitude < %s %s AND (entityguid = '%s' OR shared = 1) ORDER BY updated LIMIT 2000", TABLE_POILOCATIONS, TABLE_BINARIES, TABLE_POIS, south, north, west, east, poiWhereStatement, myEntityGuid);
        sql = sql.replace("{level}", String.format("%02d", level));

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                _ShortLocation poi = new _ShortLocation();

                poi.Id = String.format("%06d", rs.getInt(0));
                poi.Name = rs.getString(1);
                poi.Latitude = rs.getDouble(2);
                poi.Longitude = rs.getDouble(3);
                poi.IsContent = ((rs.getInt(4) != 0) || (rs.getInt(5) != 0));
                poi.Updated = rs.getLong(6);

                markers.add(poi);
            }

            rs.close();
        }

        return markers;
    }

    public String getFilterPoiWhereStatement(enmFilterPoi filterPoi, String names, String myEntityGuid) {
        StringBuilder poiWhereStatement = new StringBuilder();
        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (filterPoi == enmFilterPoi.JustItems) {
            ArrayList<String> poisUnfiltered = getPoisNameUnfiltered();

            if (poisUnfiltered.size() > 0) {
                ArrayList<String> poisFiltered = getPoisNameFiltered();

                for (String name : poisFiltered) {
                    if (poiWhereStatement.length() == 0) {
                        poiWhereStatement.append("AND p1.name IN ('" + name + "'");
                    } else {
                        poiWhereStatement.append(", '" + name + "'");
                    }
                }
            }

            if (poiWhereStatement.length() != 0) {
                poiWhereStatement.append(") AND p2.level{level} = 1");
            }
        } else if (filterPoi == enmFilterPoi.Created) {
            poiWhereStatement.append("AND p1.entityguid = '" + myEntityGuid + "'");
        } else if (filterPoi == enmFilterPoi.Tick) {
            for (String name : names.split(";")) {
                if (poiWhereStatement.length() == 0) {
                    poiWhereStatement.append("AND p1.name IN ('" + name + "'");
                } else {
                    poiWhereStatement.append(", '" + name + "'");
                }
            }

            if (poiWhereStatement.length() != 0) {
                poiWhereStatement.append(")");
            }
        } else if (filterPoi == enmFilterPoi.New) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.HOUR, -(24 * 7));

            long newPois = instance.getTime().getTime();
            poiWhereStatement.append("AND p1.updated > " + newPois);
        } else {
            poiWhereStatement.append("AND p2.level{level} = 1");
        }

        return poiWhereStatement.toString();
    }

    public int writePoiLocation(boolean isAdministrator, int id, String area, String name, double[] points, int scope, boolean forceCreate, String boatGuid, byte[] image, String action, String message) {
        int retval = -1;

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.nowAsLong();

        if (id >= 0) {
            String sql;

            if (forceCreate) {
                sql = String.format("UPDATE %s SET reviewedfeedback = %s, name = %s, action = %s, message = %s, status = %s, updated = %s, created = %s WHERE id = %s",
                        TABLE_POILOCATIONS, _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), gs(name), gs(action), gs(message),
                        _PoiLocation.enmStatus.Created.ordinal(), now, now, id);
            } else {
                sql = String.format("UPDATE %s SET reviewedfeedback = %s, name = %s, action = %s, message = %s, status = %s, updated = %s WHERE id = %s",
                        TABLE_POILOCATIONS, _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), gs(name), gs(action), gs(message),
                        _PoiLocation.enmStatus.Updated.ordinal(), now, id);
            }

            if (write(sql)) {
                retval = id;
                writeCurrentLocalDate(now);
            }
        } else {
            String routes = getRoutes(13, points[0], points[1]);

            //CHECK LONG DATE AS MAX
            String sql = String.format("INSERT INTO %s (reviewedfeedback, area, name, latitude, longitude, route, scope, entityguid, action, message, startdate, enddate, externalid, shared, updated, created) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    TABLE_POILOCATIONS, _PoiLocation.enmReviewedFeedback.NotReviewed.ordinal(), gs(area), gs(name), points[0], points[1],
                    gs(routes), scope, gs(boatGuid), gs(action), gs(message), 0, Long.MAX_VALUE, 0, gb(isAdministrator), now, now);

            if (write(sql)) {
                retval = getLastRowId();

                if (retval > 0) {
                    writeExternalId(retval);
                    writeCurrentLocalDate(now);
                }
            }
        }

        if (retval > 0) writeBinary(retval, XP_Library_DB.enmBinaryType.PoiLocation.ordinal(), image);

        return retval;
    }

    public boolean writeEntityFavourite(String entityGuid, boolean favourite) {
        boolean retval = false;

        String sql = String.format("SELECT 1 FROM %s WHERE entityguid = '%s'", TABLE_ENTITYMETA, entityGuid);
        boolean exist = (getSingleInt(sql) != 0);

        if (exist) {
            sql = String.format("UPDATE %s SET favourite = %s WHERE entityguid = '%s'", TABLE_ENTITYMETA, gb(favourite), entityGuid);
            retval = write(sql);
        } else {
            sql = String.format("INSERT INTO %s (favourite, entityguid) VALUES (%s, %s)", TABLE_ENTITYMETA, gb(favourite), gs(entityGuid));
            retval = write(sql);
        }

        return retval;
    }

    public ArrayList<_ShortLocation> getChangedEntities(boolean displayCurrentEntity, _MySettings.enmFilter filter, String myEntityGuid, double north, double west, double south, double east) {
        ArrayList<_ShortLocation> entities = new ArrayList<>();

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long oldestEntity = XPLIBCM.getOldestEntityAllowedToMove();

        north += margin;
        south -= margin;
        west -= margin;
        east += margin;

        String sql;

        if (filter == _MySettings.enmFilter.all) {
            sql = String.format("SELECT entityguid, longitude, latitude, iconname, lastmoved, direction FROM %s WHERE latitude > %s AND latitude < %s AND longitude > %s AND longitude < %s AND lastmoved > %s LIMIT 500", TABLE_ENTITIES, south, north, west, east, oldestEntity);
        } else {
            String filterSearch = "";

            if (filter == _MySettings.enmFilter.entitytrader) {
                filterSearch = "entitytype = 0";
            } else if (filter == favourite) {
                sql = String.format("SELECT entityguid FROM %s WHERE favourite = 1", TABLE_ENTITYMETA);
                ArrayList<String> favourites = getArrayString(sql);

                String favouriteSearch = XPLIBCM.concatenateString(",", favourites, true);

                filterSearch = "entityguid IN (" + favouriteSearch + ")";
            } else if (filter == _MySettings.enmFilter.socialmedia) {
                filterSearch = "(IFNULL(facebook, '') <> '' OR IFNULL(twitter, '') <> '' OR IFNULL(instagram, '') <> '' OR IFNULL(youtube, '') <> '' OR IFNULL(patreon, '') <> '')";
            } else if (filter == _MySettings.enmFilter.none) {
                filterSearch = "entitytype = 99";
            }

            String currentEntity = (displayCurrentEntity ? currentEntity = "entityguid = '" + myEntityGuid + "' OR " : "");

            sql = String.format("SELECT entityguid, longitude, latitude, iconname, lastmoved, direction FROM %s WHERE %s (%s AND latitude < %s AND latitude > %s AND longitude < %s AND longitude > %s AND lastmoved > %s) LIMIT 500", TABLE_ENTITIES, currentEntity, filterSearch, north, south, east, west, oldestEntity);
        }

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                _ShortLocation entity = new _ShortLocation();

                entity.Id = rs.getString(0);
                entity.IsContent = false;
                entity.Longitude = rs.getDouble(1);
                entity.Latitude = rs.getDouble(2);
                entity.Name = rs.getString(3);
                entity.Updated = rs.getLong(4);

                entities.add(entity);
            }

            rs.close();
        }

        return entities;
    }

    public boolean initialiseMapCache() {
        boolean retval = false;

        String sql = String.format("SELECT waterwayguid FROM %s WHERE waterwayguid <> '%s'", TABLE_ROUTES, getAllRouteGuid());
        ArrayList<String> routes = getArrayString(sql);

        for (String route : routes) {
            sql = String.format("UPDATE %s SET totaltiles = 0, tilesdownloaded = 0, percentage = 0, empty = 1, myversion = 0, lastserverdate = 0, currentserverdate = 0 WHERE waterwayguid = '%s'", TABLE_ROUTES, route);
            write(sql);

            sql = String.format("DELETE FROM %s WHERE waterwayguid = '%s'", TABLE_TILES, route);
            write(sql);
        }

        retval = writeVacuum(true);

        return retval;
    }

    public boolean writeEmptyRoute(String waterwayGuid, boolean delete) {
        String sql;
        String totalTiles = "";

        if (delete) {
            totalTiles = ", totaltiles = 0";
            sql = String.format("DELETE FROM %s WHERE waterwayguid = '%s'", TABLE_TILES, waterwayGuid);
        } else {
            sql = String.format("UPDATE %s SET tile = null, download = 0, priority = 0 WHERE waterwayguid = '%s'", TABLE_TILES, waterwayGuid);
        }

        write(sql);

        sql = String.format("UPDATE %s SET empty = 1, tilesdownloaded = 0, percentage = 0 %s WHERE waterwayguid = '%s'" , TABLE_ROUTES, totalTiles, waterwayGuid);
        write(sql);

        return writeVacuum(true);
    }

    public String getInClause() {
        String sql = String.format("SELECT waterwayid FROM %s w1 WHERE purchased = 1 AND paused = 0 AND totaltiles <> 0 AND totaltiles = (SELECT COUNT(*) FROM %s WHERE waterwayguid = w1.waterwayguid) AND tilesdownloaded < totaltiles LIMIT 1",TABLE_ROUTES, TABLE_TILES);
        String inClause = getSingleString(sql);
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        return (XPLIBCM.isBlank(inClause) ? "" : String.format("waterwayid = %s", inClause));
    }

    public ArrayList<_Tile> getWaitingTiles(String whereClause) {
        ArrayList<_Tile> tiles = new ArrayList<>();
        XP_Library_CM LIBCM = new XP_Library_CM();

        if (!LIBCM.isBlank(whereClause)) {
            String[] parts = whereClause.split("=");
            int routeId = Integer.parseInt(parts[1].trim());

            String sql = String.format("SELECT key, waterwayguid, tileurl, waterwayid FROM %s INDEXED BY speedUpGetWaitingTilesWaterwayId WHERE %s AND downloaded = 0 ORDER BY priority, created DESC LIMIT 100", TABLE_TILES, whereClause);
//            String sql = String.format("SELECT key, waterwayguid, tileurl, waterwayid FROM %s p1 INDEXED BY speedUpGetWaitingTilesWaterwayId WHERE %s AND downloaded = 0 ORDER BY priority, created DESC LIMIT 100", TABLE_TILES, whereClause);
            SQLighterRs rs = getRecordSet(sql);

            if (rs != null) {
                String keyClause = "";

                while (rs.hasNext()) {
                    _Tile tile = new _Tile();

                    tile.RouteId = routeId;
                    tile.Key = rs.getInt(0);
                    tile.RouteGuid = rs.getString(1);
                    tile.TileUrl = rs.getString(2);

                    tiles.add(tile);

                    if (keyClause.isEmpty()) {
                        keyClause += tile.Key.toString();
                    } else {
                        keyClause += "," + tile.Key.toString();
                    }
                }

                rs.close();

                if (!keyClause.isEmpty()) whereClause += String.format(" AND key in (%s)", keyClause);
            }

            sql = String.format("UPDATE %s SET downloaded = -1 WHERE %s", TABLE_TILES, whereClause);
            write(sql);
        }

        return tiles;
    }

    public long getMaxCurrentServerDate() {
        long maxCurrentServerDate;
        long routeCurrentServerDate;
        long entityCurrentServerDate;

        String sql = String.format("SELECT MAX(currentserverdate) FROM %s", TABLE_ROUTES);
        routeCurrentServerDate = getSingleLong(sql);

        _MySettings mySettings = getMySettings();

//        sql = String.format("SELECT currentserverdate FROM %s", TABLE_MYSETTINGS);
//        entityCurrentServerDate = getSingleLong(sql);

        entityCurrentServerDate = mySettings.CurrentServerDate;

        if (routeCurrentServerDate == 0 && entityCurrentServerDate == 0) maxCurrentServerDate = -1;
        else if (routeCurrentServerDate == 0) maxCurrentServerDate = entityCurrentServerDate;
        else if (entityCurrentServerDate == 0) maxCurrentServerDate = routeCurrentServerDate;
        else maxCurrentServerDate = (entityCurrentServerDate > routeCurrentServerDate ? entityCurrentServerDate : routeCurrentServerDate);

        return maxCurrentServerDate;
    }

    public boolean writeBuyRoute(String routeGuid) {
        writeEmptyRoute(routeGuid, false);

        String sql = String.format("UPDATE %s SET purchased = 1 WHERE waterwayguid = '%s'", TABLE_ROUTES, routeGuid);
        boolean retval = write(sql);

        if (retval && (routeGuid.equals(getAllRouteGuid()) || getHistoricRouteGuids().contains(routeGuid))) {
            sql = String.format("SELECT waterwayguid FROM %s WHERE waterwayguid <> '%s' AND purchased = 0", TABLE_ROUTES, getAllRouteGuid());
            ArrayList<String> routeGuids = getArrayString(sql);

            for (String route : routeGuids) {
                retval = writeEmptyRoute(route, false);
                if (!retval) break;

                sql = String.format("UPDATE %s SET purchased = 1 WHERE waterwayguid = '%s'", TABLE_ROUTES, route);
                retval = write(sql);
                if (!retval) break;
            }
        }

        return retval;
    }

    public boolean writeEntity(String prefix, String entityGuid, String entityName, String description, byte[] avatar, int icon, String iconName, byte[] avatarMarker, Boolean avatarChecked, int entityType, String phone, String trading, String facebook, String twitter, String instagram, String website, String email, String reference, String metaData1, String metaData2, String youtube, String patreon, boolean isActive, double longitude, double latitude, long updated, boolean zeroAngleFixed, _Entity.enmStatus status, int direction, double distance, int tracker1, long checkSum, long lastCheckSum, int strength, long lastMoved) {
        _MySettings mySettings = getMySettings();
        if (mySettings.SendPosition && !isActive) {
            writeSendPosition(false);
        }

        String sql = String.format("SELECT 1 FROM %s WHERE entityguid = '%s'", TABLE_ENTITIES, entityGuid);
        boolean exist = (getSingleInt(sql) != 0);

        if (exist) {
            sql = String.format("UPDATE %s SET entityname = %s, entitytype = %s, people = %s, phone = %s, tradingname = %s, facebook = %s, twitter = %s, instagram = %s, youtube = %s, patreon = %s, website = %s, email = %s, reference = %s, metadata1 = %s, metadata2 = %s, avatar = %s, icon = %s, iconname = %s, avatarmarker = %s, changed = %s, avatarchecked = %s, publish = %s, isactive = %s, longitude = %s, latitude = %s, updated = %s, zeroanglefixed = %s, status = %s, direction = %s, distance = %s, locks = %s, checksum = %s, lastchecksum = %s, strength = %s, lastmoved = %s, description = %s WHERE entityguid = %s",
                    TABLE_ENTITIES, gs(entityName), entityType, gs(description), gs(phone), gs(trading), gs(facebook), gs(twitter), gs(instagram), gs(youtube),
                    gs(patreon), gs(website), gs(email), gs(reference), metaData1, metaData2, gh(avatar), icon, gs(iconName), gh(avatarMarker), gb(true),
                    gb(avatarChecked), gb(true), gb(isActive), longitude, latitude, updated, gb(zeroAngleFixed), status.ordinal(), direction, distance,
                    tracker1, checkSum, lastCheckSum, strength, lastMoved, gs(description), gs(entityGuid));
        } else {
            sql = String.format("INSERT INTO %s (entityguid, entityname, entitytype, people, phone, tradingname, facebook, twitter, instagram, youtube, patreon, website, email, reference, metadata1, metadata2, avatar, icon, iconname, avatarmarker, changed, avatarchecked, publish, isactive, longitude, latitude, zeroanglefixed, locks, distance, status, direction, updated, checksum, lastchecksum, strength, lastmoved, description) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                    TABLE_ENTITIES, gs(entityGuid), gs(entityName), entityType, gs(description), gs(phone), gs(trading), gs(facebook), gs(twitter), gs(instagram), gs(youtube),
                    gs(patreon), gs(website), gs(email), gs(reference), metaData1, metaData2, gh(avatar), icon, gs(iconName), gh(avatarMarker), gb(true),
                    gb(avatarChecked), gb(true), gb(isActive), longitude, latitude, gb(zeroAngleFixed), tracker1, distance,
                    status.ordinal(), direction, updated, checkSum, lastCheckSum, strength, lastMoved, gs(description));
        }

        boolean retval;

        if (prefix != null) {
            retval = writeWithPrefix(prefix, sql);
        } else {
            retval = write(sql);
        }

        return retval;
    }

    public boolean writeEntity(String prefix, _Entity entity) {
        boolean retval = writeEntity(prefix, entity.EntityGuid, entity.EntityName, entity.Description, entity.Avatar, entity.Icon, entity.IconName, entity.AvatarMarker, entity.AvatarChecked, entity.EntityType, entity.Phone, entity.TradingName, entity.Facebook, entity.Twitter, entity.Instagram, entity.Website, entity.Email, entity.Reference, entity.MetaData1, entity.MetaData2, entity.YouTube, entity.Patreon, entity.IsActive, entity.Longitude, entity.Latitude, entity.Updated, entity.ZeroAngleFixed, entity.Status, entity.Direction, entity.Distance, entity.Tracker1, entity.CheckSum, entity.LastCheckSum, entity.Strength, entity.LastMoved);
        if (retval && entity.EntityGuid.equals(getMyEntityGuid())) refreshMyEntitySettings();
        return retval;
    }

    public boolean writeEntityPosition(Locality locality, String entityGuid, _Entity.enmStatus status) {
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        long now = XPLIBCM.nowAsLong();

        String sql = String.format("UPDATE %s SET longitude = %s, latitude = %s, direction = %s, changed = 1, zeroanglefixed = %s, status = %s, lastmoved = %s WHERE entityguid = '%s'", TABLE_ENTITIES, locality.getLongitude(), locality.getLatitude(), locality.getBearing(), (locality.getZeroAngleFixed() ? 1 : 0), status.ordinal(), now, entityGuid);
        boolean retval = write(sql);

        if (retval) {
            refreshMyEntitySettings();

            _MySettings mySettings = getMySettings();
            if (mySettings.EntityMoved == _MySettings.enmEntityMoved.Sent && mySettings.SendPosition) {
                sql = String.format("UPDATE %s SET accuracy = %s, stoppedtrigger = %s, entitymoved = %s", TABLE_MYSETTINGS, locality.getAccuracy(), (status == _Entity.enmStatus.Moving ? now : null), _MySettings.enmEntityMoved.PositionNotSent);
            } else {
                sql = String.format("UPDATE %s SET accuracy = %s, stoppedtrigger = %s", TABLE_MYSETTINGS, locality.getAccuracy(), (status == _Entity.enmStatus.Moving ? now : null));
            }

            retval = write(sql);

            refreshMySettings();
        }

        return retval;
    }

    public boolean writeRouteContainer(int routeId, String routeGuid, String tilesUrl, String[] tiles, boolean downloaded) {
        String sql = String.format("DELETE FROM %s WHERE waterwayguid = '%s'", TABLE_TILES, routeGuid);
        boolean retval = write(sql);

        if (tiles.length > 0) {
            StringBuilder values = new StringBuilder();

            for (int i = 0; i < tiles.length; i++) {
                String tile = tiles[i];

                String[] splitTile = tile.split(",");
                int key = Integer.parseInt(splitTile[0]);
                String url = String.format("%s%s.png", tilesUrl, splitTile[1]);

                if (values.length() > 0) values.append(",");
                values.append(String.format("(%s, %s, %s, 1, %s, %s)", key, gs(routeGuid), routeId, (downloaded ? 1 : 0), gs(url)));
            }

            sql = String.format("INSERT INTO %s (key, waterwayguid, waterwayid, type, downloaded, tileurl) VALUES %s", TABLE_TILES, values);
            retval = write(sql);

            values.setLength(0);

            if (retval) {
                sql = String.format("SELECT P1.key FROM %s P1 LEFT OUTER JOIN %s P2 ON P2.key = P1.key WHERE P2.key IS null AND p1.waterwayguid = %s", TABLE_TILES, TABLE_CROSSCHECK, gs(routeGuid));
                SQLighterRs rs = getRecordSet(sql);

                while (rs.hasNext()) {
                    int key = rs.getInt(0);

                    if (values.length() > 0) values.append(",");
                    values.append(String.format("(%s, %s)", key, routeId));
                }

                if (values.length() > 0) {
                    sql = String.format("INSERT INTO %s (key, waterwayid) VALUES %s", TABLE_CROSSCHECK, values);
                    retval = write(sql);
                }
            }
        }

        if (retval) writeTotalTiles(routeGuid, tiles.length);

        return retval;
    }

//    public boolean writeRouteContainer(int routeId, String routeGuid, String tilesUrl, String[] tiles, boolean downloaded) {
//        String sql = String.format("DELETE FROM %s WHERE waterwayguid = '%s'", TABLE_TILES, routeGuid);
//        boolean retval = write(sql);
//
//        writeTotalTiles(routeGuid, tiles.length);
//
////        write("BEGIN TRANSACTION");
//
//        for (String tile : tiles) {
//            String[] splitTile = tile.split(",");
//            int key = Integer.parseInt(splitTile[0]);
//
//            sql = String.format("INSERT INTO %s (key, waterwayguid, waterwayid, type, downloaded, tileurl) VALUES (%s, %s, %s, 1, %s, %s)", TABLE_TILES, key, gs(routeGuid), routeId, (downloaded ? 1 : 0), gs(String.format("%s%s.png", tilesUrl, splitTile[1])));
//            retval = write(sql);
//
//            if (!retval) break;
//
//            sql = String.format("SELECT 1 FROM %s WHERE key = %s LIMIT 1", TABLE_CROSSCHECK, key);
//            boolean exist = (getSingleInt(sql) != 0);
//
//            if (!exist) {
//                sql = String.format("INSERT INTO %s (key, waterwayid) VALUES (%s, %s)", TABLE_CROSSCHECK, key, routeId);
//                retval = write(sql);
//                if (!retval) break;
//            }
//        }
//
////        write("COMMIT");
//
//        return retval;
//    }

//    public void writeRoute(String waterwayGuid, String price, int version, String availability, String name, String description, String type, ArrayList<String> zipFiles, String fileName, int mbs) {
//        String sql = String.format("SELECT currentversion FROM %s WHERE waterwayguid = '%s'", TABLE_ROUTES, waterwayGuid);
//        int currentVersion = getSingleInt(sql);
//        if (currentVersion != 0 && currentVersion != version) {
//            sql = String.format("DELETE FROM %s WHERE waterwayguid = '%s'", TABLE_TILES, routeGuid);
//            boolean retval = write(sql);
//
//
//        }
//
//        Cursor cursor = db().rawQuery("SELECT currentversion FROM " + TABLE_ROUTES + " WHERE waterwayguid = '" + waterwayGuid + "'", null);
//
//        if (cursor.moveToFirst()) {
//            if (cursor.getInt(0) != version) {
//                db().execSQL("DELETE FROM tiles WHERE type = 0 AND waterwayguid = '" + waterwayGuid + "'");
//
//                for (String file : zipFiles) {
//                    ContentValues zipValues = new ContentValues();
//                    zipValues.put("tileurl", getSystem().BaseUrl + file);
//                    zipValues.put("waterwayguid", waterwayGuid);
//                    zipValues.put("downloaded", 0);
//                    zipValues.put("type", 0);
//                    zipValues.put("priority", 0);
//                    db().insert(TABLE_TILES, null, zipValues);
//                }
//
//                ContentValues values = new ContentValues();
//                XP_Library_CM LIBCM = new XP_Library_CM();
//
//                values.put("price", (LIBCM.isBlank(price) ? null : price));
//                values.put("currentversion", version);
//                values.put("availability", availability);
//                values.put("waterwayname", name);
//                values.put("type", type);
//                values.put("description", description);
//                values.put("filename", fileName);
//                values.put("mbs", mbs);
//
//                XP_Library_WS LIBWS = new XP_Library_WS();
//                byte[] map = LIBWS.downloadBinary(getSystem().BaseUrl + fileName + ".jpg");
//                if (map != null) values.put("map", map);
//
//                db().update(TABLE_ROUTES, values, "waterwayguid = '" + waterwayGuid + "'", null);
//            }
//        }
//
//        cursor.close();
//    }

    public ArrayList<_PoiLocation> getPoiLocations(boolean isAdministrator, boolean isReviewer, String entityGuid, int batchSize, long fromDate) {
        ArrayList<_PoiLocation> poiLocations = new ArrayList<>();

        String sql;

        if (isAdministrator || isReviewer) {
            sql = "SELECT p1.id, area, name, latitude, longitude, scope, entityguid, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, null, action, CASE WHEN message IS NULL OR message = '' THEN 0 ELSE 1 END AS ismessage, created, updated, deleted, twitter, facebook, startdate, enddate, title, locked, b1.binary AS image, message, externalid, feedback, reviewedfeedback, columns, shared, route, lastchecksum, checksum, metadata, status, category FROM " + TABLE_POILOCATIONS + " p1 LEFT OUTER JOIN " + TABLE_BINARIES + " b1 ON b1.id = p1.id AND b1.type = 0 WHERE updated > " + fromDate + " AND shared <> 0 ORDER BY updated LIMIT " + batchSize;
        } else {
            sql = "SELECT p1.id, area, name, latitude, longitude, scope, entityguid, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, null, action, CASE WHEN message IS NULL OR message = '' THEN 0 ELSE 1 END AS ismessage, created, updated, deleted, twitter, facebook, startdate, enddate, title, locked, b1.binary AS image, message, externalid, feedback, reviewedfeedback, columns, shared, route, lastchecksum, checksum, metadata, status, category FROM " + TABLE_POILOCATIONS + " p1 LEFT OUTER JOIN " + TABLE_BINARIES + " b1 ON b1.id = p1.id AND b1.type = 0 WHERE updated > " + fromDate + " AND entityguid = '" + entityGuid + "' AND shared <> 0 ORDER BY updated LIMIT " + batchSize;
        }

        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                _PoiLocation poi = new _PoiLocation();

                poi.Id = rs.getInt(0);
                poi.Area = rs.getString(1);
                poi.Name = rs.getString(2);
                poi.Latitude = rs.getDouble(3);
                poi.Longitude = rs.getDouble(4);
                poi.Scope = rs.getInt(5);
                poi.Message = (rs.getString(21));
                poi.EntityGuid = rs.getString(6);
                poi.Image = (rs.getBlob(20));
                poi.Action = rs.getString(9);
                poi.Updated = rs.getLong(12);
                poi.IsImage = (rs.getInt(7) != 0);
                poi.IsMessage = (rs.getInt(10) != 0);
                poi.IsTwitter = (rs.getInt(14) != 0);
                poi.IsFacebook = (rs.getInt(15) != 0);
                poi.StartDate = rs.getLong(16);
                poi.EndDate = rs.getLong(17);
                poi.Title = rs.getString(18);
                poi.IsLocked = (rs.getInt(19) != 0);
                poi.Created = rs.getLong(11);
                poi.Deleted = (rs.getInt(13) != 0);
                poi.ExternalId = rs.getInt(22);
                poi.Feedback = rs.getString(23);
                poi.ReviewedFeedback = _PoiLocation.enmReviewedFeedback.values()[rs.getInt(24)];
                poi.Columns = rs.getString(25);
                poi.Shared = rs.getInt(26);
                poi.Route = rs.getString(27);
                poi.LastCheckSum = rs.getLong(28);
                poi.CheckSum = rs.getLong(29);
                poi.MetaData = rs.getString(30);
                poi.Status = _PoiLocation.enmStatus.values()[rs.getInt(31)];

                poiLocations.add(poi);
            }

            rs.close();
        }

        return poiLocations;
    }

    public ArrayList<_PoiLocation> getPoiAllLocations(String entityGuid) {
        ArrayList<_PoiLocation> poiLocations = new ArrayList<>();

        String sql = "SELECT p1.id, area, name, latitude, longitude, scope, entityguid, CASE WHEN b1.binary IS NULL THEN 0 ELSE 1 END AS isimage, null, action, CASE WHEN message IS NULL OR message = '' THEN 0 ELSE 1 END AS ismessage, created, updated, deleted, twitter, facebook, startdate, enddate, title, locked, b1.binary AS image, message, externalid, feedback, reviewedfeedback, columns, shared, route, lastchecksum, checksum, metadata, status, category FROM " + TABLE_POILOCATIONS + " p1 LEFT OUTER JOIN " + TABLE_BINARIES + " b1 ON b1.id = p1.id AND b1.type = 0 WHERE entityguid = '"+ entityGuid + "' ORDER BY updated";
        SQLighterRs rs = getRecordSet(sql);

        if (rs != null) {
            while (rs.hasNext()) {
                _PoiLocation poi = new _PoiLocation();

                poi.Id = rs.getInt(0);
                poi.Area = rs.getString(1);
                poi.Name = rs.getString(2);
                poi.Latitude = rs.getDouble(3);
                poi.Longitude = rs.getDouble(4);
                poi.Scope = rs.getInt(5);
                poi.Message = (rs.getString(21));
                poi.EntityGuid = rs.getString(6);
                poi.Image = (rs.getBlob(20));
                poi.Action = rs.getString(9);
                poi.Updated = rs.getLong(12);
                poi.IsImage = (rs.getInt(7) != 0);
                poi.IsMessage = (rs.getInt(10) != 0);
                poi.IsTwitter = (rs.getInt(14) != 0);
                poi.IsFacebook = (rs.getInt(15) != 0);
                poi.StartDate = rs.getLong(16);
                poi.EndDate = rs.getLong(17);
                poi.Title = rs.getString(18);
                poi.IsLocked = (rs.getInt(19) != 0);
                poi.Created = rs.getLong(11);
                poi.Deleted = (rs.getInt(13) != 0);
                poi.ExternalId = rs.getInt(33);
                poi.Feedback = rs.getString(22);
                poi.ReviewedFeedback = _PoiLocation.enmReviewedFeedback.values()[rs.getInt(23)];
                poi.Columns = rs.getString(24);
                poi.Shared = rs.getInt(25);
                poi.Route = rs.getString(26);
                poi.LastCheckSum = rs.getLong(27);
                poi.CheckSum = rs.getLong(28);
                poi.MetaData = rs.getString(29);
                poi.Status = _PoiLocation.enmStatus.values()[rs.getInt(30)];

                poiLocations.add(poi);
            }

            rs.close();
        }

        return poiLocations;
    }

    public int writePoiLocation(String prefix, _PoiLocation poiLocation) {
        int poiId = 0;

        String updateSql = String.format("UPDATE %s SET area = %s, name = %s, entityguid = %s, latitude = %s, longitude = %s, scope = %s, action = %s, message = %s, created = %s, updated = %s, deleted = %s, startdate = %s, enddate = %s, lastchecksum = %s, checksum = %s, shared = %s, externalid = %s, route = %s, metadata = %s, status = %s, shared = %s, reviewedfeedback = %s",
                TABLE_POILOCATIONS,
                gs(poiLocation.Area), gs(poiLocation.Name), gs(poiLocation.EntityGuid), poiLocation.Latitude, poiLocation.Longitude, poiLocation.Scope, gs(poiLocation.Action),
                gs(poiLocation.Message), poiLocation.Created, poiLocation.Updated, gb(poiLocation.Deleted), poiLocation.StartDate, poiLocation.EndDate,
                poiLocation.LastCheckSum, poiLocation.CheckSum, poiLocation.Shared, poiLocation.ExternalId, gs(poiLocation.Route), gs(poiLocation.MetaData),
                poiLocation.Status.ordinal(), poiLocation.Shared, poiLocation.ReviewedFeedback.ordinal());

        String insertSql = String.format("INSERT INTO %s (area, name, entityguid, latitude, longitude, scope, action, message, created, updated, deleted, startdate, enddate, lastchecksum, checksum, shared, externalid, route, metadata, status, shared, reviewedfeedback) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
                TABLE_POILOCATIONS,
                gs(poiLocation.Area), gs(poiLocation.Name), gs(poiLocation.EntityGuid), poiLocation.Latitude, poiLocation.Longitude, poiLocation.Scope, gs(poiLocation.Action),
                gs(poiLocation.Message), poiLocation.Created, poiLocation.Updated, gb(poiLocation.Deleted), poiLocation.StartDate, poiLocation.EndDate,
                poiLocation.LastCheckSum, poiLocation.CheckSum, poiLocation.Shared, poiLocation.ExternalId, gs(poiLocation.Route), gs(poiLocation.MetaData),
                poiLocation.Status.ordinal(), poiLocation.Shared, poiLocation.ReviewedFeedback.ordinal());

        String sql = String.format("SELECT 1 FROM %s WHERE id = %s AND entityguid = '%s'", TABLE_POILOCATIONS, poiLocation.ExternalId, poiLocation.EntityGuid);
        boolean exist = (getSingleInt(sql) != 0);

        if (poiLocation.EntityGuid.equals(getMyEntityGuid()) && exist) {
            sql = String.format("%s WHERE id = %s AND entityguid = '%s'", updateSql, poiLocation.ExternalId, poiLocation.EntityGuid);

            if (writeWithPrefix(prefix, sql)) poiId = poiLocation.Id;
        } else if (!poiLocation.EntityGuid.equals(getMyEntityGuid())) {
            sql = String.format("SELECT id FROM %s WHERE externalid = %s AND entityguid = '%s'", TABLE_POILOCATIONS, poiLocation.ExternalId, poiLocation.EntityGuid);
            int tempId = getSingleInt(sql);

            if (tempId > 0) {
                sql = String.format("%s WHERE externalid = %s AND entityguid = '%s'", updateSql, poiLocation.ExternalId, poiLocation.EntityGuid);

                if (writeWithPrefix(prefix, sql)) poiId = tempId;
            } else {
                if (writeWithPrefix(prefix, insertSql)) poiId = getLastRowId();
            }
        } else {
            if (writeWithPrefix(prefix, insertSql)) poiId = getLastRowId();
        }

        writeBinary(poiId, XP_Library_DB.enmBinaryType.PoiLocation.ordinal(), poiLocation.Image);

        return poiId;
    }

    public boolean writeTileRequest(String tileUrl, Integer key, String entityGuid) {
        boolean retval = false;

        String sql = String.format("SELECT 1 FROM %s WHERE key = %s AND waterwayguid = '%s' AND downloaded = 0", TABLE_TILES, key, entityGuid);
        boolean exist = (getSingleInt(sql) != 0);

        if (exist) {
            XP_Library_CM XPLIBCM = new XP_Library_CM();
            sql = String.format("INSERT INTO %s (key, waterwayguid, tileurl, downloaded, type, created) VALUES (%s, %s, %s, 0, 1, %s)", TABLE_TILES, key, entityGuid, tileUrl, XPLIBCM.nowAsLong());
            retval = write(sql);

            sql = String.format("SELECT totaltiles, tilesdownloaded FROM %s WHERE waterwayguid = '%s'", TABLE_ROUTES, entityGuid);
            SQLighterRs rs = getRecordSet(sql);

            if (rs != null) {
                if (rs.hasNext()){
                    int tt = rs.getInt(0) + 1;
                    int td = rs.getInt(1);
                    int pc = (int)((double)td / (double)tt * 100.0);

                    sql = String.format("UPDATE %s SET totaltiles = %s, percentage = %s WHERE waterwayguid = '%s'", TABLE_ROUTES, tt, pc, entityGuid);
                    retval = write(sql);
                }

                rs.close();
            }
        }

        return retval;
    }

    public void writeRoute(String waterwayGuid, String price, Integer version, String availability, String name, String description, String type, ArrayList<String> zipFiles, String fileName, Integer mbs) {
        try {
            String sql = String.format("SELECT currentversion FROM %s WHERE waterwayguid = '%s'", TABLE_ROUTES, waterwayGuid);
            int currentVersion = getSingleInt(sql);

            if (currentVersion != version) {
                sql = String.format("DELETE FROM tiles WHERE type = 0 AND waterwayguid = '%s'", waterwayGuid);
                write(sql);

                XP_Library_WS XPLIBWS = new XP_Library_WS();
                byte[] map = XPLIBWS.downloadBinaryGET(Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + fileName + ".jpg");

                sql = String.format("UPDATE %s SET price = %s, currentversion = %s, availability = %s, waterwayname = %s, type = %s, description = %s, filename = %s, mbs = %s, map = %s WHERE waterwayguid = %s",
                        TABLE_ROUTES, price, version, gs(availability), gs(name), gs(type), gs(description), gs(fileName), mbs, gh(map), gs(waterwayGuid));
                write(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean downloadTiles(String waterwayGuid, boolean downloaded) {
        boolean retval = false;

        _Route waterway = Bootstrap.getInstance().getDatabase().getRoute(waterwayGuid);
        String tilesFilePath = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + waterway.FileName + ".zip";

        XP_Library_WS XPLIBWS = new XP_Library_WS();
        byte[] zipFile = XPLIBWS.downloadBinaryGET(tilesFilePath);

        XP_Library_ZP XPLIBZP = new XP_Library_ZP();
        byte[] contents = XPLIBZP.extractFile(zipFile, "Tiles.csv");
        String tiles = new String(contents);

        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (!XPLIBCM.isBlank(tiles)) {
            String[] tilesAry;

            if (tiles.indexOf("\r\n") > 0) {
                tilesAry = tiles.split("\r\n");
            } else {
                tilesAry = tiles.split("\n");
            }

            if (tilesAry.length > 0) retval = Bootstrap.getInstance().getDatabase().writeRouteContainer(waterway.RouteId, waterwayGuid, Bootstrap.getInstance().getDatabase().getSystem().TilesUrl, tilesAry, downloaded);
        }

        return retval;
    }

    public double roundDown(double value) {
        return (double) Math.round(value * 1000000) / 1000000;
    }

    private String gs(String value) {
        if (value != null) {
            value = value.replace("'", "''");
        }

        return (value == null ? null : String.format("'%s'", value));
    }

    private String gh(byte[] value) {
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        return (value == null ? null : String.format("X'%s'", XPLIBCM.bytesToHex(value)));
    }

    private int gb(boolean value) {
        return (value ? 1 : 0);
    }
}