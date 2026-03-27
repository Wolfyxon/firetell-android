package com.wolfyxon.firetell.android.lib;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData {
    public String displayName;
    public String uid;

    public UserData(JSONObject json) throws JSONException {
        this.uid = json.getString("uid");
        this.displayName = json.getString("displayName");
    }
}
