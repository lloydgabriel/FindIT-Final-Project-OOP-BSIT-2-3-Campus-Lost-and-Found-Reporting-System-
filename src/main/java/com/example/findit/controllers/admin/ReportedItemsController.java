package com.example.findit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.findit.controllers.admin.ReportedItemsController.ReportedItem;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ReportedItemsController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER!
    @FXML private AdminSidebarController sidebarController;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;

    @FXML private TableView<ReportedItem> itemsTable;
    @FXML private TableColumn<ReportedItem, String> colType, colItemName, colCategory, colDate, colReportedBy, colLocation, colAction;

    private final ObservableList<ReportedItem> masterData = FXCollections.observableArrayList();
    private FilteredList<ReportedItem> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) { sidebarController.setActiveTab("Reported"); }
        
        typeFilter.setItems(FXCollections.observableArrayList("All", "Lost", "Found"));
        typeFilter.getSelectionModel().selectFirst();
        configureTableColumns();
        loadItems();
        wireSearchAndFilter();
    }

    private void configureTableColumns() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReportedBy.setCellValueFactory(new PropertyValueFactory<>("reportedBy"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(col -> new TableCell<ReportedItem, String>() {
            private final Label badge = new Label();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    badge.setText(item);
                    if (item.equalsIgnoreCase("Lost")) {
                        badge.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #C62828; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    } else if (item.equalsIgnoreCase("Found")) {
                        badge.setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    }
                    setGraphic(badge);
                }
            }
        });

        colAction.setCellFactory(col -> new TableCell<ReportedItem, String>() {
            private final Button viewBtn = new Button();
            private final Button deleteBtn = new Button();

            {
                ImageView eyeIcon = createIcon("/com/example/findit/assets/ViewEye.png");
                ImageView trashIcon = createIcon("/com/example/findit/assets/trash.png");

                viewBtn.setGraphic(eyeIcon);
                deleteBtn.setGraphic(trashIcon);
                String transparentStyle = "-fx-background-color: transparent; -fx-cursor: hand;";
                viewBtn.setStyle(transparentStyle);
                deleteBtn.setStyle(transparentStyle);

                viewBtn.setOnAction(e -> {
                    ReportedItem item = getTableView().getItems().get(getIndex());
                    handleViewItem(item);
                });

                deleteBtn.setOnAction(e -> {
                    ReportedItem item = getTableView().getItems().get(getIndex());
                    handleDeleteItem(item);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox actionBox = new javafx.scene.layout.HBox(8, viewBtn, deleteBtn);
                    actionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    setGraphic(actionBox);
                }
            }
        });
    }
    
    private ImageView createIcon(String path) {
        java.io.InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("X Missing table icon: " + path);
            return new ImageView(); 
        }
        ImageView imgView = new ImageView(new Image(stream));
        imgView.setFitWidth(20);
        imgView.setFitHeight(20);
        imgView.setPreserveRatio(true);
        return imgView;
    }
    private void loadItems() {
        masterData.addAll(
            new ReportedItem("Lost", "Laptop", "Electronics", "2025-01-08", "Juan dela Cruz", "Library"),
            new ReportedItem("Found", "Black Wallet", "Wallet", "2025-01-09", "Maria Santos", "Cafeteria")
        );
    }

    private void wireSearchAndFilter() {
        filteredData = new FilteredList<>(masterData, p -> true);
        itemsTable.setItems(filteredData);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void applyFilter() {
        String search = searchField.getText().toLowerCase().trim();
        String typeValue = typeFilter.getValue();

        filteredData.setPredicate(item -> {
            boolean matchesSearch = search.isEmpty()
                    || item.getItemName().toLowerCase().contains(search)
                    || item.getCategory().toLowerCase().contains(search)
                    || item.getLocation().toLowerCase().contains(search)
                    || item.getReportedBy().toLowerCase().contains(search);

            boolean matchesType = "All".equals(typeValue)
                    || item.getType().equalsIgnoreCase(typeValue);

            return matchesSearch && matchesType;
        });
    }

    private void handleViewItem(ReportedItem item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Item Details");
        alert.setHeaderText(item.getItemName());
        alert.setContentText(
                "Type: "        + item.getType()       + "\n" +
                "Category: "    + item.getCategory()   + "\n" +
                "Date: "        + item.getDate()        + "\n" +
                "Reported By: " + item.getReportedBy() + "\n" +
                "Location: "    + item.getLocation()
        );
        alert.showAndWait();
    }

    public static class ReportedItem {
        private final String type, itemName, category, date, reportedBy, location;
        public ReportedItem(String typ, String itm, String cat, String dt, String rep, String loc) {
            type = typ; itemName = itm; category = cat; date = dt; reportedBy = rep; location = loc;
        }
        public String getType() { return type; } public String getItemName() { return itemName; }
        public String getCategory() { return category; } public String getDate() { return date; }
        public String getReportedBy() { return reportedBy; } public String getLocation() { return location; }
    }

    private void handleDeleteItem(ReportedItem item) {
        // CONFIRMATION DIALOG BEFORE DELETION
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Confirmation");
        confirmDialog.setHeaderText("Delete Report: " + item.getItemName());
        confirmDialog.setContentText("Are you sure you want to delete this report? This action cannot be undone.");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                masterData.remove(item);
                
                // REMOVE COMMENT IF DATABASE HANDLES THIS FOR DELETION OF REPORTED ITEMS
                // SQL 'DELETE FROM items WHERE id = ?' execution right here!
            }
        });
    }
}

