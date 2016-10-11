package com.example.d3.cardstack;

/**
 * Created by Daniel on 05-Oct-16.
 */
public class user_rest {
    private String _id;
    private String username;
    private String password;

    public user_rest(String _id, String username, String password){
        this._id = _id;
        this.username = username;
        this.password = password;

    }

    public String getID_cs() {
        return this._id;
    }

    public String getUser_cs(){
        return this.username;
    }

    public String getPassword_cs(){
        return this.password;
    }

    public void setUser_cs(){this.username = username;}

    public void setPassword_cs(){this.password = password;}
}
