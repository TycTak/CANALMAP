package com.tyctak.canalmap.libraries;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tyctak.canalmap.Global;
import com.tyctak.map.entities._Route;
import com.tyctak.map.entities._ShortLocation;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class Library_OS {
    private String TAG = "Library_OS";

//    public Long handlerJSON(String category, Long currentServerDate, String input) throws JSONException {
//        Long retval = 0l;
//
//        JSONObject json = new JSONObject(input);
//
//        if (json != null) {
//            String connection = json.getString("cnn");
//
//            if (connection.equals("ok")) {
//                JSONArray arry = (JSONArray) json.get("ary");
//
//                for (int i = 0; i < arry.length(); i++) {
//                    try {
//                        JSONObject item = arry.getJSONObject(i);
//
//                        if (category.equals("entities")) {
//                            _Entity entity = new _Entity();
//                            if (entity.fromJSON(item)) {
//                                if (!entity.EntityGuid.equals(Global.getMyEntityGuid())) {
//                                   Global.getInstance().getDb().writeEntity("SQL SC", entity);
//                                }
//                            }
//                        } else {
//                            _PoiLocation poiLocation = new _PoiLocation();
//                            if (poiLocation.fromJSON(item)) {
//                                Global.getInstance().getDb().writePoiLocation("SQL SC", poiLocation);
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//                if (category.equals("entities")) {
//                    Global.getInstance().getDb().writeEntityLastServerDate(currentServerDate);
//                    retval = currentServerDate;
//                } else {
//                    JSONArray sds = (JSONArray) json.get("sds");
//                    Long lowestServerDate = currentServerDate;
//
//                    for (int i = 0; i < sds.length(); i++) {
//                        try {
//                            JSONObject item = sds.getJSONObject(i);
//
//                            String subCategory = item.getString("sc");
//                            Long serverDate = item.getLong("sd");
//
//                            if (lowestServerDate > serverDate || lowestServerDate == 0) lowestServerDate = serverDate;
//
//                            Global.getInstance().getDb().writeRouteLastServerDate(subCategory, serverDate);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                    retval = (arry.length() == 0 ? currentServerDate : lowestServerDate);
//                }
//            }
//        }
//
//        return retval;
//    }

//    public GeoPoint getClosestCanalTile(GeoPoint point) {
//        GeoPoint retval = null;
//        ArrayList<Point> points = GetAllSurroundingTiles(point, 17);
//
//        if (points.size() != 0) {
//            retval = new GeoPoint(points.get(0).x, points.get(0).y);;
//        } else {
//            int zoom = 16;
//
//            while (zoom <= 17) {
//                while (points.size() == 0) {
//                    points = GetAllSurroundingTiles(point, zoom);
//                    zoom -= 1;
//                }
//
//                zoom += 1;
//
//                if (zoom <= 16) {
//                    point = new GeoPoint(points.get(0).x, points.get(0).y);
//                    zoom += 1;
//                }
//            }
//
////            {
////                {
////                    zoom -= 1;
////                    points = GetAllSurroundingTiles(point, zoom);
////                }
////                while (points.size() == 0) ;
////
////                if (zoom < 17) {
////                    zoom += 2;
////                    point = new GeoPoint(points.get(0).x, points.get(0).y);
////                }
////            } while (zoom < 18);
//
//            retval = new GeoPoint(points.get(0).x, points.get(0).y);
//        }
//
//        return retval;
//    }

//    public boolean getTileExists(GeoPoint geoPoint, int zoom) {
//        Point point = getMapTileFromCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude(), zoom);
//        int key = ((zoom << zoom) + point.x << zoom) + point.y;
//
////        final int x = pTile.getX();
////        final int y = pTile.getY();
////        final int z = pTile.getZoomLevel();
////        final int index = ((z << z) + x << z) + y;
////
////        Log.d(TAG, "TILE x=" + x + ",y=" + y + ",z=" + z);
////
////        _Tile tile = Global.getLIBDB().getTile(index)
//
//        _Tile tile = Global.getInstance().getDb().getTile(key);
//        return (tile.Priority != -1 && tile.Priority != -2);
//        //        return Global.getLIBDB().getTileExists(key);
//    }

    public boolean isRoutePurchased(GeoPoint geoPoint) {
        int zoom = 15;
        Point point = getMapTileFromCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude(), zoom);
        int key = ((zoom << zoom) + point.x << zoom) + point.y;

        Log.d(TAG, "TILE_IRP x=" + point.x + ",y=" + point.y + ",z=" + zoom + ",k=" + key);

        return Global.getInstance().getDb().getIsRoutePurchased(key);
    }

    public ArrayList<_Route> getRoutes(int zoom, GeoPoint geoPoint) {
        Point point = getMapTileFromCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude(), zoom);
        int key = ((zoom << zoom) + point.x << zoom) + point.y;

        return Global.getInstance().getDb().getListCrossCheckedRoutes(key);
    }

//    public String getRoutes(int zoom, double latitude, double longitude) {
//        String retval = "";
//
//        Point point = getMapTileFromCoordinates(latitude, longitude, zoom);
//        int key = ((zoom << zoom) + point.x << zoom) + point.y;
//
//        ArrayList<_Route> routes = Global.getInstance().getDb().getListCrossCheckedRoutes(key);
//
//        for (_Route route : routes) {
//            if (retval.isEmpty()) {
//                retval += route.RouteGuid;
//            } else {
//                retval += "," + route.RouteGuid;
//            }
//        }
//
//        return retval;
//    }

    public Point getMapTileFromCoordinates(final double latitude, final double longitude, final int zoom) {
        final int y = (int) Math.floor((1 - Math.log(Math.tan(latitude * Math.PI / 180) + 1 / Math.cos(latitude * Math.PI / 180)) / Math.PI) / 2 * (1 << zoom));
        final int x = (int) Math.floor((longitude + 180) / 360 * (1 << zoom));
        return new Point(x, y);
    }

//    private double ConvertToRadians(double angle)
//    {
//        return (angle * Math.PI) / 180;
//    }
//
//    private double ToDegrees(double radians)
//    {
//        return radians * 180 / Math.PI;
//    }

//    public ArrayList<Point> GetAllSurroundingTiles(GeoPoint point, int zoom)
//    {
//        Integer[] zoomLevelRadius =  new Integer[19];
//        zoomLevelRadius[7] = 50000;
//        zoomLevelRadius[8] = 35000;
//        zoomLevelRadius[9] = 25000;
//        zoomLevelRadius[10] = 17000;
//        zoomLevelRadius[11] = 12000;
//        zoomLevelRadius[12] = 9000;
//        zoomLevelRadius[13] = 6500;
//        zoomLevelRadius[14] = 3750;
//        zoomLevelRadius[15] = 2250;
//        zoomLevelRadius[16] = 1250;
//        zoomLevelRadius[17] = 650;
//
//        int radius = zoomLevelRadius[zoom];
//
//        ArrayList<Point> points = new ArrayList();
//
//        double latitudeRadians = ConvertToRadians(point.getLatitude());
//        double longitudeRadians = ConvertToRadians(point.getLongitude());
//
//        int circles = (radius / 300);
//
//        for (int x = circles; x <= radius; x = x + circles) {
//            for (int i = 0; i < 360; i = i + 45) {
//                double d = x / RADIUS_EARTH_METERS;
//
//                double angleRadians = ConvertToRadians(i);
//                double newLatitudeRadians = Math.asin(Math.sin(latitudeRadians) * Math.cos(d) + Math.cos(latitudeRadians) * Math.sin(d) * Math.cos(angleRadians));
//                double newLongitudeRadians = longitudeRadians + Math.atan2(Math.sin(angleRadians) * Math.sin(d) * Math.cos(latitudeRadians), Math.cos(d) - Math.sin(latitudeRadians) * Math.sin(latitudeRadians));
//
//                double latitude = ToDegrees(newLatitudeRadians);
//                double longitude = ToDegrees(newLongitudeRadians);
//
//                Point newPoint = getMapTileFromCoordinates(latitude, longitude, zoom);
//                if (!points.contains(newPoint)) points.add(newPoint);
//            }
//        }
//
//        return points;
//    }

//    public OverlayItem getEntityOverlayItem(Context context, _ShortLocation shortLocation, int zoomLevel) {
//        OverlayItem retval;
//
//        GeoPoint location = new GeoPoint(shortLocation.Latitude, shortLocation.Longitude);
//        retval = new OverlayItem(shortLocation.Name, shortLocation.Id, location);
//        retval.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
//
//        Library_GR LIBGR = new Library_GR();
//        Drawable marker = LIBGR.getResizedDrawable(shortLocation.Name, shortLocation.IsContent, context, zoomLevel);
//
//        retval.setMarker(marker);
//
//        return retval;
//    }

    public OverlayItem getOverlayItem(Context context, _ShortLocation shortLocation, int zoomLevel) {
        OverlayItem retval;

        Log.d(TAG, "getOverlayItem");

        GeoPoint location = new GeoPoint(shortLocation.Latitude, shortLocation.Longitude);
        retval = new OverlayItem(shortLocation.Name, shortLocation.Id, location);

        retval.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);

        Library_GR LIBGR = new Library_GR();
        Drawable marker = LIBGR.getResizedDrawable(shortLocation.Name, shortLocation.IsContent, context, zoomLevel);

        retval.setMarker(marker);

        return retval;
    }

//    public OverlayItem getEntityItem(Context context, _Entity entity, int zoomLevel) {
//        OverlayItem retval;
//
//        GeoPoint location = new GeoPoint(entity.Latitude, entity.Longitude);
//        retval = new OverlayItem(entity.EntityGuid, entity.EntityName, entity.Description, location);
//
//        String name = context.getResources().getResourceName(entity.Icon);
//        boolean isAvatar = (name.contains("ic_icon__marker"));
//
//        Library_GR LIBGR = new Library_GR();
//        retval.setMarker(LIBGR.getResizedIcon(context, entity.Icon, entity.IconName, zoomLevel, entity.AvatarMarker, entity.Status, entity.Direction, isAvatar, entity.ZeroAngleFixed));
//
//        if (!isAvatar) {
//            retval.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
//        } else {
//            retval.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
//        }
//
//        return retval;
//    }
}