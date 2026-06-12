package com.example.findit.controllers.user;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class UserDashboardController {

    @FXML private TextField txtSearch;
    @FXML private ListView<ItemReport> searchSuggestions;

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Dashboard");
        if (txtSearch != null) {
            txtSearch.setOnAction(e -> goToItemsFromSearch());
            txtSearch.textProperty().addListener((obs, oldValue, newValue) -> updateSearchSuggestions(newValue));
        }
        if (searchSuggestions != null) {
            searchSuggestions.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(ItemReport item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getItemName() + " (" + item.getType() + ") - " + item.getLocation());
                    }
                }
            });
            searchSuggestions.setVisible(false);
            searchSuggestions.setManaged(false);
        }
    }

    @FXML
    public void handleReportLost(ActionEvent event) {
        UserNavigationHelper.switchScene(event, "/com/example/findit/views/user/LostForm.fxml");
    }

    @FXML
    public void handleReportFound(ActionEvent event) {
        UserNavigationHelper.switchScene(event, "/com/example/findit/views/user/FoundForm.fxml");
    }

    @FXML
    public void handleFilter(ActionEvent event) {
        UserSidebarController.setActivePage("Items");
        UserNavigationHelper.switchScene(event, "/com/example/findit/views/user/Items.fxml");
    }

    private void goToItemsFromSearch() {
        UserSidebarController.setActivePage("Items");
        if (txtSearch != null && txtSearch.getScene() != null) {
            UserNavigationHelper.switchScene(txtSearch, "/com/example/findit/views/user/Items.fxml");
        }
    }

    private void updateSearchSuggestions(String query) {
        if (searchSuggestions == null) {
            return;
        }

        String searchText = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (searchText.isEmpty()) {
            hideSearchSuggestions();
            return;
        }

        var matches = AppDataStore.getItemReports().stream()
                .filter(item -> matchesSearch(item, searchText))
                .limit(8)
                .toList();

        searchSuggestions.setItems(FXCollections.observableArrayList(matches));
        boolean hasMatches = !matches.isEmpty();
        searchSuggestions.setVisible(hasMatches);
        searchSuggestions.setManaged(hasMatches);
        searchSuggestions.setPrefHeight(Math.min(220, Math.max(48, matches.size() * 42)));
    }

    private boolean matchesSearch(ItemReport item, String searchText) {
        return safe(item.getItemName()).toLowerCase(Locale.ROOT).contains(searchText)
                || safe(item.getCategory()).toLowerCase(Locale.ROOT).contains(searchText)
                || safe(item.getLocation()).toLowerCase(Locale.ROOT).contains(searchText)
                || safe(item.getType()).toLowerCase(Locale.ROOT).contains(searchText);
    }

    @FXML
    private void handleSuggestionClick(MouseEvent event) {
        if (event.getClickCount() < 1 || searchSuggestions == null) {
            return;
        }

        ItemReport selectedItem = searchSuggestions.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            openItemDetails(selectedItem);
        }
    }

    private void openItemDetails(ItemReport item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ItemDetails.fxml"));
            Parent root = loader.load();
            ItemDetailsController controller = loader.getController();
            controller.setItem(item);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(txtSearch.getScene().getWindow());
            dialog.setTitle("Item Details");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            System.err.println("Could not open item details from dashboard search.");
            e.printStackTrace();
        }
    }

    private void hideSearchSuggestions() {
        searchSuggestions.getItems().clear();
        searchSuggestions.setVisible(false);
        searchSuggestions.setManaged(false);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
