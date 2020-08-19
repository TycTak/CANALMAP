package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Route;
import com.tyctak.canalmap.libraries.Library_UI;

public class Dialog_Route extends AppCompatActivity {

    private final String TAG = "Dialog_Route";

    String routeGuid;
    boolean paused = false;
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_waterway);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.8));

        routeGuid = getIntent().getStringExtra("routeGuid");
        boolean purchased = getIntent().getBooleanExtra("purchased", false);
        paused = getIntent().getBooleanExtra("paused", false);
        boolean isBuyAll = (routeGuid.equals(Global.getInstance().getDb().getAllRouteGuid()));
        boolean nobuttons = getIntent().getBooleanExtra("nobuttons", false);

        _Route route = Global.getInstance().getDb().getRoute(routeGuid);

        TextView title = (TextView) findViewById(R.id.waterwayName);
        title.setText(route.RouteName);

        LinearLayout btnGrp1 = (LinearLayout) findViewById(R.id.dialog_waterway_buttons);
        ImageView map = (ImageView) findViewById(R.id.mapImage);

        TextView route_description = (TextView) findViewById(R.id.waterway_description);
        route_description.setText(route.Description);

        if (nobuttons || (route.Empty && route.TilesDownloaded == 0)) {
            btnGrp1.setVisibility(View.GONE);
        } else if (route.CurrentVersion < 0) {
            btnGrp1.setVisibility(View.GONE);
            route_description.setVisibility(View.GONE);

            RelativeLayout availability_layout = (RelativeLayout) findViewById(R.id.availability_layout_dialog);
            availability_layout.setVisibility(View.VISIBLE);

            TextView availability_text = (TextView) findViewById(R.id.availability_text_dialog);
            TextView availability_date = (TextView) findViewById(R.id.availability_date);
            availability_text.setText(String.format(getString(R.string.sorry_not_available), route.Availability));
            availability_date.setText(route.Availability);
            map.setVisibility(View.GONE);
        } else if (route.Purchased) {
            btnGrp1.setVisibility(View.VISIBLE);
        } else if (route.TotalTiles != route.TilesDownloaded) {
            btnGrp1.setVisibility(View.VISIBLE);
        } else {
            btnGrp1.setVisibility(View.GONE);
        }

        if (isBuyAll && purchased) {
            RelativeLayout buyall_layout = (RelativeLayout) findViewById(R.id.buyall_layout_dialog);
            buyall_layout.setVisibility(View.VISIBLE);
            btnGrp1.setVisibility(View.GONE);
        }

        Library lib = new Library();
        map.setImageBitmap(lib.decodeBinary(route.Map));

        final String routeGuid = route.RouteGuid;

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Z_Activity_FullScreenImage.class);
                intent.putExtra("RouteGuid", routeGuid);
                startActivity(intent);
            }
        });
    }

    public void clickEmptyButton(View view) {
        final Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_emptywaterway_title), getString(R.string.confirm_emptywaterway), new Runnable() {
            @Override
            public void run() {
                Global.getInstance().getDb().writeEmptyRoute(routeGuid, true);
                LIBUI.popupExitDialog("Restart Application", "You have emptied the waterway, we will now exit this Canal Map so that it will automatically free up space on your device. Just restart this Canal Map using the normal application button.", activity);
            }
        }, this);
    }
}
