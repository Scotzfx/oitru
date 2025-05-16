package Controller;

import Main.UserSession;
import static Main.UserSession.getCurrentUserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class ItemController {

    @FXML
    private Label itemName, reporterName;
    @FXML
    private Label itemDescription;
    @FXML
    private Label itemLocation;
    @FXML
    private Label itemDate;
    @FXML
    private ImageView itemImage;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem deleteButton;
    
    // —— inject your fields…
    @FXML private VBox containerItem;
    
    // —— store the passed-in ID
    private int itemID;

    /** Called by LostController right after loading.
     * @param id */
    public void setItemID(int id) {
      this.itemID = id;
    }
    
    
    

    // Method to update the content of the item, including the image
    public void updateItem(String name, String description, String status, String date, String reporter,  Image image) {
        itemName.setText(name);
        itemDescription.setText(description);
        itemLocation.setText(status);
        itemDate.setText(date);
        reporterName.setText("Reported by: " + reporter);
     
        
        
        if (image != null) {
            itemImage.setImage(image);  // Set the image from database
        } else {
            itemImage.setImage(null);  // Optionally, set a default image if no image is available
        }
    }
   
    @FXML
    public void deletePost(ActionEvent event) {
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "This will delete item #" + itemID,
                                ButtonType.OK, ButtonType.CANCEL);
      confirm.setHeaderText("Delete item?");
      confirm.showAndWait().ifPresent(btn -> {
        if (btn == ButtonType.OK) {
          try {
            boolean ok = DataBaseConnection.deleteItem(itemID);
            if (ok) {
              // remove *this* VBox from its parent
              ((VBox)containerItem.getParent()).getChildren().remove(containerItem);
            } else {
              new Alert(Alert.AlertType.ERROR, "Delete failed").showAndWait();
            }
          } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
          }
        }
      });
    }
    
    @FXML
    public void claimNow(ActionEvent event) {
        try {
            String reporterUsername = DataBaseConnection.getReporterUsernameByItemID(itemID);
            String currentUser = UserSession.getCurrentUsername();

            if (reporterUsername == null) {
                new Alert(Alert.AlertType.ERROR, "Could not find the reporter.").showAndWait();
                return;
            }

            int senderID = DataBaseConnection.getUserID(currentUser);
            int receiverID = DataBaseConnection.getUserID(reporterUsername);

            String confirmationMsg = "Hi, I am interested in confirming the item transaction for item #" + itemID + ". Can we proceed?";
            DataBaseConnection.sendMessage(senderID, receiverID, currentUser + ": " + confirmationMsg);

            // ✅ NEW: Update item status to 'Claimed'
            DataBaseConnection.markItemAsClaimed(itemID);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Confirmation message sent to " + reporterUsername + "\nItem marked as Claimed.");
            alert.setHeaderText("Message Sent");
            alert.showAndWait();

        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error sending message: " + ex.getMessage()).showAndWait();
        }
    }


    

    // Handle the Details button action
    @FXML
    public void handleItemDetails(ActionEvent event) {
        // Code to handle the "Details" button click (e.g., open a new window with more details)
        System.out.println("Details button clicked!");
    }
    
   
    @FXML
    private void initialize() {
    String role = getCurrentUserRole();
    
    boolean isAdmin = ("admin".equals(role));
        if(isAdmin){
            System.out.println(role);
            menuButton.setManaged(true);
        }else{
            menuButton.setManaged(false);
        }
             
    }
    
}

