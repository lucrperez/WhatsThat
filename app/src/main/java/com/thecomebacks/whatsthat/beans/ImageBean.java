package com.thecomebacks.whatsthat.beans;

import org.json.JSONObject;

public class ImageBean {

    private String encodedImage;

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    public static ImageBean getUserFromResponse(String response) {
        ImageBean image = null;
        try {
            JSONObject json = new JSONObject(response);

            if (json.has("encodedImage")) {
                image = new ImageBean();
                image.setEncodedImage(json.getString("encodedImage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }
}
