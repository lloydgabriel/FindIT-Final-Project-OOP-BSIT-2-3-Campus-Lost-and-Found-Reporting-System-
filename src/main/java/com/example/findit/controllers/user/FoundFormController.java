package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;
import com.example.findit.util.ImageStorage;
import com.example.findit.util.InputValidator;

import java.io.File;
import java.io.IOException;

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
            showUploadPrompt();
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
                || txtLocation.getText().isBlank()
                || txtDescription.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        if (dpDate.getValue() != null) {
            boolean futureDate;
            try {
                futureDate = InputValidator.isFutureDate(dpDate.getValue());
            } catch (IllegalStateException e) {
                showAlert(Alert.AlertType.ERROR, "Date Check Error",
                        "The current Philippine date could not be verified. Please try again later.");
                return;
            }

            if (futureDate) {
                showAlert(Alert.AlertType.WARNING, "Invalid Date", "The report date cannot be later than today.");
                return;
            }
        }

        if (!txtContact.getText().isBlank() && !InputValidator.isValidContact(txtContact.getText())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Contact",
                    "Please enter a valid 11-digit phone number or email address.");
            return;
        }

        if (!InputValidator.isValidNameText(txtItemName.getText())
                || !InputValidator.isValidNameText(txtLocation.getText())
                || (!txtReporterName.getText().isBlank() && !InputValidator.isValidNameText(txtReporterName.getText()))
                || !InputValidator.isValidDescriptionText(txtDescription.getText())) {
            showAlert(Alert.AlertType.WARNING, "Invalid Characters",
                    "Please remove unsupported special characters from the report.");
            return;
        }

        ItemReport savedItem;
        try {
            savedItem = AppDataStore.addItemReport(
                    "Found",
                    txtItemName.getText().trim(),
                    cmbCategory.getValue(),
                    dpDate.getValue() == null ? "" : dpDate.getValue().toString(),
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
        ButtonType doneButton = new ButtonType("Done");
        alert.getButtonTypes().setAll(doneButton);

        TextField idField = new TextField(savedItem.getTrackingId());
        idField.setEditable(false);
        idField.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-alignment: center; -fx-background-color: #F0F0F0;");

        VBox layout = new VBox(15, 
            new Label("Keep this ID to edit or archive your report later."),
            new Label("Please screenshot or take a picture of this tracking ID before tapping Done."),
            idField
        );
        layout.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(layout);
        Button done = (Button) alert.getDialogPane().lookupButton(doneButton);
        done.setStyle("-fx-cursor: hand; -fx-background-color: #800000; -fx-text-fill: white; -fx-font-weight: bold;");
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
            showImagePreview(file.getName());
        }
    }

    private void showUploadPrompt() {
        if (uploadArea == null) {
            return;
        }

        Label title = new Label("Upload Image (Optional)");
        title.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold; -fx-font-size: 15;");
        Label instruction = new Label("Click to upload or drag and drop");
        instruction.setStyle("-fx-text-fill: #666666; -fx-font-weight: bold; -fx-font-size: 15;");
        Label note = new Label("PNG, JPG up to 5MB");
        note.setStyle("-fx-text-fill: #999999; -fx-font-size: 12;");

        uploadArea.getChildren().setAll(title, instruction, note);
        uploadArea.setStyle("-fx-border-color: #CCCCCC; -fx-border-style: dashed; -fx-border-radius: 8; -fx-background-color: #FAFAFA; -fx-cursor: hand;");
    }

    private void showImagePreview(String fileName) {
        if (uploadArea == null) {
            return;
        }

        Image image = ImageStorage.loadImage(selectedImagePath);
        if (image == null) {
            selectedImagePath = null;
            showUploadPrompt();
            showAlert(Alert.AlertType.ERROR, "Image Error",
                    "The selected image could not be previewed. Please choose another PNG or JPG file.");
            return;
        }

        ImageView preview = new ImageView(image);
        preview.setFitHeight(70);
        preview.setFitWidth(180);
        preview.setPreserveRatio(true);
        preview.setSmooth(true);

        Label selected = new Label("Selected: " + fileName);
        selected.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");

        Button removeButton = new Button("Remove Image");
        removeButton.setStyle("-fx-background-color: #CCCCCC; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: #333333; -fx-font-weight: bold;");
        removeButton.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> event.consume());
        removeButton.setOnAction(event -> {
            event.consume();
            removeSelectedImage();
        });

        uploadArea.getChildren().setAll(preview, selected, removeButton);
        uploadArea.setStyle("-fx-border-color: #800000; -fx-border-radius: 8; -fx-background-color: #FFF8E1; -fx-cursor: hand;");
    }

    private void removeSelectedImage() {
        selectedImagePath = null;
        showUploadPrompt();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
