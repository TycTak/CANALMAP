package com.tyctak.map.entities;

import com.tyctak.map.libraries.XP_Library_FS;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public interface ILibrary_FS {
    FileInputStream getFileInputStream(XP_Library_FS.enmFolder filePath, String fileName);
    FileOutputStream getFileOutputStream(XP_Library_FS.enmFolder filePath, String fileName);
    boolean isFileExists(XP_Library_FS.enmFolder filePath, String fileName);
    boolean copyFile(XP_Library_FS.enmFolder source, XP_Library_FS.enmFolder destination, String fileName);
}
