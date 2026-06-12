package com.example.findit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER SO WE CAN TALK TO IT!
    @FXML private AdminSidebarController sidebarController;

    @FXML private Label foundItemsCount, lostReportsCount, matchedCount;
    @FXML private ProgressBar pbElectronics, pbWallet, pbDocument;
    @FXML private Label lblElectronics, lblWallet, lblDocument;

    @FXML private TableView<ItemRow> recentItemsTable;
    @FXML private TableColumn<ItemRow, String> colImage, colItem, colCategory, colLocation, colStatus, colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tells the sidebar to highlight the Dashboard button!
        if (sidebarController != null) { sidebarController.setActiveTab("Dashboard"); }
        
        configureTableColumns();
        loadRecentItems();
    }

    private void configureTableColumns() {
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadRecentItems() {
        foundItemsCount.setText("12");
        lostReportsCount.setText("8");
        matchedCount.setText("3");

        ObservableList<ItemRow> data = FXCollections.observableArrayList(
                new ItemRow("", "Sample Lost Item", "Electronics", "Library", "Lost", "2025-01-10"),
                new ItemRow("", "Sample Found Item", "Wallet", "Cafeteria", "Found", "2025-01-11")
        );
        recentItemsTable.setItems(data);
    }

    public static class ItemRow {
        private final String image, itemName, category, location, status, date;
        public ItemRow(String img, String name, String cat, String loc, String stat, String dt) {
            this.image = img; this.itemName = name; this.category = cat;
            this.location = loc; this.status = stat; this.date = dt;
        }
        public String getImage() { return image; } public String getItemName() { return itemName; }
        public String getCategory() { return category; } public String getLocation() { return location; }
        public String getStatus() { return status; } public String getDate() { return date; }
    }
}