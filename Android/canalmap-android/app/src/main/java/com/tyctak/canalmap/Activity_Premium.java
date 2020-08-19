package com.tyctak.canalmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.tyctak.map.entities._MySettings;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.canalmap.libraries.Library_GG;

public class Activity_Premium extends AppCompatActivity {

    final private String TAG = "Activity_Premium";
    final private Activity_Premium activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_sub);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.premium);

        String premiumReason = getIntent().getStringExtra("premiumreason");

        TextView premiumOption = (TextView) findViewById(R.id.premiumOption);
        XP_Library_CM LIBCM = new XP_Library_CM();
        premiumOption.setVisibility(LIBCM.isBlank(premiumReason) ? View.GONE : View.VISIBLE);

        TextView premiumReasonTextView = (TextView) findViewById(R.id.premiumReasonText);
        LinearLayout premiumReasonLinear = (LinearLayout) findViewById(R.id.premiumReason);

        if (LIBCM.isBlank(premiumReason)) {
            premiumReasonLinear.setVisibility(View.GONE);
        } else {
            premiumReasonLinear.setVisibility(View.VISIBLE);
            premiumReasonTextView.setText(premiumReason);
        }

        LIBGG = new Library_GG(this);

        LinearLayout notPremium = (LinearLayout) findViewById(R.id.notPremium);
        LinearLayout alreadyPremium = (LinearLayout) findViewById(R.id.alreadyPremium);
        LinearLayout cancellation = (LinearLayout) findViewById(R.id.cancellation);
        LinearLayout alreadySecurityPremium = (LinearLayout) findViewById(R.id.alreadySecurityPremium);

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.IsPremium) {
            notPremium.setVisibility(View.GONE);
            alreadyPremium.setVisibility(View.VISIBLE);
            cancellation.setVisibility(View.VISIBLE);
            alreadySecurityPremium.setVisibility(View.GONE);
        } else if (mySettings.IsSecurityPremium) {
            notPremium.setVisibility(View.GONE);
            alreadyPremium.setVisibility(View.GONE);
            cancellation.setVisibility(View.GONE);
            alreadySecurityPremium.setVisibility(View.VISIBLE);
        } else {
            notPremium.setVisibility(View.VISIBLE);
            alreadyPremium.setVisibility(View.GONE);
            cancellation.setVisibility(View.VISIBLE);
            alreadySecurityPremium.setVisibility(View.GONE);
        }
    }

    Library_GG LIBGG;

    public void clickPremiumBuyButton(View view) {
        final String productId = Global.getInstance().getDb().getPremiumGuid();

        final Library_UI LIBUI = new Library_UI();

        try {
            if (BuildConfig.DEBUG) {
                Library LIB = new Library();
                long expiryDate = XP_Library_CM.getDate(XP_Library_CM.now()) +  30 * 24 * 3600 * 1000l;

                Global.getInstance().getDb().writePremium(expiryDate);

                activity.finish();
            } else if (LIBGG.isNull()) {
                new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activity, R.string.msg_failed_ready2);
                    }
                }.run();
            } else if (LIBGG.isReady()) {
                LIBGG.buyProduct(BillingClient.SkuType.SUBS, productId, this, new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        // Failure
                    }
                });
            } else {
                new Runnable() {
                    @Override
                    public void run() {
                        LIBUI.snackBar(activity, R.string.msg_failed_ready);
                    }
                }.run();
            }
        } catch (Exception e) {
            Log.d(TAG, "ERROR for general Exception when running writeBuyRoute");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }
}