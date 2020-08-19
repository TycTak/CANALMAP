package com.tyctak.map.libraries;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._Incoming;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.entities._PoiLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

public class XP_Library_PK {

    private final String TAG = "XP_Library_PK";
    private static final Logger LOGGER = Logger.getLogger( "XP_Library_DB" );

    private final int TIME_BUFFER = 0;
    private final int BATCH_SIZE = 100;

    private static String lastEntityJson = null;

    public void executeSend() {
        try {
            XP_Library_ZP XPLIBZP = new XP_Library_ZP();
            XP_Library_DB.getIncomingInfo info = Bootstrap.getInstance().getDatabase().getIncoming();

            LOGGER.info(String.format("getIncoming=%s", info.incomings.size()));

            String entityGuid = Bootstrap.getInstance().getMySettings().EntityGuid;

            long[] values = Bootstrap.getInstance().getDatabase().getSendPosition(TIME_BUFFER);
            boolean sendPosition = (values[0] != 0);
            _MySettings.enmEntityMoved entityMoved = _MySettings.enmEntityMoved.values()[(int) values[1]];
            long lastLocalDate = values[2];
            boolean isPremium = (values[3] > XP_Library_CM.getDate(XP_Library_CM.now()));
            long localDateUpdated = 0;

            JSONObject jsonObj;
            String batchFileName = null;

            if (info.incomings.size() != 0 && !info.isMore) {
                //Put latest on the very last record of all batches
                _Incoming incoming = info.incomings.get(info.incomings.size() - 1); //
                StringBuilder packet = Bootstrap.getInstance().getDatabase().getIncomingPacket(incoming.Id);
                batchFileName = incoming.FileName;
                jsonObj = new JSONObject(packet.toString());
                info.incomings.remove(info.incomings.size() - 1);
            } else {
                jsonObj = new JSONObject();
            }

            if (lastLocalDate >= 0 && !info.isMore) {
                _MySettings mySettings = Bootstrap.getInstance().getDatabase().getMySettings();
                ArrayList<_PoiLocation> poiLocations = Bootstrap.getInstance().getDatabase().getPoiLocations(mySettings.IsAdministrator, mySettings.IsReviewer, entityGuid, BATCH_SIZE, lastLocalDate);

                if (poiLocations.size() > 0) {
                    JSONArray arry;

                    if (!jsonObj.has("pl")) {
                        arry = new JSONArray();
                    } else {
                        arry = jsonObj.getJSONArray("pl");
                    }

                    for (_PoiLocation poi : poiLocations) {
                        if (poi.Shared == -1) {
                            Bootstrap.getInstance().getDatabase().writePrivatePublished(poi.Id);
                            poi.Shared = 0;
                        }

                        arry.put(poi.toJSON());
                    }

                    jsonObj.put("pl", arry);

                    localDateUpdated = poiLocations.get(poiLocations.size() - 1).Updated;
                }
            }

            JSONArray entities = new JSONArray();
            _Entity entity = Bootstrap.getInstance().getDatabase().getEntity(entityGuid);

            boolean sendEntity = false;

            if (isPremium && !info.isMore) {
                if (entityMoved == _MySettings.enmEntityMoved.StoppedNotSent || (entity.Publish && !sendPosition)) {
                    entity.Longitude = 0.0;
                    entity.Latitude = 0.0;
                    entity.Direction = 0;
                    sendEntity = true;
                    LOGGER.info(String.format("Send #B"));
                } else if (entity.Publish && sendPosition) {
                    sendEntity = true;
                    LOGGER.info(String.format("Send #C"));
                } else if (entityMoved == _MySettings.enmEntityMoved.PositionNotSent) {
                    sendEntity = true;
                    LOGGER.info(String.format("Send #D"));
                }
            } else if (entity.IsActive && !info.isMore) {
                LOGGER.info(String.format("Send #E"));
                entity.IsActive = false;
                entity.Longitude = 0.0;
                entity.Latitude = 0.0;
                entity.Direction = 0;
                Bootstrap.getInstance().getDatabase().writeMyEntityIsActive(Bootstrap.getInstance().getMySettings().EntityGuid, false);
                sendEntity = true;
            }

            String currentEntityJson = null;
            XP_Library_CM LIBCM = new XP_Library_CM();

            if (sendEntity && !LIBCM.isBlank(entity.EntityName)) {
                if (!jsonObj.has("es")) {
                    jsonObj.put("es", entities);
                } else {
                    for (int i = jsonObj.getJSONArray("es").length() - 1; i >= 0; i--) {
                        jsonObj.getJSONArray("es").remove(i);
                    }
                }

                currentEntityJson = entity.toJSON().toString();
                jsonObj.getJSONArray("es").put(entity.toJSON());
            }

            if (jsonObj.has("pl") || (jsonObj.has("es") && sendEntity && (LIBCM.isBlank(lastEntityJson) || !currentEntityJson.equals(lastEntityJson)))) {
                lastEntityJson = currentEntityJson;
                if (!jsonObj.has("pb")) jsonObj.put("pb", Bootstrap.getInstance().getMySettings().EncryptedEntityGuid);

                _Incoming incoming;

                if (batchFileName == null) {
                    int batchId = Bootstrap.getInstance().getDatabase().getMyBatchId(true);
                    batchFileName = XPLIBZP.batchFileName(Bootstrap.getInstance().getDatabase().getSystem().FtpPrefix, entityGuid, batchId);
                    incoming = Bootstrap.getInstance().getDatabase().insertPacket(batchFileName, jsonObj.toString());
                } else {
                    incoming = Bootstrap.getInstance().getDatabase().updatePacket(batchFileName, jsonObj.toString());
                }

                info.incomings.add(incoming);

                Bootstrap.getInstance().getDatabase().writeUpdateLocalDate(localDateUpdated);
                if (entity.Publish) Bootstrap.getInstance().getDatabase().writeEntityPublish(entityGuid);
            }

            if (info.incomings.size() > 0) {
                XP_Library_WS XPLIBWS = new XP_Library_WS();

                LOGGER.info("Bootstrap.getInstance().getMySettings().EncryptedEntityGuid = " + Bootstrap.getInstance().getMySettings().EntityGuid);

                String sessionPassword = XPLIBWS.getSessionPassword(Bootstrap.getInstance().getMySettings().EncryptedEntityGuid);

                if (!LIBCM.isBlank(sessionPassword)) {
                    for (_Incoming incoming : info.incomings) {
                        StringBuilder packet = Bootstrap.getInstance().getDatabase().getIncomingPacket(incoming.Id);
                        jsonObj = new JSONObject(packet.toString());
                        jsonObj.put("sp", sessionPassword);

                        if (XPLIBWS.sendPacket(incoming.FileName, jsonObj.toString())) {
                            Bootstrap.getInstance().getDatabase().writePacketCompleted(incoming.Id);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}