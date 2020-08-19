package com.tyctak.map.libraries;

public class XP_Library_MP {

    public static double convertZoomLevel(int zoomLevel) {
        double retval = 0;

        switch (zoomLevel) {
            case (1) :
                retval = 0.1;
                break;
        }

        return retval;
    }

    public static Double convertToRadians(Double degree) {
        return degree * Math.PI / 180;
    }
}
