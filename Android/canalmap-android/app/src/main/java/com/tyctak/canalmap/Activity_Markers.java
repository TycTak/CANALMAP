package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Poi;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_SC;

import java.util.ArrayList;

public class Activity_Markers extends AppCompatActivity {

    final private String TAG = "Activity_Markers";
    final private Activity_Markers activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.markersTitle);

        final Library_UI LIBUI = new Library_UI();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                XP_Library_SC XPLIBSC = new XP_Library_SC();
                boolean isPremium = XPLIBSC.isPremium(mySettings);

                LinearLayout linearlayoutEditors = (LinearLayout) findViewById(R.id.linearlayout_editors);

                if (mySettings.IsAdministrator) {
                    ArrayList<_Poi> pois = Global.getInstance().getDb().getPois("markers", "Editors");

                    if (pois.size() > 0) {
                        linearlayoutEditors.setVisibility(View.VISIBLE);
                        LIBUI.addPois(isPremium,"markers", null, null, null, -1, -1, activity, pois, (FlexboxLayout) findViewById(R.id.icon_flow_editors), true, null);
                    } else {
                        linearlayoutEditors.setVisibility(View.GONE);
                    }
                } else {
                    linearlayoutEditors.setVisibility(View.GONE);
                }

                LIBUI.addPois(isPremium,"markers", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("markers", "Moorings"), (FlexboxLayout)findViewById(R.id.icon_flow_moorings), true, null);
                LIBUI.addPois(isPremium,"markers", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("markers", "Activities"), (FlexboxLayout)findViewById(R.id.icon_flow_activities), true, null);
                LIBUI.addPois(isPremium,"markers", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("markers", "Area of Interest"), (FlexboxLayout)findViewById(R.id.icon_flow_areainterest), true, null);
                LIBUI.addPois(isPremium,"markers", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("markers", "Routes"), (FlexboxLayout)findViewById(R.id.icon_flow_routes), true, null);

                ProgressBar progress = (ProgressBar)findViewById(R.id.progressMarkers);
                progress.setVisibility(View.GONE);

                ScrollView markers = (ScrollView) findViewById(R.id.scrollViewMarkers);
                markers.setVisibility(View.VISIBLE);
            }
        }, 100);

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        TextView premiumNoticeText = (TextView) findViewById(R.id.premiumNotice);
        XP_Library_SC XPLIBSC = new XP_Library_SC();
        premiumNoticeText.setVisibility(XPLIBSC.isPremium(mySettings) ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);

        return super.onOptionsItemSelected(item);
    }
}