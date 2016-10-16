package com.company;

import java.util.ArrayList;

/**
 * Created by michaelplott on 10/16/16.
 */
public class Message {
    int id;
    String text;
    String author;
    ArrayList<Reply> replies;
    int userID;

    public Message() {
    }

    public Message(int id, String text, String author, int userID) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.userID = userID;
    }

    public Message(int id, String text, String author, ArrayList<Reply> replies, int userID) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.replies = replies;
        this.userID = userID;
    }
}
