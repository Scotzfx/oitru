/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;


/**
 * FXML Controller class
 *
 * @author Scotzf
 */
public class DashboardController implements Initializable {
    
    private Stage stage;
    private Scene scene;

   
    
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
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
