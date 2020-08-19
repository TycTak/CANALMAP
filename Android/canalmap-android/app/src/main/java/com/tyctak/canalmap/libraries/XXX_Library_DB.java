package com.tyctak.canalmap.libraries;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.tyctak.canalmap.Global;
import java.io.File;

//public class XXX_Library_DB extends SQLiteOpenHelper {
//
//    private final String TAG = "Library_DB";
//    private final String TABLE_ENTITIES = "entities";
//
//    public Library_DB(Context ctx) {
//        super(ctx, String.valueOf((Global.getInstance().DatOnSdCard() ? ContextCompat.getExternalFilesDirs(ctx, "")[ContextCompat.getExternalFilesDirs(ctx, "").length - 1].getPath() + File.separator + XP_Library_DB.DATABASE_NAME : ctx.getDatabasePath(XP_Library_DB.DATABASE_NAME))), null, XP_Library_DB.DATABASE_VERSION);
//    }
//
//    private SQLiteDatabase db() {
//        return this.getWritableDatabase();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.d(TAG, "onCreate");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
//
//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
//
//    public Cursor getEntitiesCursor(boolean isFavourite, boolean isShopping, boolean isSocialMedia, String filter, boolean isActive) {
//        Cursor cursor;
//        String filterSql = "";
//        XP_Library_CM LIBCM = new XP_Library_CM();
//
//        if (!LIBCM.isBlank(filter)) {
//            filterSql = " AND (b1.people LIKE '%" + filter + "%' OR b1.entityname LIKE '%" + filter + "%')";
//        }
//
//        if (isFavourite) {
//            filterSql += " AND e1.favourite <> 0";
//        }
//
//        if (isShopping) {
//            filterSql += " AND b1.entitytype = 0";
//        }
//
//        if (isSocialMedia) {
//            filterSql += " AND (IFNULL(b1.facebook, '') <> '' OR IFNULL(b1.twitter, '') <> '' OR IFNULL(b1.instagram, '') <> '' OR IFNULL(b1.youtube, '') <> '' OR IFNULL(b1.patreon, '') <> '')";
//        }
//
////        if (isActive) {
////            filterSql += " OR b1.entityguid = '" + Global.getMyEntityGuid() + "'";
////        }
//
//        Long oldestEntityAllowedToDisplay = XP_Library_CM.getOldestEntityAllowedToDisplay();
//
//        //b1.entityguid <> '" + myEntityGuid + "'
//        cursor = db().rawQuery("SELECT 0 AS _id, b1.entityguid, b1.entityname, b1.people, b1.avatarmarker, b1.status, b1.longitude, b1.latitude, b1.locks, b1.distance, b1.facebook, b1.twitter, b1.instagram, e1.favourite, b1.entitytype, b1.youtube, b1.website, b1.lastmoved FROM " + TABLE_ENTITIES + " b1 LEFT OUTER JOIN entitymeta e1 ON e1.entityguid = b1.entityguid WHERE b1.entityname <> '' AND b1.entityname IS NOT NULL " + filterSql + " AND isactive = 1 ORDER BY b1.entityname", null);
//        //updated > " + oldestEntityAllowedToDisplay + " AND
//
//        return cursor;
//    }
//}