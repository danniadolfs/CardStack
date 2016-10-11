package com.example.d3.cardstack;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private List<leaderboard_rest> highscore;
    private ListView resultListView;
    private static final String url = "http://cardstackserver.herokuapp.com/scores";
    private static final String url_arcade = "http://cardstackserver.herokuapp.com/scores-arcade";
    public boolean arcade_mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboards_list);
        resultListView = (ListView) findViewById(R.id.searchResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_leaderboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public static List<leaderboard_rest> parseSLeaderboardListJson(String jsonString) throws JSONException {
        List<leaderboard_rest> Leaderboard = new ArrayList<>();
        JSONArray jsonResults = new JSONArray(jsonString);
        Boolean arcade = false;
        
        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject jsonResult = jsonResults.getJSONObject(i);
            Log.d("JSONObject",jsonResult.getString("name"));
            Log.d("JSONObject",jsonResult.getString("score"));
                Leaderboard.add(new leaderboard_rest(
                        jsonResult.getString("_id"),
                        jsonResult.getString("name"),
                        jsonResult.getString("score"),
                        jsonResult.getString("time")
                ));

        }


        Collections.reverse(Leaderboard);
        return Leaderboard;
    }

    public leaderboard_rest getHighscore(int i){
        return highscore.get(i);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, List<leaderboard_rest>> {
        @Override
        protected List<leaderboard_rest> doInBackground(Void... params) {
            List<leaderboard_rest> results = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                results = parseSLeaderboardListJson(getResponseString(conn));
                conn.disconnect();
            } catch (Exception e) {
                Log.e("LeaderboardActivity", e.getMessage(), e);
            }
            return results;
        }

        @Override
        //@JsonIgnoreProperties(ignoreUnknown = true)
        protected void onPostExecute(List<leaderboard_rest> response) {
            super.onPostExecute(response);
            highscore = response;
            try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LeaderboardAdapter srAdapter = new LeaderboardAdapter(
                                    getApplicationContext(),
                                    R.layout.fragment_leaderboard,
                                    highscore
                            );
                            resultListView.setAdapter(srAdapter);
                            resultListView.invalidate();
                        }
                    });
                }
            catch (NullPointerException e){ Log.d("onPostExecute",e.toString());}
        }
    }
}

class LeaderboardAdapter extends ArrayAdapter<leaderboard_rest> {

    private Context c;
    private int id;
    private List<leaderboard_rest> items;

    public LeaderboardAdapter(Context context, int textViewResourceId, List<leaderboard_rest> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    public leaderboard_rest getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }
        final leaderboard_rest sr = items.get(position);
        if (sr != null) {
            TextView name = (TextView) v.findViewById(R.id.context_name);
            TextView score = (TextView) v.findViewById(R.id.context_score);
            TextView time = (TextView) v.findViewById(R.id.context_time);

            if(name!=null)
                name.setText(sr.getName_cs());
            if(score!=null)
                score.setText("Score: "+sr.getScore_cs());
            if(time!=null)
                time.setText("Time: "+sr.getTime_cs());
        }
        return v;
    }
}

