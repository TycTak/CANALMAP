package com.tyctak.map.libraries;

import com.tyctak.map.entities._File;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class XP_Library_ZP {
    public void zipFileADD(String fileName, byte[] byteStream, String zipFilePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            ZipEntry entry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(entry);

            zipOutputStream.write(byteStream, 0, byteStream.length);
            zipOutputStream.closeEntry();

            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public ArrayList<_File> getAllFiles(byte[] byteStream, ArrayList<_File> files) {

        try {
            InputStream inputStream = new ByteArrayInputStream(byteStream);
            ZipInputStream zis = new ZipInputStream(inputStream);

            ZipEntry ze;
            while((ze = zis.getNextEntry()) != null) {
                for (_File file: files) {
                    if (file.getName().equals(ze.getName())) {
                        _File importFile = extractFile(zis, ze);
                        file.setContents(importFile.getContents());
                        break;
                    }
                }
            }

            zis.close();
            inputStream.close();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return files;
    }

    public byte[] extractFile(byte[] byteStream, String fileName) {
        byte[] fileContents = null;

        try {
            InputStream inputStream = new ByteArrayInputStream(byteStream);
            ZipInputStream zis = new ZipInputStream(inputStream);

            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                if (ze.getName().equals(fileName)) {
                    _File importFile = extractFile(zis, ze);
                    fileContents = importFile.getContents();
                    break;
                }

                ze = zis.getNextEntry();
            }

            zis.close();
            inputStream.close();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return fileContents;
    }

    public String batchFileName(String prefix, String guid, int batchId) {
        return prefix + "_" + guid + "_" + String.format("%07d", batchId);
    }

    private _File extractFile(ZipInputStream zis, ZipEntry ze) throws IOException {
        ByteArrayOutputStream streamBuilder = new ByteArrayOutputStream();

        int bytesRead;
        byte[] tempBuffer = new byte[8192];
        while ((bytesRead = zis.read(tempBuffer)) != -1) {
            streamBuilder.write(tempBuffer, 0, bytesRead);
        }

//        String contents = new String(streamBuilder.toByteArray());

        return new _File(ze.getName().toLowerCase(), streamBuilder.toByteArray(), null);
    }

    public ArrayList<String> readImportFile(FileInputStream inputStream) {
        ArrayList<String> contents = new ArrayList<>();

        try {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ZipInputStream zis = new ZipInputStream(bis);

            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                _File importFile = extractFile(zis, ze);
                contents.add(new String(importFile.getContents()));
                ze = zis.getNextEntry();
            }

            zis.close();
            inputStream.close();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }
}