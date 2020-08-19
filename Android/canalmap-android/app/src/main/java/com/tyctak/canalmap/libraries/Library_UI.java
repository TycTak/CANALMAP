package com.tyctak.canalmap.libraries;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.tyctak.canalmap.BuildConfig;
import com.tyctak.canalmap.Global;
import com.tyctak.canalmap.Library;
import com.tyctak.canalmap.MyApp;
import com.tyctak.canalmap.R;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._Poi;

import java.util.ArrayList;

public class Library_UI {

    private final String TAG = "Library_UI";

    public void dV(View target, boolean isVisible) {
        target.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void dVi(View target, boolean isVisible) {
        target.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void dV(View target, int value) {
        if (target.getVisibility() != value) target.setVisibility(value);
    }

    public String getAppVersion() {
        String retval;

        try {
            PackageInfo packageInfo = MyApp.getContext().getPackageManager().getPackageInfo(MyApp.getContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            retval = MyApp.getContext().getString(R.string.version) + " " + versionName + (BuildConfig.DEBUG ? "d " : " ") + Global.getInstance().getDb().getDatabaseVersion() + ":" + Global.getInstance().getDb().getClassVersion();
        } catch (PackageManager.NameNotFoundException e) {
            retval = MyApp.getContext().getString(R.string.versionUnknown);
        }

        return retval;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void runSnackBarOnUIThread(final Activity parent, final Integer id) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Library_UI LIBUI = new Library_UI();
                LIBUI.snackBar(parent, id);
                //R.string.msg_failed_network
            }
        };
        mainHandler.post(myRunnable);
    }

    public void snackBar(final Activity parent, final Integer id, final Snackbar.Callback callback) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
                Snackbar snackbar = Snackbar.make(viewGroup, id, Snackbar.LENGTH_LONG);
                snackbar.addCallback(callback);
                snackbar.show();
            }
        };
        mainHandler.post(myRunnable);

//        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
//        Snackbar snackbar = Snackbar.make(viewGroup, id, Snackbar.LENGTH_LONG);
//        snackbar.addCallback(callback);
//        snackbar.show();
    }

    public void snackBarError(Activity parent, String message) {
        String errorText = parent.getString(R.string.msg_email_support);
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar.make(viewGroup, errorText + ". " + message, Snackbar.LENGTH_LONG).show();
    }

    public void snackBar(final Activity parent, final Integer id) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
                Snackbar snackbar = Snackbar.make(viewGroup, id, Snackbar.LENGTH_LONG);
//                snackbar.addCallback(callback);
                snackbar.show();
            }
        };
        mainHandler.post(myRunnable);

//        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
//        Snackbar.make(viewGroup, id, Snackbar.LENGTH_LONG).show();
    }

    public void addPois(final boolean isPremium, final String area, final double[] points, final TextView markerText, final ImageView imageView, final int index, final int poiId, final Activity parent, ArrayList<_Poi> pois, FlexboxLayout flowLayout, boolean withText, final Runnable runnable) {
        if (flowLayout == null) return;

        final Library LIB = new Library();

        int imgWidth = LIB.ConvertToDensityPixels(parent, 40);
        int imgHeight = LIB.ConvertToDensityPixels(parent, 40);
        int txtWidth = LIB.ConvertToDensityPixels(parent, 110);
        int rightMargin = LIB.ConvertToDensityPixels(parent, 5);
        int leftMargin = LIB.ConvertToDensityPixels(parent, 12);
        int topMargin = LIB.ConvertToDensityPixels(parent, 15);

        Library_GR LIBGR = new Library_GR();

        for (final _Poi poi : pois) {
            LinearLayout ll = new LinearLayout(parent);
            ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setPadding((!withText ? leftMargin : 0 ), topMargin, rightMargin, 0);

            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    _MySettings mySettings = Global.getInstance().getDb().getMySettings();
//                    XP_Library_SC XPLIBSC = new XP_Library_SC();

                    if (isPremium) {
                        Log.d(TAG, "onClick");

                        ImageView vw = (ImageView) ((LinearLayout) view.getParent()).findViewById(9999);

                        Library_GR LIBGR = new Library_GR();
                        Bitmap bitmap = LIBGR.getIconFiltered(MyApp.getContext(), poi, true);

                        vw.setImageDrawable(new BitmapDrawable(bitmap));

                        Library_UI LIBUI = new Library_UI();

                        if (!poi.Filter) {
                            LIBUI.snackBar(parent, "That POI will NOT be shown on the map");
                            vw.setImageAlpha(110);
                        } else {
                            LIBUI.snackBar(parent, "That POI will be shown on the map");
                            vw.setImageAlpha(255);
                        }
                    } else {
                        Library_UI LIBUI = new Library_UI();
                        LIBUI.snackBar(parent, "You need to be a premium subscriber");
                    }
                }
            };

            ImageView imgV = LIBGR.getImage(parent, poi.Name, imgWidth, imgHeight, 0, 0, 0, 0, (!withText ? new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (poi.Filter) {
                        int id = (int) parent.getResources().getIdentifier(poi.Name, "drawable", parent.getPackageName());
                        Library_GR LIBGR = new Library_GR();
                        Bitmap marker = LIBGR.getDrawableImage(parent, id, 384, 384);
                        if (marker != null) {
                            Bitmap image = null;
                            if (imageView != null) {
                                image = LIBGR.getBitmapFromImageView(imageView);
                            }

                            String text = markerText.getText().toString();

                            int rowId = (int) imageView.getTag();

                            byte[] imageBinary = (image == null ? null : LIB.encodeBinary(image));
//                            byte[] markerBinary = (marker == null ? null : LIB.encodeBinary(marker));
                            _MySettings mySettings = Global.getInstance().getDb().getMySettings();

                            rowId = Global.getInstance().getDb().writePoiLocation(mySettings.IsAdministrator, rowId, area, poi.Name, points, 0, false, Global.getInstance().getDb().getMyEntityGuid(), imageBinary, poi.Action, text);

                            Intent intent = new Intent();
                            intent.putExtra("PoiId", rowId);
                            parent.setResult(Activity.RESULT_OK, intent);
                        }

                        parent.finish();
                    }
                }
            } : onClick));

//            selectedImage.setImageDrawable(new BitmapDrawable(LIBGR.getDrawableImage(MyApp.getContext(), poiName, 50, 50)));
            imgV.setImageDrawable(new BitmapDrawable(LIBGR.getIconFiltered(MyApp.getContext(), poi, false)));

//            Library_UI LIBUI = new Library_UI();
            if (!poi.Filter) {
//                LIBUI.snackBar(parent, "That POI will NOT be shown on the map");
                imgV.setImageAlpha(110);
            } else {
//                LIBUI.snackBar(parent, "That POI will be shown on the map");
                imgV.setImageAlpha(255);
            }

            ll.addView(imgV);

            if (withText) {
                TextView txt = new TextView(parent);
                txt.setTag(poi.Name);
                txt.setHeight(imgHeight);
                txt.setWidth(txtWidth);
                txt.setPadding(leftMargin, 0, 0, 0);
                txt.setGravity(Gravity.VERTICAL_GRAVITY_MASK);

                txt.setText(poi.Description);
                ll.addView(txt);

                txt.setOnClickListener(onClick);
            }

            flowLayout.addView(ll);
        }

        flowLayout.invalidate();
    }

    public void snackBar(final Activity parent, final String message) {
        Handler mainHandler = new Handler(parent.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
                Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG).show();
            }
        };
        mainHandler.post(myRunnable);

//        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) parent.findViewById(android.R.id.content)).getChildAt(0);
//        Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG).show();
    }

    public void confirmationDialogYesNo(String title, String message, final Runnable method, final Activity target) {
        confirmationDialogYesNo(title, message, method, null, target);
    }

    public void confirmationDialogYesNo(String title, String message, final Runnable yesMethod, final Runnable noMethod, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (yesMethod != null) yesMethod.run();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (noMethod != null) noMethod.run();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void confirmationDialogYesNoDontAsk(String title, String message, final Runnable yesMethod, final Runnable noMethod, final Runnable dontAskMethod, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                yesMethod.run();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                noMethod.run();
            }
        }).setNeutralButton("Don't Ask", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dontAskMethod.run();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void confirmationDialogMarkersSymbolsNo(String title, String message, final Runnable markersMethod, final Runnable symbolsMethod, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setCancelable(true).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Markers", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                markersMethod.run();
            }
        }).setNegativeButton("Symbols", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                symbolsMethod.run();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public Integer getUpdateGpsInterval(Integer update) {
        Integer retval;

        switch (update) {
            case 0: retval = (10 * 1000); break;
            case 1: retval = (15 * 1000); break;
            case 2: retval = (20 * 1000); break;
            case 3: retval = (25 * 1000); break;
            case 4: retval = (30 * 1000); break;
            case 5: retval = (60 * 1000); break;
            case 6: retval = (90 * 1000); break;
            case 7: retval = (120 * 1000); break;
            case 8: retval = (150 * 1000); break;
            default: retval = (180 * 1000); break;
        }

        return (int)retval;
    }

    public String getUpdateGpsText(Integer update) {
        String retval;

        switch (update) {
            case 0: retval = MyApp.getContext().getString(R.string.gpstext0); break;
            case 1: retval = MyApp.getContext().getString(R.string.gpstext1); break;
            case 2: retval = MyApp.getContext().getString(R.string.gpstext2); break;
            case 3: retval = MyApp.getContext().getString(R.string.gpstext3); break;
            case 4: retval = MyApp.getContext().getString(R.string.gpstext4); break;
            case 5: retval = MyApp.getContext().getString(R.string.gpstext5); break;
            case 6: retval = MyApp.getContext().getString(R.string.gpstext6); break;
            case 7: retval = MyApp.getContext().getString(R.string.gpstext7); break;
            case 8: retval = MyApp.getContext().getString(R.string.gpstext8); break;
            default: retval = MyApp.getContext().getString(R.string.gpstext); break;
        }

        return retval;
    }

    public void confirmationDialogSelectImage(final Runnable galleryMethod, final Runnable cameraMethod, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle("Select Image");
        builder.setMessage("Do you want to select an image from your gallery or take a new one with the camera").setCancelable(false).setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                galleryMethod.run();
            }
        }).setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cameraMethod.run();
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void popupExitDialog(String title, String message, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                target.setResult(Activity.RESULT_CANCELED);
                target.finishAffinity();
                System.exit(0);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                target.setResult(Activity.RESULT_CANCELED);
                target.finishAffinity();
                System.exit(0);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void popupMessageDialog(String title, String message, final Activity target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(target);

        builder.setTitle(title);
        builder.setMessage(message).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                target.finishAffinity();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                target.finishAffinity();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
