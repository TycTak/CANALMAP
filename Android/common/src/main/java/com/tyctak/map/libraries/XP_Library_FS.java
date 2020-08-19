package com.tyctak.map.libraries;

//import android.content.Context;
//import android.content.res.AssetFileDescriptor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;

//import com.tyctak.map.BuildConfig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

//import static android.os.Environment.getExternalStorageDirectory;

public class XP_Library_FS {

    private final String TAG = "Library_FS";

    public enum enmFolder {
        Assets,
        Database,
        SdCard,
        Files
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

//    public boolean isFileExists(String filePath, String fileName) {
//        File file = new File(filePath, fileName);
//        return file.exists();
//    }

//    public int getFileSize(String filePath, String fileName) {
//        File file = new File(filePath, filePath);
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

    public boolean copyFile(FileInputStream isSource, FileOutputStream osDestination) throws IOException {
        boolean retval = false;

        FileChannel isChannel = isSource.getChannel();
        FileChannel osChannel = osDestination.getChannel();

        osChannel.force(true);

        int length;
        final int bufferSize = (int) 1024000;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (true) {
            length = isChannel.read(buffer);
            if (length == -1) break;
            buffer.flip();
            osChannel.write(buffer);
            buffer.clear();
        }

        retval = true;

        osChannel.force(true);
        osDestination.flush();
        isChannel.close();
        osDestination.close();
        isSource.close();
        osDestination.close();

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

    public ArrayList<String> readImportFile(FileInputStream fileInputStream) {
        XP_Library_ZP XPLIBZP = new XP_Library_ZP();
        ArrayList<String> contents = XPLIBZP.readImportFile(fileInputStream);

        return contents;
    }

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

    public ArrayList<String> readTextFile(InputStream inputStream) {
        ArrayList<String> retval = new ArrayList<>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferreader = new BufferedReader(inputStreamReader);

            String line;

            while ((line = bufferreader.readLine()) != null) {
                retval.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retval;
    }

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

//    public Uri createTemporaryFile(Context context, String part, String ext)
//    {
//        File tempDir = new File(context.getFilesDir(), "");
//        //File tempDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
////        File tempDir = new File();
////        File tempDir = new File(getExternalPath(context) + "/temp/");
////        if(!tempDir.exists()) tempDir.mkdirs();
//
//        File file = null;
//
//        try {
//            file = File.createTempFile(part, ext, tempDir);
//            file.delete();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Uri uri;
//
//        if (Build.VERSION.SDK_INT >= 24) {
//            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
//        } else {
//            uri = Uri.fromFile(file);
//        }
//
//        return uri;
//    }

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
