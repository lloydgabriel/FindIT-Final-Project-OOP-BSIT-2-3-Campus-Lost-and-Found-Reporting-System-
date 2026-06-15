package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.geometry.Pos;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;
import com.example.findit.util.ImageStorage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class FoundFormController {

    @FXML private TextField txtItemName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtLocation;
    @FXML private TextField txtReporterName;
    @FXML private TextField txtContact;
    @FXML private TextArea txtDescription;
    @FXML private VBox uploadArea;
    private String selectedImagePath;

    @FXML
    public void initialize() {
        cmbCategory.getItems().addAll(
                "Electronics", "Wallet", "Documents", "Clothing", "Accessories", "Other"
        );
        if (uploadArea != null) {
            uploadArea.setOnMouseClicked(e -> handleUploadImage());
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        UserSidebarController.setActivePage("Dashboard");
        UserNavigationHelper.switchScene(event, "/com/example/findit/views/user/Dashboard.fxml");
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        handleBack(event);
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        if (txtItemName.getText().isBlank() || cmbCategory.getValue() == null
                || dpDate.getValue() == null || txtLocation.getText().isBlank()
                || txtReporterName.getText().isBlank() || txtContact.getText().isBlank()
                || txtDescription.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        ItemReport savedItem;
        try {
            savedItem = AppDataStore.addItemReport(
                    "Found",
                    txtItemName.getText().trim(),
                    cmbCategory.getValue(),
                    dpDate.getValue().toString(),
                    txtLocation.getText().trim(),
                    txtReporterName.getText().trim(),
                    txtContact.getText().trim(),
                    txtDescription.getText().trim(),
                    selectedImagePath
            );
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "The report could not be saved. Please try again.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Submitted");
        alert.setHeaderText("Success! Your Submission is Saved.");
        TextField idField = new TextField(savedItem.getTrackingId());
        idField.setEditable(false);
        idField.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-alignment: center; -fx-background-color: #F0F0F0;");

        // Copy Button
        Button copyBtn = new Button("Copy to Clipboard");
        copyBtn.setStyle("-fx-cursor: hand; -fx-background-color: #800000; -fx-text-fill: white;");
        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(savedItem.getTrackingId());
            clipboard.setContent(content);
            copyBtn.setText("Copied!");
        });

        // Download Button
        Button downloadBtn = new Button("Save as .txt File");
        downloadBtn.setStyle("-fx-cursor: hand; -fx-background-color: #E65100; -fx-text-fill: white;");
        downloadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Tracking ID");
            fileChooser.setInitialFileName("FindIT_Receipt_" + savedItem.getTrackingId() + ".txt");
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("--- FindIT Tracking Receipt ---");
                    writer.println("Item: " + savedItem.getItemName());
                    writer.println("Date: " + savedItem.getDate());
                    writer.println("Tracking ID: " + savedItem.getTrackingId());
                    writer.println("-------------------------------");
                    downloadBtn.setText("Saved!");
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "Save Error", "Could not save the file.");
                }
            }
        });

        VBox layout = new VBox(15, 
            new Label("Keep this ID to edit or delete your report later:"), 
            idField, 
            new HBox(10, copyBtn, downloadBtn) {{ setAlignment(Pos.CENTER); }}
        );
        layout.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(layout);
        alert.showAndWait();

        UserSidebarController.setActivePage("Items");
        UserNavigationHelper.switchScene(event, "/com/example/findit/views/user/Items.fxml");
    }

    @FXML
    public void handleUploadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Item Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) txtItemName.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                selectedImagePath = ImageStorage.toPortableImagePath(file);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Image Error",
                        "The selected image could not be prepared. Please choose a smaller PNG or JPG file.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Image Selected", "Selected: " + file.getName());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}