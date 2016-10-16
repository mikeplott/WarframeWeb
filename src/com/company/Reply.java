package com.company;

/**
 * Created by michaelplott on 10/16/16.
 */
public class Reply {
    int id;
    String text;
    String author;
    int messageID;
    int userID;

    public Reply() {
    }

    public Reply(int id, String text, String author, int userID) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.userID = userID;
    }

    public Reply(int id, String text, String author, int messageID, int userID) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.messageID = messageID;
        this.userID = userID;
    }
}
