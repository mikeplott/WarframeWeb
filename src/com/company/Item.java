package com.company;

/**
 * Created by michaelplott on 10/14/16.
 */
public class Item {
    int id;
    String name;
    String category;
    String voidRelic;

    public Item() {
    }

    public Item(String name, String category, String voidRelic) {
        this.name = name;
        this.category = category;
        this.voidRelic = voidRelic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVoidRelic() {
        return voidRelic;
    }

    public void setVoidRelic(String voidRelic) {
        this.voidRelic = voidRelic;
    }
}
