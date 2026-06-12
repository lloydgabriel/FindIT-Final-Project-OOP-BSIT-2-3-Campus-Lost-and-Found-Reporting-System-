package com.example.findit.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for MatchSuggestion.fxml  (modal overlay)
 *
 * Required fx:id additions in MatchSuggestion.fxml:
 *
 *   Header row:
 *     Label "Pending" badge          → fx:id="statusBadge"
 *
 *   Lost-item column:
 *     Label "Item Name"              → fx:id="lostItemName"
 *     Label "Reported by:"           → fx:id="lostReportedBy"
 *     Label "Date:"                  → fx:id="lostDate"
 *     Label "Location:"              → fx:id="lostLocation"
 *
 *   Found-item column:
 *     Label "Item Name"              → fx:id="foundItemName"
 *     Label "Reported by:"           → fx:id="foundReportedBy"
 *     Label "Date:"                  → fx:id="foundDate"
 *     Label "Location:"              → fx:id="foundLocation"
 *
 *   Action buttons:
 *     Button "Confirm Match"         → onAction="#handleConfirmMatch"
 *     Button "Close"                 → onAction="#handleClose"
 */
public class MatchSuggestionController implements Initializable {

    // ── FXML bindings ─────────────────────────────────────────────────────────

    // Status
    @FXML private Label statusBadge;

    // Lost item side
    @FXML private Label lostItemName;
    @FXML private Label lostReportedBy;
    @FXML private Label lostDate;
    @FXML private Label lostLocation;

    // Found item side
    @FXML private Label foundItemName;
    @FXML private Label foundReportedBy;
    @FXML private Label foundDate;
    @FXML private Label foundLocation;

    // Internal state
    private int currentMatchId = -1;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Labels default to the values set in FXML; they'll be overwritten by loadMatch().
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Populates the modal with data for the given match ID.
     * Called by {@link MatchSuggestionPanelController#openMatchDetail(int)}.
     */
    public void loadMatch(int matchId) {
        this.currentMatchId = matchId;

        // TODO: fetch MatchSuggestion object from your service/repository using matchId
        // and set the label texts below.  The example values are placeholders.

        statusBadge.setText("Pending");

        lostItemName.setText("Lost Item Name");
        lostReportedBy.setText("Reported by: Juan dela Cruz");
        lostDate.setText("Date: 2025-01-08");
        lostLocation.setText("Location: Library");

        foundItemName.setText("Found Item Name");
        foundReportedBy.setText("Reported by: Maria Santos");
        foundDate.setText("Date: 2025-01-09");
        foundLocation.setText("Location: Cafeteria");
    }

    // ── Action handlers ───────────────────────────────────────────────────────

    /** Confirms the match: persists the decision and closes the dialog. */
    @FXML
    private void handleConfirmMatch() {
        if (currentMatchId < 0) return;

        // TODO: call matchService.confirmMatch(currentMatchId)

        showAlert(Alert.AlertType.INFORMATION, "Match Confirmed",
                "The match has been confirmed successfully.");

        statusBadge.setText("Confirmed");
        statusBadge.setStyle("-fx-background-color: #C8E6C9; -fx-background-radius: 12; -fx-text-fill: #2E7D32;");

        closeDialog();
    }

    /** Closes the modal without making any change. */
    @FXML
    private void handleClose() {
        closeDialog();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void closeDialog() {
        Stage stage = (Stage) statusBadge.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
