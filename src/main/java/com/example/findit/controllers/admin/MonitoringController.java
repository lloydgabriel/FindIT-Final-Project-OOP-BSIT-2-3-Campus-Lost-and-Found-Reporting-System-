package com.example.findit.controllers.admin;

import com.example.findit.dao.ActivityLogDAO;
import com.example.findit.dao.ValidationDAO;
import com.example.findit.model.ActivityLog;
import com.example.findit.model.ValidationLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MonitoringController implements Initializable {

    @FXML private AdminSidebarController sidebarController;

    @FXML private Label lblTodayCheckIn;
    @FXML private Label lblTodayCheckOut;
    @FXML private Label lblApproved;
    @FXML private Label lblRejected;
    @FXML private Label lblPending;

    @FXML private TableView<IERow>           ieTable;
    @FXML private TableColumn<IERow, String> colIEUserId;
    @FXML private TableColumn<IERow, String> colIEAction;
    @FXML private TableColumn<IERow, String> colIEDescription;
    @FXML private TableColumn<IERow, String> colIETime;

    @FXML private TableView<ValRow>           valTable;
    @FXML private TableColumn<ValRow, String> colValItem;
    @FXML private TableColumn<ValRow, String> colValAdmin;
    @FXML private TableColumn<ValRow, String> colValType;
    @FXML private TableColumn<ValRow, String> colValRemarks;
    @FXML private TableColumn<ValRow, String> colValTime;

    @FXML private ComboBox<String> ieFilter;
    @FXML private ComboBox<String> valFilter;
    @FXML private TextField        ieSearch;
    @FXML private TextField        valSearch;

    private final ObservableList<IERow>  ieMaster  = FXCollections.observableArrayList();
    private final ObservableList<ValRow> valMaster = FXCollections.observableArrayList();
    private FilteredList<IERow>  ieFiltered;
    private FilteredList<ValRow> valFiltered;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) {
            sidebarController.setActiveTab("Monitoring");
        }
        configureIETable();
        configureValTable();
        setupFilters();
        loadData();
    }

    private void configureIETable() {
        colIEUserId     .setCellValueFactory(new PropertyValueFactory<>("userId"));
        colIEAction     .setCellValueFactory(new PropertyValueFactory<>("action"));
        colIEDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colIETime       .setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        colIEAction.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                badge.setText(item);
                String style = "CHECK_IN".equalsIgnoreCase(item)
                        ? "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;"
                        : "-fx-background-color: #FFCDD2; -fx-text-fill: #C62828;";
                badge.setStyle(style + " -fx-font-weight: bold; -fx-padding: 2 8 2 8;"
                        + " -fx-background-radius: 4;");
                setGraphic(badge);
            }
        });
    }

    private void configureValTable() {
        colValItem   .setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colValAdmin  .setCellValueFactory(new PropertyValueFactory<>("validatedBy"));
        colValType   .setCellValueFactory(new PropertyValueFactory<>("validationType"));
        colValRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        colValTime   .setCellValueFactory(new PropertyValueFactory<>("validatedAt"));

        colValType.setCellFactory(col -> new TableCell<>() {
            private final Label badge = new Label();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                badge.setText(item);
                String style = switch (item.toUpperCase()) {
                    case "APPROVED" -> "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;";
                    case "REJECTED" -> "-fx-background-color: #FFCDD2; -fx-text-fill: #C62828;";
                    default         -> "-fx-background-color: #FFF9C4; -fx-text-fill: #F57F17;";
                };
                badge.setStyle(style + " -fx-font-weight: bold; -fx-padding: 2 8 2 8;"
                        + " -fx-background-radius: 4;");
                setGraphic(badge);
            }
        });
    }

    private void setupFilters() {
        ieFilter .setItems(FXCollections.observableArrayList("All", "CHECK_IN", "CHECK_OUT"));
        valFilter.setItems(FXCollections.observableArrayList("All", "APPROVED", "REJECTED", "PENDING"));
        ieFilter .getSelectionModel().selectFirst();
        valFilter.getSelectionModel().selectFirst();

        ieFiltered  = new FilteredList<>(ieMaster,  p -> true);
        valFiltered = new FilteredList<>(valMaster, p -> true);

        ieTable .setItems(ieFiltered);
        valTable.setItems(valFiltered);

        ieSearch .textProperty().addListener((obs, o, n) -> applyIEFilter());
        valSearch.textProperty().addListener((obs, o, n) -> applyValFilter());
        ieFilter .valueProperty().addListener((obs, o, n) -> applyIEFilter());
        valFilter.valueProperty().addListener((obs, o, n) -> applyValFilter());
    }

    private void applyIEFilter() {
        String search = ieSearch.getText().toLowerCase().trim();
        String type   = ieFilter.getValue();
        ieFiltered.setPredicate(row -> {
            boolean matchesType   = "All".equals(type)
                    || row.getAction().equalsIgnoreCase(type);
            boolean matchesSearch = search.isEmpty()
                    || row.getUserId().toLowerCase().contains(search)
                    || row.getAction().toLowerCase().contains(search)
                    || row.getDescription().toLowerCase().contains(search);
            return matchesType && matchesSearch;
        });
    }

    private void applyValFilter() {
        String search = valSearch.getText().toLowerCase().trim();
        String type   = valFilter.getValue();
        valFiltered.setPredicate(row -> {
            boolean matchesType   = "All".equals(type)
                    || row.getValidationType().equalsIgnoreCase(type);
            boolean matchesSearch = search.isEmpty()
                    || row.getValidationType().toLowerCase().contains(search)
                    || row.getRemarks().toLowerCase().contains(search);
            return matchesType && matchesSearch;
        });
    }

    private void loadData() {
        loadIELogs();
        loadValidationLogs();
        updateSummary();
    }

    private void loadIELogs() {
        ieMaster.clear();
        for (ActivityLog log : new ActivityLogDAO().getIngressEgressLogs()) {
            ieMaster.add(new IERow(
                    String.valueOf(log.getUserId()),
                    log.getAction(),
                    log.getDescription(),
                    log.getCreatedAt() != null ? log.getCreatedAt().format(FMT) : "-"
            ));
        }
    }

    private void loadValidationLogs() {
        valMaster.clear();
        for (ValidationLog log : new ValidationDAO().getAllValidations()) {
            valMaster.add(new ValRow(
                    String.valueOf(log.getItemId()),
                    String.valueOf(log.getValidatedBy()),
                    log.getValidationType(),
                    log.getRemarks(),
                    log.getValidatedAt() != null ? log.getValidatedAt().format(FMT) : "-"
            ));
        }
    }

    private void updateSummary() {
        ActivityLogDAO ieDao  = new ActivityLogDAO();
        ValidationDAO  valDao = new ValidationDAO();
        lblTodayCheckIn .setText(String.valueOf(ieDao.countTodayByAction("CHECK_IN")));
        lblTodayCheckOut.setText(String.valueOf(ieDao.countTodayByAction("CHECK_OUT")));
        lblApproved     .setText(String.valueOf(valDao.countByType("APPROVED")));
        lblRejected     .setText(String.valueOf(valDao.countByType("REJECTED")));
        lblPending      .setText(String.valueOf(valDao.countByType("PENDING")));
    }

    @FXML
    private void handleRefresh() { loadData(); }

    public static class IERow {
        private final String userId, action, description, createdAt;
        public IERow(String userId, String action, String description, String createdAt) {
            this.userId      = userId;
            this.action      = action;
            this.description = description != null ? description : "-";
            this.createdAt   = createdAt;
        }
        public String getUserId()      { return userId; }
        public String getAction()      { return action; }
        public String getDescription() { return description; }
        public String getCreatedAt()   { return createdAt; }
    }

    public static class ValRow {
        private final String itemId, validatedBy, validationType, remarks, validatedAt;
        public ValRow(String itemId, String validatedBy, String validationType,
                      String remarks, String validatedAt) {
            this.itemId         = itemId;
            this.validatedBy    = validatedBy;
            this.validationType = validationType;
            this.remarks        = remarks != null ? remarks : "-";
            this.validatedAt    = validatedAt;
        }
        public String getItemId()         { return itemId; }
        public String getValidatedBy()    { return validatedBy; }
        public String getValidationType() { return validationType; }
        public String getRemarks()        { return remarks; }
        public String getValidatedAt()    { return validatedAt; }
    }
}