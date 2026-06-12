package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClaimItemsController {

    @FXML private TextField txtClaimantName;
    @FXML private TextField txtStudentNumber;
    @FXML private TextField txtContact;
    @FXML private TextArea txtProofDescription;

    @FXML
    public void handleSubmit(ActionEvent event) {
        if (txtClaimantName.getText().isBlank() || txtStudentNumber.getText().isBlank()
                || txtContact.getText().isBlank() || txtProofDescription.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Claim Submitted",
                "Your claim has been submitted. Visit the Lost & Found office with valid ID.");
        closeWindow(event);
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
