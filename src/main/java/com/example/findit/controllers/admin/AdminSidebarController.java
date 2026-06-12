package com.example.findit.controllers.admin;

import com.example.findit.model.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminSidebarController {

    @FXML private VBox sidebarContainer;
    @FXML private Label logoText;
    @FXML private Button btnDashboard, btnReported, btnClaims, btnMatch, btnLogout;
    @FXML private Button btnMonitoring;
    @FXML private ImageView imgMonitoring;
    @FXML private ImageView imgDashboard, imgReported, imgClaims, imgMatch;
    
    private boolean isSidebarExpanded = true;

    // --- SIDEBAR TOGGLE ---
   @FXML private ImageView logoImage; // <-- Don't forget to declare the new logo image!

    // --- SIDEBAR TOGGLE ---
    @FXML
    private void toggleSidebar() {
        if (isSidebarExpanded) {
            sidebarContainer.setPrefWidth(75.0);
            
            // Hides the text AND the logo so the hamburger has room
            logoText.setVisible(false);
            logoText.setManaged(false); 
            logoImage.setVisible(false);
            logoImage.setManaged(false);
            
            btnDashboard.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnReported.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnClaims.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnMatch.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btnLogout.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            isSidebarExpanded = false;
        } else {
            sidebarContainer.setPrefWidth(220.0);
            
            // Brings both back
            logoText.setVisible(true);
            logoText.setManaged(true);
            logoImage.setVisible(true);
            logoImage.setManaged(true);
            
            btnDashboard.setContentDisplay(ContentDisplay.LEFT);
            btnReported.setContentDisplay(ContentDisplay.LEFT);
            btnClaims.setContentDisplay(ContentDisplay.LEFT);
            btnMatch.setContentDisplay(ContentDisplay.LEFT);
            btnLogout.setContentDisplay(ContentDisplay.LEFT);
            isSidebarExpanded = true;
        }
    }

    // --- ACTIVE TAB HIGHLIGHTER ---
    public void setActiveTab(String tabName) {
        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: #FFCC00; -fx-border-color: transparent transparent transparent #FFCC00; -fx-border-width: 0 0 0 3;";
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #FFFFFF; -fx-border-color: transparent;";

        // 1. Reset all button text styles
        btnDashboard.setStyle(defaultStyle);
        btnReported.setStyle(defaultStyle);
        btnClaims.setStyle(defaultStyle);
        btnMatch.setStyle(defaultStyle);
        btnMonitoring.setStyle(defaultStyle);

        // 2. Load the base default images for all buttons
        imgDashboard.setImage(safeLoadImage("/com/example/findit/assets/dashboard.png"));
        imgReported.setImage(safeLoadImage("/com/example/findit/assets/ItemReportedAdmin.png"));
        imgClaims.setImage(safeLoadImage("/com/example/findit/assets/claim.png"));
        imgMatch.setImage(safeLoadImage("/com/example/findit/assets/MatchSuggestion.png"));
        imgMonitoring.setImage(safeLoadImage("/com/example/findit/assets/reportsidebar.png"));

        // 3. Create the white effect and apply it to ALL icons
        ColorAdjust makeWhite = new ColorAdjust();
        makeWhite.setBrightness(1.0);
        imgMonitoring.setEffect(makeWhite);
        imgDashboard.setEffect(makeWhite);
        imgReported.setEffect(makeWhite);
        imgClaims.setEffect(makeWhite);
        imgMatch.setEffect(makeWhite);

        // 4. Highlight the requested active tab, swap to yellow, and REMOVE the white effect!
        switch (tabName) {
            case "Dashboard":
                btnDashboard.setStyle(activeStyle);
                imgDashboard.setImage(safeLoadImage("/com/example/findit/assets/yellow_icons/dashboard.png"));
                imgDashboard.setEffect(null); 
                break;
            case "Reported":
                btnReported.setStyle(activeStyle);
                imgReported.setImage(safeLoadImage("/com/example/findit/assets/yellow_icons/ItemReportedAdmin.png"));
                imgReported.setEffect(null);
                break;
            case "Claims":
                btnClaims.setStyle(activeStyle);
                imgClaims.setImage(safeLoadImage("/com/example/findit/assets/yellow_icons/ClaimsAdmin.png"));
                imgClaims.setEffect(null);
                break;
            case "Match":
                btnMatch.setStyle(activeStyle);
                imgMatch.setImage(safeLoadImage("/com/example/findit/assets/yellow_icons/MatchSuggestion.png"));
                imgMatch.setEffect(null);
                break;
            case "Monitoring":
                btnMonitoring.setStyle(activeStyle);
                imgMonitoring.setImage(safeLoadImage(
                        "/com/example/findit/assets/reportsidebar.png"));
                imgMonitoring.setEffect(null);
                break;

        }
    }

    // --- ROUTING ---
    @FXML private void goToDashboard(ActionEvent event) { navigateTo(event, "/com/example/findit/views/admin/AdminDashboard.fxml", "Dashboard"); }
    @FXML private void goToReportedItems(ActionEvent event) { navigateTo(event, "/com/example/findit/views/admin/ReportedItems.fxml", "Reported Items"); }
    @FXML private void goToClaims(ActionEvent event) { navigateTo(event, "/com/example/findit/views/admin/Claims.fxml", "Claims"); }
    @FXML private void goToMatchSuggestions(ActionEvent event) { navigateTo(event, "/com/example/findit/views/admin/MatchSuggestionPanel.fxml", "Match Suggestions"); }
    @FXML private void handleLogout(ActionEvent event) {
        SessionManager.checkOut("Admin Login");
        navigateTo(event, "/com/example/findit/views/admin/AdminLogin.fxml", "Admin Login");
    }
    @FXML
    private void goToMonitoring(ActionEvent event) {
        navigateTo(event,
                "/com/example/findit/views/admin/Monitoring.fxml",
                "Monitoring");
    }

    private void navigateTo(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image safeLoadImage(String path) {
        java.io.InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("❌ MISSING IMAGE: " + path);
            return null;
        }
        return new Image(stream);
    }
}
