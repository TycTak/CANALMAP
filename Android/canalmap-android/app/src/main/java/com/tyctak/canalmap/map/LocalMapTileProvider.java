package com.tyctak.canalmap.map;

import android.content.Context;

import org.osmdroid.tileprovider.IMapTileProviderCallback;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

public class LocalMapTileProvider extends MapTileProviderArray implements IMapTileProviderCallback {

    protected IFilesystemCache tileWriter;

//    /**
//     * Creates a {@link MapTileProviderBasic}.
//     */
//    public LocalMapTileProvider(final Context pContext) {
//        this(pContext, TileSourceFactory.DEFAULT_TILE_SOURCE);
//    }

    public SimpleRegisterReceiver getRegisteredReceiver() {
        return (SimpleRegisterReceiver)mRegisterReceiver;
    }

    /**
     * Creates a {@link MapTileProviderBasic}.
     */
    public LocalMapTileProvider(final Context pContext, final ITileSource pTileSource) {
        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
                pTileSource, pContext,null);
    }

//    /**
//     * Creates a {@link MapTileProviderBasic}.
//     */
//    public LocalMapTileProvider(final Context pContext, final ITileSource pTileSource, final IFilesystemCache cacheWriter) {
//        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
//                pTileSource, pContext,cacheWriter);
//    }

    /**
     * Creates a {@link MapTileProviderBasic}.
     */
    public LocalMapTileProvider(final IRegisterReceiver pRegisterReceiver,
                                final INetworkAvailablityCheck aNetworkAvailablityCheck, final ITileSource pTileSource,
                                final Context pContext, final IFilesystemCache cacheWriter) {
        super(pTileSource, pRegisterReceiver);

        if (cacheWriter != null) {
            tileWriter = cacheWriter;
        } else {
            //tileWriter = new SqlTileWriter();
            tileWriter = new LocalTileWriter();
        }

        final LocalMapTileDatabaseProvider cachedProvider = new LocalMapTileDatabaseProvider(pRegisterReceiver, pTileSource);
        mTileProviderList.add(cachedProvider);

//        final MapTileAssetsProvider assetsProvider = new MapTileAssetsProvider(
//                pRegisterReceiver, pContext.getAssets(), pTileSource);
//        mTileProviderList.add(assetsProvider);
//
//        if (Build.VERSION.SDK_INT < 10) {
//            final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
//                    pRegisterReceiver, pTileSource);
//            mTileProviderList.add(fileSystemProvider);
//        } else {
//            final MapTileSqlCacheProvider cachedProvider = new MapTileSqlCacheProvider(pRegisterReceiver, pTileSource);
//            mTileProviderList.add(cachedProvider);
//        }
//        final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
//                pRegisterReceiver, pTileSource);
//        mTileProviderList.add(archiveProvider);

        final MapTileDownloader downloaderProvider = new MapTileDownloader(pTileSource, tileWriter, aNetworkAvailablityCheck);
        mTileProviderList.add(downloaderProvider);
    }

    @Override
    public IFilesystemCache getTileWriter() {
        return tileWriter;
    }

    @Override
    public void detach(){
        //https://github.com/osmdroid/osmdroid/issues/213
        //close the writer
        if (tileWriter!=null)
            tileWriter.onDetach();
        tileWriter=null;
        super.detach();
    }
}
