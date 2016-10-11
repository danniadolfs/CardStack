package com.example.d3.cardstack;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LeaderboardActivity extends Activity {

    private ListView resultListView;
    private List<Score> results;

    private static final String REST_SEARCH_URL =
            "http://" + Util.HOST_URL + "";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayResults();

    }

    /**
     * Displays search results in the ListView
     */
    private void displayResults() {
        new SearchTask().execute();
        //if (results == null) return;
        //new SearchTask().execute();
        String rslt = ""+ results;
        //Toast.makeText(getApplication(),rslt,Toast.LENGTH_SHORT).show();
        // Need to execute on separate thread to update UI
    }

    // GET request template
    private class GetStringRequest extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String response = "No response";
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(REST_SEARCH_URL).openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException(conn.getResponseMessage() +": with " + REST_SEARCH_URL);
                }
                response = Util.getResponseString(conn);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    /**
     * An AsyncTask that queries the server for a result
     */
    private class SearchTask extends AsyncTask<String, Void, List<Score>> {

        @Override
        protected List<Score> doInBackground(String... params) {
            List<Score> results = null;

            // Send search request to server
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(REST_SEARCH_URL).openConnection();
                // Check for server response
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                        cancel(true);
                    else
                        throw new IOException(conn.getResponseMessage() +": with " + REST_SEARCH_URL);
                }
                // Receive response data from server
                results = Util.parseScoreListJson(Util.getResponseString(conn));
                conn.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Score> Score) {
            super.onPostExecute(Score);
            try {

                results = Score;
                displayResults();
            } catch (NullPointerException e) {
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}