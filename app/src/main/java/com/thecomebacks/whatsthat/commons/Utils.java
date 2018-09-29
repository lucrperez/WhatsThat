package com.thecomebacks.whatsthat.commons;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {

    public static final String md5(final String toEncode) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(toEncode.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



    public String generateGETRequest(String path) {
        InputStream is = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();

            response = readIt(is);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String generatePostOrPutRequest(String method, String path, Object objectToPost) {
        InputStream is = null;
        String response = null;

        try {
            URL url = new URL(Constants.BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod(method);
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");


            JSONObject jsonPost = objectToJson(objectToPost);

            if (jsonPost != null) {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(jsonPost.toString().getBytes("UTF-8"));

                wr.flush();
                wr.close();

                conn.connect();

                int status = conn.getResponseCode();
                if (status >= 400 && status < 600) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                response = readIt(is);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public String readIt(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private JSONObject objectToJson(Object objectToPost) {
        JSONObject jsonPost = null;

        try {
            Class cls = objectToPost.getClass();
            Method method = cls.getDeclaredMethod("toJson");
            Object returnValue = method.invoke(objectToPost);
            jsonPost = (JSONObject) returnValue;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonPost;
    }
}
