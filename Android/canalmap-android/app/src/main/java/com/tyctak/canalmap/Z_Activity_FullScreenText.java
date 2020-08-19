package com.tyctak.canalmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Z_Activity_FullScreenText extends AppCompatActivity {

    final private String TAG = "Z_Activity_FullScreenImage";
    final private Z_Activity_FullScreenText activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreentext);

        Bundle extras = getIntent().getExtras();
        String text = extras.getString("text");

        TextView textView = (TextView) findViewById(R.id.fullscreentext);

        if (text != null && !text.isEmpty()) {
            textView.setText(text);
        }

        LinearLayout fullscreentext_layout = (LinearLayout) findViewById(R.id.fullscreentext_layout);
        fullscreentext_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
