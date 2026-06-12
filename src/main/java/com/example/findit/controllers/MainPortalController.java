package com.example.findit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainPortalController {

    @FXML
    public void goToUserDashboard(ActionEvent event) {
        // Points to the User Dashboard FXML
        switchScene(event, "/com/example/findit/views/user/Dashboard.fxml");
    }

    @FXML
    public void goToAdminLogin(ActionEvent event) {
        // Points to the Admin Login FXML
        switchScene(event, "/com/example/findit/views/admin/AdminLogin.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not load the FXML file at " + fxmlPath);
            e.printStackTrace(); 
        }
    }
}