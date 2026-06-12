package com.example.findit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;

public class ClaimsController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER!
    @FXML private AdminSidebarController sidebarController;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableView<ClaimRow> claimsTable;
    @FXML private TableColumn<ClaimRow, String> colType, colItemName, colCategory, colDate, colReportedBy, colLocation, colAction;

    private final ObservableList<ClaimRow> masterData = FXCollections.observableArrayList();
    private FilteredList<ClaimRow> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) { sidebarController.setActiveTab("Claims"); }
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Pending", "Approved", "Rejected"));
        statusFilter.getSelectionModel().selectFirst();
        
        configureTableColumns();
        loadClaims();
        wireSearchAndFilter();
    }

    private void configureTableColumns() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReportedBy.setCellValueFactory(new PropertyValueFactory<>("reportedBy"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAction.setCellFactory(col -> new TableCell<ClaimRow, String>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    badge.setText(item);
                    if (item.equalsIgnoreCase("Unclaimed")) {
                        badge.setStyle("-fx-background-color: #FFE0B2; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    } else if (item.equalsIgnoreCase("Claimed")) {
                        badge.setStyle("-fx-background-color: #E8EAF6; -fx-text-fill: #3F51B5; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    } else {
                        badge.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    }
                    setGraphic(badge);
                }
            }
        });
        colAction.setCellFactory(col -> new TableCell<ClaimRow, String>() {
            private final Button approveBtn = new Button();
            private final Button rejectBtn  = new Button();
            private final Button deleteBtn  = new Button();

            {
                // Load your specific images
                ImageView checkIcon = createIcon("/com/example/findit/assets/check.png");
                ImageView ekisIcon  = createIcon("/com/example/findit/assets/ekis.png");
                ImageView trashIcon = createIcon("/com/example/findit/assets/trash.png");

                // Inject the images into the buttons
                approveBtn.setGraphic(checkIcon);
                rejectBtn.setGraphic(ekisIcon);
                deleteBtn.setGraphic(trashIcon);

                // Make the button backgrounds completely transparent and add a pointer cursor
                String transparentStyle = "-fx-background-color: transparent; -fx-cursor: hand;";
                approveBtn.setStyle(transparentStyle);
                rejectBtn.setStyle(transparentStyle);
                deleteBtn.setStyle(transparentStyle);

                // Wiring the click actions
                approveBtn.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e  -> handleReject(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e  -> handleDeleteItem(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(2, approveBtn, rejectBtn, deleteBtn);
                    box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });
    }

    private void loadClaims() {
        masterData.addAll(
                new ClaimRow("Unclaimed", "Laptop",       "Electronics", "2025-01-08", "Juan dela Cruz", "Library",   "Pending"),
                new ClaimRow("Claimed",   "Black Wallet", "Wallet",      "2025-01-09", "Maria Santos",   "Cafeteria", "Pending"),
                new ClaimRow("Unclaimed", "ID Card",      "Document",    "2025-01-10", "Jose Reyes",     "Gym",       "Approved")
        );
    }

    private void handleDeleteItem(ClaimRow item) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Claim Confirmation");
        confirmDialog.setHeaderText("Delete Claim: " + item.getItemName());
        confirmDialog.setContentText("Are you sure you want to permanently delete this claim request?");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                masterData.remove(item);
                
                // TODO: Add backend SQL 'DELETE FROM claims WHERE claim_id = ?'
            }
        });
    }

    private void wireSearchAndFilter() {
        filteredData = new FilteredList<>(masterData, p -> true);
        claimsTable.setItems(filteredData);

        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    private void applyFilter() {
        String search = searchField.getText().toLowerCase().trim();
        String statusValue = statusFilter.getValue();

        filteredData.setPredicate(row -> {
            boolean matchesSearch = search.isEmpty()
                    || row.getItemName().toLowerCase().contains(search)
                    || row.getCategory().toLowerCase().contains(search)
                    || row.getLocation().toLowerCase().contains(search)
                    || row.getReportedBy().toLowerCase().contains(search);

            boolean matchesStatus = "All Status".equals(statusValue)
                    || row.getClaimStatus().equalsIgnoreCase(statusValue);

            return matchesSearch && matchesStatus;
        });
    }

    // Button actions
    private void handleApprove(ClaimRow row) {
        row.setClaimStatus("Approved");
        claimsTable.refresh();
    }

    private void handleReject(ClaimRow row) {
        row.setClaimStatus("Rejected");
        claimsTable.refresh();
    }

    private ImageView createIcon(String path) {
        java.io.InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("❌ Missing table icon: " + path);
            return new ImageView(); // Returns empty view to prevent crashes
        }
        ImageView imgView = new ImageView(new Image(stream));
        imgView.setFitWidth(20);  // Size of the icon
        imgView.setFitHeight(20);
        imgView.setPreserveRatio(true);
        return imgView;
    }
    public static class ClaimRow {
        private final String type, itemName, category, date, reportedBy, location;
        private String claimStatus;

        public ClaimRow(String type, String itemName, String category, String date, String reportedBy, String location, String claimStatus) {
            this.type = type; this.itemName = itemName; this.category = category;
            this.date = date; this.reportedBy = reportedBy; this.location = location;
            this.claimStatus = claimStatus;
        }

        public String getType() { return type; } public String getItemName() { return itemName; }
        public String getCategory() { return category; } public String getDate() { return date; }
        public String getReportedBy() { return reportedBy; } public String getLocation() { return location; }
        public String getClaimStatus() { return claimStatus; }
        public void setClaimStatus(String s) { this.claimStatus = s; }
    }


}