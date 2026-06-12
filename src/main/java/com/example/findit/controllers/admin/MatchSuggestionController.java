package com.example.findit.controllers.admin;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemMatch;
import com.example.findit.model.ItemReport;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchSuggestionController implements Initializable {
    @FXML private Label statusBadge;
    @FXML private Label lostItemName;
    @FXML private Label lostReportedBy;
    @FXML private Label lostDate;
    @FXML private Label lostLocation;
    @FXML private Label foundItemName;
    @FXML private Label foundReportedBy;
    @FXML private Label foundDate;
    @FXML private Label foundLocation;
    @FXML private Button confirmButton;

    private ItemMatch currentMatch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void loadMatch(ItemMatch match) {
        this.currentMatch = match;
        setStatus(match.getStatus());
        populateLostItem(match.getLostItem());
        populateFoundItem(match.getFoundItem());
    }

    private void populateLostItem(ItemReport item) {
        lostItemName.setText(item.getItemName());
        lostReportedBy.setText("Reported by: " + safe(item.getReportedBy()));
        lostDate.setText("Date: " + safe(item.getDate()));
        lostLocation.setText("Location: " + safe(item.getLocation()));
    }

    private void populateFoundItem(ItemReport item) {
        foundItemName.setText(item.getItemName());
        foundReportedBy.setText("Reported by: " + safe(item.getReportedBy()));
        foundDate.setText("Date: " + safe(item.getDate()));
        foundLocation.setText("Location: " + safe(item.getLocation()));
    }

    @FXML
    private void handleConfirmMatch() {
        if (currentMatch == null) {
            return;
        }

        try {
            AppDataStore.confirmMatch(currentMatch);
            setStatus("Confirmed");
            showAlert(Alert.AlertType.INFORMATION, "Match Confirmed",
                    "An approved claim has been added to the Claims tab.");
            closeDialog();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "The match could not be confirmed. Please try again.");
        }
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void setStatus(String status) {
        statusBadge.setText(status);
        if ("Confirmed".equalsIgnoreCase(status)) {
            statusBadge.setStyle("-fx-background-color: #C8E6C9; -fx-background-radius: 12; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            if (confirmButton != null) {
                confirmButton.setDisable(true);
                confirmButton.setText("Match Confirmed");
            }
        } else {
            statusBadge.setStyle("-fx-background-color: #FFE0B2; -fx-background-radius: 12; -fx-text-fill: #E65100; -fx-font-weight: bold;");
            if (confirmButton != null) {
                confirmButton.setDisable(false);
                confirmButton.setText("Confirm Match");
            }
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) statusBadge.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
