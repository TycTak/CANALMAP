package com.tyctak.map.entities;

import org.json.JSONObject;

public interface IJson {
    JSONObject toJSON();
    boolean fromJSON(JSONObject json);
}