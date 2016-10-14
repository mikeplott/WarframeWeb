package com.company;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by michaelplott on 10/14/16.
 */
public class MainTest {

    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testInsertUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    public void testUserItemList() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertUserItem(conn, "lsjdf", "ljkdalf", "adjflaf", user.id);
        Main.insertUserItem(conn , "daslfjlja", "lakjsdfasfl", "akjsdlfja", user.id);
        ArrayList userItems = Main.userList(conn, user.id);
        conn.close();
        assertTrue(userItems.size() == 2);
    }

    @Test
    public void selectUserItem() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertUserItem(conn, "lsjdf", "ljkdalf", "adjflaf", user.id);
        Main.insertUserItem(conn , "daslfjlja", "lakjsdfasfl", "akjsdlfja", user.id);
        Item item = Main.selectUserItem(conn, 1);
        conn.close();
        assertTrue(item != null);
    }
}