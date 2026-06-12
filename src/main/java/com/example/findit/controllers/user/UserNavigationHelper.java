package com.example.findit.controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

final class UserNavigationHelper {

    private UserNavigationHelper() { }

    static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent newRoot = FXMLLoader.load(UserNavigationHelper.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    static void switchScene(Node source, String fxmlPath) {
        try {
            Parent newRoot = FXMLLoader.load(UserNavigationHelper.class.getResource(fxmlPath));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.getScene().setRoot(newRoot);
        } catch (IOException e) {
            System.err.println("Could not load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
