package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.example.findit.model.AppDataStore;
import com.example.findit.util.ImageStorage;

import java.io.File;
import java.io.IOException;

public class LostFormController {

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

        try {
            AppDataStore.addItemReport(
                    "Lost",
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

        showAlert(Alert.AlertType.INFORMATION, "Report Submitted",
                "Your lost item report has been submitted successfully.");
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
