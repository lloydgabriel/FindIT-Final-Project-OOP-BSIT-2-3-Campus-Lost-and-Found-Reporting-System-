package com.example.findit.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class AdminLoginController {

    @FXML private PasswordField passwordField;
    private static final String ADMIN_PASSWORD = "admin"; // placeholder for backend

    @FXML
    private void handleLogin() {
        String entered = passwordField.getText();

        if (entered == null || entered.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Password cannot be empty.");
            return;
        }

        if (entered.equals(ADMIN_PASSWORD)) {
            navigateTo("/com/example/findit/views/admin/AdminDashboard.fxml", "Admin Dashboard");
        } else {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Incorrect password. Please try again.");
            passwordField.clear();
        }
    }

    @FXML
    private void goBackToMain() {
        navigateTo("/com/example/findit/views/user/MainPortal.fxml", "FindIT");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) passwordField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}