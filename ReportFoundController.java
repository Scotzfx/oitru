package Controller;

import Main.UserSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ReportFoundController implements Initializable {

    @FXML
    private TextField itemNameField, locationField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button uploadButton, submitButton;
    @FXML
    private ImageView itemImageView;

    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set action for the upload button to handle image upload
        uploadButton.setOnAction(e -> handleUploadImage());

        // Set action for the submit button to handle report submission
        submitButton.setOnAction(e -> handleSubmitReport());
    }

    // Method to handle image upload
    public void handleUploadImage() {
        // Open file chooser to select an image file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedImageFile != null) {
            // Display the selected image in the ImageView
            itemImageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    // Method to handle the report submission
    public void handleSubmitReport() {
    // Get the data from the form fields
    String itemName = itemNameField.getText();
    String description = descriptionField.getText();
    String location = locationField.getText();

    // Check if all fields are filled out
    if (itemName.isEmpty() || description.isEmpty() || location.isEmpty()) {
        showAlert("Error", "Please fill in all fields.");
        return;
    }

    // Handle image data
    byte[] imageData = null;
    if (selectedImageFile != null) {
        try {
            // Solution 1: Check file size first
            long maxSize = 1 * 1024 * 1024; // 1MB limit
            if (selectedImageFile.length() > maxSize) {
                // Solution 2: Compress image if too large
                imageData = compressImage(selectedImageFile, 0.7f); // 70% quality
            } else {
                imageData = Files.readAllBytes(selectedImageFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to process image");
            return;
        }
    }

    // Insert the data into the database
    try (Connection connection = DataBaseConnection.getConnection()) {
        // Solution 3: Alternative query for large images
        String query = "INSERT INTO item (ItemName, Description, Location, Image, Status, UserName, DateSubmitted, Category) " +
                      "VALUES (?, ?, ?, ?, 'Pending', ?, NOW(), 'Found')";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, itemName);
        statement.setString(2, description);
        statement.setString(3, location);

        if (imageData != null) {
            statement.setBytes(4, imageData);
        } else {
            statement.setNull(4, java.sql.Types.BLOB);
        }

        String theName = UserSession.getCurrentUsername(); // Replace with actual user ID
        statement.setString(5, theName);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            showAlert("Success", "Report submitted successfully!", () -> {
                try {
                    switchToLostScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            showAlert("Error", "Failed to submit report.");
        }
    } catch (SQLException e) {
        if (e.getMessage().contains("max_allowed_packet")) {
            // Solution 4: Fallback to file system storage
            handleLargeImageSubmission(itemName, description, location);
        } else {
            e.printStackTrace();
            showAlert("Database Error", "Failed to submit report: " + e.getMessage());
        }
    }
}

// New helper methods
private byte[] compressImage(File imageFile, float quality) throws IOException {
    BufferedImage originalImage = ImageIO.read(imageFile);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
    ImageWriter writer = writers.next();
    
    ImageWriteParam param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(quality);
    
    try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
        writer.setOutput(ios);
        writer.write(null, new IIOImage(originalImage, null, null), param);
    }
    writer.dispose();
    
    return baos.toByteArray();
}

    private void handleLargeImageSubmission(String itemName, String description, String location) {
        try {
            // Save image to filesystem
            String imagePath = saveImageToDisk(selectedImageFile);

            // Store only the path in database
            try (Connection connection = DataBaseConnection.getConnection()) {
                String query = "INSERT INTO item (ItemName, Description, Location, ImagePath, Status, UserID, DateSubmitted, Category) " +
                             "VALUES (?, ?, ?, ?, 'Pending', ?, NOW(), 'Found')";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, itemName);
                statement.setString(2, description);
                statement.setString(3, location);
                statement.setString(4, imagePath);
                statement.setInt(5, 1); // User ID

                if (statement.executeUpdate() > 0) {
                    showAlert("Success", "Report submitted with external image storage!");
                    switchToLostScreen();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit report with large image");
        }
    }

    private String saveImageToDisk(File imageFile) throws IOException {
        String uploadDir = "uploads/";
        new File(uploadDir).mkdirs();

        String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
        Path destination = Paths.get(uploadDir + fileName);
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void showAlert(String title, String message) {
        showAlert(title, message, null);
    }

    private void showAlert(String title, String message, Runnable afterShow) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        if (afterShow != null) {
            afterShow.run();
        }
    }

    private void switchToLostScreen() throws IOException {
        AnchorPane lostScreen = FXMLLoader.load(getClass().getResource("/Fxml/Found.fxml"));
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.setScene(new Scene(lostScreen));
        stage.show();
    }

    // Method to convert the selected image file into a byte array for database storage
    private byte[] convertImageToByteArray(File imageFile) {
        try {
            // Read the image file into a byte array
            java.nio.file.Path path = imageFile.toPath();
            return java.nio.file.Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
        private byte[] compressImage(File imageFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Adjust quality parameter (0.0-1.0) for compression
        float quality = 0.7f; 

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(originalImage, null, null), param);
        }
        writer.dispose();

        return baos.toByteArray();
    }
}
