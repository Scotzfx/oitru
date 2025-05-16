
package Controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;




public class RegisterUIController implements Initializable {
    
    private Stage stage;
    private Scene scene;
    @FXML
    private TextField courseField, nameField, userNameField, emailField;
    @FXML
    private PasswordField passwordField;
    
    public void register(ActionEvent event) throws IOException, SQLException{
        
        String cF, nF, uNF, eF, pF;
       
        cF = courseField.getText();
        nF = nameField.getText();
        uNF = userNameField.getText();
        eF = emailField.getText();
        pF = hashPassword(passwordField.getText().trim());
         if(nF.isBlank() || cF.isBlank() || uNF.isBlank() || eF.isBlank() || pF.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Details");
            alert.setContentText("Please fill in Details");
            alert.showAndWait();
        } else {
            try{
                DataBaseConnection.insertUser(cF,nF,uNF,eF,pF);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Succeful");
                alert.setHeaderText(null);
                alert.showAndWait();
                Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Login.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene  = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }catch(SQLException e){
                System.out.println("Database error: " + e.getMessage());
            }
        }
        
         
    }
    
    
    
    
    
    
    
    
    public static String hashPassword(String password) {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray()); 
}

    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
