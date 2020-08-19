package com.tyctak.canalmap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.tyctak.map.entities._File;
import com.tyctak.map.entities._Tile;
import com.tyctak.map.libraries.XP_Library_WS;
import com.tyctak.map.libraries.XP_Library_ZP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service_MD extends Service {

    private final String TAG = "Service_MD";

    private final int INITIAL_DELAY_SECONDS = 5;
    private final int REPEATING_DELAY_SECONDS = 30;

    final XP_Library_WS XPLIBWS = new XP_Library_WS();

    private static boolean isStopping = false;

    ScheduledExecutorService scheduledExecutorService;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        Service_MD getService() {
            return Service_MD.this;
        }
    }

    public boolean isRunning() {
        return (scheduledExecutorService != null && !scheduledExecutorService.isShutdown());
    }

    private class responsePacket {
        public int key;
        public String routeGuid;
        public byte[] bytes;
    }

    public class MyRunnable implements Runnable {

        private String tileList;
        private ArrayList<_File> files;

        public MyRunnable(String pTileList, ArrayList<_File> pFiles) {
            tileList = pTileList;
            files = pFiles;
        }

        public void run() {
            byte[] bytes = XPLIBWS.downloadBinaryGET("https://tiles.cancam.co.uk/tiles.php?tiles=" + tileList);
            XP_Library_ZP XPLIBZP = new XP_Library_ZP();
            files = XPLIBZP.getAllFiles(bytes, files);

            for (int i = 0; i < files.size(); i++) {
                _File downloadedFile = files.get(i);
                String[] parts = downloadedFile.getReference().split(";");

                if (downloadedFile.getContents() != null) {
                    Global.getInstance().getDb().writeRetrievedTile(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), downloadedFile.getContents());
                }

                if (((i % 100) == 0 && i != 0) || i == (files.size() - 1)) {
                    Global.getInstance().getDb().checkTilesDownloaded(Integer.parseInt(parts[0]));
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
//        LIB = new Library();
//        final XP_Library_WS XPLIBWS = new XP_Library_WS();

        Log.d(TAG, "START onStartCommand");

        if (scheduledExecutorService == null) {
            scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "START onStartCommand.scheduledExecutorService");

                    try {
                        String inClause = Global.getInstance().getDb().getInClause();

                        isStopping = false;

                        Global.getInstance().getDb().writeResetAllTiles();

                        while (!Global.getInstance().getDb().getPaused() && !isStopping) {
                            Log.d(TAG, "WHILE onStartCommand.scheduledExecutorService");

                            final ArrayList<_Tile> tiles = Global.getInstance().getDb().getWaitingTiles(inClause);

                            if (tiles != null && tiles.size() > 0) {

//                                final XP_Library_WS XPLIBWS = new XP_Library_WS();

                                if (!XPLIBWS.isNetworkAvailable()) {
//                                    XPLIBWS.cancel();
                                    break;
                                } else {
//                                    while (XPLIBWS.queuedCalls() > 50) {
//                                        XP_Library_CM.Delay(1);
//                                    }

// ############################################################################################

                                    String tileList = "";
                                    ArrayList<_File> files = new ArrayList<>();

                                    for (int i = 0; i < tiles.size(); i++) {
                                        _Tile tile = tiles.get(i);
                                        String suffix = XPLIBWS.getUrlSuffix(tile.TileUrl);
                                        tileList += (tileList.isEmpty() ? suffix : "!" + suffix);

                                        _File file = new _File(suffix,tile.RouteId + ";" + tile.Key);
                                        files.add(file);

                                        if ((((i % 100) == 0) && i > 0) || (i == (tiles.size() - 1))) {
                                            MyRunnable runnable = new MyRunnable(tileList, files);
                                            runnable.run();

                                            tileList = "";
                                            files = new ArrayList<>();
                                        }
                                    }

// ############################################################################################

//                                    for (_Tile tile : tiles) {
//                                        String tag = tile.RouteGuid + ";" + tile.Key;
//
//                                        XPLIBWS.downloadBinary(tag, tile.TileUrl, new Callback() {
//                                            @Override
//                                            public void onFailure(Call call, IOException e) {
//                                            }
//
//                                            @Override
//                                            public void onResponse(Call call, Response response) throws IOException {
//                                                String tag = (String) call.request().tag();
//
//                                                String[] items = tag.split(";");
//
//                                                String routeGuid = items[0];
//                                                int key = Integer.parseInt(items[1]);
//
//                                                try {
//                                                    if (!response.isSuccessful()) {
//                                                        Log.d(TAG, "BITMAP RETURNED FAILED RESPONSE - " + tag);
//                                                        Global.getInstance().getDb().writeRetrievedTile(key, routeGuid, null);
//                                                    } else {
//                                                        byte[] bytes = XPLIBWS.getBinary(response);
//
//                                                        if (bytes == null) {
//                                                            throw new RuntimeException("Bitmap did not download " + tag);
//                                                        } else {
//                                                            Global.getInstance().getDb().writeRetrievedTile(key, routeGuid, bytes);
//                                                        }
//                                                    }
//                                                } catch (RuntimeException e) {
//                                                    Log.e(TAG, "CODE PRODUCED ERROR - IGNORE" + tag);
//                                                    Global.getInstance().getDb().writeRetrievedTile(key, routeGuid, null);
//                                                    throw e;
//                                                } catch (ProtocolException e) {
//                                                    if (e.getMessage().equals("unexpected end of stream")) {
//                                                        Global.getInstance().getDb().writeRetryTile(routeGuid, key);
//                                                        Log.e(TAG, "PROTOCOL EXCEPTION - TRY AGAIN " + tag);
//                                                    } else {
//                                                        Global.getInstance().getDb().writeRetrievedTile(key, routeGuid, null);
//                                                    }
//
//                                                    throw e;
//                                                } catch (Exception e) {
//                                                    Log.e(TAG, "BITMAP CORRUPTED OR DISK FULL - IGNORE " + tag);
//                                                    Global.getInstance().getDb().writeRetrievedTile(key, routeGuid, null);
//
//                                                    throw e;
//                                                } finally {
//                                                    response.close();
//                                                }
//                                            }
//                                        });
//                                    }

                                    if (Global.getInstance().getDb().getStopped(tiles.get(0).RouteGuid)) break;
                                }
                            } else {
//                                Global.getInstance().getDb().checkTilesDownloaded(Global.getInstance().getDb().getRouteGuid());
                                break;
                            }
                        }

                        if (isStopping) {
                            stopSelf();
                        } else if (Global.getInstance().getDb().getTilesLeft() == 0) {
                            Log.d(TAG, "SHUTDOWN onStartCommand.scheduledExecutorService");
                            stopSelf();
                        } else {
                            Log.d(TAG, "REPEAT onStartCommand.scheduledExecutorService #" + Global.getInstance().getDb().getTilesLeft());
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "STOP onStartCommand.scheduledExecutorService");
                }
            }, INITIAL_DELAY_SECONDS, REPEATING_DELAY_SECONDS, TimeUnit.SECONDS);
        }

        Log.d(TAG, "STOP onStartCommand");

        return START_STICKY;
    }

    private ArrayList<responsePacket> getResponse(String tags, byte[] bytes) {
        ArrayList<responsePacket> responsePackets = new ArrayList<>();

        String[] tagList = tags.split("#");
        int position = 0;

        for (String tagItem : tagList) {
            try {
                String[] items = tagItem.split(";");

                responsePacket packet = new responsePacket();
                packet.routeGuid = items[0];
                packet.key = Integer.parseInt(items[1]);

                if (bytes != null) {
                    byte[] seperator = "<end>".getBytes();
                    int lastPosition = position;
                    position = indexOf(bytes, position, bytes.length, seperator);
                    packet.bytes = Arrays.copyOfRange(bytes, lastPosition, (position == -1 ? bytes.length : position));
                    position += seperator.length;
                } else {
                    packet.bytes = null;
                }

                responsePackets.add(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return responsePackets;
    }

    private static int indexOf( byte[] data, int start, int stop, byte[] seperator) {
        if( data == null || seperator == null) return -1;

        int[] failure = computeFailure(seperator);

        int j = 0;

        for( int i = start; i < stop; i++) {
            while (j > 0 && ( seperator[j] != '*' && seperator[j] != data[i])) {
                j = failure[j - 1];
            }

            if (seperator[j] == '*' || seperator[j] == data[i]) {
                j++;
            }

            if (j == seperator.length) {
                return i - seperator.length + 1;
            }
        }

        return -1;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j>0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DESTROY Service_MD START");

        if (scheduledExecutorService != null) {
            isStopping = true;
            scheduledExecutorService.shutdown();
//            XPLIBWS.cancelAsyncCalls();
        }

        Log.d(TAG, "DESTROY Service_MD END");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}