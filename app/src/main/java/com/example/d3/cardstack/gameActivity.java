package com.example.d3.cardstack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d3.cardstack.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;

/**
 *
 */
public class gameActivity extends AppCompatActivity  {

    private final Handler mHideHandler = new Handler();
    public String playerName = "Anonymous";
    private View mControlsView;
    private long tStart;
    private boolean game_menu = true;
    private TextView cardText;
    private ImageView cardImg;
    private Boolean arcade_mode = false;
    private Boolean hard_mode = false;

    // Canvas.drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint);

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

        setContentView(R.layout.fragment_game_menu);
        mControlsView = findViewById(R.id.fullscreen_content);
        Button normal_button, arcade_button;
        normal_button = (Button) findViewById(R.id.normal_button);
        arcade_button = (Button) findViewById(R.id.arcade_button);
        final EditText player_name = (EditText)findViewById(R.id.player_name);
        final CheckBox hardModeBox = (CheckBox)findViewById(R.id.checkBox);

        hardModeBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCheckBoxClicked(v);
            }

        });
        normal_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playerName = (player_name.getText().toString());
                game_menu = false;
                setContentView(R.layout.activity_game);
                redraw();
                tStart = System.currentTimeMillis();
                new gameLogic().stackOfCards();
                currentCard();

                final Handler mHandler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // do your stuff - don't create a new runnable here!
                        redraw();
                        mHandler.postDelayed(this, 1000);
                    }
                };

                // start it with:
                mHandler.post(runnable);

            }
        });

        arcade_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                arcade_mode = true;
                playerName = (player_name.getText().toString());
                game_menu = false;
                setContentView(R.layout.activity_game);
                redraw();
                //RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_view);
                //relativeLayout.addView(new Rectangle(getApplicationContext()));

                new gameLogic().stackOfCards();
                currentCard();

                //120000 = 2 min
                new CountDownTimer(120000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timer = "" + millisUntilFinished / 1000;
                        redraw();
                    }

                    public void onFinish() {
                        //false for arcade mode (i.e not normal)
                        stackComplete(false);
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
    }

    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.checkBox:
                if(checked){
                    Toast.makeText(gameActivity.this, "Hard mode on", Toast.LENGTH_SHORT).show();
                    hard_mode = true;
                }
        }

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
                if(!game_menu) {
                    mVelocityTracker.addMovement(event);

                    float centerX = event.getX() - (cardImg.getLeft() - event.getY() + cardImg.getRight() + event.getY() / 2) / 2;
                    float centerY = event.getY() - (cardImg.getTop() - event.getX() + cardImg.getBottom() + event.getX() / 2) / 2;

                    cardImg.setX(centerX);
                    cardImg.setY(centerY);
                    cardText.setX(centerX);
                    cardText.setY(centerY);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    mVelocityTracker.computeCurrentVelocity(1000);
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.

                    break;
                }
            case MotionEvent.ACTION_UP:
                if(!game_menu) {
                    float x = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                    float y = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                    if (game_menu) return true;
                    checkCardType();

                    if (x > 100) {
                        if (isDiamonds) {
                            if (diamonds_stack.empty() && currentCard.contains("D")) nextCard(diamonds_stack);
                            if (checkCurrentCardNumber() == checkNextCardNumber(diamonds_stack)) {
                                nextCard(diamonds_stack);
                                cardPlaced();
                            }
                        }
                        redraw();
                    }
                    if (x < 100) {
                        if (isHearts) {
                            if (hearts_stack.empty() && currentCard.contains("H")) nextCard(hearts_stack);
                            if (checkCurrentCardNumber() == checkNextCardNumber(hearts_stack)) {
                                nextCard(hearts_stack);
                                cardPlaced();
                            }
                        }
                        redraw();
                    }
                    if (y < 100) {
                        if (isSpades) {
                            if (spades_stack.empty() && currentCard.contains("S")) nextCard(spades_stack);
                            if (checkCurrentCardNumber() == checkNextCardNumber(spades_stack)) {
                                nextCard(spades_stack);
                                cardPlaced();
                            }
                        }
                        redraw();
                    }
                    if (y > 100) {
                        if (isClovers) {
                            if (clovers_stack.empty() && currentCard.contains("C")) nextCard(clovers_stack);
                            if (checkCurrentCardNumber() == checkNextCardNumber(clovers_stack)) {
                                nextCard(clovers_stack);
                                cardPlaced();
                            }
                        }
                        redraw();
                    }

                    else{
                        if (!playingcards.empty()) nextCard(playingcards_skip);
                    }
                    redraw();
                    xpos = (int) event.getX();
                    ypos = (int) event.getY();
                    //String pos = xcor + "," + ycor;
                    cardPlaced();
                }
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.clear();
                break;
        }

        return true;
    }


    /**
     * Display card image from sprite sheet
     *
     * CardImage(int card_type, int card_nr)
     * card_type is (0-3)
     * card_nr is (2-14)
     */
    public void cardImage(int card_type, int card_nr){
        //          left, top,right,bottom
        //HEART     A: 0,   0,  300,   420
        //SPADE     A: 0, 420,  300,   420
        //DIAMOND   A: 0, 840,  300,   420
        //CLOVER    A: 0,1260,  300,   420

        //          left, top,right,bottom
        //HEART   2: 300,   0,  300,   420
        //SPADE   2:   0, 420,  300,   420
        //DIAMOND 2:   0, 840,  300,   420
        //CLOVER  2:   0,1260,  300,   420

        //START HEART:    0,   0, 300, 420
        //NEXT  HEART: +300,  +0,  +0,  +0

        //START SPADE:    0, 420, 300, 420
        //NEXT  SPADE: +300,  +0,  +0,  +0

        //START DIAMO:    0, 840, 300, 420
        //NEXT  DIAMO: +300,  +0,  +0,  +0

        //START CLOVE:    0,1260, 300, 420
        //NEXT  CLOVE: +300,  +0,  +0,  +0

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.playingcards);

        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int card_width  = /*300*/bitmap.getWidth()/13;
        int card_height = /*420*/bitmap.getHeight()/4;


        card_nr -= 1;
        if(card_nr == 13) card_nr = 0;
        int card_x_pos = card_nr*card_width;
        int card_y_pos = card_type*card_height;

        Bitmap card = Bitmap.createBitmap(bitmap,
                /*card_x_pos*/card_x_pos,
                /*card_y_pos*/card_y_pos,
                /*card_width*/card_width,
                /*card_height*/card_height);

        cardImg.setImageBitmap(card);
    }

    /**
    * Reset position of current card image
    */
    public void cardPlaced(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        int offsetX = cardImg.getLeft()/2 + cardImg.getRight()/2;
        int offsetY = cardImg.getTop()/2 + cardImg.getBottom()/2;

        cardImg.setX(width/2 -offsetX/2);
        cardImg.setY(height/2 -offsetY/2);
        cardText.setX(width/2 -offsetX/2);
        cardText.setY(height/2 -offsetY/2);
    }

    /* DRAWING RECTANGLES */
    public Rect spades,clover,hearts,diamond,middle;
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

            paintb.setColor(Color.parseColor("#C5B183"));
            paintr.setColor(Color.parseColor("#C5AC75"));
            painty.setColor(Color.YELLOW);
            spades = new Rect(left,top,right,(int)(0.15*bottom));
            clover = new Rect(left,bottom-(int)(0.15*bottom),right,bottom);
            hearts = new Rect(left,top+(int)(bottom*0.15),left+(int)(right*0.25),(int)(bottom*0.85));
            diamond = new Rect(right-(int)(0.25*right),top+(int)(bottom*0.15),right,(int)(bottom*0.85));
            //middle = new Rect((int)(Math.floor(0.14*bottom)),top+(int)(bottom*0.15),right-(int)(right*0.25),(int)(bottom*0.85));

            canvas.drawRect(spades, paintb);
            canvas.drawRect(clover, paintb);
            canvas.drawRect(hearts, paintr);
            canvas.drawRect(diamond,paintr);
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


            paintText.setColor(Color.BLACK);
            paintText.setTextSize(40);
            canvas.drawText("Score: "+score,height-height+50,left+50,paintText);

            double tEnd = System.currentTimeMillis();
            double tDelta = tEnd - tStart;
            int elapsedM = (int) (tDelta / 1000.0);
            if(!arcade_mode)timer = ""+elapsedM;
            canvas.drawText(timer,height-height+50,left+100,paintText);
            //SPADES
            canvas.drawText(spades_card_peek, spadesX-15, bottom-bottom+100, paintText);
            //HEARTS
            canvas.drawText(hearts_card_peek,left+10, spadesY, paintText);
            //CLOVERS
            canvas.drawText(clovers_card_peek,spadesX-5, bottom-100, paintText);
            //DIAMONDS
            canvas.drawText(diamonds_card_peek,right-100, spadesY, paintText);
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

    public int score = 0;
    public String timer = "";
    public Stack playingcards = new Stack();
    public Stack playingcards_skip = new Stack();

    public Stack spades_stack = new Stack();
    public String spades_card_peek = "S";

    public Stack clovers_stack = new Stack();
    public String clovers_card_peek = "C";

    public Stack diamonds_stack = new Stack();
    public String diamonds_card_peek = "D";

    public Stack hearts_stack = new Stack();
    public String hearts_card_peek = "H";

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
            cardText.setText(currentCard);
            Log.d("Current Card is: ", currentCard);
        }
        int cardType = 0;
        checkCardType();
        if(isHearts) cardType = 0;
        if(isSpades) cardType = 1;
        if(isDiamonds) cardType = 2;
        if(isClovers) cardType = 3;
        cardImage(cardType,Integer.parseInt(currentCard.substring(0, 2)));
    }

    public void checkCardType(){
        isSpades = currentCard.contains("S");
        isHearts = currentCard.contains("H");
        isDiamonds = currentCard.contains("D");
        isClovers = currentCard.contains("C");
    }

    public void nextCard(Stack stack){
        Log.d("Game", "Next card on stack.");
        Iterator itr = playingcards.iterator();
        if(stack != playingcards_skip) {
            if (!playingcards.empty()) {
                if (stack.isEmpty()) {
                    checkCardType();
                    score += 100;
                    stack.push(playingcards.pop());
                } else stack.push(playingcards.pop());
            }
        }
        if(stack == playingcards_skip) playingcards_skip.push(playingcards.pop());
        if(playingcards.size() == 0) addRestToStack();
        if(playingcards.empty() && playingcards_skip.empty()) stackComplete(true);
        if(getPoints()) score += 10;
        if(!getPoints()) score -= 100;

        //check card
        currentCard();

        Random random = new Random();
        int i1 = random.nextInt(10000 - 2000 + 1) + 10000;
        if(hard_mode)stealCardPoint(i1);
    }

    private void stealCardPoint(int chance){
        //steal points
        final String cardToSteal = currentCard;
        Handler mHandler = new Handler();
        if(chance > 15000) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    Log.d("Steal Card","current: "+currentCard+" & StealCard: "+cardToSteal);
                    if(cardToSteal == currentCard){
                        Log.d(cardToSteal,currentCard);
                        Toast.makeText(gameActivity.this, "Card Stolen", Toast.LENGTH_SHORT).show();
                        score -= 133;
                        nextCard(playingcards_skip);
                    }
                }
            }, 3000);
        }
    }

    public boolean getPoints(){
        if(isDiamonds) {
            //if (diamonds_stack.empty()) nextCard(diamonds_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(diamonds_stack)) {
                return false;
            }
        }
        if(isSpades) {
            //if (spades_stack.empty()) nextCard(spades_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(spades_stack)) {
                return false;
            }
        }
        if(isClovers) {
            //if (clovers_stack.empty()) nextCard(clovers_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(clovers_stack)) {
                return false;
            }
        }
        if(isHearts) {
            //if (hearts_stack.empty()) nextCard(hearts_stack);
            if (checkCurrentCardNumber() == checkNextCardNumber(hearts_stack)) {
                return false;
            }
        }
        redraw();
        return true;
    }

    public int checkNextCardNumber(Stack stack){
        if(!stack.empty()){
            String nr = stack.peek().toString();
            nr = nr.substring(0,2);
            int number = Integer.parseInt(nr);

            if(number == 14) return 2;
            else return number+1;
        }
        return 0;
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

    public void stackComplete(Boolean mode){
        Toast.makeText(getApplication(),"GAME WON!!! "+" Results: ",Toast.LENGTH_SHORT).show();
        if(mode){
            long tEnd = System.currentTimeMillis();
            long tDelta = tEnd - tStart;
            double elapsedSeconds = tDelta / 1000.0;
            timer = ""+elapsedSeconds;
            Toast.makeText(getApplication(),"Time taken: "+elapsedSeconds +" Sec.",Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplication(),"Score: " +score,Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(getApplication(),"Score: " +score,Toast.LENGTH_SHORT).show();
        String results = score +"";
        new PostTask(playerName,results).execute();
        finish();
    }

    public void redraw(){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_view);
        cardImg = (ImageView)findViewById(R.id.myImageView);
        cardText = (TextView)findViewById(R.id.cardtext);
        relativeLayout.removeAllViews();
        relativeLayout.addView(new Rectangle(this));
        relativeLayout.addView(cardImg);
        relativeLayout.addView(cardText);
        peekCheat();

        if(!clovers_card_peek.contains("C")) playingcards_skip.push(clovers_stack.pop());
        if(!diamonds_card_peek.contains("D")) playingcards_skip.push(diamonds_stack.pop());
        if(!spades_card_peek.contains("S")) playingcards_skip.push(spades_stack.pop());
        if(!hearts_card_peek.contains("H")) playingcards_skip.push(hearts_stack.pop());
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


    private class PostTask extends AsyncTask<Void, Void, String> {

        private final String mScore;

        PostTask(String name, String score) {
            playerName = name;
            mScore = score;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            HttpURLConnection conn = null;

            try {
                String score_url = "scores";
                if (arcade_mode) score_url = "scores-arcade";
                conn = (HttpURLConnection) new URL(Util.HOST_URL + score_url).openConnection();

                // set to POST
                conn = Util.setPostConnection(conn);
                JSONObject POST_PARAM = new JSONObject();
                POST_PARAM.put("name", playerName);
                POST_PARAM.put("score", mScore);
                if(!arcade_mode)POST_PARAM.put("time",timer);

                Util.sendPostJSON(conn, POST_PARAM);

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // Check for server response
                    throw new IOException(conn.getResponseMessage() +": with " + Util.HOST_URL+score_url);
                }
                // Receive uploaded file location from server
                response = conn.getHeaderField("Location");
                conn.disconnect();

                Log.d("Server Connection","Connecting");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            //Do something
        }

        @Override
        protected void onCancelled() {
            //Do something
        }
    }
}

/*------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------
* ------------------------------------------------------------------------------------------------*/