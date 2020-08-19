package com.tyctak.canalmap.map;

import android.util.Log;

import com.tyctak.canalmap.Global;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.modules.DatabaseFileArchive;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.Counters;

import java.io.File;
import java.io.InputStream;

public class LocalTileWriter implements IFilesystemCache {
    public static final String DATABASE_FILENAME = "cancam.sqlite";
    public static final String COLUMN_EXPIRES ="expires";
    /**
     * disables cache purge of expired tiled on start up
     * if this is set to false, the database will only purge tiles if manually called or if
     * the storage device runs out of space.
     *
     * expired tiles will continue to be overwritten as new versions are downloaded regardless
     *
     * @since 5.6
     */
//    public static boolean CLEANUP_ON_START=true;
    protected File db_file;
//    protected SQLiteDatabase db;
    protected long lastSizeCheck=0;

//    private final static String DATABASE_PATH = "/data/data/com.tyctak.cancam/databases/";

    /**
     * a questimated size (average-ish) size of a tile
     */
//    final int questimate=4000;
//    static boolean hasInited=true;

    public LocalTileWriter() {

//        Configuration.getInstance().getOsmdroidTileCache().mkdirs();
        //db_file = new File(Configuration.getInstance().getOsmdroidTileCache().getAbsolutePath() + File.separator + DATABASE_FILENAME);
        //db_file = new File(DATABASE_PATH + File.separator + DATABASE_FILENAME);

        try {
            //db = Global.getgetLIBDB().dbConn;
            //db = SQLiteDatabase.openOrCreateDatabase(db_file, null);
            //db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" + COLUMN_KEY + " INTEGER , " + DatabaseFileArchive.COLUMN_PROVIDER + " TEXT, " + DatabaseFileArchive.COLUMN_TILE + " BLOB, " + COLUMN_EXPIRES +" INTEGER, PRIMARY KEY (" + COLUMN_KEY + ", " + DatabaseFileArchive.COLUMN_PROVIDER + "));");
        } catch (Throwable ex) {
            Log.e(IMapView.LOGTAG, "Unable to open the SQLiteDatabase tile writer. Check external storage availability.", ex);
        }
//        if (!hasInited) {
//            hasInited = true;
//
//            if (CLEANUP_ON_START) {
//                // do this in the background because it takes a long time
//                final Thread t = new Thread() {
//                    @Override
//                    public void run() {
//                        runCleanupOperation();
//                    }
//                };
//                t.setPriority(Thread.MIN_PRIORITY);
//                t.start();
//            }
//        }
    }

    /**
     * this could be a long running operation, don't run on the UI thread unless necessary.
     * This function prunes the database for old or expired tiles.
     *
     * @since 5.6
     */
//    public void runCleanupOperation() {
//        if (db == null) {
//            if (Configuration.getInstance().isDebugMode()) {
//                Log.d(IMapView.LOGTAG, "Finished init thread, aborted due to null database reference");
//            }
//            return;
//        }
//
//        try {
//            if (db_file.length() > Configuration.getInstance().getTileFileSystemCacheMaxBytes()) {
//                //run the reaper (remove all old expired tiles)
//                //keep if now is < expiration date
//                //delete if now is > expiration date
//                long now = System.currentTimeMillis();
//                //this part will nuke all expired tiles, not super useful if you're offline
//                //int rows = db.delete(TABLE, "expires < ?", new String[]{System.currentTimeMillis() + ""});
//                //Log.d(IMapView.LOGTAG, "Local storage cache purged " + rows + " expired tiles in " + (System.currentTimeMillis() - now) + "ms, cache size is " + db_file.length() + "bytes");
//
//                //attempt to trim the database
//                //note, i considered adding a looping mechanism here but sqlite can behave differently
//                //i.e. there's no guarantee that the database file size shrinks immediately.
//                Log.i(IMapView.LOGTAG, "Local cache is now " + db_file.length() + " max size is " + Configuration.getInstance().getTileFileSystemCacheMaxBytes());
//                long diff = db_file.length() - Configuration.getInstance().getTileFileSystemCacheMaxBytes();
//                long tilesToKill = diff / questimate;
//                Log.d(IMapView.LOGTAG, "Local cache purging " + tilesToKill + " tiles.");
//                if (tilesToKill > 0)
//                    try {
//                        db.execSQL("DELETE FROM " + TABLE + " WHERE " + COLUMN_KEY + " in (SELECT " + COLUMN_KEY + " FROM " + TABLE + " ORDER BY " + COLUMN_EXPIRES + " DESC LIMIT " + tilesToKill + ")");
//                    } catch (Throwable t) {
//                        Log.e(IMapView.LOGTAG, "error purging tiles from the tile cache", t);
//                    }
//                Log.d(IMapView.LOGTAG, "purge completed in " + (System.currentTimeMillis() - now) + "ms, cache size is " + db_file.length() + " bytes");
//            }
//        } catch (Exception ex) {
//            if (Configuration.getInstance().isDebugMode()) {
//                Log.d(IMapView.LOGTAG, "SqliteTileWriter init thread crash, db is probably not available", ex);
//            }
//        }
//
//        if (Configuration.getInstance().isDebugMode()) {
//            Log.d(IMapView.LOGTAG, "Finished init thread");
//        }
//    }

    @Override
    public boolean saveFile(final ITileSource pTileSourceInfo, final MapTile pTile, final InputStream pStream) {
//        if (db == null || !db.isOpen()) {
//            Log.d(IMapView.LOGTAG, "Unable to store cached tile from " + pTileSourceInfo.name() + " " + pTile.toString() + ", database not available.");
//            Counters.fileCacheSaveErrors++;
//            return false;
//        }
//        try {
//            ContentValues cv = new ContentValues();
//            final long x = (long) pTile.getX();
//            final long y = (long) pTile.getY();
//            final long z = (long) pTile.getZoomLevel();
//            final long index = ((z << z) + x << z) + y;
//            cv.put(DatabaseFileArchive.COLUMN_PROVIDER, pTileSourceInfo.name());
//            BufferedInputStream bis = new BufferedInputStream(pStream);
//
//            List<Byte> list = new ArrayList<Byte>();
//            //ByteArrayBuffer baf = new ByteArrayBuffer(500);
//            int current = 0;
//            while ((current = bis.read()) != -1) {
//                list.add((byte) current);
//            }
//
//            byte[] bits = new byte[list.size()];
//            for (int i = 0; i < list.size(); i++) {
//                bits[i] = list.get(i);
//            }
//            cv.put(COLUMN_KEY, index);
//            cv.put(DatabaseFileArchive.COLUMN_TILE, bits);
//            //this shouldn't happen, but just in case
//            if (pTile.getExpires() != null)
//                cv.put(COLUMN_EXPIRES, pTile.getExpires().getTime());
//            db.delete(TABLE, COLUMN_KEY + "=? and " + DatabaseFileArchive.COLUMN_PROVIDER + "=?", new String[]{index + "", pTileSourceInfo.name()});
//            db.insert(TABLE, null, cv);
//            if (Configuration.getInstance().isDebugMode())
//                Log.d(IMapView.LOGTAG, "tile inserted " + pTileSourceInfo.name() + pTile.toString());
//            if (System.currentTimeMillis() > lastSizeCheck + 300000){
//                lastSizeCheck = System.currentTimeMillis();
//                if (db_file!=null && db_file.length() > Configuration.getInstance().getTileFileSystemCacheTrimBytes()) {
//                    //runCleanupOperation();
//                }
//            }
//        } catch (SQLiteFullException ex) {
//            //the drive is full! trigger the clean up operation
//            //may want to consider reducing the trim size automagically
//            //runCleanupOperation();
//        } catch (Throwable ex) {
//            //note, although we check for db null state at the beginning of this method, it's possible for the
//            //db to be closed during the execution of this method
//            Log.e(IMapView.LOGTAG, "Unable to store cached tile from " + pTileSourceInfo.name() + " " + pTile.toString() + " db is " + (db == null ? "null" : "not null"), ex);
//            Counters.fileCacheSaveErrors++;
//        }
        return false;
    }

    /**
     * Returns true if the given tile source and tile coordinates exist in the cache
     *
     * @param pTileSource
     * @param pTile
     * @return
     * @since 5.6
     */
    public boolean exists(String pTileSource, MapTile pTile) {
        boolean retval = false;

//        if (db == null || !db.isOpen()) {
//            Log.d(IMapView.LOGTAG, "Unable to test for tile exists, database not available.");
//            return retval;
//        }

        try {
            final String[] tile = {DatabaseFileArchive.COLUMN_TILE};
            final int x = pTile.getX();
            final int y = pTile.getY();
            final int z = pTile.getZoomLevel();
            final int index = ((z << z) + x << z) + y;

            retval = Global.getInstance().getDb().getHasTile(index);

            //final Cursor cur = db.query(TABLE, tile, COLUMN_KEY + " = " + index, null, null, null, null);
            //final Cursor cur = db.query(TABLE, tile, COLUMN_KEY + " = " + index + " and " + DatabaseFileArchive.COLUMN_PROVIDER + " = '" + pTileSource + "'", null, null, null, null);

//            if (cur.getCount() != 0) {
//                cur.close();
//                return true;
//            } else
//            {
//                Log.d("LocalTileWriter", "does not exist");
//            }

            //cur.close();
        } catch (Throwable ex) {
            Log.e(IMapView.LOGTAG, "Unable to store cached tile from " + pTileSource + " " + pTile.toString(), ex);
        }

        return retval;
    }

    /**
     * Returns true if the given tile source and tile coordinates exist in the cache
     *
     * @param pTileSource
     * @param pTile
     * @return
     * @since 5.6
     */
    @Override
    public boolean exists(ITileSource pTileSource, MapTile pTile) {
        return exists(pTileSource.name(), pTile);
    }

    @Override
    public void onDetach() {
//        if (db != null && db.isOpen()) {
//            try {
//                //db.close();
//                Log.i(IMapView.LOGTAG, "XP_Library_DB detached");
//            } catch (Exception ex) {
//                Log.e(IMapView.LOGTAG, "XP_Library_DB detach failed",ex);
//            }
//        }

        //db = null;
        //db_file = null;
    }

    /**
     * purges and deletes everything from the cache database
     *
     * @return
     * @since 5.6
     */
    public boolean purgeCache() {
//        if (db != null && db.isOpen()) {
//            try {
//                db.delete(TABLE, null, null);
//                return true;
//            } catch (final Throwable e) {
//                Log.w(IMapView.LOGTAG, "Error purging the db", e);
//            }
//        }
        return false;
    }

    /**
     * purges and deletes all tiles from the given tile source name from the cache database
     *
     * @return
     * @since 5.6.1
     */
    public boolean purgeCache(String mTileSourceName) {
//        if (db != null && db.isOpen()) {
//            try {
//                db.delete(TABLE, COLUMN_PROVIDER + " = ?", new String[]{mTileSourceName});
//                return true;
//            } catch (final Throwable e) {
//                Log.w(IMapView.LOGTAG, "Error purging the db", e);
//            }
//        }
        return false;
    }

    /**
     * a helper method to import file system stored map tiles into the sql tile cache
     * on successful import, the tiles are removed from the file system.
     * <p>
     * This can take a long time, so consider running this off of the main thread.
     *
     * @return
     */
//    public int[] importFromFileCache(boolean removeFromFileSystem) {
//        int[] ret = new int[]{0, 0, 0, 0};
//        //inserts
//        //insert failures
//        //deletes
//        //delete failures
//        File tilePathBase = Configuration.getInstance().getOsmdroidTileCache();
//        if (tilePathBase.exists()) {
//            File[] tileSources = tilePathBase.listFiles();
//            if (tileSources != null) {
//                for (int i = 0; i < tileSources.length; i++) {
//                    if (tileSources[i].isDirectory() && !tileSources[i].isHidden()) {
//                        //proceed
//                        File[] z = tileSources[i].listFiles();
//                        if (z != null)
//                            for (int zz = 0; zz < z.length; zz++) {
//                                if (z[zz].isDirectory() && !z[zz].isHidden()) {
//                                    File[] x = z[zz].listFiles();
//                                    if (x != null)
//                                        for (int xx = 0; xx < x.length; xx++) {
//                                            if (x[xx].isDirectory() && !x[xx].isHidden()) {
//                                                File[] y = x[xx].listFiles();
//                                                if (x != null)
//                                                    for (int yy = 0; yy < y.length; yy++) {
//                                                        if (!y[yy].isHidden() && !y[yy].isDirectory()) {
//
//                                                            try {
//                                                                ContentValues cv = new ContentValues();
//                                                                final long x1 = Long.parseLong(x[xx].getName());
//                                                                final long y1 = Long.parseLong(y[yy].getName().substring(0, y[yy].getName().indexOf(".")));
//                                                                final long z1 = Long.parseLong(z[zz].getName());
//                                                                final long index = ((z1 << z1) + x1 << z1) + y1;
//                                                                cv.put(DatabaseFileArchive.COLUMN_PROVIDER, tileSources[i].getName());
//                                                                if (!exists(tileSources[i].getName(), new MapTile((int) z1, (int) x1, (int) y1))) {
//
//                                                                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(y[yy]));
//
//                                                                    List<Byte> list = new ArrayList<Byte>();
//                                                                    //ByteArrayBuffer baf = new ByteArrayBuffer(500);
//                                                                    int current = 0;
//                                                                    while ((current = bis.read()) != -1) {
//                                                                        list.add((byte) current);
//                                                                    }
//
//                                                                    byte[] bits = new byte[list.size()];
//                                                                    for (int bi = 0; bi < list.size(); bi++) {
//                                                                        bits[bi] = list.get(bi);
//                                                                    }
//                                                                    cv.put(COLUMN_KEY, index);
//                                                                    cv.put(DatabaseFileArchive.COLUMN_TILE, bits);
//
//                                                                    long insert = db.insert(TABLE, null, cv);
//                                                                    if (insert > 0) {
//                                                                        if (Configuration.getInstance().isDebugMode())
//                                                                            Log.d(IMapView.LOGTAG, "tile inserted " + tileSources[i].getName() + "/" + z1 + "/" + x1 + "/" + y1);
//                                                                        ret[0]++;
//                                                                        if (removeFromFileSystem) {
//                                                                            try {
//                                                                                y[yy].delete();
//                                                                                ret[2]++;
//                                                                                ;
//                                                                            } catch (Exception ex) {
//                                                                                ret[3]++;
//                                                                                ;
//                                                                            }
//                                                                        }
//                                                                    } else {
//                                                                        Log.w(IMapView.LOGTAG, "tile NOT inserted " + tileSources[i].getName() + "/" + z1 + "/" + x1 + "/" + y1);
//                                                                    }
//                                                                }
//
//                                                            } catch (Throwable ex) {
//                                                                //note, although we check for db null state at the beginning of this method, it's possible for the
//                                                                //db to be closed during the execution of this method
//                                                                Log.e(IMapView.LOGTAG, "Unable to store cached tile from " + tileSources[i].getName() + " db is " + (db == null ? "null" : "not null"), ex);
//                                                                ret[1]++;
//                                                            }
//                                                        }
//                                                    }
//                                            }
//                                            if (removeFromFileSystem) {
//                                                //clean up the directories
//                                                try {
//                                                    x[xx].delete();
//                                                } catch (Exception ex) {
//                                                    Log.e(IMapView.LOGTAG, "Unable to delete directory from " + x[xx].getAbsolutePath(), ex);
//                                                    ret[3]++;
//                                                }
//                                            }
//                                        }
//                                }
//                                if (removeFromFileSystem) {
//                                    //clean up the directories
//                                    try {
//                                        z[zz].delete();
//                                    } catch (Exception ex) {
//                                        Log.e(IMapView.LOGTAG, "Unable to delete directory from " + z[zz].getAbsolutePath(), ex);
//                                        ret[3]++;
//                                    }
//                                }
//                            }
//
//
//                        if (removeFromFileSystem) {
//                            //clean up the directories
//                            try {
//                                tileSources[i].delete();
//                            } catch (Exception ex) {
//                                Log.e(IMapView.LOGTAG, "Unable to delete directory from " + tileSources[i].getAbsolutePath(), ex);
//                                ret[3]++;
//                            }
//                        }
//
//                    } else {
//                        //it's a file, nothing for us to do here
//                    }
//                }
//            }
//
//        }
//        return ret;
//        return null;
//    }

    /**
     * Removes a specific tile from the cache
     *
     * @param pTileSourceInfo
     * @param pTile
     * @return
     * @since 5.6
     */
    @Override
    public boolean remove(final ITileSource pTileSourceInfo, final MapTile pTile) {
//        if (db == null) {
//            Log.d(IMapView.LOGTAG, "Unable to delete cached tile from " + pTileSourceInfo.name() + " " + pTile.toString() + ", database not available.");
//            Counters.fileCacheSaveErrors++;
//            return false;
//        }
        try {
            final int x = pTile.getX();
            final int y = pTile.getY();
            final int z = pTile.getZoomLevel();
            final int index = ((z << z) + x << z) + y;
            //" + DatabaseFileArchive.COLUMN_PROVIDER + "
            Global.getInstance().getDb().writeDeleteTile(pTileSourceInfo.name(), index);
            //db.delete(TABLE, COLUMN_KEY + "=? and waterwayguid=?", new String[]{index + "", pTileSourceInfo.name()});
            return true;
        } catch (Throwable ex) {
            //note, although we check for db null state at the beginning of this method, it's possible for the
            //db to be closed during the execution of this method
            //Log.e(IMapView.LOGTAG, "Unable to delete cached tile from " + pTileSourceInfo.name() + " " + pTile.toString() + " db is " + (db == null ? "null" : "not null"), ex);
            Counters.fileCacheSaveErrors++;
        }
        return false;
    }

    /**
     * Returns the number of tiles in the cache for the specified tile source name
     *
     * @param tileSourceName
     * @return
     * @since 5.6
     */
//    public long getRowCount(String tileSourceName) {
//        try {
//            Cursor mCount = null;
//            if (tileSourceName == null)
//                mCount = db.rawQuery("select count(*) from " + TABLE, null);
//            else
//                mCount = db.rawQuery("select count(*) from " + TABLE + " where " + COLUMN_PROVIDER + "='" + tileSourceName + "'", null);
//            mCount.moveToFirst();
//            long count = mCount.getLong(0);
//            mCount.close();
//            return count;
//        } catch (Throwable ex) {
//            Log.e(IMapView.LOGTAG, "Unable to query for row count " + tileSourceName, ex);
//        }
//        return 0;
//    }

}
