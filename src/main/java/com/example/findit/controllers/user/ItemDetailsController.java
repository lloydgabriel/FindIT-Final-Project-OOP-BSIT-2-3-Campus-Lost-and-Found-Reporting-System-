package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemDetailsController {

    @FXML
    public void handleClaimItem(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ClaimItems.fxml"));
            Parent root = loader.load();
            Stage claimStage = new Stage();
            claimStage.initModality(Modality.APPLICATION_MODAL);
            claimStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            claimStage.setTitle("Claim Item");
            claimStage.setScene(new Scene(root));
            claimStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Could not open claim form");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
