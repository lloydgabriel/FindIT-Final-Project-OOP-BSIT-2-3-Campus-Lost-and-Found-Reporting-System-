package com.example.findit.controllers.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchSuggestionPanelController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER!
    @FXML private AdminSidebarController sidebarController;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    
    // Links to the VBox holding the cards, not a TableView!
    @FXML private VBox matchCardsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tells the sidebar to highlight the Match Suggestion button!
        if (sidebarController != null) { sidebarController.setActiveTab("Match"); }
        
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Pending", "Confirmed", "Rejected"));
        statusFilter.getSelectionModel().selectFirst();
    }

    // --- MODAL POPUP LOGIC ---
    @FXML
    private void openMatchDetail() {
        try {
            // Load the modal FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/findit/views/admin/MatchSuggestion.fxml"));
            Parent root = loader.load();
            
            // Create a new window specifically for the popup
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL); // Blocks clicking behind the popup
            dialog.initOwner(matchCardsContainer.getScene().getWindow());
            dialog.setTitle("Match Details");
            dialog.setScene(new Scene(root));
            
            // Display it
            dialog.showAndWait();
        } catch (Exception e) {
            System.err.println("❌ Could not load MatchSuggestion.fxml modal!");
            e.printStackTrace();
        }
    }
}