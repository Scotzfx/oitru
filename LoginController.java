
package Controller;

import Main.User;
import Main.UserSession;
import static Main.UserSession.getCurrentUserRole;
import static Main.UserSession.getCurrentUsername;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;







public class LoginController implements Initializable {
    
    private Stage stage;
    private Scene scene;
   
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;
    
  
    
    
    public void registerUser(ActionEvent register) throws IOException{
        System.out.println("Hello");
         Parent root = FXMLLoader.load(getClass().getResource("/Fxml/RegisterUI.fxml"));
         stage = (Stage)((Node)register.getSource()).getScene().getWindow();
         scene  = new Scene(root);
         stage.setScene(scene);
         stage.setFullScreen(false);
         stage.show();
    }
    public void loginUser(ActionEvent login) throws SQLException, IOException {
       String name = userName.getText().trim();
       String passwor = password.getText().trim();

       if (passwor.isEmpty() || name.isEmpty()) {
           showAlert("Warning", "Please fill in all fields!");
       } else {
           try {
               String[] loginResult = DataBaseConnection.loginUser(name, passwor);
               boolean isLoggedIn = Boolean.parseBoolean(loginResult[0]);
               String userRole = loginResult[1];

               if (isLoggedIn) {
                   System.out.println(userRole);
                   
                   
                   
                 
                   User userProfile = DataBaseConnection.getUserProfile(name);

                   if (userProfile != null) {
                       // Store complete user object in session
                       UserSession.setCurrentUser(userProfile);
                       UserSession.setCurrentUsername(userProfile.getUsername());
                         UserSession.setCurrentUser(userProfile.getUsername(), userRole);

                       

                       // Pass role to determine which UI to load
                       logIn(login, userRole);
                   } else {
                       showAlert("Error", "Could not load user profile");
                   }
               } else {
                   showAlert("Invalid", "User not found or incorrect password");
               }
           } catch (IOException e) {
               e.printStackTrace();
               showAlert("Error", "Login failed: " + e.getMessage());
           }
       }
   }

    
    
    public void showAlert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void logIn(ActionEvent login, String role) throws IOException {
      String fxmlFile;

      if ("admin".equalsIgnoreCase(role)) {
          fxmlFile = "/Fxml/DashBoardAdmin.fxml";
      } else {
          fxmlFile = "/Fxml/Dashboard.fxml";
      }

      Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
      Stage stage2 = (Stage) ((Node) login.getSource()).getScene().getWindow();
      Scene scene2 = new Scene(root);
      stage2.setScene(scene2);
      stage2.setFullScreen(false);
      stage2.show();
  }


    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userName.setOnKeyPressed(this::handleEnterKey);
        password.setOnKeyPressed(this::handleEnterKey);
    }
     private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                loginUser(new ActionEvent());  
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
  
}
