package com.example.findit.controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemsController {

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Items");
    }

    @FXML
    public void handleItemClick(MouseEvent event) {
        openItemDetails(event);
    }

    private void openItemDetails(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ItemDetails.fxml"));
            Parent root = loader.load();
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
}
