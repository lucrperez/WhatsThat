package com.thecomebacks.whatsthat.beans;

import org.json.JSONObject;

public class ImageBean {

    private int id;
    private String base64;

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static ImageBean getUserFromResponse(String response) {
        ImageBean image = null;
        try {
            JSONObject json = new JSONObject(response);
            image = new ImageBean();

            if (json.has("id")) {
                image.setId(json.getInt("id"));
            }

            if (json.has("base64")) {
                image.setBase64(json.getString("base64"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            image = null;
        }

        return image;
    }
}
