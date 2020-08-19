package com.tyctak.map.libraries;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._MySettings;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class XP_Library_SC {

    private final String TAG = "XP_Library_SC";
    public final static String MyInitQ = "Tk0NGUtZjk5MC";

    public enum enmPublisher {
        Private,
        Publish,
        Request
    }

    private SecretKeySpec getSKS() {
        String localId = Bootstrap.getInstance().getDeviceId().substring(0, 16);
        return new SecretKeySpec(localId.getBytes(), "AES");
    }

    private Cipher getCipher(int mode) {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(mode, getSKS());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return cipher;
    }

    public String Decrypt(String message) {
        String retval = "";

        try {
            if (message != null) {
                Cipher cipher = getCipher(Cipher.DECRYPT_MODE);

                if (cipher != null) {
                    byte[] decodedValue = Base64.decode(message.getBytes("UTF-8"), Base64.DEFAULT + Base64.NO_WRAP);
                    byte[] decodedBytes = cipher.doFinal(decodedValue);
                    retval = (decodedBytes == null ? "" : new String(decodedBytes, "UTF-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public String Encrypt(String message) {
        String retval = null;

        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);

            if (cipher != null) {
                byte[] encodedBytes = cipher.doFinal(message.getBytes("UTF-8"));
                byte[] encodedValue = Base64.encode(encodedBytes, Base64.DEFAULT + Base64.NO_WRAP);
                retval = (encodedValue == null ? "" : new String(encodedValue, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public String getSecurityFeatures(boolean isSecurityPremium, boolean isPublisher, boolean isReviewer, boolean isAdministrator,
                                      double publisherLongitude, double publisherLatitude, int publisherRadius,
                                      double reviewerLongitude, double reviewerLatitude, int reviewerRadius) {

        String sp = XP_Library_DB.splitter;

        String retval = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
                (isPublisher ? "true" : "false"), sp, (isReviewer ? "true" : "false"), sp, (isAdministrator ? "true" : "false"), sp,
                publisherLongitude, sp, publisherLatitude, sp, publisherRadius, sp,
                reviewerLongitude, sp, reviewerLatitude, sp, reviewerRadius, sp, (isSecurityPremium ? "true" : "false"));

        return Encrypt(retval);
    }

    public boolean getMyPoi(String entityGuid) {
        boolean retval;

        _MySettings mySettings = Bootstrap.getInstance().getDatabase().getMySettings();
//        if (mySettings.IsAdministrator) {
//            retval = true;
//        } else {
        retval = entityGuid.equals(mySettings.EntityGuid);
//        }

        return retval;
    }

    public boolean isEdit(String itemEntityGuid, String category, double itemLongitude, double itemLatitude, boolean isMyPoi) {
        boolean retval = false;
        _MySettings mySettings = Bootstrap.getInstance().getDatabase().getMySettings();
        _Entity myEntity = Bootstrap.getInstance().getDatabase().getMyEntitySettings();

        try {
            if (mySettings.IsAdministrator) {
                retval = true;
            } else if (mySettings.IsReviewer && !category.equals("Editors")) {
                retval = true;
            } else if (mySettings.IsPublisher && !category.equals("Editors")) {
                Locality itemLocality = new Locality("SC", itemLatitude, itemLongitude, 0, 0, 0, false);

                if (mySettings.PublisherLongitude != 0.0) {
                    Locality locality = new Locality("SC", mySettings.PublisherLatitude, mySettings.PublisherLongitude, 0, 0, 0, false);
                    float distance = locality.distanceTo(itemLocality);
                    retval = (distance <= mySettings.PublisherRadius);
                } else if (myEntity.Latitude != 0.0) {
                    Locality locality = new Locality("SC", myEntity.Latitude, myEntity.Longitude, 0, 0, 0, false);
                    float distance = locality.distanceTo(itemLocality);
                    retval = (distance <= mySettings.PublisherRadius);
                } else {
                    retval = isMyPoi;
                }
            } else {
                retval = isMyPoi;
            }
        } catch (Exception ex) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return retval;
    }

    public boolean isPremium(_MySettings mySettings) {
        return (mySettings.IsPremium || mySettings.IsSecurityPremium);
    }
}