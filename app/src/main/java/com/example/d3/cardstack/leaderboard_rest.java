package com.example.d3.cardstack;

/**
 * Created by Daniel on 03-Oct-16.
 */
public class leaderboard_rest {

    private String _id;
    private String name;
    private String score;
    private String time;

    public leaderboard_rest(String id, String name, String score, String time){
        this._id = _id;
        this.name = name;
        this.score = score;
        this.time = time;

    }

    public String getID_cs() {
        return this._id;
    }

    public String getName_cs(){
        return this.name;
    }

    public String getScore_cs(){
        return this.score;
    }

    public String getTime_cs(){
        return this.time;
    }
}
