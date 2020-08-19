package com.tyctak.map.libraries;

import com.tyctak.map.entities._Entity;
import com.tyctak.map.entities._PoiLocation;
import com.tyctak.map.entities._Role;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class XP_Library_WS {

    private final String TAG = "XP_Library_WS";
    private final int SMALL_DELAY = 5;
    private static final Logger LOGGER = Logger.getLogger( "XP_Library_DB" );

    public Long handlerJSON(String category, Long currentServerDate, String input) throws JSONException {
        Long retval = 0l;

        JSONObject json = new JSONObject(input);

        if (json != null) {
            String connection = json.getString("cnn");

            if (connection.equals("ok")) {
                JSONArray arry = (JSONArray) json.get("ary");

                for (int i = 0; i < arry.length(); i++) {
                    try {
                        JSONObject item = arry.getJSONObject(i);

                        if (category.equals("entities")) {
                            _Entity entity = new _Entity();
                            if (entity.fromJSON(item)) {
                                if (!entity.EntityGuid.equals(Bootstrap.getInstance().getMySettings().EntityGuid)) {
                                    Bootstrap.getInstance().getDatabase().writeEntity("SQL SC", entity);
                                }
                            }
                        } else {
                            _PoiLocation poiLocation = new _PoiLocation();
                            if (poiLocation.fromJSON(item)) {
                                Bootstrap.getInstance().getDatabase().writePoiLocation("SQL SC", poiLocation);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (category.equals("entities")) {
                    Bootstrap.getInstance().getDatabase().writeEntityLastServerDate(currentServerDate);
                    retval = currentServerDate;
                } else {
                    JSONArray sds = (JSONArray) json.get("sds");
                    Long lowestServerDate = currentServerDate;

                    for (int i = 0; i < sds.length(); i++) {
                        try {
                            JSONObject item = sds.getJSONObject(i);

                            String subCategory = item.getString("sc");
                            Long serverDate = item.getLong("sd");

                            if (lowestServerDate > serverDate || lowestServerDate == 0) lowestServerDate = serverDate;

                            Bootstrap.getInstance().getDatabase().writeRouteLastServerDate(subCategory, serverDate);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    retval = (arry.length() == 0 ? currentServerDate : lowestServerDate);
                }
            }
        }

        return retval;
    }

//    private OkHttpClient getInstance() {
//        return Global.getClient();
//    }

//    public void cancelAsyncCalls() {
//        try {
//            getInstance().dispatcher().cancelAll();
//        } catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }

//    private _Role getRole(JSONArray arry) {
//        _Role role = new _Role();
//
//        try {
//            for (int i = 0; i < arry.length(); i++) {
//                JSONObject item = arry.getJSONObject(i);
//                String action = item.optString("a", "+");
//                String guid = item.getString("g");
//
//                if (action.equals("+")) {
//                    if (guid.equals("*") || Bootstrap.getInstance().getMySettings().EncryptedEntityGuid.equals(guid)) {
//                        role.isAuthorised = true;
//                        role.Radius = item.optInt("r", 0);
//                        role.Longitude = item.optDouble("lo", 0.0);
//                        role.Latitude = item.optDouble("la", 0.0);;
//                    }
//                } else if (action.equals("-")) {
//                    if (guid.equals("*") || Bootstrap.getInstance().getMySettings().EncryptedEntityGuid.equals(guid)) {
//                        role.isAuthorised = false;
//                        role.Radius = 0;
//                        role.Longitude = 0.0;
//                        role.Latitude = 0.0;
//                    }
//                }
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//
//        return role;
//    }

    private _Role getRole(JSONArray arry, String type) {
        _Role role = new _Role();

        try {
            for (int i = 0; i < arry.length(); i++) {
                JSONObject item = arry.getJSONObject(i);
                String itemType = item.optString("tp");

                if (itemType.equals(type)) {
                    role.isAuthorised = true;
                    role.Radius = item.optInt("rd", 0);
                    role.Longitude = item.optDouble("ln", 0.0);
                    role.Latitude = item.optDouble("la", 0.0);;
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return role;
    }

    public String encodeString(String value) {
        String retval = "";

        try {
            retval = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean getRoles() {
        boolean retval = false;
        String encryptedGuid = Bootstrap.getInstance().getMySettings().EncryptedEntityGuid;
        String appCode = Bootstrap.getInstance().getAppCode();

        try {
            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "roles.php?app=" + appCode + "&uid=" + encryptedGuid;

            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    JSONArray arry = (JSONArray) json.get("ary");

                    _Role reviewerRole = getRole(arry, "rv");
                    _Role publisherRole = getRole(arry, "pb");
                    _Role premiumRole = getRole(arry, "pr");
                    _Role administratorRole = getRole(arry, "sa");

                    retval = Bootstrap.getInstance().getDatabase().writeSecurityFeatures(premiumRole.isAuthorised, publisherRole.isAuthorised, reviewerRole.isAuthorised,
                            administratorRole.isAuthorised, publisherRole.Longitude, publisherRole.Latitude, publisherRole.Radius,
                            reviewerRole.Longitude, reviewerRole.Latitude, reviewerRole.Radius);
                } else {
                    LOGGER.info(String.format("Null JSON object returned so unable to check server"));
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public boolean addRole(_Role.enmRoles role, String mobile) {
        boolean retval = false;
        String encryptedGuid = Bootstrap.getInstance().getMySettings().EncryptedEntityGuid;

        XP_Library_CM XPLIBCM = new XP_Library_CM();
        String appCode = Bootstrap.getInstance().getAppCode();
        String encodedMobile = XPLIBCM.UrlEncode(mobile);

        String type = "";

        if (role == _Role.enmRoles.Publisher) {
            type = "pb";
        } else if (role == _Role.enmRoles.Administrator) {
            type = "sa";
        } else if (role == _Role.enmRoles.Premium) {
            type = "pr";
        } else if (role == _Role.enmRoles.Reviewer) {
            type = "rv";
        }

        try {
            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "addrole.php?app=" + appCode + "&uid=" + encryptedGuid + "&typ=" + type + "&mob=" + encodedMobile;

            String html = downloadTextGET(url);

            if (!XPLIBCM.isBlank(html) && html.equals("ok")) {
                retval = true;
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public boolean verifyRole(String verificationCode) {
        boolean retval = false;
        String encryptedGuid = Bootstrap.getInstance().getMySettings().EncryptedEntityGuid;
        String appCode = Bootstrap.getInstance().getAppCode();

        try {
            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "verifyrole.php?app=" + appCode + "&uid=" + encryptedGuid + "&cde=" + verificationCode;

            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html) && html.equals("ok")) {
                retval = true;
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return retval;
    }

    public String verifyPhoneNumber(String countryCode, String nationalPhoneNumber) {
        String retval = "";

        try {
            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "verifyphone.php?phn=" + nationalPhoneNumber + "&cde=" + countryCode;

            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html)) {
                JSONObject json = new JSONObject(html);
                retval = json.getString("in");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return retval;
    }

//    public boolean getRoles() {
//        boolean retval = false;
//
//        try {
//            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "roles.json";
//
//            String html = downloadTextGET(url);
//            XP_Library_CM XPLIBCM = new XP_Library_CM();
//
//            if (!XPLIBCM.isBlank(html)) {
//                JSONObject json = new JSONObject(html);
//
//                if (json != null) {
//                    JSONArray arry = (JSONArray) json.get("rv");
//                    _Role reviewerRole = getRole(arry);
//
//                    arry = (JSONArray) json.get("pb");
//                    _Role publisherRole = getRole(arry);
//
//                    arry = (JSONArray) json.get("sa");
//                    _Role administratorRole = getRole(arry);
//
//                    arry = (JSONArray) json.get("pr");
//                    _Role premiumRole = getRole(arry);
//
//                    retval = Bootstrap.getInstance().getDatabase().writeSecurityFeatures(premiumRole.isAuthorised, publisherRole.isAuthorised, reviewerRole.isAuthorised,
//                            administratorRole.isAuthorised, publisherRole.Longitude, publisherRole.Latitude, publisherRole.Radius,
//                            reviewerRole.Longitude, reviewerRole.Latitude, reviewerRole.Radius);
//                } else {
//                    LOGGER.info(String.format("Null JSON object returned so unable to check server"));
//                }
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return retval;
//    }

    public boolean sendPacket(String fileName, String packet) {
        boolean retval = false;

        String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "incoming.php";
        String appCode = Bootstrap.getInstance().getAppCode();

        Map<String, String> params = new HashMap<String, String>();
        params.put("fln", fileName);
        params.put("pkt", packet);
        params.put("app", appCode);

        String html = downloadTextPOST(url, params);

        if (html != null) {
            if (html.equals("ok")) {
                retval = true;
            } else if (html.equals("reset") || html.equals("failed #3")) {
                Bootstrap.getInstance().getDatabase().writeSessionPassword(null);

                getSessionPassword(Bootstrap.getInstance().getMySettings().EncryptedEntityGuid);
            }
        }

        return retval;
    }

//    public byte[] getBinary2(URLConnection urlConnection) throws IOException {
//        byte[] retval = null;
//
//        if (response.code() == 200) {
//            InputStream inputStream = response.body().byteStream();
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            byte[] buffer = new byte[10 * 4096];
////            int len = inputStream.read(buffer);
////
////            while (len != -1 ) {
////                outputStream.write(buffer, 0, len);
////                len = inputStream.read(buffer);
////            }
//
//            int len = 0;
////            byte[] buffer = new byte[BUFFER_SIZE];
//            while ((len = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, len);
//            }
//
//            retval = outputStream.toByteArray();
//        }
//
//        return retval;
//    }

    public Long synchroniseServer(String myGuid, String category, String subCategory, long lastServerDate, long currentServerDate) {
        Long retval = lastServerDate;

        try {
            String appCode = Bootstrap.getInstance().getAppCode();

            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "synchronise.php?lsd=" + lastServerDate + "&csd=" + currentServerDate + "&sc=" + subCategory + "&ct=" + category + "&gd=" + myGuid + "&app=" + appCode;

            LOGGER.info(String.format("synchroniseServer-URL=%s", url));

            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html)) {
                retval = handlerJSON(category, currentServerDate, html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public String getSessionPassword(String publisher) {
        String retval = Bootstrap.getInstance().getDatabase().getSessionPassword();
        XP_Library_CM XPLIBCM = new XP_Library_CM();

        if (XPLIBCM.isBlank(retval)) {
            try {
                String appCode = Bootstrap.getInstance().getAppCode();

                String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "session.php?pub=" + publisher + "&sys=" + Bootstrap.getInstance().getPassword() + "&app=" + appCode;
                String html = downloadTextGET(url);

                if (!XPLIBCM.isBlank(html) && html.length() == 32) {
                    retval = Bootstrap.getInstance().getDatabase().writeSessionPassword(html);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return retval;
    }

    public boolean getSubCategories() {
        boolean retval = false;

        try {
            long maxCurrentServerDate = Bootstrap.getInstance().getDatabase().getMaxCurrentServerDate();
            String appCode = Bootstrap.getInstance().getAppCode();

            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "subcategories.php?msd=" + maxCurrentServerDate + "&app=" + appCode;
            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    String connection = json.getString("cnn");

                    if (connection.equals("ok")) {
                        JSONArray arry = (JSONArray) json.get("ary");

                        for (int i = 0; i < arry.length(); i++) {
                            JSONObject route = arry.getJSONObject(i);

                            String category = route.getString("c");
                            String routeGuid = route.getString("g");
                            long currentServerDate = route.getLong("s");

                            if (category.equals("entities")) {
                                Bootstrap.getInstance().getDatabase().writeCategoryEntity(currentServerDate);
                            } else {
                                Bootstrap.getInstance().getDatabase().writeCategoryRoute(routeGuid, currentServerDate);
                            }
                        }

                        retval = (arry.length() > 0);
                    }

//                    Global.setNetworkAvailable(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public void getRoutes() {
        try {
            String url = Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "routes.json";

            String html = downloadTextGET(url);
            XP_Library_CM XPLIBCM = new XP_Library_CM();

            if (!XPLIBCM.isBlank(html)) {
                JSONObject json = new JSONObject(html);

                if (json != null) {
                    JSONArray arry = (JSONArray) json.get("ws");

                    for (int i = 0; i < arry.length(); i++) {
                        JSONObject route = arry.getJSONObject(i);

                        String routeGuid = route.getString("g");
                        String price = route.getString("p");
                        Integer version = route.getInt("v");
                        JSONArray zipFilesTemp = route.getJSONArray("zp");
                        String availability = route.getString("a");
                        String name = route.getString("n");
                        String description = route.getString("d");
                        String type = route.getString("t");
                        String fileName = route.getString("f");
                        Integer mbs = route.getInt("m");

                        ArrayList<String> zipFiles = new ArrayList<>();
                        for (int x = 0; x < zipFilesTemp.length(); x++) {
                            zipFiles.add(zipFilesTemp.getString(x));
                        }

                        Bootstrap.getInstance().getDatabase().writeRoute(routeGuid, "0", version, availability, name, description, type, zipFiles, fileName, mbs);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void downloadBinary(String tag, String url, Callback callback) {
//        Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("Content-Type", "application/octet-stream");
//        requestBuilder.tag(tag);
//        Request request = requestBuilder.build();
//        getInstance().newCall(request).enqueue(callback);
//    }

//    public int queuedCalls() {
//        return getInstance().dispatcher().queuedCallsCount();
//    }

//    public void cancel() {
//        getInstance().dispatcher().cancelAll();
//    }

//    public byte[] getBinary(Response response) throws IOException {
//        byte[] retval = null;
//
//        if (response.code() == 200) {
//            InputStream inputStream = response.body().byteStream();
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            byte[] buffer = new byte[10 * 4096];
//            int len = inputStream.read(buffer);
//
//            while (len != -1 ) {
//                outputStream.write(buffer, 0, len);
//                len = inputStream.read(buffer);
//            }
//
//            retval = outputStream.toByteArray();
//        }
//
//        return retval;
//    }

    public byte[] downloadBinaryPOST(String url, Map<String, String> params) {
        ByteArrayOutputStream retval = new ByteArrayOutputStream();
        String formPacket = getFormPacket(params);

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();

            try {
                byte[] postDataBytes = formPacket.toString().getBytes("UTF-8");

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + postDataBytes.length);
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.getOutputStream().write(postDataBytes);

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    retval.write(buffer, 0, bytesRead);
                }

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            retval = new ByteArrayOutputStream();
            e.printStackTrace();
            LOGGER.info(String.format("Download text failed %s", url));
        }

        return retval.toByteArray();
    }

    public byte[] downloadBinaryGET(String url) {
        ByteArrayOutputStream retval = new ByteArrayOutputStream();

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();

            try {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    retval.write(buffer, 0, bytesRead);
                }

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info(String.format("Download text failed %s", url));
        }

        return retval.toByteArray();
    }

    public String getUrlSuffix(String url) {
        String retval = "";
        String[] parts = url.split("/");

        for (int i = 3; i < parts.length; i++) {
            retval += (retval.isEmpty() ? parts[i] : "/" + parts[i]);
        }

        return retval;
    }

    private void Delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkAvailable() {
        int count = 0;

        try {
            while (downloadTextGET(Bootstrap.getInstance().getDatabase().getSystem().BaseUrl + "ping.html") == null && count < 5) {
                Delay(SMALL_DELAY);
                count++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return (count < 5);
    }

    public String getFormPacket(Map<String, String> params) {
        String retval = "";

        try {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!retval.isEmpty()) retval += "&";
                retval += URLEncoder.encode(param.getKey(), "UTF-8") + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return retval;
    }

    private String downloadTextPOST(String url, Map<String, String> params) {
        StringBuilder retval = new StringBuilder();
        String formPacket = getFormPacket(params);

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();

            try {
                byte[] postDataBytes = formPacket.toString().getBytes("UTF-8");

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + postDataBytes.length);
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.getOutputStream().write(postDataBytes);
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

                for (int c; (c = br.read()) >= 0;)
                    retval.append((char) c);

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            retval = new StringBuilder();
            e.printStackTrace();
            LOGGER.info(String.format("Download text failed %s", url));
        }

        return retval.toString();
    }

    public String downloadTextGET(String url) throws IOException {
        StringBuilder retval = new StringBuilder();

        try {
            URL urlType = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlType.openConnection();

            try {
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                for (int c; (c = br.read()) >= 0;)
                    retval.append((char) c);

            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            retval = new StringBuilder();
            e.printStackTrace();
            LOGGER.info(String.format("Download text failed %s", url));
        }

        return retval.toString();
    }
}