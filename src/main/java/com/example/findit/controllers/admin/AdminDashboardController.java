package com.example.findit.controllers.admin;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ClaimRequest;
import com.example.findit.model.ItemMatch;
import com.example.findit.model.ItemReport;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER SO WE CAN TALK TO IT!
    @FXML private AdminSidebarController sidebarController;

    @FXML private Label foundItemsCount, lostReportsCount, matchedCount;
    @FXML private ProgressBar pbElectronics, pbWallet, pbDocument;
    @FXML private Label lblElectronics, lblWallet, lblDocument;
    @FXML private VBox topLocationsBox;

    @FXML private TableView<ItemRow> recentItemsTable;
    @FXML private TableColumn<ItemRow, String> colImage, colItem, colCategory, colLocation, colStatus, colDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tells the sidebar to highlight the Dashboard button!
        if (sidebarController != null) { sidebarController.setActiveTab("Dashboard"); }
        
        configureTableColumns();
        configureRecentItemsInteraction();
        refreshDashboard();
        AppDataStore.getItemReports().addListener((javafx.collections.ListChangeListener<ItemReport>) change -> refreshDashboard());
        AppDataStore.getClaimRequests().addListener((javafx.collections.ListChangeListener<ClaimRequest>) change -> refreshDashboard());
        AppDataStore.getMatchSuggestions().addListener((javafx.collections.ListChangeListener<ItemMatch>) change -> refreshDashboard());
    }

    private void configureTableColumns() {
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void configureRecentItemsInteraction() {
        recentItemsTable.setRowFactory(table -> {
            TableRow<ItemRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    showItemDetails(row.getItem().getItem());
                }
            });
            return row;
        });
    }

    private void refreshDashboard() {
        foundItemsCount.setText(String.valueOf(AppDataStore.countItemsByType("Found")));
        lostReportsCount.setText(String.valueOf(AppDataStore.countItemsByType("Lost")));
        matchedCount.setText(String.valueOf(AppDataStore.countMatches()));

        long electronics = countByCategory("Electronics");
        long wallet = countByCategory("Wallet");
        long documents = countByCategory("Documents") + countByCategory("Document");
        long total = Math.max(1, AppDataStore.getItemReports().size());

        lblElectronics.setText(electronics + " items");
        lblWallet.setText(wallet + " items");
        lblDocument.setText(documents + " items");
        pbElectronics.setProgress(electronics / (double) total);
        pbWallet.setProgress(wallet / (double) total);
        pbDocument.setProgress(documents / (double) total);
        renderTopLocations();

        ObservableList<ItemRow> data = FXCollections.observableArrayList();
        AppDataStore.getItemReports().stream()
                .limit(8)
                .map(item -> new ItemRow(item, "", item.getItemName(), item.getCategory(), item.getLocation(), item.getType(), item.getDate()))
                .forEach(data::add);
        recentItemsTable.setItems(data);
    }

    private long countByCategory(String category) {
        return AppDataStore.getItemReports().stream()
                .filter(item -> safe(item.getCategory()).equalsIgnoreCase(category))
                .count();
    }

    private void renderTopLocations() {
        if (topLocationsBox == null) {
            return;
        }

        topLocationsBox.getChildren().clear();
        Map<String, Long> locationCounts = AppDataStore.getItemReports().stream()
                .map(item -> safe(item.getLocation()).trim())
                .filter(location -> !location.isBlank())
                .collect(Collectors.groupingBy(location -> location, LinkedHashMap::new, Collectors.counting()));

        var topLocations = locationCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .toList();

        if (topLocations.isEmpty()) {
            Label emptyLabel = new Label("No locations yet");
            emptyLabel.setStyle("-fx-text-fill: #777777;");
            topLocationsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Map.Entry<String, Long> entry : topLocations) {
            topLocationsBox.getChildren().add(createLocationRow(entry.getKey(), entry.getValue()));
        }
    }

    private HBox createLocationRow(String location, long count) {
        Label locationLabel = new Label(location);
        locationLabel.setWrapText(true);
        locationLabel.setStyle("-fx-text-fill: #800000; -fx-font-weight: bold;");

        Label countLabel = new Label(count + (count == 1 ? " item" : " items"));
        countLabel.setStyle("-fx-text-fill: #666666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, locationLabel, spacer, countLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(35);
        row.setPadding(new Insets(0, 15, 0, 15));
        row.setStyle("-fx-background-color: #FDF7F7; -fx-background-radius: 20;");
        return row;
    }

    private void showItemDetails(ItemReport item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Item Details");
        alert.setHeaderText(item.getItemName());
        alert.setContentText(
                "Type: " + item.getType() + "\n" +
                "Category: " + item.getCategory() + "\n" +
                "Date: " + item.getDate() + "\n" +
                "Reported By: " + item.getReportedBy() + "\n" +
                "Contact: " + item.getContact() + "\n" +
                "Location: " + item.getLocation() + "\n\n" +
                "Description:\n" + item.getDescription()
        );
        alert.showAndWait();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static class ItemRow {
        private final ItemReport item;
        private final String image, itemName, category, location, status, date;
        public ItemRow(ItemReport item, String img, String name, String cat, String loc, String stat, String dt) {
            this.item = item;
            this.image = img; this.itemName = name; this.category = cat;
            this.location = loc; this.status = stat; this.date = dt;
        }
        public ItemReport getItem() { return item; }
        public String getImage() { return image; } public String getItemName() { return itemName; }
        public String getCategory() { return category; } public String getLocation() { return location; }
        public String getStatus() { return status; } public String getDate() { return date; }
    }
}
