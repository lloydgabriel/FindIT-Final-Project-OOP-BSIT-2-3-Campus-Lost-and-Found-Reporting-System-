package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UserDashboardController {

    @FXML private TextField txtSearch;

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Dashboard");
        if (txtSearch != null) {
            txtSearch.setOnAction(e -> goToItemsFromSearch());
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
}
