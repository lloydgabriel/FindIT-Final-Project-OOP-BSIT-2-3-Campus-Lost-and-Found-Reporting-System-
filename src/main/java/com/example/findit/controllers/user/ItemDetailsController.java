package com.example.findit.controllers.user;

import com.example.findit.model.ItemReport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemDetailsController {
    @FXML private VBox imageBox;
    @FXML private Label lblItemName;
    @FXML private Label lblCategory;
    @FXML private Label lblStatus;
    @FXML private Label lblDate;
    @FXML private Label lblLocation;
    @FXML private Label lblReportedBy;
    @FXML private Label lblDescription;
    @FXML private Button btnClaimItem;

    private ItemReport item;

    public void setItem(ItemReport item) {
        this.item = item;
        lblItemName.setText(safe(item.getItemName()));
        lblCategory.setText(safe(item.getCategory()));
        lblStatus.setText(safe(item.getType()));
        lblDate.setText(safe(item.getDate()));
        lblLocation.setText(safe(item.getLocation()));
        lblReportedBy.setText(safe(item.getReportedBy()));
        lblDescription.setText(safe(item.getDescription()));

        if ("Lost".equalsIgnoreCase(item.getType())) {
            lblStatus.setStyle("-fx-background-color: #FFCDD2; -fx-background-radius: 10; -fx-text-fill: #C62828;");
        } else {
            lblStatus.setStyle("-fx-background-color: #C8E6C9; -fx-background-radius: 10; -fx-text-fill: #2E7D32;");
        }

        boolean canClaim = "Found".equalsIgnoreCase(item.getType());
        if (btnClaimItem != null) {
            btnClaimItem.setVisible(canClaim);
            btnClaimItem.setManaged(canClaim);
        }

        imageBox.getChildren().clear();
        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            ImageView imageView = createImageView(item.getImagePath());
            if (imageView != null) {
                imageBox.getChildren().add(imageView);
            } else {
                imageBox.getChildren().add(createPlaceholder(item));
            }
        } else {
            imageBox.getChildren().add(createPlaceholder(item));
        }
    }

    private ImageView createImageView(String imagePath) {
        try {
            ImageView imageView = new ImageView(new Image(imagePath, true));
            imageView.setFitHeight(190);
            imageView.setFitWidth(210);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (IllegalArgumentException e) {
            System.err.println("Could not load detail image: " + imagePath);
            return null;
        }
    }

    private Label createPlaceholder(ItemReport item) {
        Label placeholder = new Label(safe(item.getType()) + " Item");
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-weight: bold;");
        return placeholder;
    }

    @FXML
    public void handleClaimItem(ActionEvent event) {
        if (item == null || !"Found".equalsIgnoreCase(item.getType())) {
            showAlert(Alert.AlertType.WARNING, "Claim Unavailable",
                    "Only found item reports can be claimed.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/user/ClaimItems.fxml"));
            Parent root = loader.load();
            ClaimItemsController controller = loader.getController();
            controller.setItem(item);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Claim Item");
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Claim Form Error",
                    "The claim form could not be opened. Please try again.");
        }
    }

    @FXML
    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
