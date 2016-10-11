package com.example.d3.cardstack.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.example.d3.cardstack.leaderboard_rest;
import com.example.d3.cardstack.user_rest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class Util {

    public static final String HOST_URL = "http://cardstackserver.herokuapp.com/";

    /**
     * Extract file data from actual file in storage and encode for transfer
     *
     * @param c is the context of the calling Activity
     * @param uploadFile is the file link in storage to be transferred
     * @return returns the encoded {@param uploadFile}
     */

    /**
     * Checks the connectivity to the Internet of the device
     *
     * @param c is the context of the calling Activity
     * @param CONNECTIVITY_SERVICE is the network flag of the context
     * @return true if network is connected, false otherwise
     */
    public static boolean isNetworkAvailableAndConnected(Context c, String CONNECTIVITY_SERVICE) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Sets the connection {@param conn} to 'POST' mode
     *
     * @param conn is the connected connection
     * @return the {@param conn} in 'POST' mode
     */
    public static HttpURLConnection setPostConnection(HttpURLConnection conn) {
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(2048);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Sends the json object {@param POST_PARAM} over the connection {@param conn}
     *
     * @param conn is the connected connection
     * @param POST_PARAM is the json object to be sent
     * @throws IOException
     */
    public static void sendPostJSON(HttpURLConnection conn, JSONObject POST_PARAM) throws IOException {
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(POST_PARAM.toString().getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * Receives response from the connected connection {@param conn} as String
     *
     * @param conn is the connected connection
     * @return the response from {@param conn} as String
     * @throws IOException
     */
    public static String getResponseString(HttpURLConnection conn) throws IOException {
        InputStream in = conn.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        return new String(out.toByteArray());
    }

    /**
     * Parses the received json object string as a User
     *
     * @param jsonString is the json object to be parsed
     * @return a User
     * @throws JSONException
     */
    public static user_rest parseUserJson(String jsonString) throws JSONException {
        JSONObject userJson = new JSONObject(jsonString);

        return new user_rest(
                userJson.getString("_id"),
                userJson.getString("username"),
                userJson.getString("password")
        );
    }

    public static leaderboard_rest parseScoreJson(String jsonString) throws JSONException {
        JSONObject userJson = new JSONObject(jsonString);

        return new leaderboard_rest(
                userJson.getString("id"),
                userJson.getString("name"),
                userJson.getString("score"),
                userJson.getString("time")
        );
    }
}
