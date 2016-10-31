package com.company;

/**
 * Created by michaelplott on 10/19/16.
 */
public class Buddy {
    int id;
    String name;
    int userID;

    public Buddy() {
    }

    public Buddy(int id, String name, int userID) {
        this.id = id;
        this.name = name;
        this.userID = userID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUserID() {
        return userID;
    }
}
