package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GR;

//import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

public class Dialog_FilterPois extends AppCompatActivity {

    private final String TAG = "Dialog_Icons";
    private final Activity activity = this;

    FlexboxLayout flowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filterpois);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int)(dm.widthPixels * 0.8), (int)(dm.heightPixels * 0.8));

        flowLayout = (FlexboxLayout) findViewById(R.id.icon_flow);

        ArrayList<String> names = Global.getInstance().getDb().getAllPois();

        Library LIB = new Library();

        int imgWidth = LIB.ConvertToDensityPixels(this, 40);
        int imgHeight = LIB.ConvertToDensityPixels(this, 40);
        int leftRightMargin = LIB.ConvertToDensityPixels(this, 8);
        int topMargin = LIB.ConvertToDensityPixels(this, 10);

        Library_GR LIBGR = new Library_GR();
        ArrayList<ImageView> imageViews = LIBGR.getImages(this, names, imgWidth, imgHeight, leftRightMargin, leftRightMargin, topMargin, 0, new View.OnClickListener() { public void onClick(View v) { selectIcon(v); } });

        for (ImageView imgV : imageViews) {
            imgV.setImageAlpha(110);
            flowLayout.addView(imgV);
        }
    }

    public void selectIcon(View view) {
        ImageView imgV = (ImageView) view;
        Library_UI LIBUI = new Library_UI();
        String description = Global.getInstance().getDb().getPoiDescriptionByName(imgV.getTag().toString());

        if (imgV.getImageAlpha() == 110) {
            LIBUI.snackBar(activity, "'" + description + "' will appear");
            imgV.setImageAlpha(255);
        } else {
            LIBUI.snackBar(activity, "'" + description + "' will not appear");
            imgV.setImageAlpha(110);
        }
    }

    public void clickSelect(View view) {
        FlexboxLayout flowLayout = (FlexboxLayout) ((LinearLayout) view.getParent().getParent()).findViewById(R.id.icon_flow);

        String iconNames = "";

        for (int i = 0 ; i < flowLayout.getChildCount(); i++) {
            ImageView imgV = (ImageView) flowLayout.getChildAt(i);

            if (imgV.getImageAlpha() == 255) {
                if (!iconNames.isEmpty()) iconNames += ";";
                iconNames += imgV.getTag().toString();
            }
        }

        XP_Library_CM LIBCM = new XP_Library_CM();
        if (LIBCM.isBlank(iconNames)) {
            Library_UI LIBUI = new Library_UI();
            LIBUI.snackBar(activity, "You need to select some symbols or markers");
        } else {
            Intent intent = new Intent();
            intent.putExtra("iconnames", iconNames);

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
