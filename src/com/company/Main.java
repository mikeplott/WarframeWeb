package com.company;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./warframe");
        createTables(conn);
        //fileImport(conn);

        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn, name);
                    ArrayList<Item> itemList = itemsList(conn);
                    HashMap m = new HashMap();
                    if (user != null) {
                        ArrayList<Item> userItems = userList(conn, user.id);
                        m.put("userItems", userItems);
                    }
                    m.put("name", name);
                    m.put("items", itemList);
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

        Spark.post(
                "/add-item",
                (request, response) -> {
                    String id = request.queryParams("id");
                    int itemID = Integer.parseInt(id);
                    String num = request.queryParams("quantity");
                    int quantity = Integer.parseInt(num);
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn, name);
                    if (user == null) {
                        Spark.halt(403);
                        return null;
                    }
                    Item item = selectItem(conn, itemID);
                    insertUserItem(conn, item.name, item.category, item.voidRelic, quantity, user.id);
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/delete-item",
                (request, response) -> {
                    String id = request.queryParams("id");
                    int itemID = Integer.parseInt(id);
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn, name);
                    if (user == null) {
                        Spark.halt(403);
                        response.redirect("/");
                    }
                    ArrayList<Item> userItems = userList(conn, user.id);
                    for (int i = 0; i < userItems.size(); i++) {
                        if (userItems.get(i).id == itemID) {
                            Item item = userItems.get(i);
                            if (item.userID == user.id) {
                                deleteUserItem(conn, item.id);
                            }
                        }
                    }
                    response.redirect("/");
                    return null;
                }
        );

        Spark.get(
                "/edit-item",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn, name);
                    ArrayList<Item> userList = userList(conn, user.id);
                    HashMap m = new HashMap();
                    m.put("name", user.name);
                    m.put("userItems", userList);
                    return new ModelAndView(m, "edit-items.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/edit-item",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = selectUser(conn, name);
                    if (user == null) {
                        Spark.halt(403);
                    }
                    String id = request.queryParams("id");
                    String num = request.queryParams("quantity");
                    int quantity = Integer.parseInt(num);
                    int itemID = Integer.parseInt(id);
                    Item item = selectUserItem(conn, itemID);
                    updateUserItem(conn, quantity, item.id);
                    response.redirect("/edit-item");
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
            int quantity = results.getInt("quantity");
            int userID = results.getInt("user_id");
            userList.add(new Item(itemId, name, category, relic, quantity, userID));
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

    public static void insertUserItem(Connection conn, String name, String category, String relic, int quantity, int uid) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_items VALUES(NULL, ?, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, category);
        stmt.setString(3, relic);
        stmt.setInt(4, quantity);
        stmt.setInt(5, uid);
        stmt.execute();
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS user_items (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR, quantity INT, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS item_list (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR)");
    }

    public static void insertItem(Connection conn, String name, String category, String relic) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO item_list VALUES(NULL, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, category);
        stmt.setString(3, relic);
        stmt.execute();
    }

    public static void deleteUserItem(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM user_items WHERE user_items.id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void updateUserItem(Connection conn, int quantity, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE user_items SET quantity = ? WHERE user_items.id = ?");
        stmt.setInt(1, quantity);
        stmt.setInt(2, id);
        stmt.execute();
    }

    public static void fileImport(Connection conn) throws FileNotFoundException, SQLException {
        File f = new File("items.txt");
        Scanner fileScanner = new Scanner(f);
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            String[] columns = line.split("\\|");
            String name = columns[0];
            String category = columns[1];
            String relic = columns[2];
            if (relic.isEmpty()) {
                relic = null;
                insertItem(conn, name, category, relic);
            }
            insertItem(conn, name, category, relic);
        }
    }

}
