package com.example.findit.controllers.user;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ClaimRequest;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class ClaimsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private FlowPane claimsFlow;

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Claims");
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Pending", "Approved", "Rejected"));
        statusFilter.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> renderClaims());
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderClaims());
        AppDataStore.getClaimRequests().addListener((javafx.collections.ListChangeListener<ClaimRequest>) change -> renderClaims());
        renderClaims();
    }

    private void renderClaims() {
        claimsFlow.getChildren().clear();
        List<ClaimRequest> filteredClaims = AppDataStore.getClaimRequests().stream()
                .filter(this::matchesFilters)
                .toList();

        if (filteredClaims.isEmpty()) {
            Label emptyLabel = new Label("No claims found.");
            emptyLabel.setStyle("-fx-text-fill: #777777; -fx-font-size: 16;");
            claimsFlow.getChildren().add(emptyLabel);
            return;
        }

        for (ClaimRequest claim : filteredClaims) {
            claimsFlow.getChildren().add(createClaimCard(claim));
        }
    }

    private boolean matchesFilters(ClaimRequest claim) {
        String search = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String status = statusFilter.getValue();

        boolean matchesSearch = search.isEmpty()
                || safeContains(claim.getItem().getItemName(), search)
                || safeContains(claim.getClaimantName(), search)
                || safeContains(claim.getItem().getCategory(), search)
                || safeContains(claim.getItem().getLocation(), search);
        boolean matchesStatus = "All Status".equals(status)
                || claim.getStatus().equalsIgnoreCase(status);

        return matchesSearch && matchesStatus;
    }

    private boolean safeContains(String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private VBox createClaimCard(ClaimRequest claim) {
        VBox card = new VBox(7);
        card.setPrefWidth(230);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 4); -fx-cursor: hand;");
        card.setOnMouseClicked(event -> showClaimDetails(claim));

        Label itemName = new Label(claim.getItem().getItemName());
        itemName.setWrapText(true);
        itemName.setStyle("-fx-text-fill: #4A1515; -fx-font-weight: bold; -fx-font-size: 15;");

        Label claimant = new Label("Claimant: " + claim.getClaimantName());
        claimant.setWrapText(true);
        claimant.setStyle("-fx-text-fill: #555555;");

        Label location = new Label(claim.getItem().getLocation());
        location.setWrapText(true);
        location.setStyle("-fx-text-fill: #777777;");

        Label badge = new Label(claim.getStatus());
        badge.setStyle(statusStyle(claim.getStatus()));

        card.getChildren().addAll(itemName, claimant, location, badge);
        return card;
    }

    private void showClaimDetails(ClaimRequest claim) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Claim Details");
        alert.setHeaderText(claim.getItem().getItemName() + " - " + claim.getStatus());
        alert.setContentText(
                "Claimant: " + claim.getClaimantName() + "\n" +
                "Student Number: " + claim.getStudentNumber() + "\n" +
                "Contact: " + claim.getContactInfo() + "\n\n" +
                "Item Type: " + claim.getItem().getType() + "\n" +
                "Category: " + claim.getItem().getCategory() + "\n" +
                "Location: " + claim.getItem().getLocation() + "\n" +
                "Date: " + claim.getItem().getDate() + "\n" +
                "Reported By: " + claim.getItem().getReportedBy() + "\n\n" +
                "Proof:\n" + claim.getProofDescription()
        );
        alert.showAndWait();
    }

    private String statusStyle(String status) {
        if ("Approved".equalsIgnoreCase(status)) {
            return "-fx-background-color: #C8E6C9; -fx-background-radius: 12; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-padding: 3 10 3 10;";
        }
        if ("Rejected".equalsIgnoreCase(status)) {
            return "-fx-background-color: #FFCDD2; -fx-background-radius: 12; -fx-text-fill: #C62828; -fx-font-weight: bold; -fx-padding: 3 10 3 10;";
        }
        return "-fx-background-color: #FFE0B2; -fx-background-radius: 12; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-padding: 3 10 3 10;";
    }
}
