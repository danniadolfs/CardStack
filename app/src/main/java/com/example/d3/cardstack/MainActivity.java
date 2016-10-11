package com.example.d3.cardstack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.d3.cardstack.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private EditText mNameView;
    private EditText mPasswordView;
    private static final String url = "http://cardstackserver.heroku.com/";
    private UserLoginTask mAuthTask = null;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        Button play_button,score_button,login_button,form_button,back_button;
        play_button = (Button)findViewById(R.id.play_button);
        score_button = (Button)findViewById(R.id.score_button);
        login_button = (Button)findViewById(R.id.btnLogin);
        //mNameView = (EditText) findViewById(R.id.userEditText);
        //mPasswordView = (EditText)findViewById(R.id.pwEditText);

        /*
        Button mNameSignInButton = (Button) findViewById(R.id.btnLogin);
        mNameSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        }); */


        assert play_button != null;
        if (play_button != null) {
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplication(), gameActivity.class);
                    Toast.makeText(getApplicationContext(), "play the game", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                }
            });
        }

        assert score_button != null;
        if (score_button != null) {
            score_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplication(),LeaderboardActivity.class);
                    Toast.makeText(getApplicationContext(), "View the high score", Toast.LENGTH_SHORT).show();
                    startActivity(i);

                }
            });
        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        //mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String pw = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(pw)) {
            mPasswordView.setError("Invalid Password.");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(name)) {
            mNameView.setError("Name is required.");
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(name, pw);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, user_rest> {

        private final String mName;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mName = username;
            mPassword = password;
        }

        @Override
        protected user_rest doInBackground(Void... params) {
            user_rest responseUser = null;
            HttpURLConnection conn = null;

            try {
                conn = (HttpURLConnection) new URL(url + "login").openConnection();
                ((HttpURLConnection)conn).setRequestMethod("GET");
                String credentials = mName + ":" + mPassword;
                // create Base64 encodet string
                final String basic =
                        "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);


                conn.addRequestProperty("Authorization", "Basic " + basic);
                conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.addRequestProperty("Content-Type","application/json");
                JSONObject POST_PARAM = new JSONObject();
                POST_PARAM.put("username", mName);
                POST_PARAM.put("password", mPassword);
                Util.sendPostJSON(conn, POST_PARAM);

                //OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                //Send request
                DataOutputStream out = new DataOutputStream (
                        conn.getOutputStream ());
                out.flush();
                out.close();




                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) Log.d("HTTP OK", "200");
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                    // Disconnect and reconnect
                    conn.disconnect();
                    conn = (HttpURLConnection) new URL(url+"register").openConnection();

                    // set to POST
                    conn = Util.setPostConnection(conn);
                    JSONObject POST_PARAM2 = new JSONObject();
                    POST_PARAM.put("name", mName);
                    POST_PARAM.put("pw", mPassword);
                    Util.sendPostJSON(conn, POST_PARAM);

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED){
                        throw new IOException(conn.getResponseMessage() +": with " + url);
                    }

                    // Attempt login again after User has been created
                    conn.disconnect();
                    conn = (HttpURLConnection) new URL(url+"register").openConnection();
                }
                //responseUser = Util.parseUserJson(Util.getResponseString(conn));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return responseUser;
        }

        @Override
        protected void onPostExecute(user_rest responseUser) {
            mAuthTask = null;

            if (responseUser == null) {
                //Toast.makeText(act, R.string.error_incorrect_name, Toast.LENGTH_LONG).show();
            } else {
                if (!responseUser.getPassword_cs().equals(mPassword)) {
                    Log.d("Password","Invalid");
                    // TODO mPassword hashing
                } else {
                    Toast.makeText(getApplicationContext(), "Logging in as: "+responseUser.getUser_cs(), Toast.LENGTH_LONG).show();
                    //act.displayHome(responseUser.getName());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

