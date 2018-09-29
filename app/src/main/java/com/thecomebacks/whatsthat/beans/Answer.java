package com.thecomebacks.whatsthat.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer extends UserResponse {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUser());
            json.put("hash", getHash());
            json.put("response", getResponse());
            json.put("id", getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
