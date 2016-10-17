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
        Main.insertUserItem(conn, "lsjdf", "ljkdalf", "adjflaf", 3, user.id);
        Main.insertUserItem(conn, "daslfjlja", "lakjsdfasfl", "akjsdlfja", 3, user.id);
        ArrayList userItems = Main.userList(conn, user.id);
        conn.close();
        assertTrue(userItems.size() == 2);
    }

    @Test
    public void testSelectUserItem() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertUserItem(conn, "lsjdf", "ljkdalf", "adjflaf", 3, user.id);
        Main.insertUserItem(conn, "daslfjlja", "lakjsdfasfl", "akjsdlfja", 3, user.id);
        Item item = Main.selectUserItem(conn, 1);
        conn.close();
        assertTrue(item != null);
    }

    @Test
    public void testInsertItem() throws SQLException {
        Connection conn = startConnection();
        Main.insertItem(conn, "lsjdf", "ljkdalf", "adjflaf");
        Main.insertItem(conn, "daslfjlja", "lakjsdfasfl", "akjsdlfja");
        Item item = Main.selectItem(conn, 1);
        conn.close();
        assertTrue(item != null);
    }

    @Test
    public void testInsertMessage() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Message message = Main.selectMessage(conn, user.id);
        conn.close();
        assertTrue(message != null);
    }

    @Test
    public void testInsertReply() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Message message = Main.selectMessage(conn, user.id);
        Main.insertReply(conn, "Oh hi there!", user.name, message.id, user.id);
        Reply reply = Main.selectReply(conn, message.id);
        conn.close();
        assertTrue(reply != null);
    }

    @Test
    public void testRepliesInMessages() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        Main.insertUser(conn, "NotMike", "123");
        User user = Main.selectUser(conn, "Mike");
        User user2 = Main.selectUser(conn, "NotMike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Message message = Main.selectMessage(conn, user.id);
        Main.insertReply(conn, "Oh hi there!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi Mike!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi NotMike!", user.name, message.id, user.id);
        message.replies = Main.selectAllReplies(conn, message.id);
        conn.close();
        assertTrue(message.replies.size() == 3);
    }

    @Test
    public void testSelectAllMessages() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        Main.insertUser(conn, "NotMike", "123");
        User user = Main.selectUser(conn, "Mike");
        User user2 = Main.selectUser(conn, "NotMike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Main.insertMessage(conn, "Oh hi everyone!", user2.name, user2.id);
        Message message = Main.selectMessage(conn, user.id);
        Main.insertReply(conn, "Oh hi there!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi Mike!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi NotMike!", user.name, message.id, user.id);
        Message message1 = Main.selectMessage(conn, user2.id);
        Main.insertReply(conn, "Oh hi there!", user2.name, message1.id, user2.id);
        Main.insertReply(conn, "Hi Mike!", user2.name, message1.id, user2.id);
        Main.insertReply(conn, "Hi NotMike!", user.name, message1.id, user.id);
        message.replies = Main.selectAllReplies(conn, message.id);
        message1.replies = Main.selectAllReplies(conn, message1.id);
        ArrayList<Message> messages = Main.selectAllMessages(conn);
        conn.close();
        assertTrue(messages.size() == 2);
    }

    @Test
    public void testDeleteMessge() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        Main.insertUser(conn, "NotMike", "123");
        User user = Main.selectUser(conn, "Mike");
        User user2 = Main.selectUser(conn, "NotMike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Main.insertMessage(conn, "Oh hi everyone!", user2.name, user2.id);
        Message message = Main.selectMessage(conn, user.id);
        Main.insertReply(conn, "Oh hi there!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi Mike!", user2.name, message.id, user2.id);
        Main.insertReply(conn, "Hi NotMike!", user.name, message.id, user.id);
        Message message1 = Main.selectMessage(conn, user2.id);
        Main.insertReply(conn, "Oh hi there!", user2.name, message1.id, user2.id);
        Main.insertReply(conn, "Hi Mike!", user2.name, message1.id, user2.id);
        Main.insertReply(conn, "Hi NotMike!", user.name, message1.id, user.id);
        message.replies = Main.selectAllReplies(conn, message.id);
        message1.replies = Main.selectAllReplies(conn, message1.id);
        Main.deleteMessageReplies(conn, message1.id);
        Main.deleteMessage(conn, message1.id);
        ArrayList<Message> messages = Main.selectAllMessages(conn);
        conn.close();
        assertTrue(messages.size() == 1);
    }

    @Test
    public void testMessageUpdate() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Mike", "123");
        User user = Main.selectUser(conn, "Mike");
        Main.insertMessage(conn, "Hello!", user.name, user.id);
        Message message = Main.selectMessage(conn, user.id);
        Main.updateMessage(conn, "Test", message.id);
        Message message1 = Main.selectMessage(conn, user.id);
        conn.close();
        assertTrue(message1.text.equals("Test"));
    }
}

//    @Test
//    public void testDeleteReplies() throws SQLException {
//        Connection conn = startConnection();
//        Main.insertUser(conn, "Mike", "123");
//        Main.insertUser(conn, "NotMike", "123");
//        User user = Main.selectUser(conn, "Mike");
//        User user2 = Main.selectUser(conn, "NotMike");
//        Main.insertMessage(conn, "Hello!", user.name, user.id);
//        Main.insertMessage(conn, "Oh hi everyone!", user2.name, user2.id);
//        Message message = Main.selectMessage(conn, user.id);
//        Main.insertReply(conn, "Oh hi there!", user2.name, message.id, user2.id);
//        Main.insertReply(conn, "Hi Mike!", user2.name, message.id, user2.id);
//        Main.insertReply(conn, "Hi NotMike!", user.name, message.id, user.id);
//        Message message1 = Main.selectMessage(conn, user2.id);
//        Main.insertReply(conn, "Oh hi there!", user2.name, message1.id, user2.id);
//        Main.insertReply(conn, "Hi Mike!", user2.name, message1.id, user2.id);
//        Main.insertReply(conn, "Hi NotMike!", user.name, message1.id, user.id);
//        message.replies = Main.selectAllReplies(conn, message.id);
//        message1.replies = Main.selectAllReplies(conn, message1.id);
//        Main.deleteMessageReplies(conn, message1.id);
//        ArrayList<Reply> replies = message1.replies;
//        conn.close();
//        assertTrue(replies == null);
//    }

//    @Test
//    public void testDelete() throws SQLException {
//        Connection conn = startConnection();
//        Main.insertUser(conn, "Mike", "123");
//        User user = Main.selectUser(conn, "Mike");
//        Main.insertUserItem(conn, "lsjdf", "ljkdalf", "adjflaf", 2, user.id);
//        Main.insertUserItem(conn , "daslfjlja", "lakjsdfasfl", "akjsdlfja", 3, user.id);
//        Item item = Main.selectUserItem(conn, 1);
//        if (item.userID == user.id) {
//            Main.deleteUserItem(conn, item.id);
//        }
//        ArrayList<Item> userItems = Main.userList(conn, user.id);
//        assertTrue(userItems.size() == 1);
//    }