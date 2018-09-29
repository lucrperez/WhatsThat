package com.thecomebacks.whatsthat.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer extends UserResponse {

    private int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUser());
            json.put("hash", getHash());
            json.put("response", getResponse());
            json.put("image", getImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
