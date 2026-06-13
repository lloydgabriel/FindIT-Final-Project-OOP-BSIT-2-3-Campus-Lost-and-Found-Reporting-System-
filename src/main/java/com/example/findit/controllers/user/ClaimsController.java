package com.example.findit.controllers.user;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ClaimRequest;
import com.example.findit.model.ItemReport;
import com.example.findit.util.ImageStorage;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
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
        claimsFlow.setPrefWrapLength(700);
        claimsFlow.widthProperty().addListener((obs, oldWidth, newWidth) ->
                claimsFlow.setPrefWrapLength(newWidth.doubleValue()));
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
        card.setMinWidth(210);
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Claim Details");
        dialog.setHeaderText(claim.getItem().getItemName() + " - " + claim.getStatus());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(820);

        HBox content = new HBox(22);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(10, 5, 5, 5));

        VBox details = new VBox(14);
        details.setPrefWidth(490);
        details.getChildren().addAll(
                createSection("Claim Information", createClaimGrid(claim)),
                createSection("Item Information", createItemGrid(claim.getItem())),
                createProofBlock(claim)
        );
        HBox.setHgrow(details, Priority.ALWAYS);

        content.getChildren().addAll(createImagePanel(claim.getItem()), details);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private VBox createImagePanel(ItemReport item) {
        VBox panel = new VBox(8);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(260);

        StackPane imageFrame = new StackPane();
        imageFrame.setPrefSize(260, 240);
        imageFrame.setStyle("-fx-background-color: #F0F0F3; -fx-background-radius: 10;");

        Image image = ImageStorage.loadImage(item.getImagePath());
        if (image == null) {
            Label placeholder = new Label("No Image Available");
            placeholder.setStyle("-fx-text-fill: #777777; -fx-font-weight: bold;");
            imageFrame.getChildren().add(placeholder);
        } else {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(240);
            imageView.setFitHeight(220);
            imageView.setPreserveRatio(true);
            imageFrame.getChildren().add(imageView);
        }

        Label imageLabel = new Label(item.getItemName());
        imageLabel.setWrapText(true);
        imageLabel.setStyle("-fx-text-fill: #4A1212; -fx-font-weight: bold;");
        panel.getChildren().addAll(imageFrame, imageLabel);
        return panel;
    }

    private VBox createSection(String titleText, GridPane grid) {
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: #4A1212; -fx-font-weight: bold; -fx-font-size: 13;");
        return new VBox(7, title, grid);
    }

    private GridPane createClaimGrid(ClaimRequest claim) {
        GridPane grid = createDetailsGrid();
        addDetailRow(grid, 0, "Claimant", claim.getClaimantName());
        addDetailRow(grid, 1, "Student Number", claim.getStudentNumber());
        addDetailRow(grid, 2, "Contact", claim.getContactInfo());
        addDetailRow(grid, 3, "Status", claim.getStatus());
        return grid;
    }

    private GridPane createItemGrid(ItemReport item) {
        GridPane grid = createDetailsGrid();
        addDetailRow(grid, 0, "Type", item.getType());
        addDetailRow(grid, 1, "Category", item.getCategory());
        addDetailRow(grid, 2, "Date", item.getDate());
        addDetailRow(grid, 3, "Location", item.getLocation());
        addDetailRow(grid, 4, "Reported By", item.getReportedBy());
        addDetailRow(grid, 5, "Reporter Contact", item.getContact());
        addDetailRow(grid, 6, "Description", item.getDescription());
        return grid;
    }

    private GridPane createDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPrefWidth(125);
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, valueColumn);
        return grid;
    }

    private VBox createProofBlock(ClaimRequest claim) {
        Label title = new Label("Proof");
        title.setStyle("-fx-text-fill: #4A1212; -fx-font-weight: bold; -fx-font-size: 13;");

        Label proof = new Label(safe(claim.getProofDescription()));
        proof.setWrapText(true);
        proof.setMinHeight(80);
        proof.setStyle("-fx-background-color: #F7F7F9; -fx-background-radius: 8; -fx-padding: 10; -fx-text-fill: #333333;");
        return new VBox(7, title, proof);
    }

    private void addDetailRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: #777777; -fx-font-weight: bold;");

        Label value = new Label(safe(valueText));
        value.setWrapText(true);
        value.setStyle("-fx-text-fill: #222222;");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
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
