package com.tyctak.map.libraries;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.zip.CRC32;

public class XP_Library_CM {

    public String concatenateString(String delimiter, ArrayList<String> values, boolean withQuotes) {
        String retval = "";

        for (String value : values) {
            if (!retval.isEmpty()) retval += delimiter;
            retval += (withQuotes ? "'" + value + "'" : value);
        }

        return retval;
    }

    public String concatenateInteger(String delimiter, ArrayList<Integer> values) {
        String retval = "";

        for (int value : values) {
            if (retval.isEmpty()) {
                retval += value;
            } else {
                retval += delimiter + value;
            }
        }

        return retval;
    }

    public boolean isBlank(String string) {
        if (string == null || string.length() == 0)
            return true;

        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i)))
                return false;
        }

        return true;
    }

    private boolean isWhitespace(int c){
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }

    public static Long getDate(Date date) {
        return (date != null ? date.getTime() : null);
    }

    public String UrlDecode(String value) {
        return (isBlank(value) ? null : URLDecoder.decode(value));
    }

    public static Date now() {
        return new Date();
    }

    public long nowAsLong() {
        return getDate(new Date());
    }

    public String UrlEncode(String value) {
        return (isBlank(value) ? null : URLEncoder.encode(value));
    }

    public static Long getCRC32(String value) {
        CRC32 crc = new CRC32();
        crc.update(value.getBytes());
        return crc.getValue();
    }

    public String getValue(String value) {
        return (isBlank(value) ? null : value);
    }

    public static void Delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getNumericDefault(String str, long defaultValue)
    {
        long retval = defaultValue;

        if (isNumeric(str)) {
            retval = Long.parseLong(str);
        }

        return retval;
    }

    public long getCRC() {
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        return rnd.nextLong();
    }

    public boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(Exception ex)
        {
            return false;
        }

        return true;
    }

    public String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String bytesToHex(byte[] bytes) {
        String retval = null;

        if (bytes != null) {
            char[] hexChars = new char[bytes.length * 2];

            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }

            retval = new String(hexChars);
        }

        return retval;
    }

    public Point getMapTileFromCoordinates(final double latitude, final double longitude, final int zoom) {
        final int y = (int) Math.floor((1 - Math.log(Math.tan(latitude * Math.PI / 180) + 1 / Math.cos(latitude * Math.PI / 180)) / Math.PI) / 2 * (1 << zoom));
        final int x = (int) Math.floor((longitude + 180) / 360 * (1 << zoom));
        return new Point(x, y);
    }

    public static long getOldestEntityAllowedToMove() {
        XP_Library_CM XPLIBCM = new XP_Library_CM();
        return (XPLIBCM.nowAsLong() - 7200000); // (2 * 60 * 60 * 1000) = 2 hours
    }

    public static long getOldestEntityAllowedToDisplay() {
        return (XP_Library_CM.getDate(XP_Library_CM.now()) - 2 * 604800000); // 2 weeks
    }
}
