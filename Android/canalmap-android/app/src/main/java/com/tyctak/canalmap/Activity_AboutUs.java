package com.tyctak.canalmap;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyctak.map.entities._Association;
import com.tyctak.map.entities._Country;
import com.tyctak.map.entities._MySettings;
//import com.tyctak.canalmap.libraries.Library_CP;
//import com.tyctak.canalmap.libraries.Library_DB;
import com.tyctak.map.entities._Role;
import com.tyctak.canalmap.libraries.Library_FS;
import com.tyctak.canalmap.libraries.Library_GG;
//import com.tyctak.canalmap.libraries.Library_SM;
import com.tyctak.canalmap.libraries.Library_PE;
import com.tyctak.canalmap.libraries.Library_UI;
import com.tyctak.map.libraries.XP_Library_CM;
import com.tyctak.map.libraries.XP_Library_DB;
import com.tyctak.map.libraries.XP_Library_FS;
import com.tyctak.map.libraries.XP_Library_WS;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class Activity_AboutUs extends AppCompatActivity {

    final private String TAG = "Activity_AboutUs";
    final private Activity_AboutUs activity = this;
    private Library_GG LIBGG;

    Service_MD mService;
    public final static int COPY_DATABASE_PERMISSION_REQUEST = 1;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Service_MD.LocalBinder binder = (Service_MD.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_keyboard);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.aboutUs);

        TextView version = (TextView) findViewById(R.id.aboutus_version);
        TextView located1 = (TextView) findViewById(R.id.aboutus_located1);
        TextView located2 = (TextView) findViewById(R.id.aboutus_located2);
        TextView located3 = (TextView) findViewById(R.id.aboutus_located3);
        TextView located4 = (TextView) findViewById(R.id.aboutus_located4);
        TextView located5 = (TextView) findViewById(R.id.aboutus_located5);
        TextView located6 = (TextView) findViewById(R.id.aboutus_located6);

        if (LIBGG == null) LIBGG = new Library_GG(this);
        Library_UI LIBUI = new Library_UI();

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        version.setText(LIBUI.getAppVersion());

        Library_FS LIBFS = new Library_FS();

        located1.setText("S = " + (LIBFS.isFileExists(XP_Library_FS.enmFolder.Database, Global.getInstance().getDb().getDatabaseName()) ? "OND" : "NOD" ) + "-" + (LIBFS.isFileExists(XP_Library_FS.enmFolder.SdCard, Global.getInstance().getDb().getDatabaseName()) ? "ONS" : "NOS" ) + "-" + (LIBFS.isFileExists(XP_Library_FS.enmFolder.Assets, "cancam.sqlite") ? "USD" : "UDD") + "-" + (LIBFS.isFileExists(XP_Library_FS.enmFolder.Files, "ignore.flag") ? "IGN" : "NIG"));
        located2.setText("P = " + (mySettings.IsReviewer ? "IR" : "NR") + "-" + (mySettings.IsPublisher ? "IP" : "NP") + "-" + (mySettings.IsAdministrator ? "IA" : "NA"));
        located3.setText("X = " + mySettings.IsPremium + ", S = " + mySettings.IsSecurityPremium + ", F = " + Global.getInstance().getDb().getWaitingToSend() + ",  E = " + Global.getInstance().getDb().getEntityCount() + ",  A = " + Global.getInstance().getDb().getEntityActiveCount());
        located4.setText("G = " + Global.getInstance().getDb().getMyEntityGuid().substring(0, 5) + ",  P = " + Global.getInstance().getDb().getMySettings().EncryptedEntityGuid.substring(0, 5) + ",  H = " + (Global.getInstance().getDb().getSessionPassword() == null ? "NONE" : Global.getInstance().getDb().getSessionPassword().substring(0, 5)));
        located5.setText("CL = " + mySettings.CurrentLocalDate + ",  CS = " + mySettings.CurrentServerDate + ",  LL = " + mySettings.LastLocalDate + ",  LS = " + mySettings.LastServerDate);
        located5.setText("CL = " + mySettings.CurrentLocalDate + ",  CS = " + mySettings.CurrentServerDate);
        located6.setText("LL = " + mySettings.LastLocalDate + ",  LS = " + mySettings.LastServerDate);

        LinearLayout copyButton = (LinearLayout) findViewById(R.id.copyButtonLayout);
        copyButton.setVisibility((BuildConfig.DEBUG || mySettings.IsAdministrator) ? View.VISIBLE : GONE);

        LinearLayout publisherButton = (LinearLayout) findViewById(R.id.publisherLayout);
        publisherButton.setVisibility(!mySettings.IsPublisher ? View.VISIBLE : GONE);

        LinearLayout reviewerButton = (LinearLayout) findViewById(R.id.reviewerLayout);
        reviewerButton.setVisibility(!mySettings.IsReviewer ? View.VISIBLE : GONE);

        LinearLayout administratorButton = (LinearLayout) findViewById(R.id.administratorLayout);
        administratorButton.setVisibility(!mySettings.IsAdministrator ? View.VISIBLE : GONE);

        LinearLayout premiumButton = (LinearLayout) findViewById(R.id.premiumLayout);
        premiumButton.setVisibility(!mySettings.IsPremium && !mySettings.IsSecurityPremium ? View.VISIBLE : GONE);

        LinearLayout premiumFeatures = (LinearLayout) findViewById(R.id.premiumFeatures);
        premiumFeatures.setVisibility(mySettings.IsPremium || mySettings.IsSecurityPremium ? View.VISIBLE : GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.keyboardactionbar_aboutus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void DisplayKeyboard(MenuItem item) {
        LinearLayout aboutus_buttons = (LinearLayout) findViewById(R.id.aboutus_buttons);
        aboutus_buttons.setVisibility(aboutus_buttons.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        item.setIcon(aboutus_buttons.getVisibility() == View.VISIBLE ? R.drawable.ic_keyboard_show : R.drawable.ic_keyboard_hide);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void resetEntities(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_resetentities_title), getString(R.string.confirm_resetentities), new Runnable() {
            @Override
            public void run() {
                Global.getInstance().getDb().resetEntities();
            }
        }, this);
    }

    public void clearPois(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_clearmarkers_title), getString(R.string.confirm_clearmarkers), new Runnable() {
            @Override
            public void run() {
                clearPois();
            }
        }, this);
    }

    private void clearPois() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
        progress.setVisibility(View.VISIBLE);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Global.getInstance().getDb().clearMyPois();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
                        progress.setVisibility(GONE);

                        Library_UI LIBUI = new Library_UI();
                        LIBUI.popupExitDialog(getString(R.string.restart_application_title), getString(R.string.clear_markers_done), activity);
                    }
                });
            }
        });

        worker.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, Service_MD.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    public void initialiseMapCache(View view) {
        Library_UI LIBUI = new Library_UI();

        if (mService != null && !mService.isRunning()) {
            LIBUI.confirmationDialogYesNo(getString(R.string.confirm_initialisecache_title), getString(R.string.confirm_initialisecache), new Runnable() {
                @Override
                public void run() {
                    initialiseMapCache();
                }
            }, this);
        } else {
            LIBUI.popupMessageDialog(getString(R.string.initialisecache_title),getString(R.string.initialisecache_description), activity);
        }
    }

    private void initialiseMapCache() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
        progress.setVisibility(View.VISIBLE);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                Global.getInstance().getDb().initialiseMapCache();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
                        progress.setVisibility(GONE);

                        Library_UI LIBUI = new Library_UI();
                        LIBUI.popupExitDialog(getString(R.string.restart_application_title), getString(R.string.restart_application_initialisecache), activity);
                    }
                });
            }
        });

        worker.start();
    }

    public void clearPremium(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_clearpremium_title), getString(R.string.confirm_clearpremium), new Runnable() {
            @Override
            public void run() {
                Global.getInstance().getDb().writePremium(0);
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
            }
        }, this);
    }

    public void repairOwnership(View view) {
        Library_UI LIBUI = new Library_UI();
        LIBUI.confirmationDialogYesNo(getString(R.string.confirm_repairownership_title), getString(R.string.confirm_repairownership), new Runnable() {
            @Override
            public void run() {
                if (!BuildConfig.DEBUG) {
                    repairOwnership();

                    long expiry = XP_Library_CM.getDate(XP_Library_CM.now()) +  (3 * Library_GG.ONE_WEEK);
                    Global.getInstance().getDb().writePremium(expiry);

                    Library_UI LIBUI = new Library_UI();
                    LIBUI.snackBar(activity, getString(R.string.msg_success_response));
                } else {
                    long expiry = XP_Library_CM.getDate(XP_Library_CM.now()) +  (3 * Library_GG.ONE_WEEK);
                    Global.getInstance().getDb().writePremium(expiry);
                }
            }
        }, this);
    }

    private void repairOwnership() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
        progress.setVisibility(View.VISIBLE);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                LIBGG.checkPremium();

                int messageId = LIBGG.repairPremiumPurchased();

                class completePurchase implements Runnable {
                    private int value;
                    completePurchase(int pValue) { value = pValue; }
                    public void run() {
                        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
                        progress.setVisibility(GONE);

                        Library_UI LIBUI = new Library_UI();
                        LIBUI.snackBar(activity, getString(value));
                    }
                };

                runOnUiThread(new completePurchase(messageId));
            }
        });

        worker.start();
    }

    public void publisherRequest(View view) {
        finish();

        Intent intent = new Intent(activity, Activity_Roles.class);
        intent.putExtra("role", _Role.enmRoles.Publisher.ordinal());

        startActivity(intent);
    }

    public void resetRequests(View view) {
        Global.getInstance().getDb().writePremiumRequest(false);
        Global.getInstance().getDb().writeAdminRequest(false);
    }

    public void checkSystem(View view) {
        String retval = "";

        try {
            HashMap<String, _Association> temp1 =  new HashMap<>();
            retval += "Hashmap #1=success\n";
        } catch (Exception ex) {
            retval += "Hashmap #1=failed\n" + ex.getMessage() + "\n";
        }

        try {
            HashMap<String, _Association> temp1 =  new HashMap<String, _Association>();
            retval += "Hashmap #2=success\n";
        } catch (Exception ex) {
            retval += "Hashmap #2=failed\n" + ex.getMessage() + "\n";
        }

        try {
            HashMap<String, _Country> temp1 =  new HashMap<>();
            retval += "Hashmap #3=success\n";
        } catch (Exception ex) {
            retval += "Hashmap #3=failed\n" + ex.getMessage() + "\n";
        }

        try {
            Map<String, _Country> temp1 =  new HashMap<>();
            retval += "Hashmap #4=success\n";
        } catch (Exception ex) {
            retval += "Hashmap #4=failed\n" + ex.getMessage() + "\n";
        }

        try {
            Map<String, _Country> temp1 =  new HashMap<String, _Country>();
            retval += "Hashmap #5=success\n";
        } catch (Exception ex) {
            retval += "Hashmap #5=failed\n" + ex.getMessage() + "\n";
        }

        Library_UI LIBUI = new Library_UI();
        LIBUI.popupMessageDialog(getString(R.string.check_system), retval, activity);
    }

    public void checkStatus(View view) {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                XPLIBWS.getRoles();

                Library_UI LIBUI = new Library_UI();
                LIBUI.snackBar(activity, getString(R.string.publisher_status_reset));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
            }
        });

        worker.start();
    }

    public void premiumRequest(View view) {
        Library_UI LIBUI = new Library_UI();
        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

        if (mySettings.IsPremium || mySettings.IsSecurityPremium) {
            LIBUI.snackBar(activity, getString(R.string.request_completed_premium));
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    finish();

                    Intent intent = new Intent(activity, Activity_Roles.class);
                    intent.putExtra("role", _Role.enmRoles.Premium.ordinal());

                    startActivity(intent);
                }
            };

            if (Global.getInstance().getDb().getPremiumRequest()) {
                LIBUI.confirmationDialogYesNo(getString(R.string.confirm_request_title), getString(R.string.confirm_request), new Runnable() {
                    @Override
                    public void run() {
                        new Thread(checkRequestPremium()).start();
                    }
                }, this);
            } else {
                runnable.run();
            }
        }
    }

    private Runnable checkRequestPremium() {
        final Runnable check = new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                XPLIBWS.getRoles();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Library_UI LIBUI = new Library_UI();
                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                        if (!mySettings.IsPremium && !mySettings.IsSecurityPremium) {
                            LIBUI.snackBar(activity, getString(R.string.request_not_completed));
                        }
                    }
                });
            }
        };

        return check;
    }

    private Runnable checkRequestAdmin() {
        final Runnable check = new Runnable() {
            @Override
            public void run() {
                XP_Library_WS XPLIBWS = new XP_Library_WS();
                XPLIBWS.getRoles();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Library_UI LIBUI = new Library_UI();
                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                        if (!mySettings.IsPremium && !mySettings.IsSecurityPremium) {
                            LIBUI.snackBar(activity, getString(R.string.request_not_completed));
                        }
                    }
                });
            }
        };

        return check;
    }

    public void administratorRequest(View view) {
        Library_UI LIBUI = new Library_UI();

        if (Global.getInstance().getDb().getMySettings().IsAdministrator) {
            LIBUI.snackBar(activity, getString(R.string.request_completed_admin));
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    finish();

                    Intent intent = new Intent(activity, Activity_Roles.class);
                    intent.putExtra("role", _Role.enmRoles.Administrator.ordinal());

                    startActivity(intent);
                }
            };

            if (Global.getInstance().getDb().getAdminRequest()) {
                LIBUI.confirmationDialogYesNo(getString(R.string.confirm_request_title), getString(R.string.confirm_request), new Runnable() {
                    @Override
                    public void run() {
                        new Thread(checkRequestAdmin()).start();
                    }
                }, this);
            } else {
                runnable.run();
            }
        }
    }

    public void reviewerRequest(View view) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                finish();

                Intent intent = new Intent(activity, Activity_Roles.class);
                intent.putExtra("role", _Role.enmRoles.Reviewer.ordinal());

                startActivity(intent);
            }
        };

        runnable.run();
    }

    public void copyToSDCard(View view) {
        Intent intent = new Intent(activity, Activity_Permissions.class);
        intent.putExtra("permissions", new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
        intent.putExtra("force", true);

        startActivityForResult(intent, Library_PE.GET_PERMISSIONS);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        Library_PE LIBPE = new Library_PE();
        LIBPE.handleResult(activity, new Runnable() {
            @Override
            public void run() {
                copyDatabase();
            }
        }, requestCode, resultCode, data);
    }

    private void copyDatabase() {
        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
        progress.setVisibility(View.VISIBLE);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                final Library_FS LIBFS = new Library_FS();
                LIBFS.copyDatabase();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout progress = (FrameLayout) findViewById(R.id.progressWaitingFullScreenAboutUs);
                        progress.setVisibility(GONE);

                        Library_UI LIBUI = new Library_UI();
                        if (BuildConfig.DEBUG) {
                            LIBUI.snackBar(activity, R.string.copy_sdcard_technical);
                        } else if (LIBFS.isFileExists(XP_Library_FS.enmFolder.SdCard, Global.getInstance().getDb().getDatabaseName())) {
                            LIBUI.snackBar(activity, R.string.unable_as_on_sdcard);
                        } else {
                            LIBUI.snackBar(activity, R.string.copy_sdcard);
                        }
                    }
                });
            }
        });

        worker.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LIBGG != null) LIBGG.endConnection();
    }
}