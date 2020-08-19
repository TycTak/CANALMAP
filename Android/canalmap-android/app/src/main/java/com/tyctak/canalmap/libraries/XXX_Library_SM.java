package com.tyctak.canalmap.libraries;

import com.tyctak.canalmap.Global;
import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._PoiLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class XXX_Library_SM {

    public final static String BACKUP_FILE = "tyctak_backup_cancamapp.json";

    public boolean isRestore() {
        boolean retval = false;

        ArrayList<_PoiLocation> poiLocations = Global.getInstance().getDb().getPoiAllLocations(Global.getInstance().getDb().getMyEntityGuid());
        retval = (poiLocations.size() == 0);

        return retval;
    }

    public boolean restoreBackup(String value) {
        boolean retval = false;

        try {
            JSONObject jsonObj = new JSONObject(value);

            JSONArray poiArray = jsonObj.optJSONArray("pl");
            JSONObject entityJSON = jsonObj.optJSONObject("es");

            long lastLocalDate = jsonObj.getLong("ll");
            long currentLocalDate = jsonObj.getLong("cd");
            int batchId = jsonObj.getInt("bt");

            _Entity entity = new _Entity();
            entity.fromJSON(entityJSON);

            Global.getInstance().getDb().writeMySettingsRestore(lastLocalDate, currentLocalDate, batchId);

            if (entity != null) {
                if (!Global.getInstance().getDb().getMyEntityGuid().equals(entity.EntityGuid)) {
                    Global.getInstance().getDb().writeEntityGuid(Global.getInstance().getDb().getMyEntityGuid(), entity.EntityGuid);
                }

                Global.getInstance().getDb().writeEntity(null, entity);
                //getDatabase().writeEntity(entity.EntityGuid, entity.EntityName, entity.Description, null, entity.Icon, entity.IconName, null, entity.AvatarChecked, entity.EntityType, entity.Phone, entity.TradingName, entity.Facebook, entity.Twitter, entity.Instagram, entity.Website, entity.Email, entity.Reference, entity.MetaData1, entity.MetaData2, entity.YouTube, entity.Patreon, entity.IsActive);

                Global.getInstance().getDb().refreshMyEntitySettings();
            }

            if (poiArray != null) {
                for (int i = 0; i < poiArray.length(); i++) {
                    JSONObject item = poiArray.getJSONObject(i);
                    item.put("gd", entity.EntityGuid);

                    _PoiLocation poiLocation = new _PoiLocation();
                    poiLocation.fromJSON(item);

                    Global.getInstance().getDb().writePoiLocation(null, poiLocation);
                }
            }

            retval = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retval;
    }

//    public String readBackup(Context context) {
//        String retval = "";
//
//        Library_FS LIBFS = new Library_FS();
//        byte[] bytes = LIBFS.readFile(context, Library_FS.enmFolder.SdCard, XXX_Library_SM.BACKUP_FILE);
//
//        if (bytes != null) {
//            try {
//                retval = new String(bytes, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return retval;
//    }
//
//    public boolean writeBackup(Context context, String jsonBackup) {
//        Library_FS LIBFS = new Library_FS();
//        return LIBFS.createFile(context, Library_FS.enmFolder.SdCard, XXX_Library_SM.BACKUP_FILE, jsonBackup.getBytes());
//    }
//
//    public String createBackup() {
//        String retval = null;
//
//        try {
//            JSONObject json = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//
//            ArrayList<_PoiLocation> poiLocations = Global.getInstance().getDb().getPoiAllLocations(Global.getInstance().getDb().getMyEntityGuid());
//
//            for (_PoiLocation poi : poiLocations) {
//                jsonArray.put(poi.toJSON());
//            }
//
//            json.put("pl", jsonArray);
//
//            json.put("es", Global.getInstance().getDb().getMyEntitySettings().toJSON());
//
//            long[] values = Global.getInstance().getDb().getMySettingsBackup();
//
//            json.put("ll", values[0]);
//            json.put("cd", values[1]);
//            json.put("bt", values[2]);
//
//            retval = json.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return retval;
//    }
}