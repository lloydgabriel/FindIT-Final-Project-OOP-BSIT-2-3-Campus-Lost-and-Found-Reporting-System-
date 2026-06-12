package com.example.findit.controllers.user;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;

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
    private ItemReport item;

    public void setItem(ItemReport item) {
        this.item = item;
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        if (item == null) {
            showAlert(Alert.AlertType.ERROR, "Item Missing", "Please open a claim from an item details window.");
            return;
        }

        if (!"Found".equalsIgnoreCase(item.getType())) {
            showAlert(Alert.AlertType.WARNING, "Claim Unavailable",
                    "Only found item reports can be claimed.");
            return;
        }

        if (txtClaimantName.getText().isBlank() || txtStudentNumber.getText().isBlank()
                || txtContact.getText().isBlank() || txtProofDescription.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all required fields.");
            return;
        }

        try {
            AppDataStore.addClaimRequest(
                    item,
                    txtClaimantName.getText().trim(),
                    txtStudentNumber.getText().trim(),
                    txtContact.getText().trim(),
                    txtProofDescription.getText().trim()
            );
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "The claim could not be saved. Please try again.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Claim Submitted",
                "Your claim has been submitted and is waiting for admin approval.");
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
