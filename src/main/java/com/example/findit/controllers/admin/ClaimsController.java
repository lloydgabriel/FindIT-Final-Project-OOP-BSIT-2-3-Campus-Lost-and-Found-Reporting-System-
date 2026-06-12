package com.example.findit.controllers.admin;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ClaimRequest;

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
        masterData.setAll(AppDataStore.getClaimRequests().stream()
                .map(ClaimRow::new)
                .toList());
        AppDataStore.getClaimRequests().addListener((javafx.collections.ListChangeListener<ClaimRequest>) change -> {
            masterData.setAll(AppDataStore.getClaimRequests().stream()
                    .map(ClaimRow::new)
                    .toList());
            applyFilter();
        });
    }

    private void handleDeleteItem(ClaimRow item) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Claim Confirmation");
        confirmDialog.setHeaderText("Delete Claim: " + item.getItemName());
        confirmDialog.setContentText("Are you sure you want to permanently delete this claim request?");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                AppDataStore.deleteClaimRequest(item.getRequest());
                masterData.remove(item);
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
        AppDataStore.updateClaimStatus(row.getRequest(), "Approved");
        showClaimDetails(row, "Claim Approved");
        claimsTable.refresh();
        applyFilter();
    }

    private void handleReject(ClaimRow row) {
        AppDataStore.updateClaimStatus(row.getRequest(), "Rejected");
        showClaimDetails(row, "Claim Rejected");
        claimsTable.refresh();
        applyFilter();
    }

    private void showClaimDetails(ClaimRow row, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(row.getItemName() + " - " + row.getClaimStatus());
        alert.setContentText(
                "Claimant: " + row.getClaimantName() + "\n" +
                "Student Number: " + row.getStudentNumber() + "\n" +
                "Contact: " + row.getContactInfo() + "\n\n" +
                "Proof:\n" + row.getProofDescription()
        );
        alert.showAndWait();
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
        private final ClaimRequest request;

        public ClaimRow(ClaimRequest request) {
            this.request = request;
        }

        public ClaimRequest getRequest() { return request; }
        public String getType() { return request.getItem().getType(); }
        public String getItemName() { return request.getItem().getItemName(); }
        public String getCategory() { return request.getItem().getCategory(); }
        public String getDate() { return request.getItem().getDate(); }
        public String getReportedBy() { return request.getItem().getReportedBy(); }
        public String getLocation() { return request.getItem().getLocation(); }
        public String getClaimantName() { return request.getClaimantName(); }
        public String getStudentNumber() { return request.getStudentNumber(); }
        public String getContactInfo() { return request.getContactInfo(); }
        public String getProofDescription() { return request.getProofDescription(); }
        public String getClaimStatus() { return request.getStatus(); }
        public void setClaimStatus(String s) { request.setStatus(s); }
    }


}
