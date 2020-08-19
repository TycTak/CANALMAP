package com.tyctak.canalmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.tyctak.map.entities._PoiLocation;
import com.tyctak.map.entities._Route;
import com.tyctak.map.libraries.XP_Library_CM;

//import org.jsoup.helper.StringUtil;

public class Z_Activity_FullScreenImage extends AppCompatActivity {

    final private String TAG = "Z_Activity_FullScreenImage";
    final private Z_Activity_FullScreenImage activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreenimage);

        Bundle extras = getIntent().getExtras();
        int poiId = extras.getInt("PoiId", -1);
        byte[] bytes = extras.getByteArray("Image");
        String routeGuid = extras.getString("RouteGuid");

        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (poiId >= 0) {
            _PoiLocation poiLocation = Global.getInstance().getDb().getPoiLocation(poiId);
            bytes = poiLocation.Image;
        } else if (!XPLIBCM.isBlank(routeGuid)) {
            _Route route = Global.getInstance().getDb().getRoute(routeGuid, 0);
            bytes = route.Map;
        }

        ImageView image = (ImageView) findViewById(R.id.fullscreenimage);

        if (bytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            image.setImageBitmap(bitmap);
        }

        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
