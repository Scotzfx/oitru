/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package Main;

import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D; 
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 *
 * @author Scotzf
 */
public class LostFound extends Application {
    
   
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Hellope");
        
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Login.fxml"));
        Scene scene = new Scene(root);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.8); 
        primaryStage.setHeight(screenBounds.getHeight() * 0.8);  
        
        
        primaryStage.setOnCloseRequest(this::CloseRequest);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Lost & Found");
          primaryStage.setFullScreen(false);
        primaryStage.show();
      

    }

    
    private void CloseRequest(WindowEvent event){
        event.consume();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes will be lost.");
        
        // Show the dialog and wait for user response
        Optional<ButtonType> result = alert.showAndWait();
        
        // If user confirms, close the application
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // You can add any cleanup logic here
            System.exit(0);
        }
            
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
