package com.company;

import org.h2.tools.Server;
import spark.Session;
import spark.Spark;

import java.sql.*;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./warframe");


        Spark.get(
                "/",
                (request, response) -> {
                    return null;
                }
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
    }

    public static void insertUser(Connection conn, String name, String pass) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, name = ?, password = ?)");
        stmt.setString(1, name);
        stmt.setString(2, pass);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) {
        return null;
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS user_items (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS item_list (id IDENTITY, name VARCHAR, category VARCHAR, void_relic VARCHAR)");
    }

}
