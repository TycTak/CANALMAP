package com.tyctak.canalmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;

import com.tyctak.map.libraries.XP_Library_UI;

public class Z_Activity_EditText extends AppCompatActivity {

    final private String TAG = "Z_Activity_EditText";
    final private Z_Activity_EditText activity = this;

    XP_Library_UI XPLIBUI = new XP_Library_UI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittext);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.editTextTitle);

        String description = getIntent().getStringExtra("description");
        int id = getIntent().getIntExtra("poiid", -1);

        TextView txtUpdateText = (TextView) findViewById(R.id.textUpdate);
        TextView locationDescription = (TextView) findViewById(R.id.locationDescription);

        txtUpdateText.setTag(id);
        locationDescription.append(description);

        txtUpdateText.setText(XPLIBUI.displayUpdateText(description.length()));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Message msg = new Message();
                msg.arg1 = s.length();
                myHandler.dispatchMessage(msg);

                invalidateOptionsMenu();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        };

        locationDescription.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            updateText(msg.arg1);
        }
    };

    private void updateText(Integer length) {
        TextView txtUpdateText = (TextView) findViewById(R.id.textUpdate);
        TextView locationDescription = (TextView) findViewById(R.id.locationDescription);

        txtUpdateText.setText(XPLIBUI.displayUpdateText(length));

        int poiId = (Integer) txtUpdateText.getTag();

        Intent intent = new Intent();
        intent.putExtra("process", "write");
        intent.putExtra("text", locationDescription.getText());
        intent.putExtra("poiid", poiId);
        setResult(Activity.RESULT_OK, intent);
    }
}