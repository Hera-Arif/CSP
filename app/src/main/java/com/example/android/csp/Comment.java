package com.example.android.csp;

/**
 * Created by Mapkar on 2/16/2018.
 */

public class Comment {

    String username;
    String comment;

    public Comment(){}

    public Comment(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }


    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }
}
