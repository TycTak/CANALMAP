package com.tyctak.canalmap;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.tyctak.canalmap.libraries.Library_CP;
//import com.tyctak.canalmap.libraries.Library_DB;
import com.tyctak.canalmap.libraries.Library_GG;
import com.tyctak.canalmap.libraries.Library_GR;
import com.tyctak.canalmap.libraries.Library_PE;
import com.tyctak.canalmap.libraries.Library_UI;

import java.io.IOException;

public class Activity_Splash extends AppCompatActivity {

    final private String TAG = "Activity_Splash";
    final private Activity_Splash activity = this;
//    private final Library_PE LIBPE = new Library_PE();

//    static int count = 0;
//    final private int DELAY = 1; // milliseconds
//    final private int DELAY_UNTIL_FINISH = 30; // seconds

//    private final class WorkerThread implements Runnable {
//
//        @Override
//        public void run() {
////            if (Global.getInstance().getDb().getVacuum()) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ProgressBar progress = (ProgressBar)findViewById(R.id.progressSplash);
////                        progress.setVisibility(View.VISIBLE);
////
////                        TextView progressText = (TextView) findViewById(R.id.progressText);
////                        progressText.setVisibility(View.VISIBLE);
////                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
////                        anim.setDuration(150);
////                        anim.setStartOffset(20);
////                        anim.setRepeatMode(Animation.REVERSE);
////                        anim.setRepeatCount(Animation.INFINITE);
////                        progressText.startAnimation(anim);
////                    }
////                });
////
////                Log.d(TAG, "Vacuum database");
////                Global.getInstance().getDb().cleanDatabase();
////            }
//
////            LIBPE.checkPermissions(activity,true, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE });
//        }
//    }

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Global.getInstance().initialise("cancam.sqlite", getString(R.string.app_style), getString(R.string.app_name), getString(R.string.app_code),Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                        Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                        Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                        Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                        Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                        Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                        Build.USER.length()%10 + MyApp.getContext().getPackageName(),
                MyApp.getContext().getFilesDir().getParentFile().getPath() + "/databases/");

//        Bootstrap.getInstance().initialise(Build.BOARD.length()%10+ Build.BRAND.length()%10 +
//                        Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
//                        Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
//                        Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
//                        Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
//                        Build.TAGS.length()%10 + Build.TYPE.length()%10 +
//                        Build.USER.length()%10 + MyApp.getContext().getPackageName(),
//                        MyApp.getContext().getFilesDir().getParentFile().getPath() + "/databases/",
//                        MyApp.getContext());

//        Global.getInstance().getDb().getMySettings();

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                LIBPE.checkPermissions(activity,true, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE });
//
//                ProgressBar progress = (ProgressBar)findViewById(R.id.progressSplash);
//                progress.setVisibility(View.VISIBLE);
//            }
//        });

//        Activity_Permissions PER = new Activity_Permissions();
//        PER.initiate(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new String[] { Manifest.permission.ACCESS_FINE_LOCATION });

        Intent intent = new Intent(Activity_Splash.this, Activity_Permissions.class);
        intent.putExtra("permissions", new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        intent.putExtra("force", true);
        intent.putExtra("exit", true);
        startActivityForResult(intent, Library_PE.GET_PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Library_PE LIBPE = new Library_PE();
        LIBPE.handleResult(activity, new Runnable() {
            @Override
            public void run() {
//                Library_DB LIBDB = new Library_DB(MyApp.getContext());
//                LIBDB.createDB(true);
//                LIBDB.close();
//                Global.getLIBDB().createDB(true);

                if (Global.getInstance().getDb().getVacuum()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar progress = (ProgressBar)findViewById(R.id.progressSplash);
                            progress.setVisibility(View.VISIBLE);

                            TextView progressText = (TextView) findViewById(R.id.progressText);
                            progressText.setVisibility(View.VISIBLE);
                            Animation anim = new AlphaAnimation(0.0f, 1.0f);
                            anim.setDuration(150);
                            anim.setStartOffset(20);
                            anim.setRepeatMode(Animation.REVERSE);
                            anim.setRepeatCount(Animation.INFINITE);
                            progressText.startAnimation(anim);
                        }
                    });

                    Log.d(TAG, "Vacuum database");
                    Global.getInstance().getDb().cleanDatabase();
                }

                try {
                    Global.getInstance().getDb().upgrade(MyApp.getContext().getAssets().open("release.sql"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Library_GR LIBGR = new Library_GR();

                LIBGR.initialiseGraphics(activity);
                startService(new Intent(MyApp.getContext(), Service_Closing.class));

                startService(new Intent(MyApp.getContext(), Service_MD.class));
                startService(new Intent(MyApp.getContext(), Service_WS.class));
                startService(new Intent(MyApp.getContext(), Service_PK.class));

                Library_GG LIBGG = new Library_GG(activity);
                LIBGG.checkPremium();

                Intent intent = new Intent(Activity_Splash.this, Activity_Main.class);
                startActivity(intent);
                finish();
            }
        }, requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Library_UI LIBUI = new Library_UI();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            LIBUI.popupExitDialog("Incompatible Version", "Sorry but your version of Android will not work with this application, it needs to be Android 4.4+", activity);
        }

//        Library_UI LIBUI = new Library_UI();
//        final Library_CP LIBCP = new Library_CP();
//
//        if (LIBCP.isCopyToSDCard() || LIBCP.isCopyToDevice()) {
//            LIBUI.confirmationDialogYesNoDontAsk("Move Database", "Do you want to move the data to the " + (LIBCP.isCopyToSDCard() ? "SD Card" : "Device") + ", this will free up space?", new Runnable() {
//                @Override
//                public void run() {
//                    // YES
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ProgressBar progress = (ProgressBar) findViewById(R.id.progressSplash);
//                            progress.setVisibility(View.VISIBLE);
//                        }
//                    });
//
//                    Log.d(TAG, "Copy to SDCARD");
//
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            Library_FS LIBFS = new Library_FS();
//
//                            if (LIBCP.isCopyToSDCard()) {
//                                if (LIBFS.copyFile(MyApp.getContext(), Library_FS.enmFolder.Database, Library_FS.enmFolder.SdCard, XP_Library_DB.DATABASE_NAME, null)) {
//                                    LIBFS.deleteFile(MyApp.getContext(), Library_FS.enmFolder.Database, XP_Library_DB.DATABASE_NAME);
//                                } else {
//                                    LIBFS.deleteFile(MyApp.getContext(), Library_FS.enmFolder.SdCard, XP_Library_DB.DATABASE_NAME);
//                                }
//                            } else if (LIBFS.isDataOnDevice(MyApp.getContext(), XP_Library_DB.DATABASE_NAME)) {
//                                LIBFS.deleteFile(MyApp.getContext(), Library_FS.enmFolder.SdCard, XP_Library_DB.DATABASE_NAME);
//                            } else {
////                                Library_DB LIBDB = new Library_DB(MyApp.getContext());
////                                LIBDB.createDB(false);
////                                LIBDB.closeDb();
////                                LIBDB.close();
//
//                                if (LIBFS.copyFile(MyApp.getContext(), Library_FS.enmFolder.SdCard, Library_FS.enmFolder.Database, XP_Library_DB.DATABASE_NAME, null)) {
//                                    LIBFS.deleteFile(MyApp.getContext(), Library_FS.enmFolder.SdCard, XP_Library_DB.DATABASE_NAME);
//                                } else {
//                                    LIBFS.deleteFile(MyApp.getContext(), Library_FS.enmFolder.Database, XP_Library_DB.DATABASE_NAME);
//                                }
//                            }
//
////                            continueProcess();
//                        }
//                    };
//
//                    runnable.run();
//                }
//            }, new Runnable() {
//                @Override
//                public void run() {
//                    // NO
//                    Log.d(TAG, "NO");
////                    continueProcess();
//                }
//            }, new Runnable() {
//                @Override
//                public void run() {
//                    // DO NOT ASK
//                    Log.d(TAG, "DO NOT ASK");
//                    LIBCP.createIgnoreFile();
////                    continueProcess();
//                }
//            }, this);
//        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            LIBUI.popupExitDialog("Incompatible Version", "Sorry but your version of Android will not work with this application, it needs to be Android 4.4+", activity);
//        } else {
//            Log.d(TAG, "#3");
////            continueProcess();
//        }
    }

//    private void continueProcess() {
//        Library_DB LIBDB = new Library_DB(MyApp.getContext());
//        LIBDB.createDB(true);
//        LIBDB.closeDb();

//        Log.d(TAG, "#4");

//        Thread thread = new Thread(new WorkerThread());
//        thread.start();

//        final Handler handlerPermissions = new Handler();
//        handlerPermissions.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (LIBPE.permissionGranted == Library_PE.enmPermissionGranted.Granted || LIBPE.permissionGranted == Library_PE.enmPermissionGranted.Refused) {
//                    Library_GR LIBGR = new Library_GR();
//
//                    LIBGR.initialiseGraphics(activity);
//
//                    Log.d(TAG, String.format("Permission received %s", LIBPE.permissionGranted));
//                    startService(new Intent(MyApp.getContext(), Service_Closing.class));
//
//                    startService(new Intent(MyApp.getContext(), Service_MD.class));
//                    startService(new Intent(MyApp.getContext(), Service_WS.class));
//                    startService(new Intent(MyApp.getContext(), Service_PK.class));
//
//                    Library_GG LIBGG = new Library_GG(activity);
//                    LIBGG.checkPremium();
//
//                    Intent intent = new Intent(Activity_Splash.this, Activity_Main.class);
//
//                    if (LIBPE.permissionGranted == Library_PE.enmPermissionGranted.Granted) {
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Library_UI LIBUI = new Library_UI();
//                        LIBUI.popupExitDialog("Permission Denied", "Sorry but you need to accept all permissions in order to access this application", activity);
//                    }
//                } else if (count > (DELAY_UNTIL_FINISH * 1000)) {
//                    Log.w(TAG, "Permissions did not respond in a sensible interval");
//                    activity.finish();
//                } else {
//                    count += 1;
//                    Log.w(TAG, "Splash screen delayed, waiting for response from permission");
//                    handlerPermissions.postDelayed(this, DELAY * 1000);
//                }
//            }
//        }, DELAY);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Library_PE LIBPE = new Library_PE();
//        LIBPE.permissionGranted = (LIBPE.verifyPermissions(grantResults) ? Library_PE.enmPermissionGranted.Granted : Library_PE.enmPermissionGranted.Refused);
//    }
}