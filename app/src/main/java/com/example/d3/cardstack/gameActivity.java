package com.example.d3.cardstack;


import android.content.Context;
import android.content.Intent;
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

import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
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

        new gameLogic().stackOfCards();
        currentCard();
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
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    /*
    Movement with velocity
     */
    public int xpos,ypos;
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
                    if(isSpades) {
                        if(spades_stack.empty()) nextCard(spades_stack);
                        if(checkCurrentCardNumber() == checkNextCardNumber(spades_stack)){
                            nextCard(spades_stack);

                        }
                    }
                    redraw();
                }

                if(clover.contains(xpos,ypos)){
                    if(isClovers){
                        if(clovers_stack.empty()) nextCard(clovers_stack);
                        if(checkCurrentCardNumber() == checkNextCardNumber(clovers_stack)){
                            nextCard(clovers_stack);

                        }

                    }
                    redraw();
                }

                if(hearts.contains(xpos,ypos)){
                    if(isHearts){
                        if(hearts_stack.empty()) nextCard(hearts_stack);
                        if(checkCurrentCardNumber() == checkNextCardNumber(hearts_stack)){
                            nextCard(hearts_stack);

                        }
                    }
                    redraw();
                }

                if(diamond.contains(xpos,ypos)){
                    if(isDiamonds){
                        if(diamonds_stack.empty()) nextCard(diamonds_stack);
                        if(checkCurrentCardNumber() == checkNextCardNumber(diamonds_stack)){
                            nextCard(diamonds_stack);

                        }

                    }
                    redraw();
                }

                if(middle.contains(xpos,ypos)){
                    if(!playingcards.empty()) nextCard(playingcards_skip);
                    //AI();
                    redraw();
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
    public String Score = "0";
    public class Rectangle extends View{
        Paint paintText = new Paint();
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

        public void createText(Canvas canvas)
        {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            int spadesX = width/2;
            int spadesY = height/2;
            int bottom = height;
            int left = width - width;
            int right = width;


            paintText.setColor(Color.GREEN);
            paintText.setTextSize(20);
            //SPADES
            canvas.drawText(spades_card_peek, spadesX-15, bottom-bottom+100, paintText);
            //HEARTS
            canvas.drawText(hearts_card_peek,left+10, spadesY, paintText);
            //CLOVERS
            canvas.drawText(clovers_card_peek,spadesX-15, bottom-100, paintText);
            //DIAMONDS
            canvas.drawText(diamonds_card_peek,right-50, spadesY, paintText);
            //CENTER CARD
            paintText.setColor(Color.BLACK);
            canvas.drawText(currentCard,spadesX-15,spadesY,paintText);

        }

        public void onDraw(Canvas canvas) {
            makeField(canvas);
            createText(canvas);
            super.onDraw(canvas);
        }
    }

    public int score;
    public Stack playingcards = new Stack();
    public Stack playingcards_skip = new Stack();

    public Stack spades_stack = new Stack();
    public String spades_card_peek = "";

    public Stack clovers_stack = new Stack();
    public String clovers_card_peek = "";

    public Stack diamonds_stack = new Stack();
    public String diamonds_card_peek = "";

    public Stack hearts_stack = new Stack();
    public String hearts_card_peek = "";

    public String currentCard = "";

    private boolean isSpades = false;
    private boolean isHearts = false;
    private boolean isClovers = false;
    private boolean isDiamonds = false;
    /* GAME LOGIC */
    public class gameLogic {

            //Numbers of cards
            String[] cards = {"02","03","04","05","06","07","08","09","10","11","12","13","14"};
            //Types of cards
            String[] types ={"H", "S", "C", "D"};
            String[][]stack = new String[13][4];
            String[] toShuffle = new String[52];
            int toShuffleCount = 0;
            public void stackOfCards(){
                cardstack();
            }

            private void cardstack() {
                for (int i = 0; i < cards.length; i++) {
                    for (int j = 0; j < types.length; j++) {
                        stack[i][j] = cards[i] + types[j];
                        toShuffle[toShuffleCount] = stack[i][j];
                        toShuffleCount++;
                    }
                }

                shuffleArray(toShuffle);

                for (int i = 0; i < toShuffle.length; i++) {
                    playingcards.push(toShuffle[i]);
                }
            }
    }

    //Pop card from the stack
    public void currentCard(){
        if(!playingcards.empty()){
            currentCard = (String) playingcards.peek();
            Log.d("Current Card is: ",currentCard);
        }
        checkCardType();
    }

    public void checkCardType(){
        if(currentCard.contains("S")) isSpades = true;
        else isSpades = false;
        if(currentCard.contains("H")) isHearts = true;
        else isHearts = false;
        if(currentCard.contains("D")) isDiamonds = true;
        else isDiamonds = false;
        if(currentCard.contains("C")) isClovers = true;
        else isClovers = false;
    }

    public void nextCard(Stack stack){
        Iterator itr = playingcards.iterator();
        if(itr.hasNext()) stack.push(playingcards.pop());
        if(playingcards.size() == 0) addRestToStack();
        if(playingcards.empty() && playingcards_skip.empty()) stackComplete();
        currentCard();
        checkCardType();
        Log.d("Game", "Next card on stack.");
    }

    public void AI(){
        if(isDiamonds) {
            if (diamonds_stack.empty()) nextCard(diamonds_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(diamonds_stack)) {
                nextCard(diamonds_stack);

            }
        }
        if(isSpades) {
            if (spades_stack.empty()) nextCard(spades_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(spades_stack)) {
                nextCard(spades_stack);

            }
        }
        if(isClovers) {
            if (clovers_stack.empty()) nextCard(clovers_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(clovers_stack)) {
                nextCard(clovers_stack);

            }

        }
        if(isHearts) {
            if (hearts_stack.empty()) nextCard(hearts_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(hearts_stack)) {
                nextCard(hearts_stack);

            }
        }
        redraw();
    }

    public int checkNextCardNumber(Stack stack){
        String nr = stack.peek().toString();
        nr = nr.substring(0,2);
        int number = Integer.parseInt(nr);

        if(number == 14) return 2;
        else return number+1;
    }

    public int checkCurrentCardNumber(){
        String numb_string = currentCard.substring(0, 2);
        int current_nr = Integer.parseInt(numb_string);
        return current_nr;
    }

    public void peekCheat(){
        if(!spades_stack.empty()) spades_card_peek = spades_stack.peek().toString();
        if(!hearts_stack.empty()) hearts_card_peek = hearts_stack.peek().toString();
        if(!clovers_stack.empty()) clovers_card_peek = clovers_stack.peek().toString();
        if(!diamonds_stack.empty()) diamonds_card_peek = diamonds_stack.peek().toString();
    }

    public void addRestToStack(){
        Iterator itr = playingcards_skip.iterator();
        while (itr.hasNext()) {
            Log.d("STATE", playingcards_skip.peek().toString());
            playingcards.push(playingcards_skip.pop());
        }
    }

    public void stackComplete(){
        Toast.makeText(getApplication(),"GAME WON!",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void redraw(){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_view);
        relativeLayout.removeAllViews();
        relativeLayout.addView(new Rectangle(this));
        peekCheat();
    }
    // Implementing Fisher–Yates shuffle
    public static void shuffleArray(String[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}



/*------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------*/
