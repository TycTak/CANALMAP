package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.libraries.XP_Library_SC;

public class Dialog_PoiSelect extends AppCompatActivity {

    private final String TAG = "Dialog_PoiSelect";
    double[] points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_poiselect);
        points = getIntent().getDoubleArrayExtra("geopoint");

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        TextView premiumFeature = (TextView) findViewById(R.id.premiumFeatureText);
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        premiumFeature.setVisibility(XPLIBSC.isPremium(mySettings) ? View.GONE : View.VISIBLE);
    }

    public void selectMarker(View view) {
        Intent intent = new Intent();
        intent.putExtra("GeoPoint", points);
        intent.putExtra("Action", "markers");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void selectSymbol(View view) {
        Intent intent = new Intent();
        intent.putExtra("GeoPoint", points);
        intent.putExtra("Action", "symbols");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
