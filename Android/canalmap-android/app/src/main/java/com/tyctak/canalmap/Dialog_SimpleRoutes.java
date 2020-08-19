package com.tyctak.canalmap;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Route;
import com.tyctak.canalmap.libraries.Library_OS;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_WS;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class Dialog_SimpleRoutes extends AppCompatActivity {

    final private String TAG = "Dialog_SimpleRoutes";
    final private Dialog_SimpleRoutes activity = this;
    final private Integer DELAY = 3000;

    final Library_UI LIBUI = new Library_UI();

    ArrayAdapter_SimpleRoutes arrayAdapter;
    ArrayList<_Route> routes;
    int key = 0;

    Runnable runnable;
    Boolean stillRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_simpleroutes);

        int currentZoom = getIntent().getIntExtra("zoom", 16);
        double[] gp = getIntent().getDoubleArrayExtra("geopoint");
        GeoPoint geoPoint = new GeoPoint(gp[0], gp[1]);

        Library_OS LIBOS = new Library_OS();
        int zoom = 16;
        Point point = LIBOS.getMapTileFromCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude(), zoom);
        key = ((zoom << zoom) + point.x << zoom) + point.y;

        routes = LIBOS.getRoutes(zoom, geoPoint);

        LinearLayout layoutMapFound = (LinearLayout) findViewById(R.id.layoutMapFound);
        LinearLayout layoutMapNotFound = (LinearLayout) findViewById(R.id.layoutMapNotFound);

        boolean isPurchased = Global.getInstance().getDb().getPurchased(Global.getInstance().getDb().getAllRouteGuid());

        ListView listView = (ListView) findViewById(R.id.listOfWaterways);

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (routes.size() == 0 && isPurchased) {
            layoutMapNotFound.setVisibility(View.VISIBLE);
        } else {
            layoutMapFound.setVisibility(View.VISIBLE);

            arrayAdapter = new ArrayAdapter_SimpleRoutes(this, routes);
            String listRoutes = "";

            for (_Route route : routes) {
                if (!listRoutes.isEmpty()) listRoutes += "\n";
                listRoutes += String.format("ROUTE = %s\nFILE = %s", route.RouteName, route.FileName);
            }

            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick");
                    _Route route = (_Route) parent.getItemAtPosition(position);

                    Intent intent = new Intent(activity, Dialog_Route.class);
                    intent.putExtra("routeGuid", route.RouteGuid);
                    intent.putExtra("purchased", route.Purchased);
                    intent.putExtra("nobuttons", true);
                    intent.putExtra("paused", route.Paused);

                    startActivity(intent);
                }
            });

            Point currentPoint = LIBOS.getMapTileFromCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude(), currentZoom);
            int currentKey = ((currentZoom << currentZoom) + point.x << currentZoom) + point.y;
            String currentTile = String.format("KEY = %s,%s/%s/%s", currentKey, currentZoom, currentPoint.x, currentPoint.y);
            currentTile += "\n" + listRoutes;

            Log.d(TAG, String.format("Key = %s",currentTile));

            TextView keyText = (TextView) findViewById(R.id.mapFoundKey);

            if (mySettings.IsAdministrator || BuildConfig.DEBUG) {
                keyText.setVisibility(View.VISIBLE);
                keyText.setText(currentTile);
            } else {
                keyText.setVisibility(View.GONE);
            }
        }
    }

    public void keyOnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        Log.d(TAG, Global.getInstance().getDb().getSystem().SupportEmail);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{ Global.getInstance().getDb().getSystem().SupportEmail });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Missing Key");

        TextView keyText = (TextView) findViewById(R.id.mapFoundKey);
        String key = keyText.getText().toString();

        intent.putExtra(Intent.EXTRA_TEXT, key);

        try {
            startActivity(Intent.createChooser(intent, "Send message using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, "There are no email clients installed");
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (arrayAdapter != null) {
            stillRunning = true;

            final Handler handlerCheckDetails = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    ArrayList<_Route> rtList = Global.getInstance().getDb().getListCrossCheckedRoutes(key);

                    for (_Route route : routes) {
                        int position = arrayAdapter.getPosition(route);

                        _Route ww = rtList.get(position);
                        route.TilesDownloaded = ww.TilesDownloaded;
                        route.Paused = ww.Paused;
                        route.TotalTiles = ww.TotalTiles;
                        route.Empty = ww.Empty;
                        route.CurrentVersion = ww.CurrentVersion;
                        route.MyVersion = ww.MyVersion;
                        route.Purchased = ww.Purchased;
                        route.Percentage = ww.Percentage;
                    }

                    arrayAdapter.notifyDataSetChanged();

                    if (stillRunning) handlerCheckDetails.postDelayed(this, DELAY);
                }
            };

            Log.d(TAG, "Create runnable");
            handlerCheckDetails.postDelayed(runnable, DELAY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stillRunning = false;
    }

    public void clickSimpleRouteButton(final View view) {
        Log.d(TAG, "clickSimpleRouteButton");

        final String routeGuid = view.getTag().toString();
        final _Route route = Global.getInstance().getDb().getRoute(routeGuid);

        ((Button) view).setVisibility(View.GONE);

        final Library_UI LIBUI = new Library_UI();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (!XPLIBWS.isNetworkAvailable()) {
                    refreshUI(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) view).setVisibility(View.VISIBLE);
                            LIBUI.snackBar(activity, R.string.msg_failed_network);
                        }
                    });
                    return;
                }

                Button update_button = (Button) view.findViewById(R.id.update_button_item);
                update_button.setVisibility(View.GONE);

                ((TextView) ((RelativeLayout) view.getParent().getParent()).findViewById(R.id.route_description_simple)).setText("downloading - please wait");

                new Thread(new Worker_DownloadTiles(routeGuid, new Worker_DownloadTiles.DownloadTilesListener() {
                    @Override
                    public void onSuccess() {
                        refreshUI(null);
                    }

                    @Override
                    public void onFailed() {
                        refreshUI(new Runnable() {
                            @Override
                            public void run() {
                                LIBUI.snackBar(activity, R.string.msg_failed_network);
                            }
                        });
                    }
                })).start();
            }
        };

        Runnable cancelRunnable = new Runnable() {
            @Override
            public void run() {
                refreshUI(null);
            }
        };

        arrayAdapter.setDialogRouteGuid(routeGuid);

        String message = (route.Mbs > 0 ? String.format(getString(R.string.confirm_downloadwaterway) + "\n\n%s Mbs download", route.Mbs) : getString(R.string.confirm_downloadwaterway));
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_downloadwaterway_title), message, runnable, cancelRunnable, activity);
    }

    private void refreshUI(final Runnable runnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.setDialogRouteGuid("");
                arrayAdapter.notifyDataSetChanged();
                if (runnable != null) runnable.run();
            }
        });
    }

    public void closeActivity(View view) {
        stillRunning = false;
        finish();
    }
}