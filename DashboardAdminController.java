package Controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Pos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class DashboardAdminController {

    private Stage stage;
    private Scene scene;

    @FXML
    private VBox notificationBox;

    @FXML
    public void initialize() throws SQLException {
        loadPendingItems();
    }

    private void loadPendingItems() throws SQLException {
    notificationBox.getChildren().clear();
    notificationBox.setFillWidth(true); // Ensure it fills the width
    
    Connection conn = DataBaseConnection.getConnection();
    String sql = "SELECT * FROM item WHERE isApproved = 'Pending'";

    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int itemId = rs.getInt("itemID");
            String itemName = rs.getString("itemName");
            String description = rs.getString("description");
            String category = rs.getString("category");
             String reporter = rs.getString("UserName");

            HBox itemBox = new HBox(10);
            itemBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setMaxWidth(Double.MAX_VALUE); // Important for scrolling

            Label itemLabel = new Label("[" + category + "] " + itemName + " - " + description+ "\nReported by: " + reporter);
            itemLabel.setWrapText(true);
            itemLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(itemLabel, Priority.ALWAYS); // Allow label to grow

            Button approveBtn = new Button("Approve");
            approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            approveBtn.setOnAction(e -> approveItem(itemId));

            Button rejectBtn = new Button("Reject");
            rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            rejectBtn.setOnAction(e -> rejectItem(itemId));

            itemBox.getChildren().addAll(itemLabel, approveBtn, rejectBtn);
            notificationBox.getChildren().add(itemBox);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void approveItem(int itemId) {
        String sql = "UPDATE item SET isApproved = TRUE WHERE itemID = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();
            showAlert("Approved", "Item has been approved.");
            loadPendingItems(); // Refresh

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rejectItem(int itemId) {
        String sql = "DELETE FROM item WHERE itemID = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();
            showAlert("Rejected", "Item has been rejected and removed.");
            loadPendingItems(); // Refresh

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void LostUI(ActionEvent event) throws IOException{
         Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Lost.fxml"));
         stage = (Stage)((Node)event.getSource()).getScene().getWindow();
         scene  = new Scene(root);
         stage.setScene(scene);
         stage.setFullScreen(false);
         stage.show();
    }
    public void FoundUI(ActionEvent found) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Found.fxml"));
        stage = (Stage)((Node)found.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }
    public void ChatUI(ActionEvent chat) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Chat.fxml"));
        stage = (Stage)((Node)chat.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }
    public void DashBoardUI(ActionEvent dashboard)  throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Dashboard.fxml"));
        stage = (Stage)((Node)dashboard.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }
    public void ProfileUI(ActionEvent dashboard)  throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Logout.fxml"));
        stage = (Stage)((Node)dashboard.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }
    
}
