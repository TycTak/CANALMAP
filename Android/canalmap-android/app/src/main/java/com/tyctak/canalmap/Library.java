package com.tyctak.canalmap;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Library {

    private static final String TAG = "Library";

    public static long getAvailableSpaceInMB(){
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        return availableSpace/SIZE_MB;
    }

    public static boolean isServiceRunning(Class<?> serviceClass){
        final ActivityManager activityManager = (ActivityManager) MyApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            Log.d(TAG, String.format("Service:%s", runningServiceInfo.service.getClassName()));
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
                return true; //Bootstrap.getInstance().getDatabase().getAnyPaused();
            }
        }

        return false;
    }

    public Integer ConvertToDensityPixels(Activity parent, Integer pixels) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, parent.getResources().getDisplayMetrics());
    }

    //    public Date getDate(Long date) {
//        return new Date(date);
//    }

    public static byte[] encodeBinary(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        return b;
    }

    public static Bitmap decodeBinary(byte[] image) {
        Bitmap retval = null;
        if (image != null) retval = BitmapFactory.decodeByteArray(image, 0, image.length);
        return retval;
    }
}
