package com.tyctak.canalmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tyctak.map.entities._Route;
import com.tyctak.canalmap.libraries.Library_GG;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_WS;

import java.util.ArrayList;

import static com.tyctak.canalmap.Activity_Main.setRedrawMap;

public class Activity_Routes extends AppCompatActivity {

    final private String TAG = "Activity_Routes";
    final private Activity_Routes activity = this;
    final private Integer DELAY = 1000;

    private class pauseMenu {
        public String PAUSE = "Pause";
        public String RESUME = "Resume";
    }

    final private pauseMenu pm = new pauseMenu();

    private Library_GG LIBGG;
    final Library_UI LIBUI = new Library_UI();

    ListView routeList;
    ArrayAdapter_Routes arrayAdapter;
    ArrayList<_Route> routes;

    Handler handlerCheckDetails;
    Runnable runnable;
    Boolean stillRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_pause);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.entityRoutesTitle);

        LIBGG = new Library_GG(activity);
        arrayAdapter = new ArrayAdapter_Routes(activity, null);

        routeList = (ListView) findViewById(R.id.list_waterways);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header, routeList, false);
        routeList.addHeaderView(header);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                routes = Global.getInstance().getDb().getAllRoutes();
                arrayAdapter = new ArrayAdapter_Routes(activity, routes);
                arrayAdapter.setAllPurchased(Global.getInstance().getDb().getPurchased(Global.getInstance().getDb().getAllRouteGuid()));
                arrayAdapter.setAllPaused(Global.getInstance().getDb().getPaused());

                routeList.setAdapter(arrayAdapter);

                ListView listView = (ListView) findViewById(R.id.list_waterways);
                listView.setVisibility(View.VISIBLE);
            }
        }, 100);

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _Route route = (_Route) parent.getItemAtPosition(position);

                Intent intent = new Intent(Activity_Routes.this, Dialog_Route.class);
                intent.putExtra("routeGuid", route.RouteGuid);
                intent.putExtra("purchased", route.Purchased);
                intent.putExtra("paused", route.Paused);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        stillRunning = true;
        handlerCheckDetails = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                ActionMenuItemView mi = (ActionMenuItemView) findViewById(R.id.pause_action);
                if (mi != null) mi.setVisibility(Global.getInstance().getDb().getIsDownloading() ? View.VISIBLE : View.INVISIBLE);

                arrayAdapter.setAllPurchased(Global.getInstance().getDb().getPurchased(Global.getInstance().getDb().getAllRouteGuid()));
                arrayAdapter.setAllPaused(Global.getInstance().getDb().getPaused());

                ArrayList<_Route> wwList = Global.getInstance().getDb().getAllRoutes();

                boolean changed = false;

                if (routes != null && routeList.getCount() > 0) {
                    for (_Route route : routes) {
                        int position = arrayAdapter.getPosition(route);
                        _Route rs = wwList.get(position);

                        if (!routeSameAs(rs, route)) {
                            route.TilesDownloaded = rs.TilesDownloaded;
                            route.Paused = rs.Paused;
                            route.TotalTiles = rs.TotalTiles;
                            route.Empty = rs.Empty;
                            route.Purchased = rs.Purchased;
                            changed = true;
                        }
                    }
                }

                if (changed) arrayAdapter.notifyDataSetChanged();

                if (stillRunning) handlerCheckDetails.postDelayed(this, DELAY);
            }
        };

        handlerCheckDetails.post(runnable);
    }

    private boolean routeSameAs(_Route source, _Route destination) {
        return (source.Empty == destination.Empty && source.Paused == destination.Paused && source.Purchased == destination.Purchased && source.TilesDownloaded == destination.TilesDownloaded && source.TotalTiles == destination.TotalTiles);
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerCheckDetails.removeCallbacks(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pauseactionbar_waterways, menu);
        MenuItem pause_button = (MenuItem)menu.getItem(0);

        Boolean paused = Global.getInstance().getDb().getPaused();

        if (paused) {
            pause_button.setTitle(pm.RESUME);
            pause_button.setIcon(R.drawable.ic_play_button_white);
        } else {
            pause_button.setTitle(pm.PAUSE);
            pause_button.setIcon(R.drawable.ic_pause_button_white);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        stillRunning = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LIBGG.isReady()) {
            LIBGG.endConnection();
        }
    }

    public void clickPausedButton(View view) {
        final String routeGuid = view.getTag().toString();
        setRedrawMap(true);
        Global.getInstance().getDb().writeTogglePaused(routeGuid);

        ImageButton paused_button = (ImageButton) view.findViewById(R.id.buttonPaused);
        if (paused_button.getId() == R.id.buttonPaused) {
            paused_button.setImageResource(R.drawable.ic_play_button);
        } else {
            paused_button.setImageResource(R.drawable.ic_pause_button);
        }
    }

    public void clickBuyButton(View view) {
        final String routeGuid = view.getTag().toString();

        Button buy_button = (Button) view.findViewById(R.id.buy_button_item);
        buy_button.setVisibility(View.GONE);

        try {
            if (BuildConfig.DEBUG) {
                Global.getInstance().getDb().writeBuyRoute(routeGuid);
                arrayAdapter.notifyDataSetChanged();
            } else if (LIBGG.isNull()) {
                refreshUI(new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activity, R.string.msg_failed_ready2);
                    }
                });
            } else if (LIBGG.isReady()) {
                LIBGG.buyRoute(arrayAdapter, routeGuid, this, null, null);
            } else {
                refreshUI(new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activity, R.string.msg_failed_ready);
                    }
                });
            }
        } catch (Exception e) {
            Log.d(TAG, "ERROR for general Exception when running writeBuyRoute");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pauseRoutes(MenuItem item) {
        setRedrawMap(true);

        if (item.getTitle().equals(pm.PAUSE)) {
            Global.getInstance().getDb().writePaused(true);
            item.setTitle(pm.RESUME);
            item.setIcon(R.drawable.ic_play_button_white);
        } else {
            Global.getInstance().getDb().writePaused(false);
            item.setTitle(pm.PAUSE);
            item.setIcon(R.drawable.ic_pause_button_white);
        }
    }

    public void clickRouteButton(View view) {
        final String routeGuid = view.getTag().toString();
        final _Route route = Global.getInstance().getDb().getRoute(routeGuid);

        Button update_button = (Button) view.findViewById(R.id.update_button_item);
        update_button.setVisibility(View.GONE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();

                if (!XPLIBWS.isNetworkAvailable()) {
                    refreshUI(new Runnable() {
                        @Override
                        public void run() {
                            LIBUI.snackBar(activity, R.string.msg_failed_network);
                        }
                    });

                    return;
                }

                Log.d(TAG, "#1");

                new Thread(new Worker_DownloadTiles(routeGuid, new Worker_DownloadTiles.DownloadTilesListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "#2");
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

        if (route.Empty) {
            String message = (route.Mbs > 0 ? String.format(getString(R.string.confirm_downloadwaterway) + "\n\n%s Mbs download", route.Mbs) : getString(R.string.confirm_downloadwaterway));
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_downloadwaterway_title), message, runnable, cancelRunnable, activity);
        } else {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_updatewaterway_title), getString(R.string.confirm_updatewaterway), runnable, cancelRunnable, activity);
        }
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

    public void showMore(View view) {
        Button moreInfo = (Button) findViewById(R.id.moreInfo);
        LinearLayout moreOptions = (LinearLayout) findViewById(R.id.moreOptions);

        if (moreInfo.getText().equals(getString(R.string.more))) {
            moreInfo.setText(getString(R.string.less));
            moreOptions.setVisibility(View.VISIBLE);
        } else {
            moreInfo.setText(getString(R.string.more));
            moreOptions.setVisibility(View.GONE);
        }
    }
}
