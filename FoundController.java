package Controller;

import Main.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

public class FoundController implements Initializable {

    private Stage stage;
    private Scene scene;

    private int ITEMS_TOTAL = 5;
    private Node[] itemNodes;
    private int currentIndex = 0;
    
    @FXML
    private javafx.scene.control.TextField searchField; 
    @FXML
    private VBox container1;
    @FXML
    private VBox container2;
    @FXML
    private VBox container3;
    @FXML
    private Button nextRight, previousLeft;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Item> items = DataBaseConnection.getFoundItems();
            ITEMS_TOTAL = items.size();
            itemNodes = new Node[ITEMS_TOTAL];

            int initialLoadCount = Math.min(3, ITEMS_TOTAL);
            for (int i = 0; i < initialLoadCount; i++) {
                loadItem(items, i);
            }

            currentIndex = initialLoadCount;
            updateItemContainers();
            
            // Disable previous button initially
            previousLeft.setDisable(true);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Could not load found items", e.getMessage());
        }
    }

    private void loadItem(List<Item> items, int index) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Item.fxml"));
        Node itemNode = loader.load();
        ItemController controller = loader.getController();

        Item currentItem = items.get(index);
        Image image = currentItem.getImage(); // Directly use the Image from Item

        // If no image loaded, use default
        if (image == null || image.isError()) {
            image = loadDefaultImage();
        }
        controller.setItemID(currentItem.getItemID());

        controller.updateItem(
            currentItem.getItemName(),
            currentItem.getDescription(),
            currentItem.getLocation(),
            currentItem.getDateSubmitted().toString(),
            currentItem.getUserName(),
            image
        );

        itemNodes[index] = itemNode;
    }

    private Image loadDefaultImage() {
        try {
            InputStream stream = getClass().getResourceAsStream("/IMG/default.png");
            if (stream != null) {
                Image defaultImage = new Image(stream);
                stream.close();
                return defaultImage;
            }
            System.err.println("Default image not found in resources!");
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
        return null;
    }

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
    void ReportUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/ReportFound.fxml", event);
    }
    @FXML 
    void ProfileUI(ActionEvent event) throws IOException {
        switchScene("/Fxml/Logout.fxml", event);
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

    @FXML
    private void loadPreviousItems(ActionEvent event) {
        try {
            int itemsToGoBack = Math.min(3, currentIndex - 3);

            if (itemsToGoBack <= 0) {
                previousLeft.setDisable(true);
                return;
            }

            currentIndex -= itemsToGoBack;
            currentIndex = Math.max(3, currentIndex);

            updateItemContainers();

            nextRight.setDisable(false);
            previousLeft.setDisable(currentIndex <= 3);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Could not load previous items", e.getMessage());
        }
    }

    @FXML
    private void loadMoreItems(ActionEvent event) {
        try {
            List<Item> items = DataBaseConnection.getFoundItems();
            int itemsToLoad = Math.min(3, ITEMS_TOTAL - currentIndex);

            if (itemsToLoad <= 0) {
                nextRight.setDisable(true);
                return;
            }

            for (int i = currentIndex; i < currentIndex + itemsToLoad; i++) {
                if (i >= ITEMS_TOTAL) break;
                loadItem(items, i);
            }

            currentIndex += itemsToLoad;
            updateItemContainers();

            if (currentIndex >= ITEMS_TOTAL) {
                nextRight.setDisable(true);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showErrorAlert("Loading Error", "Could not load more items", e.getMessage());
        }
    }

    private void updateItemContainers() {
        container1.getChildren().clear();
        container2.getChildren().clear();
        container3.getChildren().clear();

        if (currentIndex > 0 && itemNodes[currentIndex - 1] != null) 
            container1.getChildren().add(itemNodes[currentIndex - 1]);
        if (currentIndex > 1 && itemNodes[currentIndex - 2] != null) 
            container2.getChildren().add(itemNodes[currentIndex - 2]);
        if (currentIndex > 2 && itemNodes[currentIndex - 3] != null) 
            container3.getChildren().add(itemNodes[currentIndex - 3]);
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
        @FXML
        private void handleSearch() {
            try {
                String searchTerm = searchField.getText().trim();
                List<Item> items;

                if (searchTerm.isEmpty()) {
                    // If search field is empty, load all found items
                    items = DataBaseConnection.getFoundItems();
                } else {
                    // Search for items with similar names
                    items = DataBaseConnection.searchItemsByName(searchTerm, "Found");
                }

                // Update display with search results
                ITEMS_TOTAL = items.size();
                itemNodes = new Node[ITEMS_TOTAL];

                int initialLoadCount = Math.min(3, ITEMS_TOTAL);
                for (int i = 0; i < initialLoadCount; i++) {
                    loadItem(items, i);
                }

                currentIndex = initialLoadCount;
                updateItemContainers();

                // Reset navigation buttons
                nextRight.setDisable(ITEMS_TOTAL <= 3);
                previousLeft.setDisable(true);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showErrorAlert("Search Error", "Could not perform search", e.getMessage());
            }
        }
}