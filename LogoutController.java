package Controller;

import Main.User;
import Main.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.Parent;

public class LogoutController {
    @FXML private Label usernameLabel;
    @FXML private Label courseLabel;
    @FXML private Label gmailLabel;
    @FXML private ImageView profileImage;

    @FXML
    public void initialize() {
        // This will be called automatically when the FXML loads
        User currentUser = UserSession.getCurrentUser();
        if(currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            courseLabel.setText(currentUser.getCourse());
            gmailLabel.setText(currentUser.getEmail());
            
            // You can add profile image loading here if needed
            // profileImage.setImage(...);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ... keep your existing navigation methods ...


@FXML
    void DashBoardUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/Dashboard.fxml", event);
    }

    @FXML
    void LostUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/Lost.fxml", event);
    }

    @FXML
    void FoundUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/Found.fxml", event);
    }

    @FXML
    void ChatUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/Chat.fxml", event);
    }

    @FXML 
    void ProfileUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/ReportFound.fxml", event);
    }

    private void switchScene(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }
    
    
}
