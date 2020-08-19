package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tyctak.map.entities._Item;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_SC;

import java.util.ArrayList;

import static com.tyctak.canalmap.R.id.list_poilocations;

public class Dialog_PoiList extends AppCompatActivity {

    private final String TAG = "Dialog_PoiList";
    final Activity activityDialogPoiList = this;

    final public static String MARKERS = "markers";
    private double[] points;

    ListView itemsList;
    ArrayAdapter_Items arrayItems;
    ArrayList<_Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_poilist);

        points = getIntent().getDoubleArrayExtra("geopoint");

        ArrayList<Integer> poiLocations = getIntent().getIntegerArrayListExtra("pois");
        ArrayList<String> entities = getIntent().getStringArrayListExtra("entities");

        for (String entityGuid : entities) {
            _Item item = new _Item();
            item.Entity = Global.getInstance().getDb().getEntity(entityGuid);
            items.add(item);
        }

        Log.d(TAG,"singleTapConfirmedHelper END #1");

        ArrayList<_PoiLocation> tempPoiLocations = Global.getInstance().getDb().getPoiImageMessage(poiLocations);

        for (_PoiLocation tempPoi : tempPoiLocations) {
            _Item item = new _Item();
            item.PoiLocation = tempPoi;
            items.add(item);
        }

        Log.d(TAG,"singleTapConfirmedHelper END #2");

        arrayItems = new ArrayAdapter_Items(this, items);
        itemsList = (ListView) findViewById(list_poilocations);
        itemsList.setAdapter(arrayItems);

        Log.d(TAG,"singleTapConfirmedHelper END #3");

        LinearLayout layout = (LinearLayout) findViewById(R.id.displayMarker);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
        TextView premiumNoticeText = (TextView) findViewById(R.id.premiumNotice);
        XP_Library_SC XPLIBSC = new XP_Library_SC();

        premiumNoticeText.setVisibility(XPLIBSC.isPremium(mySettings) ? View.GONE : View.VISIBLE);
    }

    public void displayAddMarker(View view) {
        Intent intent = new Intent();
        intent.putExtra("GeoPoint", points);
        intent.putExtra("Action", "markers");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void displayAddSymbol(View view) {
        Intent intent = new Intent();
        intent.putExtra("GeoPoint", points);
        intent.putExtra("Action", "symbols");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void deleteMarker(View view) {
        Library_UI LIBUI = new Library_UI();

        final _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());

        if (item.PoiLocation.Area.equals(MARKERS)) {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deletemarker_title), getString(R.string.confirm_deletemarker), new Runnable() {
                @Override
                public void run() {
                    deleteMarker(item.PoiLocation.Id, item.PoiLocation.Shared);
                }
            }, this);
        } else {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_deletesymbol_title), getString(R.string.confirm_deletesymbol), new Runnable() {
                @Override
                public void run() {
                    deleteMarker(item.PoiLocation.Id, item.PoiLocation.Shared);
                }
            }, this);
        }
    }

    private void deleteMarker(int poiid, int shared) {
        Global.getInstance().getDb().deletePoiLocation(poiid, shared);

        Intent intent = new Intent();
        intent.putExtra("PoiId", poiid);
        intent.putExtra("Action", "delete");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void showFullPhoto(View view) {
        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());

        Intent intent = new Intent(activityDialogPoiList, Z_Activity_FullScreenImage.class);
        intent.putExtra("PoiId", item.PoiLocation.Id);
        startActivity(intent);
    }

    public void displayEntity(View view) {
        finish();

        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());

        Intent intent = new Intent(Dialog_PoiList.this, Dialog_Entity.class);
        intent.putExtra("entityguid", item.Entity.EntityGuid);
        intent.putExtra("isMap", true);
        startActivity(intent);
    }

    public void displayEdit(View view) {
        Intent intent = new Intent();
        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());
        intent.putExtra("PoiId", item.PoiLocation.Id);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Bundle extras;

        if (requestCode == 0) {
            extras = data.getExtras();

            if (extras != null) {
                final int poiId = extras.getInt("poiid");
                final int published = extras.getInt("published");
                final int position = extras.getInt("position");

                Global.getInstance().getDb().writePoiLocationPublished(poiId, published);

                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View view = itemsList.getChildAt(position);
//                            TextView publishedView = (TextView) view.findViewById(R.id.sharedText);
                            //publishedView.setText(published == 0 ? "Private" : "Public");
                            ((_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem(position)).PoiLocation.Shared = published;
                            _PoiLocation poi = Global.getInstance().getDb().getPoiLocation(poiId);

                            XP_Library_SC XPLIBSC = new XP_Library_SC();
                            boolean isMyPoi = XPLIBSC.getMyPoi(poi.EntityGuid);
                            boolean isEdit = XPLIBSC.isEdit(poi.EntityGuid, poi.Category, poi.Longitude, poi.Latitude, isMyPoi);

                            ArrayAdapter_Items.UpdateSharedMarker(view, _PoiLocation.enmReviewedFeedback.NotReviewed, published, poi.EntityGuid, isEdit, isMyPoi);
                        }
                    });
            }
        }
    }

    public void selectPublished(final View view) {
        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());

        XP_Library_SC XPLIBSC = new XP_Library_SC();
        boolean isMyPoi = XPLIBSC.getMyPoi(item.PoiLocation.EntityGuid);
        boolean isEdit = XPLIBSC.isEdit(item.PoiLocation.EntityGuid, item.PoiLocation.Category, item.PoiLocation.Longitude, item.PoiLocation.Latitude, isMyPoi);

        if (isEdit && isMyPoi) {
            Intent intent = new Intent(activityDialogPoiList, Dialog_Published.class);
            intent.putExtra("poiid", item.PoiLocation.Id);
            intent.putExtra("position", (int) view.getTag());
            startActivityForResult(intent, 0);
        }
    }

    public void showFullText(View view) {
        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());

        Intent intent = new Intent(activityDialogPoiList, Z_Activity_FullScreenText.class);
        intent.putExtra("text", item.PoiLocation.Message);
        startActivity(intent);
    }

    public void clickReview(View view) {
        _Item item = (_Item) ((ListView) findViewById(list_poilocations)).getAdapter().getItem((int)view.getTag());
    }

    public void closeMarkers(View view) {
        finish();
    }
}