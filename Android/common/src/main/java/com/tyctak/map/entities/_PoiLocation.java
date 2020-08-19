package com.tyctak.map.entities;

import com.tyctak.map.libraries.Base64;
import com.tyctak.map.libraries.Bootstrap;
import com.tyctak.map.libraries.XP_Library_CM;

import org.json.JSONException;
import org.json.JSONObject;

public class _PoiLocation implements IJson {

    XP_Library_CM XPLIBCM = new XP_Library_CM();

    @Override
    public JSONObject toJSON() {
        JSONObject json = null;

        try {
            _PoiLocation temp = new _PoiLocation();
            JSONObject jsonTemp = new JSONObject();

            jsonTemp.put("id", this.Id);
            if (!this.Area.equals(temp.Area)) jsonTemp.put("ar", this.Area);
            jsonTemp.put("nm", this.Name);
            jsonTemp.put("ln", this.Longitude);
            jsonTemp.put("lt", this.Latitude);
            if (this.Scope != temp.Scope) jsonTemp.put("sc", this.Scope);
            jsonTemp.put("mg", XPLIBCM.UrlEncode(this.Message));
            jsonTemp.put("gd", this.EntityGuid);
            jsonTemp.put("im", (this.Image == null ? null : Base64.encodeToString(this.Image, Base64.URL_SAFE + Base64.NO_WRAP)));
            jsonTemp.put("ac", this.Action);
            jsonTemp.put("up", this.Updated);
            if (this.IsTwitter != temp.IsTwitter) jsonTemp.put("it", this.IsTwitter);
            if (this.IsFacebook != temp.IsFacebook) jsonTemp.put("if", this.IsFacebook);
            if (this.StartDate != temp.StartDate) jsonTemp.put("sd", this.StartDate);
            if (this.EndDate != temp.EndDate) jsonTemp.put("ed", this.EndDate);
            jsonTemp.put("tt", this.Title);
            if (this.IsLocked != temp.IsLocked) jsonTemp.put("il", this.IsLocked);
            jsonTemp.put("ei", this.ExternalId);
            jsonTemp.put("fd", this.Feedback);
//            if (this.Reviewed != temp.Reviewed) jsonTemp.put("rv", this.Reviewed);
//            if (!this.ReviewedBy.equals(temp.ReviewedBy)) jsonTemp.put("rb", this.ReviewedBy);
//            if (this.ReviewedStatus != null && !this.ReviewedStatus.equals(temp.ReviewedStatus)) jsonTemp.put("rs", this.ReviewedStatus);
            if (this.Columns != null && !this.Columns.equals(temp.Columns)) jsonTemp.put("co", this.Columns);
            if (this.Shared != temp.Shared) jsonTemp.put("sh", this.Shared);
            jsonTemp.put("cr", this.Created);
            if (this.Deleted != temp.Deleted) jsonTemp.put("dd", this.Deleted);
            jsonTemp.put("rt", this.Route);
            jsonTemp.put("md", this.MetaData);

            long crc = XPLIBCM.getCRC32(jsonTemp.toString());
            jsonTemp.put("cc", crc);
            jsonTemp.put("lc", this.LastCheckSum);

            Bootstrap.getInstance().getDatabase().writePoiLocationCheckSum(this.Id, this.EntityGuid, crc, crc);
//            Global.getLIBDB().writePoiLocationCheckSum(this.Id, this.EntityGuid, crc, crc);

            json = jsonTemp;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public boolean fromJSON(JSONObject json) {
        boolean retval = false;

        try {
            this.Id = json.getInt("id");
            this.Area = json.optString("ar", this.Area);
            this.Name = json.getString("nm");
            this.Longitude = json.getDouble("ln");
            this.Latitude = json.getDouble("lt");
            this.Scope = json.optInt("sc", this.Scope);
            this.Message = XPLIBCM.UrlDecode(json.optString("mg", null));
            this.EntityGuid = json.getString("gd");
            this.Image = (json.optString("im").isEmpty() ? null : Base64.decode(json.getString("im"), Base64.URL_SAFE + Base64.NO_WRAP));
            this.Action = json.optString("ac", null);
            this.Updated = json.optLong("up", this.Updated);
            this.IsImage = json.optBoolean("ii", (this.Image != null ? true : false));
            this.IsMessage = json.optBoolean("is", (!XPLIBCM.isBlank(this.Message) ? true : false));
            this.IsTwitter = json.optBoolean("it", this.IsTwitter);
            this.IsFacebook = json.optBoolean("if", this.IsFacebook);
            this.StartDate = json.optLong("sd", this.StartDate);
            this.EndDate = json.optLong("ed", this.EndDate);
            this.Title = json.optString("tt", this.Title);
            this.IsLocked = json.optBoolean("il", this.IsLocked);
            this.ExternalId = json.getInt("ei");
            this.Feedback = json.optString("fd", null);
            this.ReviewedFeedback = enmReviewedFeedback.values()[json.optInt("rf")];
            this.Columns = json.optString("co", this.Columns);
            this.Shared = json.optInt("sh", this.Shared);
            this.Created = json.optLong("cr", this.Created);
            this.Deleted = json.optBoolean("dd", this.Deleted);
            this.Route = json.getString("rt");
            this.MetaData = json.optString("md", null);

            this.CheckSum = json.optLong("cc", this.CheckSum);
            this.LastCheckSum = this.CheckSum;

            retval = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retval;
    }

    public enum enmReviewedFeedback {
        NotReviewed,
        NoFeedback,
        InvalidText,
        InvalidImage,
        InvalidPosition
    }

    public enum enmStatus {
        Created,
        Updated,
        Deleted
    }

    public int Id = -1;
    public String Area = "markers";
    public String Name = "ic_missing";
    public double Longitude;
    public double Latitude;
    public int Scope = 0;
    public String Message;
    public String EntityGuid;
    public enmStatus Status = enmStatus.Updated;
    public byte[] Image;
    public byte[] Marker;
    public String Action;
    public long Updated = 0;
    public boolean IsImage = false;
    public boolean IsMessage = false;
    public boolean IsTwitter = false;
    public boolean IsFacebook = false;
    public long StartDate = 0;
    public long EndDate = 7541676254305L;
    public String Title;
    public boolean IsLocked = false;
    public int Level07;
    public int Level08;
    public int Level09;
    public int Level10;
    public int Level11;
    public int Level12;
    public int Level13;
    public int Level14;
    public int Level15;
    public int Level16;
    public int Level17;
    public int ExternalId;
    public String Feedback;
    public enmReviewedFeedback ReviewedFeedback = enmReviewedFeedback.NotReviewed;
    public String Columns = null;
    public int Shared = 1;
    public long Created = 0;
    public boolean Deleted = false;
    public String Route;
    public String MetaData;
    public long CheckSum = 0l;
    public long LastCheckSum = 0l;
    public String Category;
}