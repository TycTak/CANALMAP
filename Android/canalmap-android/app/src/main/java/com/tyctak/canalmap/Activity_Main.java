package com.tyctak.canalmap;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.map.entities._ShortLocation;
import com.tyctak.map.libraries.Bootstrap;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_ME;
import com.tyctak.canalmap.libraries.Library_OS;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.map.libraries.XP_Library_DB;
import com.tyctak.map.libraries.XP_Library_PK;
import com.tyctak.map.libraries.XP_Library_SC;
import com.tyctak.canalmap.map.LocalMapTileDatabaseProvider;
import com.tyctak.canalmap.map.LocalMapTileProvider;
import com.tyctak.canalmap.map.LocalRotationGestureOverlay;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import static com.tyctak.canalmap.R.id.displayFilterOptions;
import static com.tyctak.canalmap.R.id.server;

//import org.jsoup.helper.StringUtil;

public class Activity_Main extends AppCompatActivity {

    final private String TAG = "Activity_Main";
    final private Activity activityMain = this;

//    private final int START_ENTITY_OVERLAY = 1;
//    private final int END_ENTITY_OVERLAY = 3;
//    private final int START_POI_OVERLAY = 4;
//    private final int END_POI_OVERLAY = 16;

    private final int START_ENTITY_OVERLAY = 15;
    private final int END_ENTITY_OVERLAY = 17;
    private final int START_POI_OVERLAY = 2;
    private final int END_POI_OVERLAY = 14;
    private final int ROTATION_OVERLAY = 1;

    public final static int CHANGE_POI_REQUEST = 0;
    public final static int SELECT_POI_REQUEST = 2;
    public final static int SELECT_POI_DISPLAY = 3;
    public final static int GOTO_GEOPOINT = 4;
    public final static int RESET_FILTER = 5;
    public final static int CHECK_PREMIUM = 6;
    public final static int TICK_FILTER = 7;
    public final static int CHANGE_ACTIVE = 8;

//    private ArrayList<_ShortLocation> entities;

    private Integer MOVEMENT_INTERVAL = 1000;
    private int MAX_IN_POI_LIST = 160;
    private int MAX_IN_ENTITY_LIST = 200;
    private int MAX_ENTITY_LIST = 200;

    private MapView map;
    private IMapController mapController;
    private LocalRotationGestureOverlay rotationGesture;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private static int movePoi;

    private static boolean bound = false;
    private Service_WS mService;
    private Integer[] zoomLevelDistance = new Integer[] {0, 0, 0, 0, 0, 0, 0, 30000, 12000, 8000, 3000, 700, 400, 200, 100, 60, 45, 30};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_map);
        getSupportActionBar().setSubtitle(R.string.subtitle_map);

        drawerLayout = (DrawerLayout) findViewById(R.id.mainLayout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        final XYTileSource tileSource = new XYTileSource(Global.getInstance().getDb().getSystem().TileSourceName, Global.getInstance().getDb().getSystem().MinZoom, Global.getInstance().getDb().getSystem().MaxZoom, Global.getInstance().getDb().getSystem().PixelSize, ".png", new String[] { Global.getInstance().getDb().getSystem().TilesUrl } );

        map = (MapView)findViewById(R.id.map);
        map.setTileProvider(new LocalMapTileProvider(this, tileSource));
        map.setMultiTouchControls(true);
        mapController = map.getController();
        map.setTilesScaledToDpi(true);
        map.setBackgroundColor(Color.TRANSPARENT);

        InitialiseOverlays();

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        ImageView centreMyEntity = (ImageView) findViewById(R.id.centreMyEntity);
        centreMyEntity.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                if (mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive) {
                    _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();

                    if (myEntitySettings.Latitude != 0.0 && myEntitySettings.Longitude != 0.0) {
                        Log.d(TAG, "Lng=" + myEntitySettings.Longitude + "-Lat=" + myEntitySettings.Latitude);
                        mapController.animateTo(new GeoPoint(myEntitySettings.Latitude, myEntitySettings.Longitude));
                    } else {
                        Library_UI LIBUI = new Library_UI();
                        LIBUI.popupMessageDialog("No GPS found", "Please try again later, no GPS location has been found yet", activityMain);
                    }
                }

                return true;
            }
        });

        map.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {
                onMapChange();
                return false;
            }

            public boolean onScroll(final ScrollEvent e) {
                onMapChange();
                return false;
            }
        }, 100));

        // #################################

        final Handler handler = new Handler();

        Runnable gpsMovement = new Runnable() {
            @Override
            public void run() {
                boolean redrawMap = getRedrawMap(true);
                if (redrawMap) {
                    int tempZoomLevel = map.getZoomLevel();
                    mapController.setZoom(0);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mapController.setZoom(tempZoomLevel);
                }

//                LinearLayout networkLayout = (LinearLayout) findViewById(R.id.network_signal_layout);
//                networkLayout.setVisibility(Global.getNetworkAvailable() ? View.VISIBLE : View.GONE);

//                LinearLayout gpsLayout = (LinearLayout) findViewById(gps_signal_layout);
//                ImageView gpsSignal = (ImageView) findViewById(gps_signal);
//                _MySettings mySettings = Global.getInstance().getDb().getMySettings()();
//                int signalDrawableId = getSignalDrawableId(mySettings.Accuracy);
//                gpsSignal.setImageResource(signalDrawableId);

                startEntityThread();

                handler.postDelayed(this, MOVEMENT_INTERVAL);
            }
        };

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        if (mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive) startService(new Intent(MyApp.getContext(), Service_GP.class));

        mapController.setZoom(mySettings.LastZoomLevel);
        mapController.setCenter(new GeoPoint(mySettings.CentreLatitude, mySettings.CentreLongitude));
        setCentreMyEntity(mySettings, mySettings.CentreMyEntity);

//        Global.getLIBDB().writeSendPosition(false);

        handler.postDelayed(gpsMovement, 500);

        final Handler delayHandler = new Handler();
        delayHandler.post(new Runnable() {
            @Override
            public void run() {
                map.invalidate();

                _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                _Entity myEntitySettings = Global.getInstance().getDb().getMyEntitySettings();
                Library LIB = new Library();

                Calendar instance = Calendar.getInstance();
                Date now = XP_Library_CM.now();
                instance.setTime(now);
                instance.add(Calendar.SECOND, -330);

                if (new Date(mySettings.StoppedTrigger).before(instance.getTime()) && !myEntitySettings.Status.equals(_Entity.enmStatus.NotMoving)) {
                    Global.getInstance().getDb().writeEntityStatus(myEntitySettings.EntityGuid, _Entity.enmStatus.NotMoving);
                }

                delayHandler.postDelayed(this, 5000);
            }
        });

        ImageButton imgB = (ImageButton) findViewById(server);
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        imgB.setImageResource((mySettings.SendPosition ? R.drawable.ic_server : (XPLIBSC.isPremium(mySettings) ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled)));
        imgB.setVisibility((mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive ? View.VISIBLE : View.GONE));

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                XP_Library_WS XPLIBWS = new XP_Library_WS();
//                boolean isNetworkAvailable = XPLIBWS.isNetworkAvailable();
//                Global.setNetworkAvailable(isNetworkAvailable);
//            }
//        }).start();

        FrameLayout frame = (FrameLayout) findViewById(R.id.mapfunctions);
        frame.setVisibility(View.VISIBLE);

        setDisabled(XPLIBSC.isPremium(mySettings));

        setAppStyle(Bootstrap.getInstance().getAppStyle());

                //ImageButton filterpoi = (ImageButton) findViewById(R.id.filterpoi);
                //filterpoi.setImageResource(mySettings.FilterPoi ? R.drawable.ic_filterpoi_on : R.drawable.ic_filterpoi_off);
//        ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(2)

//        rotationGesture.onRotate(45);

    }

    private final Handler rotationHandler = new Handler() {
        public void handleMessage(Message msg) {
            rotateMap(msg.arg1);
        }
    };

    private void rotateMap(int angle) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.compass);

        if (angle == 0) {
            ll.setVisibility(View.GONE);
            ll.setRotation(angle);
            Log.d("Angle", "angle fixed");
        } else {
            ll.setVisibility(View.VISIBLE);
            ll.setRotation(angle);
            Log.d("Angle", "angle " + angle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }

    private void setDisabledMenu(boolean isPremium) {
        MenuItem socialNetwork = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.entities);
        MenuItem mySocialProfile = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.entitysettingsmenu);

        if (isPremium) {
            changeColor(socialNetwork, Color.BLACK);
            changeColor(mySocialProfile, Color.BLACK);
        } else {
            changeColor(socialNetwork, Color.LTGRAY);
            changeColor(mySocialProfile, Color.LTGRAY);
        }
    }

    private void setDisabled(boolean isPremium) {
        MenuItem socialNetwork = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.entities);
        MenuItem mySocialProfile = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.entitysettingsmenu);

        ImageButton filterButton = (ImageButton) findViewById(R.id.filter);
        ImageButton serverButton = (ImageButton) findViewById(R.id.server);

        if (isPremium) {
            filterButton.setImageResource(R.drawable.ic_filter_none);
            serverButton.setImageResource(R.drawable.ic_server_waiting);

            changeColor(socialNetwork, Color.BLACK);
            changeColor(mySocialProfile, Color.BLACK);
        } else {
            filterButton.setImageResource(R.drawable.ic_filter_none_disabled);
            serverButton.setImageResource(R.drawable.ic_server_waiting_disabled);

            changeColor(socialNetwork, Color.LTGRAY);
            changeColor(mySocialProfile, Color.LTGRAY);
        }
    }

    private void setAppStyle(String appStyle) {
        if (appStyle.equals("AGENT")) {
            MenuItem socialNetwork = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.entities);
            MenuItem myRoutes = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.waterways_covered);
            MenuItem premium = ((Menu) ((NavigationView) findViewById(R.id.navigationSidebar)).getMenu()).findItem(R.id.premiummenu);

            socialNetwork.setVisible(false);
            myRoutes.setVisible(false);
            premium.setVisible(false);

            ImageButton filterPrivate = (ImageButton) findViewById(R.id.filterpoi_created);
            filterPrivate.setVisibility(View.GONE);

            ImageButton server = (ImageButton) findViewById(R.id.server);
            server.setVisibility(View.GONE);

            ImageButton filter = (ImageButton) findViewById(R.id.filter);
            filter.setVisibility(View.GONE);
        }
    }

    private void changeColor(MenuItem item, int color) {
        SpannableString s1 = new SpannableString(item.getTitle());
        s1.setSpan(new ForegroundColorSpan(color), 0, s1.length(), 0);
        item.setTitle(s1);
    }

    public void pointNorth(View view) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout ll = (LinearLayout) findViewById(R.id.compass);
                ll.setVisibility(View.GONE);
                ll.setRotation(0);
                map.setMapOrientation(0);
            }
        }, 100);
    }

    private void onMapChange() {
        BoundingBox bb = map.getProjection().getBoundingBox();
        GeoPoint currentCenter = bb.getCenter();
        Global.getInstance().getDb().writeCentreLocation(currentCenter.getLongitude(), currentCenter.getLatitude());
        final _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.LastZoomLevel != map.getZoomLevel()) {
            Global.getInstance().getDb().writeZoomLevel(map.getZoomLevel());
        }

        startPoiThread(mySettings.LastZoomLevel, map.getZoomLevel(), getFilterPoi(), "");

        Log.d(TAG, "Lat=" + bb.getLatitudeSpan() + " Lng=" + bb.getLongitudeSpan());
    }

    private static boolean redrawMap = false;

    public static synchronized boolean getRedrawMap(boolean reset) {
        boolean retval = redrawMap;
        if (reset) redrawMap = false;
        return retval;
    }

    public static synchronized void setRedrawMap(boolean value) {
        LocalMapTileDatabaseProvider.clearBlankTiles();
        redrawMap = value;
    }

    //#################################################################################

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESET_FILTER && getFilterPoi() == XP_Library_DB.enmFilterPoi.JustItems) {
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            poiWhereStatement = "";
            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        }

        if (requestCode == RESET_FILTER && getFilterPoi() == XP_Library_DB.enmFilterPoi.New) {
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            poiWhereStatement = "";
            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle extras = data.getExtras();

        if (extras != null) {
            String action = extras.getString("Action");

            if (requestCode == CHANGE_ACTIVE) {
                resetSendPosition();
            } else if (requestCode == TICK_FILTER) {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                resetFilterPoi(XP_Library_DB.enmFilterPoi.Tick);
                String iconNames = extras.getString("iconnames");
//                Global.getLIBDB().writeFilterPoi(mySettings.FilterPoi);
                poiWhereStatement = null;
                startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), iconNames);
            } else if (requestCode == SELECT_POI_REQUEST || (requestCode == CHANGE_POI_REQUEST && action != null && !action.equals("delete"))) {
                Intent intent = null;

                if (action != null && action.equals("changeicon")) {
                    intent = new Intent(activityMain, Dialog_PoiChangeIcon.class);
                    intent.putExtra("PoiId", extras.getInt("PoiId"));

                    startActivityForResult(intent, CHANGE_POI_REQUEST);
                } else if (action != null && action.equals("moveicon")) {
                    FrameLayout frame1 = (FrameLayout) findViewById(R.id.mapfunctions);
                    FrameLayout frame2 = (FrameLayout) findViewById(R.id.moveFunctions);
                    map.setBackgroundColor(Color.TRANSPARENT);
                    frame1.setVisibility(View.GONE);
                    frame2.setVisibility(View.VISIBLE);

                    movePoi = extras.getInt("PoiId");
                    _PoiLocation poi = Global.getInstance().getDb().getPoiLocation(movePoi);
                    mapController.animateTo(new GeoPoint(poi.Latitude, poi.Longitude));
                } else {
                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                    XP_Library_SC XPLIBSC = new XP_Library_SC();
                    if (!XPLIBSC.isPremium(mySettings)) {
                        intent = new Intent(activityMain, Activity_Premium.class);
                        intent.putExtra("premiumreason", "Allows you to add your own personal markers or symbols to the canal map. These can be filtered using one of the filter options on the main map screen.");
                        startActivity(intent);
                    } else {
                        intent = new Intent(activityMain, Dialog_PoiEdit.class);
                        intent.putExtra("Selected", action);
                        intent.putExtra("GeoPoint", extras.getDoubleArray("GeoPoint"));

                        startActivityForResult(intent, CHANGE_POI_REQUEST);
                    }
                }
            } else if (requestCode == SELECT_POI_DISPLAY && action != null && !action.equals("delete")) {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                XP_Library_SC XPLIBSC = new XP_Library_SC();
                if (!XPLIBSC.isPremium(mySettings)) {
                    Intent intent = new Intent(activityMain, Activity_Premium.class);
                    intent.putExtra("premiumreason", "Allows you to add your own personal markers or symbols to the canal map. These can be filtered using one of the filter options on the main map screen.");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activityMain, Dialog_PoiEdit.class);
                    intent.putExtra("Selected", action);
                    intent.putExtra("GeoPoint", extras.getDoubleArray("GeoPoint"));
                    startActivityForResult(intent, CHANGE_POI_REQUEST);
                }
            } else if (requestCode == SELECT_POI_DISPLAY && action == null) {
                Intent intent = new Intent(activityMain, Dialog_PoiEdit.class);
                intent.putExtra("PoiId", extras.getInt("PoiId"));
                startActivityForResult(intent, CHANGE_POI_REQUEST);
            } else if (requestCode == GOTO_GEOPOINT) {
                //double[] geoPoints = extras.getDoubleArray("geopoint");
                String entityGuid = extras.getString("entityguid");
                _Entity entity = Global.getInstance().getDb().getEntity(entityGuid);
                int zoom = extras.getInt("zoom", 15);
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                if (mySettings.CentreMyEntity == _MySettings.enmCentreMyEntity.following) {
                    setCentreMyEntity(mySettings, _MySettings.enmCentreMyEntity.active);
                }

                mapController.setZoom(zoom);
                mapController.animateTo(new GeoPoint(entity.Latitude, entity.Longitude));

                mySettings.Filter = _MySettings.enmFilter.all;
                Global.getInstance().getDb().writeFilter(mySettings.Filter.ordinal());

                ImageButton filterImage = (ImageButton) findViewById(R.id.filter);
                changeFilter(false, mySettings.Filter, filterImage);

            } else if (requestCode == CHANGE_POI_REQUEST || requestCode == SELECT_POI_DISPLAY && action.equals("delete")) {
                int poiId = extras.getInt("PoiId");

                if (poiId >= 0) {
                    int minZoomLevel = Global.getInstance().getDb().getMinPoiLocationZoomLevel(poiId);

                    if (map.getZoomLevel() < minZoomLevel) {
                        _PoiLocation poi = Global.getInstance().getDb().getPoiLocation(poiId);
                        mapController.setCenter(new GeoPoint(poi.Latitude, poi.Longitude));
                        mapController.setZoom(minZoomLevel);
                    } else {
                        minZoomLevel = map.getZoomLevel();
                    }

                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                    startPoiThread(mySettings.LastZoomLevel, minZoomLevel, getFilterPoi(), "");
                }
            }
        } else if (requestCode == CHECK_PREMIUM) {
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            //ImageView filterpoi = (ImageView) findViewById(R.id.filterpoi);
            //filterpoi.setImageResource(mySettings.FilterPoi ? R.drawable.ic_filterpoi_on : R.drawable.ic_filterpoi_off);

            ImageView serverButton = (ImageView) findViewById(R.id.server);
            XP_Library_SC XPLIBSC = new XP_Library_SC();
            serverButton.setImageResource(mySettings.SendPosition ? R.drawable.ic_server : (XPLIBSC.isPremium(mySettings) ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled));
            poiWhereStatement = null;

            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        }
    }

    //#########################################################################################

    private void InitialiseOverlays() {
        if (map.getOverlays().size() == 0) {
            Log.d(TAG, "InitialiseOverlays");

            MapEventsReceiver currentMapReceive = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    Library_OS LIBOS = new Library_OS();
                    FrameLayout frame = (FrameLayout) findViewById(R.id.mapfunctions);

                    if (!LIBOS.isRoutePurchased(p)) {
                        InvokeSimpleRoutes(p);
                    } else if (frame.getVisibility() == View.VISIBLE) {
                        Log.d(TAG,"singleTapConfirmedHelper START");

                        GeoPoint p1 = new GeoPoint(Global.getInstance().getDb().roundDown(p.getLatitude()), Global.getInstance().getDb().roundDown(p.getLongitude()));

                        ArrayList<Integer> selectedPois = new ArrayList<>();
                        ArrayList<String> selectedEntities = new ArrayList<>();

                        for (int o = START_POI_OVERLAY; o <= END_POI_OVERLAY; o++) {
                            int maxPois = getOverlay(o).size(); //(getOverlay(o).size() > MAX_IN_POI_LIST ? MAX_IN_POI_LIST : getOverlay(o).size());

                            for (int i = 0; i < maxPois; i++) {
                                GeoPoint point = (GeoPoint) getOverlay(o).getItem(i).getPoint();

                                int distance = (int) point.distanceTo(p1);
                                if (distance < zoomLevelDistance[map.getZoomLevel()]) {
                                    int id = Integer.parseInt(getOverlay(o).getItem(i).getSnippet());
                                    selectedPois.add(id);
                                }
                            }
                        }

                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                        ArrayList<_ShortLocation> temp = Global.getInstance().getDb().getChangedEntities(mySettings.CentreMyEntity == _MySettings.enmCentreMyEntity.inactive, mySettings.Filter, mySettings.EntityGuid, map.getBoundingBox().getLatNorth(), map.getBoundingBox().getLonWest(), map.getBoundingBox().getLatSouth(), map.getBoundingBox().getLonEast());

                        if (temp != null) {
                            int maxEntities = (temp.size() > MAX_ENTITY_LIST ? MAX_ENTITY_LIST : temp.size());

                            for (int i = 0; i < maxEntities; i++) {
                                _ShortLocation entity = temp.get(i);
                                GeoPoint point = new GeoPoint(entity.Latitude, entity.Longitude);
                                int distance = point.distanceTo(p1);
                                if (distance < zoomLevelDistance[map.getZoomLevel()]) {
                                    selectedEntities.add(entity.Id);
                                }
                            }
                        }

                        Log.d(TAG,"singleTapConfirmedHelper END");

                        if (selectedPois.size() == 0 && selectedEntities.size() == 0) {
                            Intent intent = new Intent(activityMain, Dialog_PoiSelect.class);
                            intent.putExtra("geopoint", new double[]{p1.getLatitude(), p1.getLongitude()});
                            startActivityForResult(intent, SELECT_POI_REQUEST);
                        } else {
                            Intent intent = new Intent(activityMain, Dialog_PoiList.class);
                            intent.putIntegerArrayListExtra("pois", selectedPois);
                            intent.putStringArrayListExtra("entities", selectedEntities);
                            intent.putExtra("geopoint", new double[]{p1.getLatitude(), p1.getLongitude()});
                            startActivityForResult(intent, SELECT_POI_DISPLAY);
                        }
                    }

                    return false;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    FrameLayout frame = (FrameLayout) findViewById(R.id.mapfunctions);

                    if (frame.getVisibility() == View.VISIBLE) {
                        Log.d(TAG, "longPressHelper");
                        InvokeSimpleRoutes(p);
                    }

                    return false;
                }
            };

            MapEventsOverlay OverlayEvents = new MapEventsOverlay(activityMain, currentMapReceive);
            map.getOverlays().add(OverlayEvents);
        }

        if (map.getOverlays().size() == 1) {
            rotationGesture = new LocalRotationGestureOverlay(map, rotationHandler);
            rotationGesture.setEnabled(true);
            map.getOverlays().add(this.rotationGesture);
        }

        if (map.getOverlays().size() == 2) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 3) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 4) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 5) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 6) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 7) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 8) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 9) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 10) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 11) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 12) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 13) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 14) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 15) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 16) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }

        if (map.getOverlays().size() == 17) {
            map.getOverlays().add(new ItemizedOverlayWithFocus<OverlayItem>(this, new ArrayList<OverlayItem>(), null));
        }
    }

    private ItemizedIconOverlay getOverlay(int overlay) {
        return ((ItemizedIconOverlay) (map.getOverlays().get(overlay)));
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");

        suspendIfRequired();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        Log.d(TAG, "BEFORE overlay count = " + map.getOverlays().size());

        InitialiseOverlays();

        Log.d(TAG, "AFTER overlay count = " + map.getOverlays().size());

        Library_GR LIBGR = new Library_GR();
        LIBGR.initialiseGraphics(activityMain);

        resumeIfRequired();
    }

    private synchronized void startEntityThread() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (map.getOverlays().size() > 0) {
                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                        ArrayList<_ShortLocation> temp = Global.getInstance().getDb().getChangedEntities(mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive,  mySettings.Filter, mySettings.EntityGuid, map.getBoundingBox().getLatNorth(), map.getBoundingBox().getLonWest(), map.getBoundingBox().getLatSouth(), map.getBoundingBox().getLonEast());
                        addAllForMapLocation(33, START_ENTITY_OVERLAY, END_ENTITY_OVERLAY, temp, mySettings.LastZoomLevel, mySettings.LastZoomLevel, MAX_IN_ENTITY_LIST);

                        if (mySettings.CentreMyEntity == _MySettings.enmCentreMyEntity.following) {
                            final _Entity myEntity = Global.getInstance().getDb().getMyEntitySettings();

                            if (myEntity.Latitude != 0.0) {
//                                BoundingBox bb = map.getBoundingBox();
//                                Double newLatitude = ((bb.getLatNorth() - bb.getLatSouth()) / 100 * 20);
//                                mapController.animateTo(new GeoPoint((myEntity.Latitude + newLatitude), myEntity.Longitude));

                                mapController.animateTo(new GeoPoint(myEntity.Latitude, myEntity.Longitude));

//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        map.setMapOrientation(360 - myEntity.Direction);
//                                    }
//                                });
                            } else if (map.getMapOrientation() != 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        map.setMapOrientation(0);
                                    }
                                });
                            }
                        }
                    }
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String poiWhereStatement = null;

    private synchronized void startPoiThread(final int lastZoomLevel, final int currentZoomLevel, final XP_Library_DB.enmFilterPoi filterPoi, final String names) {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
//                    Log.d(TAG, "Test zoom " + currentZoomLevel);
                    XP_Library_CM LIBCM = new XP_Library_CM();
                    if (LIBCM.isBlank(poiWhereStatement)) poiWhereStatement = Global.getInstance().getDb().getFilterPoiWhereStatement(filterPoi, names, Global.getInstance().getDb().getMyEntityGuid());
                    ArrayList<_ShortLocation> temp = Global.getInstance().getDb().getPoiLocations(map.getBoundingBox().getLatNorth(), map.getBoundingBox().getLonWest(), map.getBoundingBox().getLatSouth(), map.getBoundingBox().getLonEast(), currentZoomLevel, poiWhereStatement, Global.getInstance().getDb().getMyEntityGuid());
                    Log.d(TAG, "temp = " + temp.size());
                    addAllForMapLocation(7, START_POI_OVERLAY, END_POI_OVERLAY, temp, lastZoomLevel, currentZoomLevel, MAX_IN_POI_LIST);
                }
            });

            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service_WS.LocalBinder binder = (Service_WS.LocalBinder) service;
            mService = binder.getService();
            bound = true;

            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            ImageButton filterImage = (ImageButton) findViewById(R.id.filter);
            changeFilter(false, mySettings.Filter, filterImage);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        Intent intent = new Intent(this, Service_WS.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        resetSendPosition();
    }

    private void resetSendPosition() {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        ImageButton sendToServerButton = (ImageButton) findViewById(server);
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        sendToServerButton.setImageResource((mySettings.SendPosition ? R.drawable.ic_server : (XPLIBSC.isPremium(mySettings) ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled)));
    }

    private void InvokeSimpleRoutes(GeoPoint p) {
        Intent intent = new Intent(activityMain, Dialog_SimpleRoutes.class);
        intent.putExtra("zoom", map.getZoomLevel());
        intent.putExtra("geopoint", new double[]{p.getLatitude(), p.getLongitude()});
        startActivityForResult(intent, 0);
    }

    public void sendPosition(View view) {
        final _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        final XP_Library_SC XPLIBSC = new XP_Library_SC();
        if (!XPLIBSC.isPremium(mySettings)) {
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", "Allows you to send your current location to other users so that they can see where you are.");
            startActivity(intent);
        } else {
            _Entity myEntity = Global.getInstance().getDb().getMyEntitySettings();
//            final _MySettings mySettings = Global.getInstance().getDb().getMySettings()();

            final Library_UI LIBUI = new Library_UI();
            XP_Library_CM LIBCM = new XP_Library_CM();

            if (LIBCM.isBlank(myEntity.EntityName)) {
                LIBUI.confirmationDialogYesNo(getString(R.string.need_to_supply_name_title), getString(R.string.need_to_supply_name_description), new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(activityMain, Activity_EntitySettings.class);
                        startActivityForResult(intent, CHANGE_ACTIVE);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activityMain, "You need a name to publish your position");
                    }
                }, activityMain);
            } else if (!Global.getInstance().getDb().getPurchased()) {
                LIBUI.confirmationDialogYesNo("Buy App", "Sorry but before you make your position known to others you must subscribe to this app, do you want to do that now?", new Runnable() {
                    @Override
                    public void run() {
                        InvokeSimpleRoutes(new GeoPoint(mySettings.CentreLatitude, mySettings.CentreLongitude));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activityMain, "You must subscribe to app before sending position");
                    }
                }, activityMain);
            } else if (myEntity.Latitude == 0.0) {
//            Library_UI LIBUI = new Library_UI();
                LIBUI.popupMessageDialog("No GPS found", "Please try again later, no GPS location has been found yet", activityMain);
            } else {
                if (!myEntity.IsActive && !mySettings.SendPosition) {
                    LIBUI.confirmationDialogYesNo("Make My Profile Visible", "You need to make your profile visible to others to send your location, do you want to do that now?", new Runnable() {
                        @Override
                        public void run() {
                            Global.getInstance().getDb().writeMyEntityIsActive(Global.getInstance().getDb().getMyEntityGuid(), true);

                            sendPosition(mySettings.SendPosition, XPLIBSC.isPremium(mySettings));
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            LIBUI.snackBar(activityMain, "You need to make your profile visible");
                        }
                    }, activityMain);
                } else {
                    sendPosition(mySettings.SendPosition, XPLIBSC.isPremium(mySettings));
                }

//                mySettings = Global.getInstance().getDb().getMySettings()();
//
//                mySettings.SendPosition = !mySettings.SendPosition;
//                Global.getLIBDB().writeSendPosition(mySettings.SendPosition);
//
//                ImageButton sendToServerButton = (ImageButton) findViewById(server);
//                sendToServerButton.setImageResource((mySettings.SendPosition ? R.drawable.ic_server : (mySettings.IsPremium ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled)));
//
//                if (mySettings.SendPosition) {
//                    LIBUI.snackBar(activityMain, "Others can now see your position");
//                } else {
//                    Global.getLIBDB().writeStopSendPosition();
//                    LIBUI.snackBar(activityMain, "Your position will now be hidden from others");
//                }
            }
        }
    }

    private void sendPosition(boolean sendPositon, boolean isPremium) {
        Library_UI LIBUI = new Library_UI();

        sendPositon = !sendPositon;
        Global.getInstance().getDb().writeSendPosition(sendPositon);

        ImageButton sendToServerButton = (ImageButton) findViewById(server);
        sendToServerButton.setImageResource((sendPositon ? R.drawable.ic_server : (isPremium ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled)));

        if (sendPositon) {
            LIBUI.snackBar(activityMain, "Others can now see your position");
        } else {
            LIBUI.snackBar(activityMain, "Your position will now be hidden from others");
        }
    }

    private class shortLocationItem {
        public shortLocationItem(int overlay, OverlayItem item, int index) {
            Overlay = overlay;
            Item = item;
            Index = index;
        }

        int Overlay;
        OverlayItem Item;
        int Index;
    }

    private void addAllForMapLocation(int keyLength, final int startOverlayIndex, final int endOverlayIndex, ArrayList<_ShortLocation> source, int lastLevel, int currentLevel, int maxInList) {
        Library_OS LIBOS = new Library_OS();

        boolean levelChanged = false; //LIBGR.getIsLevelChanged(lastLevel, currentLevel);
        final ArrayList<int[]>[] state = mergeOverlayList(keyLength, startOverlayIndex, endOverlayIndex, source, levelChanged, maxInList);

        final LinkedList<shortLocationItem> items = new LinkedList<>();

        // INSERT
        if (state[0].size() > 0) {
            for (int i = 0; i < state[0].size(); i++) {
                try {
                    int[] index = state[0].get(i);

                    _ShortLocation shortLocation = source.get(index[1]);

                    OverlayItem item = LIBOS.getOverlayItem(activityMain, shortLocation, map.getZoomLevel());
                    items.add(new shortLocationItem(index[0], item, -1));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //UPDATE
        if (state[2].size() > 0) {
            for (int i = 0; i < state[2].size(); i++) {
                try {
                    int[] index = state[2].get(i);

                    _ShortLocation shortLocation = source.get(index[2]);

                    OverlayItem item = LIBOS.getOverlayItem(activityMain, shortLocation, map.getZoomLevel());
                    items.add(new shortLocationItem(index[0], item, index[1]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //REMOVE
        if (state[1].size() > 0) {
            for (int i = 0; i < state[1].size(); i++) {
                try {
                    int[] index = state[1].get(i);
                    items.add(new shortLocationItem(index[0], null, index[1]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (items.size() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Library LIB = new Library();
                    long tme = XP_Library_CM.getDate(XP_Library_CM.now());

                    for (int i = 0; i < items.size(); i++) {
                        shortLocationItem item = items.get(i);

                        try {
                            if (item.Item != null && item.Index == -1) {
                                Log.d(TAG, tme + " add #" + item.Overlay + ":" + item.Index);
                                getOverlay(item.Overlay).addItem(item.Item);
                            } else if (item.Item == null && item.Index >= 0) {
                                Log.d(TAG, tme + " remove #" + item.Overlay + ":" + item.Index);
                                getOverlay(item.Overlay).removeItem(item.Index);
                            } else {
//                            try {
                                Log.d(TAG, tme + " update #" + item.Overlay + ":" + item.Index);
                                getOverlay(item.Overlay).removeItem(item.Index);
                                getOverlay(item.Overlay).addItem(item.Index, item.Item);
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    map.invalidate();
                }
            });
        }

        source.clear();
    }

//    class FishNameComparator implements Comparator<int[]>
//    {
////                return left.name.compareTo(right.name);
//        }
//    }

    private ArrayList<int[]>[] mergeOverlayList(int keyLength, int startOverlayIndex, int endOverlayIndex, ArrayList<_ShortLocation> source, boolean levelChanged, int maxInList) {
        ArrayList<int[]> update = new ArrayList<>();
        ArrayList<int[]> insert = new ArrayList<>();
        ArrayList<int[]> remove = new ArrayList<>();

        StringBuilder dstKey = new StringBuilder();
        ArrayList<StringBuilder> ovrKey = new ArrayList<>();
        StringBuilder srcKey = new StringBuilder();

        for (int o = startOverlayIndex; o <= endOverlayIndex; o++) {
            ItemizedIconOverlay destination = getOverlay(o);
            StringBuilder temp = new StringBuilder();

            for (int i = (destination.size() - 1); i >= 0; i--) {
                OverlayItem oi = destination.getItem(i);
                temp.append(oi.getSnippet() + ";");
            }

            dstKey.append(temp);
            ovrKey.add(temp);
        }

//        int max = endOverlayIndex - startOverlayIndex;

        for (int i = 0; i < source.size() ; i++) {
            String key = source.get(i).Id + ";";

            srcKey.append(key);
            int index = dstKey.indexOf(key);

            if (index == -1) {
                int o = 0;

                for (o = startOverlayIndex; o <= endOverlayIndex; o++) {
                    if (((ovrKey.get(o - startOverlayIndex).length() / keyLength) % maxInList) == 0) {
                        break;
                    }
                }

                insert.add(new int[] {o, i, -1});
            }
        }

        for (int o = startOverlayIndex; o <= endOverlayIndex; o++) {
            int destinationSize = (ovrKey.get(o - startOverlayIndex).length() / keyLength);

            for (int i = (destinationSize - 1); i >= 0; i--) {
                ItemizedIconOverlay destination = getOverlay(o);

                OverlayItem oi = destination.getItem(i);
                String key = oi.getSnippet() + ";";
                int index = srcKey.indexOf(key);

                if (index == -1) {
                    remove.add(new int[]{o, i, -1});
                } else {
                    update.add(new int[]{o, i, (index / keyLength)});
                }
            }
        }

        return new ArrayList[] { insert, remove, update };
    }

//    private void setGpsSignal(int value) {
//        ImageView accuracy = (ImageView) findViewById(R.id.gps_signal);
//        accuracy.setImageResource(getSignalDrawableId(value));
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void entitySettingsOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Reset);
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", "Allows you to edit your Social Profile and choose to share with other users on the Canal Social Network.");
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Activity_EntitySettings.class);
            startActivityForResult(intent, CHANGE_ACTIVE);
        }
    }

    public void premiumOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_Premium.class);
        startActivity(intent);
//        startActivityForResult(intent, CHECK_PREMIUM);
    }

    public void mySettingsOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_MySettings.class);
        startActivity(intent);
    }

    public void aboutUsOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_AboutUs.class);
        startActivityForResult(intent, CHECK_PREMIUM);
    }

    public void entitiesOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", getString(R.string.premium_explanation_showentities));
            startActivity(intent);
        } else {
            Intent intent = new Intent(activityMain, Activity_Entities.class);
            startActivityForResult(intent, GOTO_GEOPOINT);
        }
    }

    public void routesCoveredOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_Routes.class);
        startActivity(intent);
    }

    public void symbolsOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_Symbols.class);
        startActivityForResult(intent, RESET_FILTER);
    }

    public void markersOnClick(MenuItem menu) {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(this, Activity_Markers.class);
        startActivityForResult(intent, RESET_FILTER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapactionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private int getSignalDrawableId(int accuracy) {
        int retval;

        if (accuracy == 0) {
            retval = R.drawable.ic_signal0;
        } else if (accuracy < 4) {
            retval = R.drawable.ic_signal5;
        } else if (accuracy < 8) {
            retval = R.drawable.ic_signal4;
        } else if (accuracy < 14) {
            retval = R.drawable.ic_signal3;
        } else if (accuracy < 20) {
            retval = R.drawable.ic_signal2;
        } else  if (accuracy < 30) {
            retval = R.drawable.ic_signal1;
        } else {
            retval = R.drawable.ic_signal0;
        }

        return retval;
    }

    public void centreMyEntityOnClick(View view) {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.CentreMyEntity == _MySettings.enmCentreMyEntity.active) {
            setCentreMyEntity(mySettings, _MySettings.enmCentreMyEntity.following);
        } else if (mySettings.CentreMyEntity == _MySettings.enmCentreMyEntity.following) {
            stopService(new Intent(MyApp.getContext(), Service_GP.class));
            setCentreMyEntity(mySettings, _MySettings.enmCentreMyEntity.inactive);
        } else {
            startService(new Intent(MyApp.getContext(), Service_GP.class));
            setCentreMyEntity(mySettings, _MySettings.enmCentreMyEntity.active);
        }

        Global.getInstance().getDb().writeChangedEntity(mySettings.EntityGuid, true);
    }

    private void suspendIfRequired() {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive && !mySettings.SendPosition) {
            stopService(new Intent(MyApp.getContext(), Service_GP.class));
        }
    }

    private void resumeIfRequired() {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.CentreMyEntity != _MySettings.enmCentreMyEntity.inactive && !mySettings.SendPosition) {
            startService(new Intent(MyApp.getContext(), Service_GP.class));
        }
    }

    private void setCentreMyEntity(_MySettings mySettings, _MySettings.enmCentreMyEntity changeTo) {
        ImageButton centreMyEntity = (ImageButton) findViewById(R.id.centreMyEntity);
        ImageButton serverButton = (ImageButton) findViewById(R.id.server);
        LinearLayout gps_signal_layout = (LinearLayout) findViewById(R.id.gps_signal_layout);
        ImageView gps_signal = (ImageView) findViewById(R.id.gps_signal);

        int visible = (changeTo == _MySettings.enmCentreMyEntity.inactive ? View.GONE : View.VISIBLE);
        int centreMyEntityIcon = (changeTo == _MySettings.enmCentreMyEntity.inactive ? R.drawable.ic_location_waiting : changeTo == _MySettings.enmCentreMyEntity.active ? R.drawable.ic_location : R.drawable.ic_location_follow);

        Global.getInstance().getDb().writeCentreMyEntity(mySettings.EntityGuid, changeTo);
        centreMyEntity.setImageResource(centreMyEntityIcon);
        int signalDrawableId = getSignalDrawableId(mySettings.Accuracy);
        gps_signal_layout.setVisibility(visible);
        gps_signal.setImageResource(signalDrawableId);
        serverButton.setVisibility(visible);
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        serverButton.setImageResource(mySettings.SendPosition ? R.drawable.ic_server : (XPLIBSC.isPremium(mySettings) ? R.drawable.ic_server_waiting : R.drawable.ic_server_waiting_disabled));

        if (changeTo == _MySettings.enmCentreMyEntity.inactive) {
            Global.getInstance().getDb().writeSendPosition(false);
        } else if (mySettings.CentreMyEntity == changeTo) {
            Global.getInstance().getDb().writeSendPosition(mySettings.SendPosition);
        }
    }

    public void changeFilter(View view) {
        final _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", getString(R.string.premium_explanation_filter));
            startActivity(intent);
        } else {
            ImageButton filterImage = (ImageButton) findViewById(R.id.filter);
            changeFilter(true, mySettings.Filter, filterImage);
        }
    }

    public void displayFilterOptions(View view) {
        XP_Library_DB.enmFilterPoi ep = getFilterPoi();

        if (ep != XP_Library_DB.enmFilterPoi.Reset) {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Reset);
        } else {
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            ImageView filterPoiJustItems = (ImageView) findViewById(R.id.filterpoi_justitems);
            ImageView filterPoiCreated = (ImageView) findViewById(R.id.filterpoi_created);
            XP_Library_SC XPLIBSC = new XP_Library_SC();

            if (XPLIBSC.isPremium(mySettings)) {
                filterPoiJustItems.setImageResource(R.drawable.ic_filterpoi_justitems);
                filterPoiCreated.setImageResource(R.drawable.ic_filterpoi_created);
            } else {
                filterPoiJustItems.setImageResource(R.drawable.ic_filterpoi_justitems_disabled);
                filterPoiCreated.setImageResource(R.drawable.ic_filterpoi_created_disabled);
            }

            LinearLayout displayfilterOptions = (LinearLayout) findViewById(displayFilterOptions);
            displayfilterOptions.setVisibility(displayfilterOptions.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

            ImageView filterPoi = (ImageView) findViewById(R.id.filterpoi);
            filterPoi.setImageResource(displayfilterOptions.getVisibility() == View.VISIBLE ? R.drawable.ic_filterpoi_on : R.drawable.ic_filterpoi);
        }
    }

    private XP_Library_DB.enmFilterPoi getFilterPoi() {
        XP_Library_DB.enmFilterPoi retval = XP_Library_DB.enmFilterPoi.Reset;
        ImageView filterPoi = (ImageView) findViewById(R.id.filterpoi);
        int id = (int) (filterPoi.getTag() == null ? R.drawable.ic_filterpoi : filterPoi.getTag());

        if (id == R.drawable.ic_filterpoi_justitems_on) {
            retval = XP_Library_DB.enmFilterPoi.JustItems;
        } else if (id == R.drawable.ic_filterpoi_created_on) {
            retval = XP_Library_DB.enmFilterPoi.Created;
        } else if (id == R.drawable.ic_filterpoi_tick_on) {
            retval = XP_Library_DB.enmFilterPoi.Tick;
        } else if (id == R.drawable.ic_filterpoi_new_on) {
            retval = XP_Library_DB.enmFilterPoi.New;
        } else if (id == R.drawable.ic_filterpoi) {
            retval = XP_Library_DB.enmFilterPoi.Reset;
        }

        return retval;
    }

    private void resetFilterPoi(XP_Library_DB.enmFilterPoi filterPoiType) {
        LinearLayout displayfilterOptions = (LinearLayout) findViewById(displayFilterOptions);
        displayfilterOptions.setVisibility(View.GONE);

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        ImageView filterPoi = (ImageView) findViewById(R.id.filterpoi);

        if (filterPoiType == XP_Library_DB.enmFilterPoi.Reset) {
            filterPoi.setImageResource(R.drawable.ic_filterpoi);
            filterPoi.setTag(R.drawable.ic_filterpoi);
            poiWhereStatement = null;
            Global.getInstance().getDb().writeFilterPoi(false);
            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        } else if (filterPoiType == XP_Library_DB.enmFilterPoi.JustItems) {
            filterPoi.setImageResource(R.drawable.ic_filterpoi_justitems_on);
            filterPoi.setTag(R.drawable.ic_filterpoi_justitems_on);
        } else if (filterPoiType == XP_Library_DB.enmFilterPoi.Created) {
            filterPoi.setImageResource(R.drawable.ic_filterpoi_created_on);
            filterPoi.setTag(R.drawable.ic_filterpoi_created_on);
        } else if (filterPoiType == XP_Library_DB.enmFilterPoi.New) {
            filterPoi.setImageResource(R.drawable.ic_filterpoi_new_on);
            filterPoi.setTag(R.drawable.ic_filterpoi_new_on);
        } else if (filterPoiType == XP_Library_DB.enmFilterPoi.Tick) {
            filterPoi.setImageResource(R.drawable.ic_filterpoi_tick_on);
            filterPoi.setTag(R.drawable.ic_filterpoi_tick_on);
        }
    }

    public void changeFilterPoi(View view) {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Reset);
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", "Allows you to filter out symbols or markers that are not of interest to you, for example not everyone likes Sushi.");
            startActivity(intent);
        } else {
//            mySettings.FilterPoi = !mySettings.FilterPoi;

            if (Global.getInstance().getDb().getFilterPoiWhereStatement(XP_Library_DB.enmFilterPoi.JustItems, "", Global.getInstance().getDb().getMyEntityGuid()).isEmpty()) {
                Library_UI LIBUI = new Library_UI();
                LIBUI.confirmationDialogMarkersSymbolsNo("No POI Filters Created", "You need to specify which markers or symbols you do not want to show on the map", new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(activityMain, Activity_Markers.class);
                        startActivity(intent);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(activityMain, Activity_Symbols.class);
                        startActivity(intent);
                    }
                }, activityMain);
            } else {
                resetFilterPoi(XP_Library_DB.enmFilterPoi.JustItems);
                Global.getInstance().getDb().writeFilterPoi(mySettings.FilterPoi);
                poiWhereStatement = null;

                startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
            }
        }
    }

    public void changeFilterNew(View view) {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Reset);
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", "Allows you to display only symbols or markers that you have created at any level of the map.");
            startActivity(intent);
        } else {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.New);
//            Global.getLIBDB().writeFilterPoi(mySettings.FilterPoi);
            poiWhereStatement = null;

            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        }
    }

    public void changeFilterPoiCreated(View view) {
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        if (!XPLIBSC.isPremium(mySettings)) {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Reset);
            Intent intent = new Intent(activityMain, Activity_Premium.class);
            intent.putExtra("premiumreason", "Allows you to display only symbols or markers that you have created at any level of the map.");
            startActivity(intent);
        } else {
            resetFilterPoi(XP_Library_DB.enmFilterPoi.Created);
//            Global.getLIBDB().writeFilterPoi(mySettings.FilterPoi);
            poiWhereStatement = null;

            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");
        }
    }

    public void changeFilterPoiTick(View view) {
//        _MySettings mySettings = Global.getInstance().getDb().getMySettings()();
//
//        if (!mySettings.IsPremium) {
//            resetFilterPoi(enmFilterPoi.Reset);
//            Intent intent = new Intent(activityMain, Activity_Premium.class);
//            intent.putExtra("premiumreason", "Invalid");
//            startActivity(intent);
//        } else {
            Intent intent = new Intent();
            intent = new Intent(activityMain, Dialog_FilterPois.class);
            startActivityForResult(intent, TICK_FILTER);
//        }
    }

    private void changeFilter(boolean toggle, _MySettings.enmFilter filter, ImageView filterImage) {
        int resourceId;

        if (toggle) {
            Library_UI LIBUI = new Library_UI();

            if (filter == _MySettings.enmFilter.all) {
                LIBUI.snackBar(activityMain, getString(R.string.filter_entitytraders));
                filter = _MySettings.enmFilter.entitytrader;
            } else if (filter == _MySettings.enmFilter.entitytrader) {
                LIBUI.snackBar(activityMain, getString(R.string.filter_favourites));
                filter = _MySettings.enmFilter.favourite;
            } else if (filter == _MySettings.enmFilter.favourite) {
                LIBUI.snackBar(activityMain, getString(R.string.filter_socialmedia));
                filter = _MySettings.enmFilter.socialmedia;
            } else if (filter == _MySettings.enmFilter.socialmedia) {
                LIBUI.snackBar(activityMain, getString(R.string.filter_none));
                filter = _MySettings.enmFilter.none;
            } else {
                LIBUI.snackBar(activityMain, getString(R.string.filter_all));
                filter = _MySettings.enmFilter.all;
            }

            Global.getInstance().getDb().writeFilter(filter.ordinal());
        }

        if (filter == _MySettings.enmFilter.all) {
            resourceId = R.drawable.ic_filter_all;
        } else if (filter == _MySettings.enmFilter.entitytrader) {
            resourceId = R.drawable.ic_filter_entitytrader;
        } else if (filter == _MySettings.enmFilter.favourite) {
            resourceId = R.drawable.ic_filter_favourite;
        } else if (filter == _MySettings.enmFilter.socialmedia) {
            resourceId = R.drawable.ic_filter_socialmedia;
        } else {
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();
            XP_Library_SC XPLIBSC = new XP_Library_SC();
            setDisabledMenu(XPLIBSC.isPremium(mySettings));
            resourceId = (XPLIBSC.isPremium(mySettings) ? R.drawable.ic_filter_none : R.drawable.ic_filter_none_disabled);
        }

        filterImage.setImageResource(resourceId);

        for (int o = START_ENTITY_OVERLAY; o <= END_ENTITY_OVERLAY; o++) {
            getOverlay(o).removeAllItems();
        }

        map.invalidate();
    }

    public void clickCartTwitter(View view) {
        Library_ME LIBME = new Library_ME();
        if (!LIBME.LoadTwitter(activityMain, "crtnotices")) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activityMain, R.string.twitterError);
        }
    }

    public void actionMovePoi(View view) {
        if (movePoi > 0) {
            BoundingBox bb = map.getProjection().getBoundingBox();
            GeoPoint currentCenter = bb.getCenter();

            _PoiLocation poi = Global.getInstance().getDb().getPoiLocationComplete(movePoi);
            poi.Latitude = Global.getInstance().getDb().roundDown(currentCenter.getLatitude());
            poi.Longitude = Global.getInstance().getDb().roundDown(currentCenter.getLongitude());

            Library LIB = new Library();
            poi.Updated = XP_Library_CM.getDate(XP_Library_CM.now());
            poi.Status = _PoiLocation.enmStatus.Updated;
            poi.ReviewedFeedback = _PoiLocation.enmReviewedFeedback.NotReviewed;

            Global.getInstance().getDb().writePoiLocation(null, poi);
            Global.getInstance().getDb().writeCurrentLocalDate(poi.Updated);
            _MySettings mySettings = Global.getInstance().getDb().getMySettings();

            startPoiThread(mySettings.LastZoomLevel, mySettings.LastZoomLevel, getFilterPoi(), "");

            displayMapAfterMove(view);
        }
    }

    public void displayMapAfterMove(View view) {
        FrameLayout frame1 = (FrameLayout) findViewById(R.id.mapfunctions);
        FrameLayout frame2 = (FrameLayout) findViewById(R.id.moveFunctions);

        frame1.setVisibility(View.VISIBLE);
        frame2.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

        unbindService(mConnection);
        bound = false;

        Log.d(TAG, "onTaskRemoved START");

        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"#2 " + Global.getInstance().getDb());

                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                    if (mySettings.SendPosition) {
                        Log.d(TAG, "Runnable.onTaskRemoved SENDING POSITION START " + mySettings.SendPosition);
                        mySettings.SendPosition = false;
                        Global.getInstance().getDb().writeStopSendPosition(Global.getInstance().getDb().getMyEntityGuid());

                        Log.d(TAG, "Runnable.onTaskRemoved SENDING POSITION STOP");
                    }
                }
            });

            thread.start();
            thread.join();

            Thread sendThread = new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    XP_Library_PK LIBPK = new XP_Library_PK();
                    LIBPK.executeSend();
                }
            });

            sendThread.start();
            sendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
