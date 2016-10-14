package com.company;

/**
 * Created by michaelplott on 10/14/16.
 */
public class Item {
    int id;
    String name;
    String category;
    String voidRelic;
    int quantity;
    int userID;

    public Item() {
    }

    public Item(int id,String name, String category, String voidRelic) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.voidRelic = voidRelic;
    }

    public Item(int id, String name, String category, String voidRelic, int quantity, int userID) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.voidRelic = voidRelic;
        this.quantity = quantity;
        this.userID = userID;
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

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", voidRelic='" + voidRelic + '\'' +
                ", userID=" + userID +
                '}';
    }
}
