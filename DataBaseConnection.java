package Controller;

import Main.Item;
import Main.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

public class DataBaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/lost_and_found";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Method to insert a new user into the database
    public static void insertUser(String course, String name, String username, String email, String password) throws SQLException {
        String query = "INSERT INTO userinfo (COURSE, `Student Name`, userName, Gmail, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, course);   // COURSE
            pstmt.setString(2, name);     // `Student Name`
            pstmt.setString(3, username); // `userName`
            pstmt.setString(4, email);    // Gmail
            pstmt.setString(5, password); // password

            pstmt.executeUpdate();
        }
    }

    // Method to validate user login
    public static String[] loginUser(String username, String password) throws SQLException {
        String query = "SELECT password, role FROM userinfo WHERE userName = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String role = rs.getString("role");
                    boolean verified = BCrypt.verifyer().verify(password.toCharArray(), storedHash).verified;
                    return new String[]{String.valueOf(verified), role};
                } else {
                    return new String[]{"false", null};
                }
            }
        }
    }

    // Method to get all items from the item table
        public static List<Item> getLostItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE Category = 'Lost' AND Status != 'Claimed' AND isApproved != 'Pending'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Extract the BLOB from the result set
                byte[] imageBytes = rs.getBytes("Image");  // Assuming "Image" is the column storing the BLOB

                // Convert byte[] to Image (if imageBytes is not null)
                Image image = imageBytes != null ? new Image(new ByteArrayInputStream(imageBytes)) : null;

                Item item = new Item(
                    rs.getInt("itemID"),
                    rs.getString("itemName"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    image, // Pass the Image object to the Item constructor
                    rs.getString("Status"),
                    rs.getString("UserName"),
                    rs.getDate("DateSubmitted"),
                    rs.getDate("DateClaimed"),
                    rs.getString("Category")
                );
                items.add(item);
            }
        }
        return items;
    }

        // Method to get a connection to the database
        public static Connection getConnection() throws SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                throw new SQLException("Failed to connect to the database", e);
            }
        }
        public static int getTotalItemsPendingAndLost() throws SQLException {
        String query = "SELECT COUNT(*) FROM item WHERE Status = ? AND Category = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "Pending");
            pstmt.setString(2, "Lost");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Get the count of the rows
                } else {
                    return 0;  // If no results, return 0
                }
            }
        }
    }
        
        public static List<Item> getFoundItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE Category = 'Found' AND Status != 'Claimed' AND isApproved != 'Pending'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                byte[] imageBytes = rs.getBytes("Image");
                Image image = imageBytes != null ? new Image(new ByteArrayInputStream(imageBytes)) : null;

                Item item = new Item(
                    rs.getInt("itemID"),
                    rs.getString("itemName"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    image,
                    rs.getString("Status"),
                    rs.getString("UserName"),
                    rs.getDate("DateSubmitted"),
                    rs.getDate("DateClaimed"),
                    rs.getString("Category")
                );
                items.add(item);
            }
        }
        return items;
    }
        public static boolean deleteItem(int itemID) throws SQLException {
        String query = "DELETE FROM item WHERE itemID = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, itemID);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0; // Returns true if deletion was successful
        }
    }
        
        
                // Add to DataBaseConnection.java
        public static List<Item> searchItemsByName(String searchTerm, String category) throws SQLException {
            List<Item> items = new ArrayList<>();
            String query = "SELECT * FROM item WHERE itemName LIKE ? AND Category = ?";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, category);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        byte[] imageBytes = rs.getBytes("Image");
                        Image image = imageBytes != null ? new Image(new ByteArrayInputStream(imageBytes)) : null;

                        Item item = new Item(
                            rs.getInt("itemID"),
                            rs.getString("itemName"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            image,
                            rs.getString("Status"),
                            rs.getString("UserName"),
                            rs.getDate("DateSubmitted"),
                            rs.getDate("DateClaimed"),
                            rs.getString("Category")
                        );
                        items.add(item);
                    }
                }
            }
            return items;
        }
        
   // Add to DataBaseConnection.java
public static List<String> getAllUsernamesExcept(String currentUser) throws SQLException {
    List<String> usernames = new ArrayList<>();
    String query = "SELECT userName FROM userinfo WHERE userName != ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, currentUser);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                usernames.add(rs.getString("userName"));
            }
        }
    }
    return usernames;
}

// Updated method to get user ID
public static int getUserID(String username) throws SQLException {
    String query = "SELECT id FROM userinfo WHERE userName = ?";  // Changed userID to id
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, username);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");  // Changed userID to id
            }
        }
    }
    return -1;
}

// Update message sending method
public static void sendMessage(int senderID, int receiverID, String message) throws SQLException {
    String query = "INSERT INTO messages (senderID, receiverID, message) VALUES (?, ?, ?)";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, senderID);
        pstmt.setInt(2, receiverID);
        pstmt.setString(3, message);
        
        pstmt.executeUpdate();
    }
}

// Update method to get messages
public static List<String> getMessagesBetween(int user1ID, int user2ID) throws SQLException {
    List<String> messages = new ArrayList<>();
    String query = "SELECT u.userName, m.message FROM messages m " +
                   "JOIN userinfo u ON m.senderID = u.id " +  // Changed userID to id
                   "WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) " +
                   "ORDER BY m.timestamp";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, user1ID);
        pstmt.setInt(2, user2ID);
        pstmt.setInt(3, user2ID);
        pstmt.setInt(4, user1ID);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String sender = rs.getString("userName");
                String msg = rs.getString("message");
                messages.add(sender + ": " + msg);
            }
        }
    }
    return messages;
}
public static String getReporterUsernameByItemID(int itemID) throws SQLException {
    String query = "SELECT i.UserName FROM item i WHERE i.itemID = ?";
    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, itemID);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("UserName");
        }
    }
    return null;
}

public static Connection connect() throws SQLException {
    

    return DriverManager.getConnection(URL, USER, PASSWORD);
}

public static boolean markItemAsDone(int itemID) throws SQLException {
    String query = "UPDATE item SET Status = 'Completed' WHERE itemID = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, itemID);
        int rowsAffected = pstmt.executeUpdate();

        return rowsAffected > 0; // Returns true if the update was successful
    }
}

public static void markItemAsClaimed(int itemID) {
    String updateQuery = "UPDATE item SET Status = 'Claimed', DateClaimed = CURRENT_TIMESTAMP WHERE itemID = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
        pstmt.setInt(1, itemID);
        pstmt.executeUpdate();
        System.out.println("Item #" + itemID + " marked as Claimed with current timestamp.");
    } catch (SQLException e) {
        System.err.println("Error updating item status: " + e.getMessage());
    }
}

// Enhanced method to get complete user profile
public static User getUserProfile(String username) throws SQLException {
    String query = "SELECT id, COURSE, `Student Name`, userName, Gmail, role FROM userinfo WHERE userName = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, username);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("COURSE"),
                    rs.getString("Student Name"),
                    rs.getString("userName"),
                    rs.getString("Gmail"),
                    rs.getString("role")
                );
            }
        }
    }
    return null;
}


}
