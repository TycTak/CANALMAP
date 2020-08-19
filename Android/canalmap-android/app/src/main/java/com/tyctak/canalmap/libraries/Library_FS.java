package com.tyctak.canalmap.libraries;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.tyctak.canalmap.BuildConfig;
import com.tyctak.canalmap.Global;
import com.tyctak.canalmap.MyApp;
import com.tyctak.map.entities.ILibrary_FS;
import com.tyctak.map.libraries.XP_Library_DB;
import com.tyctak.map.libraries.XP_Library_FS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class Library_FS implements ILibrary_FS {

    private final String TAG = "Library_FS";

    public void copyDatabase() {
        if (!isFileExists(XP_Library_FS.enmFolder.SdCard, Global.getInstance().getDb().getDatabaseName()) || BuildConfig.DEBUG) {
            copyFile(XP_Library_FS.enmFolder.Database, XP_Library_FS.enmFolder.SdCard, Global.getInstance().getDb().getDatabaseName());
        }
    }

    public FileOutputStream getFileOutputStream(XP_Library_FS.enmFolder filePath, String fileName) {
        FileOutputStream fileOutputStream = null;

        try {
            switch (filePath) {
                case Assets:
                    AssetFileDescriptor afd = MyApp.getContext().getAssets().openFd(fileName);
                    fileOutputStream = afd.createOutputStream();
                    break;
                case Database:
                    fileOutputStream = new FileOutputStream(new File(MyApp.getContext().getDatabasePath(fileName).getPath(), fileName));
                    break;
                case SdCard:
                    fileOutputStream = new FileOutputStream(new File(getExternalStorageDirectory(), fileName));
                    break;
                case Files:
                    fileOutputStream = new FileOutputStream(new File(MyApp.getContext().getFilesDir(), fileName));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileOutputStream;
    }

    public FileInputStream getFileInputStream(XP_Library_FS.enmFolder filePath, String fileName) {
        FileInputStream fileInputStream = null;

        try {
            switch (filePath) {
                case Assets:
                    AssetFileDescriptor afd = MyApp.getContext().getAssets().openFd(fileName);
                    fileInputStream = afd.createInputStream();
                    break;
                case Database:
                    fileInputStream = new FileInputStream(new File("", MyApp.getContext().getDatabasePath(fileName).getPath()));
                    break;
                case SdCard:
                    fileInputStream = new FileInputStream(new File(getExternalStorageDirectory(), fileName));
                    break;
                case Files:
                    fileInputStream = new FileInputStream(new File(MyApp.getContext().getFilesDir(), fileName));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileInputStream;
    }

    public boolean isFileExists(XP_Library_FS.enmFolder filePath, String fileName) {
        boolean retval = false;
        FileInputStream fileInputStream = null;

        try {
            switch (filePath) {
                case Assets:
                    AssetFileDescriptor afd = MyApp.getContext().getAssets().openFd(fileName);
                    retval = (afd != null);
                    break;
                case Database:
                    fileInputStream = new FileInputStream(new File("", MyApp.getContext().getDatabasePath(fileName).getPath()));
                    retval = (fileInputStream != null);
                    break;
                case SdCard:
                    fileInputStream = new FileInputStream(new File(getExternalStorageDirectory(), fileName));
                    retval = (fileInputStream != null);
                    break;
                case Files:
                    fileInputStream = new FileInputStream(new File(MyApp.getContext().getFilesDir(), fileName));
                    retval = (fileInputStream != null);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }

//    public boolean isDatabase(Context context, boolean datOnSdCard, String databaseName) {
//        boolean retval = false;
//
//        boolean isFileExists = isFileExists(context, databaseName);
//        boolean isDataOnDevice = isDataOnDevice(context, databaseName);
//
//        if (!datOnSdCard && isDataOnDevice) {
//            retval = true;
//        } else if (datOnSdCard && isFileExists) {
//            retval = true;
//        }
//
//        return retval;
//    }

//    public String getExternalPath(Context context) {
//        String retval;
//
//        try {
//            File[] files = ContextCompat.getExternalFilesDirs(context, "");
//            retval = files[files.length - 1].getPath();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            retval = Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "files";
//        }
//
//        return retval;
//    }

//    public boolean isFileExists(String databasePath, String databaseName) {
//        File file = new File(databasePath, databaseName);
//        return file.exists();
//    }

//    public int getFileSize(Context context, String fileName) {
//        File file = new File(getExternalPath(context), fileName);
//        return (int) (file.length() / 1024);
//    }

//    public int getFileSize(String filePath) {
//        File file = new File("", filePath);
//        return (int) (file.length() / 1024 / 1024);
//    }

//    public boolean isDataOnDevice(Context context, String databaseName) {
//        File file = new File("", context.getDatabasePath(databaseName).getPath());
//        return file.exists();
//    }

//    public boolean deleteDatabase(Context context, boolean onSDCard, String databaseName) {
//        String databasePath;
//
//        if (onSDCard) {
//            databasePath = getExternalPath(context) + File.separator + databaseName;
//        } else {
//            databasePath = context.getDatabasePath(databaseName).getPath();
//        }
//
//        return deleteFile(databasePath);
//    }

    public boolean copyFile(XP_Library_FS.enmFolder source, XP_Library_FS.enmFolder destination, String fileName) {
        boolean retval = false;

        try {
            FileInputStream isSource = getFileInputStream(source, fileName);

            if (destination == XP_Library_FS.enmFolder.Assets) throw new RuntimeException("You cannot copy to the Assets folder, its read only");
            FileOutputStream osDestination = getFileOutputStream(destination, fileName);

            XP_Library_FS XPLIBFS = new XP_Library_FS();
            XPLIBFS.copyFile(isSource, osDestination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }

//    public ArrayList<_File> downloadZipFile(String url) {
//        ArrayList<_File> files = new ArrayList<>();
//        if (!Global.getNetworkAvailable()) return files;
//
//        try {
//            Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/octet-stream").build();
//            Response response = getInstance().newCall(request).execute();
//
//            if (response.code() == 200) {
//                XP_Library_ZP LIBZP = new XP_Library_ZP();
//
//                InputStream inputStream = response.body().byteStream();
//                BufferedInputStream bis = new BufferedInputStream(inputStream);
//                ArchiveInputStream zis = new ArchiveStreamFactory().createArchiveInputStream(bis);
//
//                ArchiveEntry ze = zis.getNextEntry();
//                while(ze != null) {
//                    files.add(LIBZP.extractFile(zis, ze));
//                    ze = zis.getNextEntry();
//                }
//
//                zis.close();
//                inputStream.close();
//            }
//
//            response.close();
//        } catch (ArchiveException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return files;
//    }

//    public ArrayList<String> readImportFile(FileInputStream fileInputStream) {
//        XP_Library_ZP XPLIBZP = new XP_Library_ZP();
//        ArrayList<String> contents = XPLIBZP.readImportFile(fileInputStream);
//
//        return contents;
//    }

//    public ArrayList<String> readFile(String filePath, String fileName) {
//        ArrayList<String> retval = null;
//
//        try {
//            FileReader fr = new FileReader(String.format("%s/%s",filePath, fileName));
////            fr.read()
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }

//    public ArrayList<String> readReleaseFile(Context context, int oldVersion, int newVersion) {
//        ArrayList<String> sqlScript = new ArrayList<>();
//
//        try {
//            InputStream inputStream = context.getAssets().open("release.sql");
////            FileDescriptor fd = context.getAssets().openFd("release.sql").getFileDescriptor();
////            FileReader fr = new FileReader(fd);
//
//
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            boolean started = false;
//            String upgradeKey = "--Upgrade " + newVersion;
//            String upgradeFromKey = "--Upgrade " + oldVersion + "-" + newVersion;
//
//            while ((line = br.readLine()) != null) {
//                if (line.equals(upgradeKey) || line.equals(upgradeFromKey)) {
//                    started = true;
//                } else if (line.startsWith("--Upgrade ")) {
//                    started = false;
//                } else if (started) {
//                    sqlScript.add(line);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return sqlScript;
//    }

//    public boolean createFile(String filePath) {
//        boolean retval = false;
//
//        try {
//            File file = new File(filePath);
//            retval = file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }

//    public boolean createFile(String filePath, byte[] contents) {
//        boolean retval = false;
//
//        try {
//            File file = new File(filePath);
//            retval = file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }

//    public byte[] readFile(Context context, enmFolder sourceFolder, String fileName) {
//        byte[] bytes = null;
//
//        try {
//            File filePath = null;
//
//            switch (sourceFolder) {
//                case Cache : filePath = context.getCacheDir(); break;
//                case SdCard: filePath = getExternalStorageDirectory(); break;
//            }
//
//            if (filePath != null) {
//                File file = new File(filePath + File.separator + fileName);
//
//                if (file.exists()) {
//                    int size = (int) file.length();
//                    bytes = new byte[size];
//
//                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//                    buf.read(bytes, 0, bytes.length);
//                    buf.close();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return bytes;
//    }

//    public boolean createFile(Context context, enmFolder destinationFolder, String fileName, byte[] contents) {
//        boolean retval = false;
//
//        try {
//            File filePath = null;
//
//            switch (destinationFolder) {
//                case Cache : filePath = context.getCacheDir(); break;
//                case SdCard: filePath = getExternalStorageDirectory(); break;
//            }
//
//            if (filePath != null) {
//                File file = new File(filePath + File.separator + fileName);
//                file.createNewFile();
//
//                FileOutputStream osDestination = new FileOutputStream(file);
//                osDestination.write(contents);
//                osDestination.flush();
//                osDestination.close();
//
//                retval = true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }

//    public boolean fileExists(String filePath) {
//        boolean retval = false;
//
//        File file = new File(filePath);
//        retval = file.exists();
//
//        return retval;
//    }

//    public ArrayList<String> readTextFile(InputStream inputStream) {
//        ArrayList<String> retval = new ArrayList<>();
//
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferreader = new BufferedReader(inputStreamReader);
//
//            String line;
//
//            while ((line = bufferreader.readLine()) != null) {
//                retval.add(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }
//
//    public ArrayList<String> readTextFile(String filePath, String fileName) {
//        ArrayList<String> retval = new ArrayList<>();
//
//        try {
//            FileReader fr = new FileReader(String.format("%s%s%s",filePath, File.separator, fileName));
//
//            BufferedReader bufferreader = new BufferedReader(fr);
//
//            String line;
//
//            while ((line = bufferreader.readLine()) != null) {
//                retval.add(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }

//    public boolean deleteFile(Context context, enmFolder source, String fileName) {
//        File file = null;
//        boolean retval = false;
//
//        FileOutputStream osDestination = null;
//        switch (source) {
//            case Assets:
//                throw new RuntimeException("You cannot delete a file in the Assets folder");
//            case Database:
//                file = new File("", context.getDatabasePath(fileName).getPath());
//                break;
//            case SdCard:
//                file = new File(getExternalStorageDirectory(), fileName);
//                break;
//            case SdCard:
//                file = new File(getExternalPath(context), fileName);
//                break;
//        }
//
//        if (file.exists()) retval = file.delete();
//
//        return retval;
//    }

//    public boolean deleteFile(String filePath) {
//        boolean retval = false;
//
//        File file = new File(filePath);
//        if (file.exists()) retval = file.delete();
//
//        return retval;
//    }

//    private static String mCurrentPhotoPath;
//
//    public File createImageFile(Context context, String part, String ext) {
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
//
//        File image = null;
//
//        try {
//             image = File.createTempFile(
//                    part,  /* prefix */
//                    ext,         /* suffix */
//                    storageDir      /* directory */
//            );
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//
//        return image;
//    }

    public Uri createTemporaryFile(String part, String ext)
    {
        File tempDir = new File(MyApp.getContext().getFilesDir(), "");
        //File tempDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
//        File tempDir = new File();
//        File tempDir = new File(getExternalPath(context) + "/temp/");
//        if(!tempDir.exists()) tempDir.mkdirs();

        File file = null;

        try {
            file = File.createTempFile(part, ext, tempDir);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri;

        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(MyApp.getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

//    public ArrayList<String> getFiles(String directory, final String pattern) {
//        FilenameFilter fnf = new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.matches(pattern) ;
//            }
//        };
//
//        ArrayList<String> files = new ArrayList<String>(Arrays.asList(new File(directory).list(fnf)));
//        Collections.sort(files);
//
//        return files;
//    }
}
