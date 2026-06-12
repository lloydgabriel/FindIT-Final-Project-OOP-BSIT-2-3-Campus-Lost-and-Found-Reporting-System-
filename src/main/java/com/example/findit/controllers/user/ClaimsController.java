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

public class ClaimsController {

    @FXML
    public void initialize() {
        UserSidebarController.setActivePage("Claims");
    }

    @FXML
    public void handleClaimClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ClaimGallery.fxml"));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialog.setTitle("Claim Item");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
        } catch (IOException e) {
            System.err.println("Could not open claim form");
            e.printStackTrace();
        }
    }
}
