package com.example.d3.cardstack;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import layout.HighscoreFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
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
        private final Runnable mHideRunnable = new Runnable() {
            @Override
            public void run() {
                hide();
            }
        };

        /**
         * Touch listener to use for in-layout UI controls to delay hiding the
         * system UI. This is to prevent the jarring behavior of controls going away
         * while interacting with activity UI.
         */


        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            mControlsView = findViewById(R.id.fullscreen_content_controls);
            mContentView = findViewById(R.id.fullscreen_content);
            Button play_button, score_button;
            play_button = (Button) findViewById(R.id.play_button);
            score_button = (Button) findViewById(R.id.score_button);


            // Set up the user interaction to manually show or hide the system UI.

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
                        /* if(findViewById(R.id.fragment_highscore) !=null) {
                            //if(savedInstanceState != null) return;
                            Fragment fragment = new Fragment();
                            fragment.setArguments(getIntent().getExtras());
                            getSupportFragmentManager().beginTransaction().add(R.id.fullscreen_content, fragment).commit();
                        } */
                        Intent i = new Intent(getApplication(), LeaderboardActivity.class);
                        Toast.makeText(getApplicationContext(), "View the leaderboards", Toast.LENGTH_SHORT).show();
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
            // Schedule a runnable to remove the status and navigation bar after a delay
            mHideHandler.removeCallbacks(mShowPart2Runnable);
            mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        }

        /**
         * Schedules a call to hide() in [delay] milliseconds, canceling any
         * previously scheduled calls.
         */
        private void delayedHide(int delayMillis) {
            mHideHandler.removeCallbacks(mHideRunnable);
            mHideHandler.postDelayed(mHideRunnable, delayMillis);
        }
}