package com.tyctak.canalmap.libraries;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.tyctak.map.entities.ILibrary_PE;

import java.util.ArrayList;
import java.util.Arrays;

public class Library_PE {

    private final String TAG = "Library_PE";

    public final Library_PE activity = this;
    public final static int GET_PERMISSIONS = 45;

    public static ILibrary_PE.enmPermissionGranted permissionGranted = ILibrary_PE.enmPermissionGranted.Standby;
    private final int REQUEST_ACCESS = 0;

    public ILibrary_PE.enmPermissionGranted checkPermissions(Activity parent, Boolean forceRequest, String[] permissions) {
        ILibrary_PE.enmPermissionGranted retval = ILibrary_PE.enmPermissionGranted.Standby;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> failedPermissions = checkAllPermissions(parent, permissions);

            if (failedPermissions.size() > 0) {
                if (forceRequest) {
                    ActivityCompat.requestPermissions(parent, failedPermissions.toArray(new String[failedPermissions.size()]), REQUEST_ACCESS);
                } else {
                    retval = ILibrary_PE.enmPermissionGranted.Refused;
                }
            } else {
                retval = ILibrary_PE.enmPermissionGranted.Granted;
            }
        } else {
            retval = ILibrary_PE.enmPermissionGranted.Granted;
        }

        permissionGranted = retval;

        return retval;
    }

    private ArrayList<String> checkAllPermissions(Activity parent, String[] permissions) {
        ArrayList<String> retval = new ArrayList<>();
        retval.addAll(Arrays.asList(permissions));

        try {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(parent, permission) != PackageManager.PERMISSION_DENIED) {
                    retval.remove(permission);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        Boolean retval = true;

        if(grantResults.length >= 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    retval = false;
                    break;
                }
            }
        }

        return retval;
    }

    public void handleResult(Activity activity, Runnable granted, int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PERMISSIONS) {
            ILibrary_PE.enmPermissionGranted permissionGranted = ILibrary_PE.enmPermissionGranted.Refused;
            boolean exit = data.getBooleanExtra("exit", false);

            if (resultCode == Activity.RESULT_OK) {
                permissionGranted = ILibrary_PE.enmPermissionGranted.values()[data.getIntExtra("granted", ILibrary_PE.enmPermissionGranted.Refused.ordinal())];

                if (permissionGranted == ILibrary_PE.enmPermissionGranted.Granted) {
                    if (granted != null) granted.run();
                } else {
                    Library_UI LIBUI = new Library_UI();

                    if (exit) {
                        LIBUI.popupExitDialog("Permission Denied", "Sorry but you need to allow all permission(s) in order to access this application", activity);
//                    } else {
//                        LIBUI.popupMessageDialog("Permission Denied", "Sorry but you need to allow in order to do that", activity);
                    }
                }
            } else {
                Library_UI LIBUI = new Library_UI();
                if (exit) {
                    LIBUI.popupExitDialog("Permission Denied", "Sorry but you need to accept all permission(s) in order to access this application", activity);
                } else {
                    LIBUI.popupMessageDialog("Permission Denied", "Sorry but you need to accept in order to do that", activity);
                }
            }
        }
    }
}