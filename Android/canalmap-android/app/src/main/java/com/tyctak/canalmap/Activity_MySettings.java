package com.tyctak.canalmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tyctak.map.entities._MySettings;
import com.tyctak.canalmap.libraries.Library_UI;

public class Activity_MySettings extends AppCompatActivity {

    final private String TAG = "Activity_MySettings";
    final private Activity_MySettings activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysettings);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_save);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.mySettingsTitle);

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        final SeekBar updateGps = (SeekBar) findViewById(R.id.updateGps);
        final TextView txtUpdateGps = (TextView) findViewById(R.id.textUpdateGps);

        updateGps.setProgress(mySettings.UpdateGps);
        Library_UI LIBUI = new Library_UI();
        txtUpdateGps.setText(LIBUI.getUpdateGpsText(mySettings.UpdateGps));

        updateGps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Message msg = new Message();
                progress = (progress % 10);
                msg.arg1 = progress;
                myHandler.dispatchMessage(msg);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saveactionbar_mysettings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void saveMySettings(MenuItem item) {
        SeekBar updateGps = (SeekBar) findViewById(R.id.updateGps);
        Global.getInstance().getDb().writeGpsTiming(updateGps.getProgress());
        onBackPressed();
    }

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            updateGps(msg.arg1);
        }
    };

    private void updateGps(Integer update) {
        TextView txtUpdateGps = (TextView) findViewById(R.id.textUpdateGps);
        Library_UI LIBUI = new Library_UI();
        txtUpdateGps.setText(LIBUI.getUpdateGpsText(update));
    }
}
