package Controller;

import static Main.UserSession.getCurrentUsername;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatController {
    
    private Stage stage;
    private Scene scene;

    @FXML
    private ListView<String> accountListView;

    @FXML
    private VBox chatContainer;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button fileButton;

    private String currentUser;
    private String selectedContact;

    @FXML
    public void initialize() {
        try {
            // Set current user from session
            currentUser = getCurrentUsername();
            
            // Load all users except current user
            List<String> users = DataBaseConnection.getAllUsernamesExcept(currentUser);
            accountListView.getItems().addAll(users);
            
            // Disable send button until contact is selected
            sendButton.setDisable(true);

            // Set up contact selection listener
            accountListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                selectedContact = newVal;
                try {
                    loadChatHistory(selectedContact);
                    sendButton.setDisable(false);
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to load chat history: " + e.getMessage());
                }
            });

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load contacts: " + e.getMessage());
        }
    }
    
    @FXML
    public void handleSendMessage() {
        if (selectedContact == null || messageInput.getText().trim().isEmpty()) {
            return;
        }

        try {
            String message = messageInput.getText().trim();
            int senderID = DataBaseConnection.getUserID(currentUser);
            int receiverID = DataBaseConnection.getUserID(selectedContact);

            // Save to database
            DataBaseConnection.sendMessage(senderID, receiverID, message);

            // Refresh chat display
            messageInput.clear();
            loadChatHistory(selectedContact);
            
        } catch (SQLException e) {
            showAlert("Send Error", "Failed to send message: " + e.getMessage());
        }
    }

    private void loadChatHistory(String contact) throws SQLException {
        // Clear previous messages
        chatContainer.getChildren().clear();
        
        // Get IDs for both users
        int user1ID = DataBaseConnection.getUserID(currentUser);
        int user2ID = DataBaseConnection.getUserID(contact);

        // Retrieve messages from database
        List<String> messages = DataBaseConnection.getMessagesBetween(user1ID, user2ID);
        
        // Display each message
        for (String msg : messages) {
            boolean isCurrentUser = msg.startsWith(currentUser + ":");
            addMessageToChat(msg, isCurrentUser);
        }

        scrollToBottom();
    }
private void addMessageToChat(String msg, boolean isCurrentUser) {
    Text text = new Text(msg.replaceFirst("^.*?: ", "")); // Remove sender prefix
    TextFlow flow = new TextFlow(text);
    
    // Style based on sender
    if (isCurrentUser) {
        flow.setStyle("-fx-background-color: #DCF8C6; -fx-padding: 5; -fx-background-radius: 10;");
        flow.setMaxWidth(chatContainer.getWidth() * 0.8); // Limit width for better appearance
        HBox.setHgrow(flow, Priority.NEVER);
    } else {
        flow.setStyle("-fx-background-color: #E5E5EA; -fx-padding: 5; -fx-background-radius: 10;");
        flow.setMaxWidth(chatContainer.getWidth() * 0.8);
        HBox.setHgrow(flow, Priority.NEVER);
    }
    
    // Create container for proper alignment
    HBox messageContainer = new HBox();
    messageContainer.setAlignment(isCurrentUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    messageContainer.getChildren().add(flow);
    
    // Add spacing between messages
    VBox.setMargin(messageContainer, new Insets(5, 10, 5, 10));
    chatContainer.getChildren().add(messageContainer);
}

    private void scrollToBottom() {
        // JavaFX needs these delays to properly scroll
        new Thread(() -> {
            try {
                Thread.sleep(100);
                javafx.application.Platform.runLater(() -> {
                    chatScrollPane.setVvalue(1.0);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    public void LostUI(ActionEvent event) throws IOException {
        switchScene(event, "/Fxml/Lost.fxml");
    }
    
    public void FoundUI(ActionEvent event) throws IOException {
        switchScene(event, "/Fxml/Found.fxml");
    }
    
    public void ChatUI(ActionEvent event) throws IOException {
        switchScene(event, "/Fxml/Chat.fxml");
    }
    
    public void DashBoardUI(ActionEvent event) throws IOException {
        switchScene(event, "/Fxml/Dashboard.fxml");
    }
    public void ProfileUI(ActionEvent event) throws IOException {
        switchScene(event, "/Fxml/Logout.fxml");
    }
    
    private void switchScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}