package com.tyctak.canalmap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Poi;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_SC;

import java.util.ArrayList;

public class Dialog_PoiChangeIcon extends AppCompatActivity {

    private final String TAG = "Dialog_PoiChangeIcon";
    final Activity dialogMarkers = this;

    final public static String MARKERS = "markers";
    final public static String SYMBOLS = "symbols";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_poichangeicon);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int poiId = getIntent().getIntExtra("PoiId", -1);
        _PoiLocation poiLocation = Global.getInstance().getDb().getPoiLocation(poiId);

        Library LIB = new Library();

        String selected = poiLocation.Area;

        if (selected.equals(MARKERS)) {
            getSupportActionBar().setTitle(R.string.markersTitle);
        } else {
            getSupportActionBar().setTitle(R.string.symbolsTitle);
        }

        final double[] points = new double[] { poiLocation.Longitude, poiLocation.Latitude};

        final Library_UI LIBUI = new Library_UI();

        final ImageView markerImage = (ImageView) findViewById(R.id.marker_image);
        markerImage.setImageBitmap(LIB.decodeBinary(poiLocation.Image));
        markerImage.setTag(poiLocation.Id);

        final TextView markerText = (TextView) findViewById(R.id.marker_text);
        markerText.setText(poiLocation.Message);

        int id = this.getResources().getIdentifier(poiLocation.Name, "drawable", this.getPackageName());
        ImageView image = (ImageView) findViewById(R.id.marker_selected);
        image.setImageResource(id);

        markerImage.setVisibility(View.GONE);
        markerText.setVisibility(View.GONE);

        final LinearLayout markerLayout = (LinearLayout) findViewById(R.id.marker_layout);
        final LinearLayout symbolLayout = (LinearLayout) findViewById(R.id.symbol_layout);

        Handler handler = new Handler();

        final _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        final boolean isPremium = XPLIBSC.isPremium(mySettings);

        if (selected.equals(MARKERS)) {
            markerLayout.setVisibility(View.VISIBLE);
            symbolLayout.setVisibility(View.GONE);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mySettings.IsAdministrator) {
                        ArrayList<_Poi> pois = Global.getInstance().getDb().getPois("markers", "Editors");
                        LinearLayout linearlayoutEditors = (LinearLayout) findViewById(R.id.linearlayout_editors);

                        if (pois.size() > 0) {
                            linearlayoutEditors.setVisibility(View.VISIBLE);
                            LIBUI.addPois(isPremium,"markers", points, markerText, markerImage, 0, poiId, dialogMarkers, pois, (FlexboxLayout) findViewById(R.id.icon_flow_editors), false, null);
                        } else {
                            linearlayoutEditors.setVisibility(View.GONE);
                        }
                    }

                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPoisRecent("markers", Global.getInstance().getDb().getMyEntityGuid()), (FlexboxLayout) findViewById(R.id.symbol_select_markers_recent), false, null);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("markers", "Moorings"), (FlexboxLayout) findViewById(R.id.symbol_select_moorings), false, null);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("markers", "Activities"), (FlexboxLayout) findViewById(R.id.icon_flow_activities), false, null);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("markers", "Routes"), (FlexboxLayout) findViewById(R.id.icon_flow_routes), false, null);
                    LIBUI.addPois(isPremium, MARKERS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("markers", "Area of Interest"), (FlexboxLayout) findViewById(R.id.icon_flow_areainterest), false, null);
                }
            });
        } else {
            markerLayout.setVisibility(View.GONE);
            symbolLayout.setVisibility(View.VISIBLE);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPoisRecent("symbols", Global.getInstance().getDb().getMyEntityGuid()), (FlexboxLayout)findViewById(R.id.symbol_select_symbols_recent), false, null);
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("symbols", "Services"), (FlexboxLayout)findViewById(R.id.symbol_select_services), false, null);
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("symbols", "Food & Drink"), (FlexboxLayout)findViewById(R.id.symbol_select_fooddrink), false, null);
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("symbols", "Travel"), (FlexboxLayout)findViewById(R.id.symbol_select_travel), false, null);
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("symbols", "Essentials"), (FlexboxLayout)findViewById(R.id.symbol_select_essentials), false, null);
                    LIBUI.addPois(isPremium, SYMBOLS, points, markerText, markerImage, 0, poiId, dialogMarkers, Global.getInstance().getDb().getPois("symbols", "Entertainment"), (FlexboxLayout)findViewById(R.id.symbol_select_entertainment), false, null);
                }
            });
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
}
