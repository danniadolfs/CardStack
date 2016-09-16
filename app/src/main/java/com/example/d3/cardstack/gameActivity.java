package com.example.d3.cardstack;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class gameActivity extends AppCompatActivity  {

    private final Handler mHideHandler = new Handler();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mControlsView = findViewById(R.id.fullscreen_content);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_view);
        relativeLayout.addView(new Rectangle(this));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    /*
    Movement with velocity
     */
    public int xpos,ypos;
    private static final String DEBUG_TAG = "Velocity";
    private VelocityTracker mVelocityTracker = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.d("", "X velocity: " +
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                pointerId));
                Log.d("", "Y velocity: " +
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                pointerId));
                break;
            case MotionEvent.ACTION_UP:
                float x = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                float y = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                xpos = (int) event.getX();
                ypos = (int) event.getY();
                //String pos = xcor + "," + ycor;

                //Toast.makeText(getApplicationContext(),"touch", Toast.LENGTH_SHORT).show();
                if(spades.contains(xpos,ypos)){
                    Toast.makeText(getApplicationContext(),"Spades", Toast.LENGTH_SHORT).show();
                }

                if(clover.contains(xpos,ypos)){
                    Toast.makeText(getApplicationContext(),"clover", Toast.LENGTH_SHORT).show();
                }

                if(hearts.contains(xpos,ypos)){
                    Toast.makeText(getApplicationContext(),"hearts", Toast.LENGTH_SHORT).show();
                }

                if(diamond.contains(xpos,ypos)){
                    Toast.makeText(getApplicationContext(),"diamond", Toast.LENGTH_SHORT).show();
                }

                if(middle.contains(xpos,ypos)){
                    Toast.makeText(getApplicationContext(),"Middle", Toast.LENGTH_SHORT).show();
                }


            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.clear();
                break;
        }
        return true;
    }

    /* DRAWING RECTANGLES */

    public Rect spades,clover,hearts,diamond,middle;
    public class Rectangle extends View{
        Paint paintb = new Paint();
        Paint paintr = new Paint();
        Paint painty = new Paint();

        public Rectangle(Context context) {
            super(context);
        }

        public void makeField(Canvas canvas)
        {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            int top = height - height;
            int bottom = height;
            int left = width - width;
            int right = width;

            paintb.setColor(Color.BLACK);
            paintr.setColor(Color.RED);
            painty.setColor(Color.YELLOW);
            spades = new Rect(left,top,right,(int)(0.15*bottom));
            clover = new Rect(left,bottom-(int)(0.15*bottom),right,bottom);
            hearts = new Rect(left,top+(int)(bottom*0.15),left+(int)(right*0.25),(int)(bottom*0.85));
            diamond = new Rect(right-(int)(0.25*right),top+(int)(bottom*0.15),right,(int)(bottom*0.85));
            middle = new Rect((int)(Math.floor(0.14*bottom)),top+(int)(bottom*0.15),right-(int)(right*0.25),(int)(bottom*0.85));

            canvas.drawRect(spades, paintb);
            canvas.drawRect(clover, paintb);
            canvas.drawRect(hearts, paintr);
            canvas.drawRect(diamond,paintr);
            canvas.drawRect(middle, painty);
        }

        public void onDraw(Canvas canvas) {
            makeField(canvas);
            super.onDraw(canvas);
        }
    }
}



/*------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------*/
