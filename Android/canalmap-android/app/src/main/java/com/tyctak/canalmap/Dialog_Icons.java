package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.canalmap.libraries.Library_FS;
import com.tyctak.canalmap.libraries.Library_GR;

import java.util.ArrayList;

public class Dialog_Icons extends AppCompatActivity {

    private final String TAG = "Dialog_Icons";
    private final Activity activity = this;

    FlexboxLayout flowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_icons);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int)(dm.widthPixels * 0.8), (int)(dm.heightPixels * 0.8));

        flowLayout = (FlexboxLayout) findViewById(R.id.icon_flow);

        ArrayList<String> names = Global.getInstance().getDb().getProfileIcons();

        Library LIB = new Library();

        int imgWidth = LIB.ConvertToDensityPixels(this, 40);
        int imgHeight = LIB.ConvertToDensityPixels(this, 40);
        int leftRightMargin = LIB.ConvertToDensityPixels(this, 8);
        int topMargin = LIB.ConvertToDensityPixels(this, 10);

        Library_GR LIBGR = new Library_GR();
        ArrayList<ImageView> imageViews = LIBGR.getImages(this, names, imgWidth, imgHeight, leftRightMargin, leftRightMargin, topMargin, 0, new View.OnClickListener() { public void onClick(View v) { selectIcon(v); } });

        for (ImageView imgV : imageViews) {
            flowLayout.addView(imgV);
        }
    }

    public void selectIcon(View view) {
        Intent intent = new Intent();
        String iconName = view.getTag().toString();
        Integer iconId = this.getResources().getIdentifier(iconName, "drawable", this.getPackageName());

        intent.putExtra("iconid", iconId);
        intent.putExtra("iconname", iconName);

        setResult(RESULT_OK, intent);
        finish();
    }
}
