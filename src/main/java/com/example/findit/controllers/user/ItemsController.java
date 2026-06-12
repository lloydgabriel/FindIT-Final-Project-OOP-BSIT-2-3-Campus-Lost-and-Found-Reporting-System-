package com.example.findit.controllers.user;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ItemsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private GridPane itemsGrid;

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Items");
        categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Electronics", "Wallet", "Documents", "Clothing", "Accessories", "Other"
        ));
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Lost", "Found"));
        categoryFilter.getSelectionModel().selectFirst();
        statusFilter.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> renderItems());
        categoryFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderItems());
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderItems());
        AppDataStore.getItemReports().addListener((javafx.collections.ListChangeListener<ItemReport>) change -> renderItems());
        renderItems();
    }

    @FXML
    public void handleItemClick(MouseEvent event) {
        Object item = findReportFromEvent(event);
        if (item instanceof ItemReport report) {
            openItemDetails(event, report);
        }
    }

    private Object findReportFromEvent(MouseEvent event) {
        Node node = event.getPickResult() == null
                ? (Node) event.getSource()
                : event.getPickResult().getIntersectedNode();

        while (node != null) {
            if (node.getUserData() instanceof ItemReport) {
                return node.getUserData();
            }
            node = node.getParent();
        }

        return ((Node) event.getSource()).getUserData();
    }

    private void renderItems() {
        itemsGrid.getChildren().clear();

        List<ItemReport> filteredItems = AppDataStore.getItemReports().stream()
                .filter(this::matchesFilters)
                .toList();

        if (filteredItems.isEmpty()) {
            Label emptyLabel = new Label("No items found.");
            emptyLabel.setStyle("-fx-text-fill: #777777; -fx-font-size: 16;");
            itemsGrid.add(emptyLabel, 0, 0, 4, 1);
            return;
        }

        for (int index = 0; index < filteredItems.size(); index++) {
            ItemReport item = filteredItems.get(index);
            VBox card = createItemCard(item);
            itemsGrid.add(card, index % 4, index / 4);
        }
    }

    private boolean matchesFilters(ItemReport item) {
        String search = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String category = categoryFilter.getValue();
        String status = statusFilter.getValue();

        boolean matchesSearch = search.isEmpty()
                || safe(item.getItemName()).toLowerCase(Locale.ROOT).contains(search)
                || safe(item.getCategory()).toLowerCase(Locale.ROOT).contains(search)
                || safe(item.getLocation()).toLowerCase(Locale.ROOT).contains(search)
                || safe(item.getReportedBy()).toLowerCase(Locale.ROOT).contains(search);

        boolean matchesCategory = "All Categories".equals(category)
                || safe(item.getCategory()).equalsIgnoreCase(category);
        boolean matchesStatus = "All Status".equals(status)
                || safe(item.getType()).equalsIgnoreCase(status);

        return matchesSearch && matchesCategory && matchesStatus;
    }

    private VBox createItemCard(ItemReport item) {
        VBox card = new VBox(8);
        card.setPrefWidth(185);
        card.setMinHeight(205);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 4); -fx-cursor: hand;");
        card.setUserData(item);
        card.setOnMouseClicked(this::handleItemClick);

        Region imageBox = createImageBox(item);
        Label name = new Label(item.getItemName());
        name.setWrapText(true);
        name.setStyle("-fx-text-fill: #4A1515; -fx-font-weight: bold; -fx-font-size: 14;");

        Label category = new Label(item.getCategory());
        category.setStyle("-fx-text-fill: #777777;");

        Label location = new Label(item.getLocation());
        location.setWrapText(true);
        location.setStyle("-fx-text-fill: #555555;");

        Label badge = new Label(item.getType());
        badge.setAlignment(Pos.CENTER);
        badge.setMinWidth(62);
        if ("Lost".equalsIgnoreCase(item.getType())) {
            badge.setStyle("-fx-background-color: #FFCDD2; -fx-background-radius: 12; -fx-text-fill: #C62828; -fx-font-weight: bold; -fx-padding: 3 10 3 10;");
        } else {
            badge.setStyle("-fx-background-color: #C8E6C9; -fx-background-radius: 12; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-padding: 3 10 3 10;");
        }

        card.getChildren().addAll(imageBox, name, category, location, badge);
        return card;
    }

    private Region createImageBox(ItemReport item) {
        VBox imageBox = new VBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPrefHeight(92);
        imageBox.setStyle("-fx-background-color: #EFEFEF; -fx-background-radius: 10;");

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            ImageView imageView = createImageView(item.getImagePath(), 88, 160);
            if (imageView != null) {
                imageBox.getChildren().add(imageView);
            } else {
                imageBox.getChildren().add(createPlaceholder(item));
            }
        } else {
            imageBox.getChildren().add(createPlaceholder(item));
        }

        return imageBox;
    }

    private ImageView createImageView(String imagePath, double height, double width) {
        try {
            ImageView imageView = new ImageView(new Image(imagePath, true));
            imageView.setFitHeight(height);
            imageView.setFitWidth(width);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (IllegalArgumentException e) {
            System.err.println("Could not load item image: " + imagePath);
            return null;
        }
    }

    private Label createPlaceholder(ItemReport item) {
        Label placeholder = new Label(safe(item.getType()));
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-weight: bold;");
        return placeholder;
    }

    private void openItemDetails(MouseEvent event, ItemReport item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ItemDetails.fxml"));
            Parent root = loader.load();
            ItemDetailsController controller = loader.getController();
            controller.setItem(item);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialog.setTitle("Item Details");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            System.err.println("Could not open item details");
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
