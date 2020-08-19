package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.map.entities._MySettings;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_SC;

public class Activity_Symbols extends AppCompatActivity {

    final private String TAG = "Activity_Symbols";
    final private Activity_Symbols activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbols);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.symbolsTitle);

        final Library_UI LIBUI = new Library_UI();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                XP_Library_SC XPLIBSC = new XP_Library_SC();
                boolean isPremium = XPLIBSC.isPremium(mySettings);

                LIBUI.addPois(isPremium,"symbols", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("symbols", "Services"), (FlexboxLayout)findViewById(R.id.icon_flow_services), true, null);
                LIBUI.addPois(isPremium,"symbols", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("symbols", "Essentials"), (FlexboxLayout)findViewById(R.id.icon_flow_essentials), true, null);
                LIBUI.addPois(isPremium,"symbols", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("symbols", "Food & Drink"), (FlexboxLayout)findViewById(R.id.icon_flow_fooddrink), true, null);
                LIBUI.addPois(isPremium,"symbols", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("symbols", "Entertainment"), (FlexboxLayout)findViewById(R.id.icon_flow_entertainment), true, null);
                LIBUI.addPois(isPremium,"symbols", null, null, null, -1, -1, activity, Global.getInstance().getDb().getPois("symbols", "Travel"), (FlexboxLayout)findViewById(R.id.icon_flow_travel), true, null);

                ProgressBar progress = (ProgressBar)findViewById(R.id.progressSymbols);
                progress.setVisibility(View.GONE);

                ScrollView symbols = (ScrollView) findViewById(R.id.scrollViewSymbols);
                symbols.setVisibility(View.VISIBLE);
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
