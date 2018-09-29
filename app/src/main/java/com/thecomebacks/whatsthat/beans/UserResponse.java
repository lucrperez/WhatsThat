package com.thecomebacks.whatsthat.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class UserResponse extends User {
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUser());
            json.put("hash", getHash());
            json.put("response", getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
