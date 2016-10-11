package com.example.d3.cardstack;

import android.content.Context;
import android.net.ConnectivityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import com.example.d3.cardstack.Score;
public class Util {

    public static final String HOST_URL = "http://cardstackserver.herokuapp.com/scores";

    public static boolean isNetworkAvailableAndConnected(Context c, String CONNECTIVITY_SERVICE) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

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

    public static void sendPostJSON(HttpURLConnection conn, JSONObject POST_PARAM) throws IOException {
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(POST_PARAM.toString().getBytes("UTF-8"));
        out.flush();
        out.close();
    }

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

    public static List<Score> parseScoreListJson(String jsonString) throws JSONException {
        List<Score> Scores = new ArrayList<>();
        JSONArray jsonResults = new JSONArray(jsonString);

        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject jsonResult = jsonResults.getJSONObject(i);
            Scores.add(new Score(
                    jsonResult.getLong("id"),
                    jsonResult.getString("name"),
                    jsonResult.getString("time")
            ));
            // Set default uploader if none
            Score sr = Scores.get(i);
        }
        return Scores;
    }
}
