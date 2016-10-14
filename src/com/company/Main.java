package com.company;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./warframe");


        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");

                    HashMap m = new HashMap();
                    m.put("name", name);
                    return new ModelAndView(m, "index.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("name");
                    String pass = request.queryParams("pass");
                    User user = selectUser(conn, name);
                    if (user == null) {
                        insertUser(conn, name, pass);
                    }
                    else if (!pass.equals(user.password)) {
                        Spark.halt(403);
                        return null;
                    }
                    Session session = request.session();
                    session.attribute("loginName", name);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return null;
                }
        );
    }

    public static void insertUser(Connection conn, String name, String pass) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, pass);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String username = results.getString("name");
            String pass = results.getString("password");
            return new User(id, username, pass);
        }
        return null;
    }

    public static ArrayList<Item> itemsList(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_list");
        ResultSet results = stmt.executeQuery();
        ArrayList<Item> items = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String cat = results.getString("category");
            String relic = results.getString("void_relic");
            items.add(new Item(id, name, cat, relic));
        }
        return items;
    }

    public static ArrayList<Item> userList(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_items INNER JOIN users ON user_items.user_id = users.id WHERE user_items.user_id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        ArrayList<Item> userList = new ArrayList<>();
        while (results.next()) {
            int itemId = results.getInt("id");
            String name = results.getString("name");
            String category = results.getString("category");
            String relic = results.getString("void_relic");
            userList.add(new Item(itemId, name, category, relic));
        }
        return userList;
    }

    public static Item selectItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM item_list WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int itemID = results.getInt("id");
            String name = results.getString("name");
            String category = results.getString("category");
            String relic = results.getString("void_relic");
            return new Item(itemID, name, category, relic);
        }
        return null;
    }

    public static Item selectUserItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_items WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int itemID = results.getInt("id");
            String name = results.getString("name");
            String category = results.getString("category");
            String relic = results.getString("void_relic");
            return new Item(itemID, name, category, relic);
        }
        return null;
    }

    public static void insertUserItem(Connection conn, String name, String category, String relic, int uid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_items VALUES(NULL, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, category);
        stmt.setString(3, relic);
        stmt.setInt(4, uid);
        stmt.execute();
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS user_items (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS item_list (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR)");
    }

}
