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
import java.sql.*;
import java.util.ResourceBundle;

import com.example.findit.controllers.admin.AdminSidebarController;
import com.example.findit.util.DBConnection;


public class AdminDashboardController implements Initializable {

    @FXML private AdminSidebarController sidebarController;

    @FXML private Label foundItemsCount;
    @FXML private Label lostReportsCount;
    @FXML private Label matchedCount;

    @FXML private ProgressBar pbElectronics, pbWallet, pbDocument;
    @FXML private Label lblElectronics, lblWallet, lblDocument;

    @FXML private TableView<ItemRow> recentItemsTable;

    @FXML private TableColumn<ItemRow, String> colImage;
    @FXML private TableColumn<ItemRow, String> colItem;
    @FXML private TableColumn<ItemRow, String> colCategory;
    @FXML private TableColumn<ItemRow, String> colLocation;
    @FXML private TableColumn<ItemRow, String> colStatus;
    @FXML private TableColumn<ItemRow, String> colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (sidebarController != null) {
            sidebarController.setActiveTab("Dashboard");
        }

        configureTableColumns();
        loadDashboardStats();
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

    private void loadDashboardStats() {

        try (Connection conn = DBConnection.connect()) {

            String foundSql =
                    "SELECT COUNT(*) FROM items WHERE status = 'Found'";

            String lostSql =
                    "SELECT COUNT(*) FROM items WHERE status = 'Lost'";

            String matchedSql =
                    "SELECT COUNT(*) FROM items WHERE status = 'Matched'";

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM items")) {

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("TOTAL ITEMS: " + rs.getInt(1));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(foundSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    foundItemsCount.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(lostSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    lostReportsCount.setText(String.valueOf(rs.getInt(1)));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(matchedSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    matchedCount.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

            foundItemsCount.setText("0");
            lostReportsCount.setText("0");
            matchedCount.setText("0");
        }
    }

    private void loadRecentItems() {

        ObservableList<ItemRow> data = FXCollections.observableArrayList();

        String sql = """
            SELECT item_name,
                   category,
                   location,
                   status,
                   date_reported
            FROM items
            ORDER BY date_reported DESC
            LIMIT 10
            """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                data.add(new ItemRow(
                        "",
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getString("location"),
                        rs.getString("status"),
                        rs.getString("date_reported")
                ));
            }

            recentItemsTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class ItemRow {

        private final String image;
        private final String itemName;
        private final String category;
        private final String location;
        private final String status;
        private final String date;

        public ItemRow(String image,
                       String itemName,
                       String category,
                       String location,
                       String status,
                       String date) {

            this.image = image;
            this.itemName = itemName;
            this.category = category;
            this.location = location;
            this.status = status;
            this.date = date;
        }

        public String getImage() {
            return image;
        }

        public String getItemName() {
            return itemName;
        }

        public String getCategory() {
            return category;
        }

        public String getLocation() {
            return location;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }
}