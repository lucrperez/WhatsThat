package com.thecomebacks.whatsthat.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String user;
    private String hash;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUser());
            json.put("hash", getHash());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
