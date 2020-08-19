package com.tyctak.canalmap.libraries;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.tyctak.canalmap.MyApp;

import java.io.File;

public class XXX_Library_CP {
    private final String TAG = "Library_CP";
    private final int MAX_ALLOWED = 300; // MBs

//    final private String ignoreFlag = MyApp.getContext().getFilesDir() + "/ignore.flag";

//    public boolean isCopyToSDCard() {
//        Library_FS LIBFS = new Library_FS();
//
//        boolean isInstalledOnSDCard = isInstalledOnSdCard(MyApp.getContext());
//        boolean isFileExists = LIBFS.isFileExists(MyApp.getContext(), XP_Library_DB.DATABASE_NAME);
//        int fileSize = LIBFS.getFileSize(MyApp.getContext().getDatabasePath(XP_Library_DB.DATABASE_NAME).getPath());
//
//        return (isInstalledOnSDCard && !isFileExists && !getIgnoreFlag() && fileSize < MAX_ALLOWED);
//    }
//
//    public boolean isCopyToDevice() {
//        Library_FS LIBFS = new Library_FS();
//
//        boolean isInstalledOnSDCard = isInstalledOnSdCard(MyApp.getContext());
//        boolean isFileExists = LIBFS.isFileExists(MyApp.getContext(), XP_Library_DB.DATABASE_NAME);
//        int fileSize = LIBFS.getFileSize(LIBFS.getExternalPath(MyApp.getContext()) + File.separator + XP_Library_DB.DATABASE_NAME);
//
//        return (!isInstalledOnSDCard && isFileExists && !getIgnoreFlag() && fileSize < MAX_ALLOWED);
//    }

//    public boolean getIgnoreFlag() {
//        Library_FS LIBFS = new Library_FS();
//        return LIBFS.fileExists(ignoreFlag);
//    }

//    public boolean createIgnoreFile() {
//        Library_FS LIBFS = new Library_FS();
//        return LIBFS.createFile(ignoreFlag);
//    }
//
//    public boolean deleteIgnoreFile() {
//        Library_FS LIBFS = new Library_FS();
//        return LIBFS.deleteFile(ignoreFlag);
//    }

    private static boolean isInstalledOnSdCard(Context ctx) {
        boolean retval = false;

        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
            PackageManager pm = ctx.getPackageManager();

            try {
                PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
                ApplicationInfo ai = pi.applicationInfo;
                retval = (ai.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE;
            } catch (PackageManager.NameNotFoundException e) {
                // ignore
            }
        }

        return retval;
    }
}