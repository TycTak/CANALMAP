package com.tyctak.map.entities;

import com.tyctak.map.libraries.Base64;
import com.tyctak.map.libraries.Bootstrap;
import com.tyctak.map.libraries.XP_Library_CM;

import org.json.JSONException;
import org.json.JSONObject;

public class _Entity implements IJson {

    XP_Library_CM XPLIBCM = new XP_Library_CM();

//    public _Entity() {
//        EntityGuid = XPLIBCM.getUUID().replace("-", "");
//    }

    public enum enmStatus {
        Moving,
        NotMoving
    }

    public JSONObject toJSON() {
        JSONObject json = null;

        try {
            _Entity temp = new _Entity();
            JSONObject jsonTemp = new JSONObject();

            jsonTemp.put("gd", this.EntityGuid);
            jsonTemp.put("en", XPLIBCM.UrlEncode(this.EntityName));
            jsonTemp.put("de", XPLIBCM.UrlEncode(this.Description));
            if (this.AvatarChecked != temp.AvatarChecked) jsonTemp.put("ac", this.AvatarChecked);
            Double.compare(this.Longitude, temp.Longitude);
            if (Double.compare(this.Longitude, temp.Longitude) != 0) jsonTemp.put("ln", this.Longitude);
            if (Double.compare(this.Latitude, temp.Latitude) != 0) jsonTemp.put("lt", this.Latitude);
            jsonTemp.put("av", (this.Avatar == null ? null : Base64.encodeToString(this.Avatar, Base64.URL_SAFE + Base64.NO_WRAP)));
            if (this.IsActive != temp.IsActive) jsonTemp.put("ia", this.IsActive);
            if (this.Direction != temp.Direction) jsonTemp.put("dr", this.Direction);
            if (!this.IconName.equals(temp.IconName)) jsonTemp.put("ic", this.IconName);
            if (this.Tracker1 != temp.Tracker1) jsonTemp.put("t1", this.Tracker1);
            if (Double.compare(this.Distance, temp.Distance) != 0) jsonTemp.put("ds", this.Distance);
            if (this.Tracker2 != temp.Tracker2) jsonTemp.put("t2", this.Tracker2);
            if (this.Tracker3 != temp.Tracker3) jsonTemp.put("t3", this.Tracker3);
            if (this.Tracker4 != temp.Tracker4) jsonTemp.put("t4", this.Tracker4);
            if (this.Tracker5 != temp.Tracker5) jsonTemp.put("t5", this.Tracker5);
            if (this.EntityType != temp.EntityType) jsonTemp.put("et", this.EntityType);
            jsonTemp.put("ph", XPLIBCM.UrlEncode(this.Phone));
            jsonTemp.put("tr", XPLIBCM.UrlEncode(this.TradingName));
            jsonTemp.put("fa", XPLIBCM.UrlEncode(this.Facebook));
            jsonTemp.put("tw", XPLIBCM.UrlEncode(this.Twitter));
            jsonTemp.put("is", XPLIBCM.UrlEncode(this.Instagram));
            jsonTemp.put("ws", XPLIBCM.UrlEncode(this.Website));
            if (this.Strength != temp.Strength) jsonTemp.put("sr", this.Strength);
            jsonTemp.put("re", XPLIBCM.UrlEncode(this.Reference));
            jsonTemp.put("m1", XPLIBCM.UrlEncode(this.MetaData1));
            jsonTemp.put("m2", XPLIBCM.UrlEncode(this.MetaData2));
            jsonTemp.put("em", XPLIBCM.UrlEncode(this.Email));
            jsonTemp.put("yt", XPLIBCM.UrlEncode(this.YouTube));
            jsonTemp.put("pt", XPLIBCM.UrlEncode(this.Patreon));

            long crc = XP_Library_CM.getCRC32(jsonTemp.toString());
            jsonTemp.put("cc", crc);
            jsonTemp.put("lc", this.LastCheckSum);

            Bootstrap.getInstance().getDatabase().writeEntityCheckSum(this.EntityGuid, crc, crc);

            json = jsonTemp;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public boolean fromJSON(JSONObject json) {
        boolean retval = false;

        try {
            this.EntityGuid = json.getString("gd");
            this.EntityName = XPLIBCM.UrlDecode(json.optString("en", null));
            this.Description = XPLIBCM.UrlDecode(json.optString("de", null));
            this.AvatarChecked = json.optBoolean("ac", this.AvatarChecked);
            this.IsActive = json.optBoolean("ia", this.IsActive);
            this.Longitude = json.optDouble("ln", this.Longitude);
            this.Latitude = json.optDouble("lt", this.Latitude);
            this.Avatar = (json.optString("av").isEmpty() ? null : Base64.decode(json.getString("av"), Base64.URL_SAFE + Base64.NO_WRAP));
            this.Status = Enum.valueOf(enmStatus.class, json.optString("st", this.Status.toString()));
            this.Direction = json.optInt("dr", this.Direction);
            this.ZeroAngleFixed = json.optBoolean("zf", this.ZeroAngleFixed);
            this.IconName = json.optString("ic", this.IconName);

            this.Updated = XPLIBCM.getDate(XP_Library_CM.now());

//            Library_GR LIBGR = new Library_GR();
//            Bitmap avatarMarker = null;
//
//            try {
//                Library LIB = new Library();
//                avatarMarker = LIBGR.getAvatarMarker(MyApp.getContext(), this.Icon, this.IconName, LIB.decodeBinary(this.Avatar), this.IsHireBoat);
//                this.AvatarMarker = (avatarMarker != null ? LIB.encodeBinary(avatarMarker) : null);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

            this.Tracker1 = json.optInt("t1", this.Tracker1);
            this.Distance = json.optDouble("ds", this.Distance);
            this.Tracker2 = json.optInt("t2", this.Tracker2);
            this.Tracker3 = json.optInt("t3", this.Tracker3);
            this.Tracker4 = json.optInt("t4", this.Tracker4);
            this.Tracker5 = json.optInt("t5", this.Tracker5);
            this.EntityType = json.optInt("et", this.EntityType);
            this.Phone = XPLIBCM.UrlDecode(json.optString("ph", null));
            this.TradingName = XPLIBCM.UrlDecode(json.optString("tr", null));
            this.Facebook = XPLIBCM.UrlDecode(json.optString("fa", null));
            this.Twitter = XPLIBCM.UrlDecode(json.optString("tw", null));
            this.Instagram = XPLIBCM.UrlDecode(json.optString("is", null));
            this.Website = XPLIBCM.UrlDecode(json.optString("ws", null));
            this.Strength = json.optInt("sr", this.Strength);
            this.Reference = XPLIBCM.UrlDecode(json.optString("re", null));
            this.MetaData1 = XPLIBCM.UrlDecode(json.optString("m1", null));
            this.MetaData2 = XPLIBCM.UrlDecode(json.optString("m2", null));
            this.Email = XPLIBCM.UrlDecode(json.optString("em", null));
            this.YouTube = XPLIBCM.UrlDecode(json.optString("yt", null));
            this.Patreon = XPLIBCM.UrlDecode(json.optString("pt", null));

            this.CheckSum = json.optLong("cc", this.CheckSum);
            this.LastCheckSum = this.CheckSum;

            retval = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public String EntityGuid;
    public String EntityName;
    public String Description;
    public boolean AvatarChecked = false;
    public double Longitude = 0.0;
    public double Latitude = 0.0;
    public boolean IsHireBoat = false;
    public byte[] Avatar = null;
    public int Icon = 0; //R.drawable.ic_icon__logo;
    public enmStatus Status = enmStatus.NotMoving;
    public int Direction = 0;
    public boolean ZeroAngleFixed = false;
    public byte[] AvatarMarker = null;
    public int Tracker1 = 0;
    public double Distance = 0.0;
    public String IconName = "ic_icon__logo";
    public boolean Publish = false;

    public int EntityType = 2;
    public String Phone;
    public String TradingName;
    public String Facebook;
    public String Twitter;
    public String Instagram;
    public String Website;
    public int Tracker2 = 0;
    public int Tracker3 = 0;
    public int Tracker4 = 0;
    public int Tracker5 = 0;
    public long LastMoved;
    public int Strength = 0;
    public String Reference;
    public String MetaData1;
    public String MetaData2;
    public String Email;
    public boolean Favourite = false;
    public String YouTube;
    public String Patreon;

    public long CheckSum = 0l;
    public long LastCheckSum = 0l;

    public long Updated = 0l;
    public long Created = 0l;

    public boolean IsActive = false;
}