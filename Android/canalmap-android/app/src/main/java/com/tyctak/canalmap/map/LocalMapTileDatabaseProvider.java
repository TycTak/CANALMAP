package com.tyctak.canalmap.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tyctak.canalmap.Global;
import com.tyctak.canalmap.Library;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.map.entities._Tile;

import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.modules.MapTileFileStorageProviderBase;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.StreamUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.ContentValues.TAG;

public class LocalMapTileDatabaseProvider extends MapTileFileStorageProviderBase {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================

    private final AtomicReference<ITileSource> mTileSource = new AtomicReference<ITileSource>();
    private LocalTileWriter mWriter;
//    private final long mMaximumCachedFileAge;

    private static final String[] tile = {"tile"};
    private static final String[] columns={"tile"}; //,"expires"
//    private Drawable mBackgroundImage;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * The tiles may be found on several media. This one works with tiles stored on the file system.
     * It and its friends are typically created and controlled by {@link MapTileProviderBase}.
     */
    public LocalMapTileDatabaseProvider(final IRegisterReceiver pRegisterReceiver,
                                        final ITileSource pTileSource, final long pMaximumCachedFileAge) {
        super(pRegisterReceiver,
                Configuration.getInstance().getTileFileSystemThreads(),
                Configuration.getInstance().getTileFileSystemMaxQueueSize());

        setTileSource(pTileSource);
//        mMaximumCachedFileAge = pMaximumCachedFileAge;
        mWriter = new LocalTileWriter();

    }

    public LocalMapTileDatabaseProvider(final IRegisterReceiver pRegisterReceiver,
                                        final ITileSource pTileSource) {
        this(pRegisterReceiver, pTileSource, OpenStreetMapTileProviderConstants.DEFAULT_MAXIMUM_CACHED_FILE_AGE);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean getUsesDataConnection() {
        return false;
    }

    @Override
    protected String getName() {
        return "SQL Cache Archive Provider";
    }

    @Override
    protected String getThreadGroupName() {
        return "sqlcache";
    }

    @Override
    protected Runnable getTileLoader() {
        return new TileLoader();
    }

    @Override
    public int getMinimumZoomLevel() {
        ITileSource tileSource = mTileSource.get();
        return tileSource != null ? tileSource.getMinimumZoomLevel() : OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL;
    }

    @Override
    public int getMaximumZoomLevel() {
        ITileSource tileSource = mTileSource.get();
        return tileSource != null ? tileSource.getMaximumZoomLevel()
                : microsoft.mappoint.TileSystem.getMaximumZoomLevel();
    }

    @Override
    protected void onMediaMounted() {

    }

    @Override
    protected void onMediaUnmounted() {
        if (mWriter!=null)
            mWriter.onDetach();
        mWriter=new LocalTileWriter();
    }

    @Override
    public void setTileSource(final ITileSource pTileSource) {
        mTileSource.set(pTileSource);
    }

    @Override
    public void detach() {
        if (mWriter!=null)
            mWriter.onDetach();
        mWriter=null;
        super.detach();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * returns true if the given tile for the current map source exists in the cache db
     * @param pTile
     * @return
     */
    public boolean hasTile(final MapTile pTile) {
        ITileSource tileSource = mTileSource.get();
        if (tileSource == null) {
            return false;
        }

        final int x = pTile.getX();
        final int y = pTile.getY();
        final int z = pTile.getZoomLevel();
        final int index = ((z << z) + x << z) + y;

//        final Cursor cur = mWriter.db.query(DatabaseFileArchive.TABLE,columns,"key = " + index + " and provider = '" + tileSource.name() + "'", null, null, null, null);
//        final Cursor cur = mWriter.db.query(DatabaseFileArchive.TABLE, columns,"key = " + index, null, null, null, null);
//
//        if(cur.getCount() != 0) {
//            cur.close();
//            return true;
//        }

        return Global.getInstance().getDb().getHasTile(index);
    }

    private static HashMap<String, Bitmap> blankTiles = new HashMap<>();

    public static void clearBlankTiles() {
        blankTiles.clear();
    }

    private static synchronized HashMap<String, Bitmap> getInstance() {
        if (blankTiles == null) {
            blankTiles = new HashMap<>();
        }
        return blankTiles;
    }

    private static synchronized void putTile(String key, Bitmap bitmap) {
        getInstance().put(key, bitmap);
    }

    private static synchronized Bitmap getTile(String key) {
        return getInstance().get(key);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    protected class TileLoader extends MapTileModuleProviderBase.TileLoader {

        @Override
        public Drawable loadTile(final MapTileRequestState pState) {

            Bitmap tile_notpurchased = null;
            Bitmap tile_notavailable = null;
            Bitmap tile_downloading = null;

            ITileSource tileSource = mTileSource.get();
            if (tileSource == null) {
                return null;
            }

            final MapTile pTile = pState.getMapTile();

            InputStream inputStream = null;
            try {
                final int x = pTile.getX();
                final int y = pTile.getY();
                final int z = pTile.getZoomLevel();
                final int index = ((z << z) + x << z) + y;

                Log.d(TAG, "TILE x=" + x + ",y=" + y + ",z=" + z);

                _Tile tile = Global.getInstance().getDb().getTile(index);

                if (tile.Tile == null) {
                    Log.d("MapTileProvider", "does not exist " + index);

                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    Drawable blank;

                    if (tile.Priority == -2) {
                        if (tile_notavailable == null) {
                            int color = Color.rgb(180, 61, 85);
                            tile_notavailable = Bitmap.createBitmap(512, 512, conf);
                            Canvas c = new Canvas(tile_notavailable);
                            PositionText("sorry a CANAL map is not", c, tile_notavailable, 0, color);
                            PositionText("available for this area", c, tile_notavailable, 34, color);
                        }

                        blank = new BitmapDrawable(tile_notavailable);
                    } else if (tile.Priority == -1) {
                        if (tile_notpurchased == null) {
                            int color = Color.rgb(180, 61, 85);
                            tile_notpurchased = Bitmap.createBitmap(512, 512, conf);
                            Canvas c1 = new Canvas(tile_notpurchased);
                            PositionText("move up a level and", c1, tile_notpurchased, 0, color);
                            PositionText("PRESS & HOLD on a canal", c1, tile_notpurchased, 34, color);
                            PositionText("to show relevant map", c1, tile_notpurchased, 64, color);
                            PositionText("for that location", c1, tile_notpurchased, 96, color);
                        }

                        blank = new BitmapDrawable(tile_notpurchased);
                    } else {
                        if (tile_downloading == null) {
                            int color = Color.rgb(180, 61, 85);
                            tile_downloading = Bitmap.createBitmap(512, 512, conf);
                            Canvas c1 = new Canvas(tile_downloading);
                            PositionText("please try later", c1, tile_downloading, 0, color);
                            PositionText("this location is", c1, tile_downloading, 34, color);
                            PositionText("downloading", c1, tile_downloading, 64, color);
                        }

                        blank = new BitmapDrawable(tile_downloading);
                    }
                    return blank;
                }

                Library LIB = new Library();
                Library_GR LIBGR = new Library_GR();
                Drawable drawable = null;
                try {
                    if (tile.Tile == null) {
                        Log.d("LocalMapTileDatabaseProvider", "TILEERROR");
                        tile_notavailable = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(tile_notavailable);
                        PositionText("!ERROR!", c, tile_notavailable, 0, Color.rgb(137, 224, 224));
                        PositionText("please try downloading map again", c, tile_notavailable, 34, Color.rgb(137, 224, 224));
                        PositionText("or email support@tyctak.com", c, tile_notavailable, 64, Color.rgb(137, 224, 224));
                        PositionText("quoting " + tile.Key, c, tile_notavailable, 96, Color.rgb(137, 224, 224));

                        return new BitmapDrawable(tile_notavailable);
                    } else {
                        drawable = new BitmapDrawable(LIB.decodeBinary(tile.Tile));
                    }
                } catch ( Exception e ) {

                }
                return drawable;
            } catch (final Throwable e) {
                Log.e(IMapView.LOGTAG,"Error loading tile", e);
            } finally {
                if (inputStream != null) {
                    StreamUtils.closeStream(inputStream);
                }
            }

            return null;
        }

        private void PositionText(String str, Canvas c, Bitmap bm, int offSet, int color) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setTextSize(24);
            Rect bounds = new Rect();
            paint.getTextBounds(str, 0, str.length(), bounds);
            int x1 = (bm.getWidth() - bounds.width()) / 2;
            int y1 = (bm.getHeight() + bounds.height()) / 2;
            c.drawText(str, x1, y1 + offSet - 40, paint);
        }

        private void PositionText(String str, Canvas c, Bitmap bm, int offSet) {
            PositionText(str, c, bm, offSet, Color.rgb(180, 61, 85));
        }
    }
}
