package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.map.entities._Role;

public class Dialog_Published extends AppCompatActivity {

    private final String TAG = "Dialog_Published";

    static int poiId;
    static int position;
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_published);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.8));

        poiId = getIntent().getIntExtra("poiid", -1);
        position = getIntent().getIntExtra("position", -1);

        if (poiId >= 0) {
            _PoiLocation poiLocation = Global.getInstance().getDb().getPoiLocation(poiId);

            if (poiLocation.Shared <= 0) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.publishedPrivate);
                layout.setBackgroundResource(R.drawable.customselecteditem);
                layout.setEnabled(false);
            } else if (poiLocation.Shared == 1) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.publishedPublic);
                layout.setBackgroundResource(R.drawable.customselecteditem);
                layout.setEnabled(false);
            }
        }
    }

    public final static int PUBLISHED = 101;
    public final static int PRIVATE = 99;

    public void changePublished(View view) {
        int published = Integer.parseInt((String)view.getTag());
        if (published == 0) published = -1;

        Intent intent = new Intent(activity, Activity_Roles.class);
        intent.putExtra("role", _Role.enmRoles.Publisher.ordinal());
        intent.putExtra("args", new int[] {poiId, position});

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (published == 1 && !mySettings.IsPublisher) {
            LinearLayout puLayout = (LinearLayout) findViewById(R.id.publishedPublic);
            puLayout.setBackgroundResource(R.drawable.customselecteditem);

            LinearLayout prLayout = (LinearLayout) findViewById(R.id.publishedPrivate);
            prLayout.setBackgroundResource(R.drawable.customunselecteditem);

            startActivityForResult(intent, PUBLISHED);
        } else if (published == 1 && mySettings.IsPublisher) {
            returnToParent(PUBLISHED);
        } else {
            returnToParent(PRIVATE);
//            LinearLayout puLayout = (LinearLayout) findViewById(R.id.publishedPublic);
//            puLayout.setBackgroundResource(R.drawable.customunselecteditem);
//
//            LinearLayout prLayout = (LinearLayout) findViewById(R.id.publishedPrivate);
//            prLayout.setBackgroundResource(R.drawable.customselecteditem);
//
//            startActivityForResult(intent, PRIVATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            if (requestCode == 101) {
                LinearLayout puLayout = (LinearLayout) findViewById(R.id.publishedPublic);
                puLayout.setBackgroundResource(R.drawable.customunselecteditem);

                LinearLayout prLayout = (LinearLayout) findViewById(R.id.publishedPrivate);
                prLayout.setBackgroundResource(R.drawable.customselecteditem);
            } else if (requestCode == 99) {
                LinearLayout puLayout = (LinearLayout) findViewById(R.id.publishedPublic);
                puLayout.setBackgroundResource(R.drawable.customunselecteditem);

                LinearLayout prLayout = (LinearLayout) findViewById(R.id.publishedPrivate);
                prLayout.setBackgroundResource(R.drawable.customselecteditem);
            }

            return;
        }

        Bundle extras = data.getExtras();

        returnToParent(requestCode);
    }

    private void returnToParent(int requestCode) {
        if (requestCode == PUBLISHED) {
            Intent intent = new Intent();
            intent.putExtra("poiid", poiId);
            intent.putExtra("position", position);
            intent.putExtra("published", 1);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (requestCode == PRIVATE) {
            Intent intent = new Intent();
            intent.putExtra("poiid", poiId);
            intent.putExtra("position", position);
            intent.putExtra("published", -1);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
