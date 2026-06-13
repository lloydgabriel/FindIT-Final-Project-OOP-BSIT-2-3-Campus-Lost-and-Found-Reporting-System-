package com.example.findit.controllers.admin;

import com.example.findit.model.AppDataStore;
import com.example.findit.model.ItemReport;
import com.example.findit.util.ImageStorage;
import com.example.findit.util.ResponsiveTable;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ReportedItemsController implements Initializable {

    // INJECTS THE SIDEBAR CONTROLLER!
    @FXML private AdminSidebarController sidebarController;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;

    @FXML private TableView<ItemReport> itemsTable;
    @FXML private TableColumn<ItemReport, String> colType, colItemName, colCategory, colDate, colReportedBy, colLocation, colAction;

    private FilteredList<ItemReport> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (sidebarController != null) { sidebarController.setActiveTab("Reported"); }
        
        typeFilter.setItems(FXCollections.observableArrayList("All", "Lost", "Found"));
        typeFilter.getSelectionModel().selectFirst();
        configureTableColumns();
        ResponsiveTable.fillAvailableWidth(itemsTable);
        wireSearchAndFilter();
    }

    private void configureTableColumns() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReportedBy.setCellValueFactory(new PropertyValueFactory<>("reportedBy"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setStyle("-fx-alignment: CENTER;");
        colAction.setStyle("-fx-alignment: CENTER;");
        colType.setCellFactory(col -> new TableCell<ItemReport, String>() {
            private final Label badge = new Label();
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setStyle("-fx-alignment: CENTER;");
                    badge.setText(item);
                    if (item.equalsIgnoreCase("Lost")) {
                        badge.setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #C62828; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    } else if (item.equalsIgnoreCase("Found")) {
                        badge.setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 12;");
                    }
                    setGraphic(badge);
                }
            }
        });

        colAction.setCellFactory(col -> new TableCell<ItemReport, String>() {
            private final Button viewBtn = new Button();
            private final Button deleteBtn = new Button();

            {
                ImageView eyeIcon = createIcon("/com/example/findit/assets/ViewEye.png");
                ImageView trashIcon = createIcon("/com/example/findit/assets/trash.png");

                viewBtn.setGraphic(eyeIcon);
                deleteBtn.setGraphic(trashIcon);
                String transparentStyle = "-fx-background-color: transparent; -fx-cursor: hand;";
                viewBtn.setStyle(transparentStyle);
                deleteBtn.setStyle(transparentStyle);

                viewBtn.setOnAction(e -> {
                    ItemReport item = getTableView().getItems().get(getIndex());
                    handleViewItem(item);
                });

                deleteBtn.setOnAction(e -> {
                    ItemReport item = getTableView().getItems().get(getIndex());
                    handleDeleteItem(item);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox actionBox = new javafx.scene.layout.HBox(8, viewBtn, deleteBtn);
                    actionBox.setAlignment(javafx.geometry.Pos.CENTER);
                    actionBox.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(actionBox);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }
    
    private ImageView createIcon(String path) {
        java.io.InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("X Missing table icon: " + path);
            return new ImageView(); 
        }
        ImageView imgView = new ImageView(new Image(stream));
        imgView.setFitWidth(20);
        imgView.setFitHeight(20);
        imgView.setPreserveRatio(true);
        return imgView;
    }
    private void wireSearchAndFilter() {
        filteredData = new FilteredList<>(AppDataStore.getItemReports(), p -> true);
        itemsTable.setItems(filteredData);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void applyFilter() {
        String search = searchField.getText().toLowerCase().trim();
        String typeValue = typeFilter.getValue();

        filteredData.setPredicate(item -> {
            boolean matchesSearch = search.isEmpty()
                    || item.getItemName().toLowerCase().contains(search)
                    || item.getCategory().toLowerCase().contains(search)
                    || item.getLocation().toLowerCase().contains(search)
                    || item.getReportedBy().toLowerCase().contains(search);

            boolean matchesType = "All".equals(typeValue)
                    || item.getType().equalsIgnoreCase(typeValue);

            return matchesSearch && matchesType;
        });
    }

    private void handleViewItem(ItemReport item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Item Details");
        dialog.setHeaderText(item.getItemName());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(760);

        javafx.scene.layout.HBox content = new javafx.scene.layout.HBox(22);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(10, 5, 5, 5));

        VBox details = new VBox(12);
        details.setPrefWidth(430);
        details.getChildren().addAll(
                createDetailsGrid(item),
                createDescriptionBlock(item)
        );
        javafx.scene.layout.HBox.setHgrow(details, Priority.ALWAYS);

        content.getChildren().addAll(createImagePanel(item), details);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private VBox createImagePanel(ItemReport item) {
        VBox panel = new VBox(8);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(250);

        StackPane imageFrame = new StackPane();
        imageFrame.setPrefSize(250, 230);
        imageFrame.setStyle("-fx-background-color: #F0F0F3; -fx-background-radius: 10;");

        Image image = ImageStorage.loadImage(item.getImagePath());
        if (image == null) {
            Label placeholder = new Label("No Image Available");
            placeholder.setStyle("-fx-text-fill: #777777; -fx-font-weight: bold;");
            imageFrame.getChildren().add(placeholder);
        } else {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(230);
            imageView.setFitHeight(210);
            imageView.setPreserveRatio(true);
            imageFrame.getChildren().add(imageView);
        }

        Label imageLabel = new Label(item.getType() + " Item Image");
        imageLabel.setStyle("-fx-text-fill: #4A1212; -fx-font-weight: bold;");
        panel.getChildren().addAll(imageFrame, imageLabel);
        return panel;
    }

    private GridPane createDetailsGrid(ItemReport item) {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(9);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPrefWidth(105);
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, valueColumn);

        addDetailRow(grid, 0, "Type", item.getType());
        addDetailRow(grid, 1, "Category", item.getCategory());
        addDetailRow(grid, 2, "Date", item.getDate());
        addDetailRow(grid, 3, "Reported By", item.getReportedBy());
        addDetailRow(grid, 4, "Contact", item.getContact());
        addDetailRow(grid, 5, "Location", item.getLocation());
        return grid;
    }

    private VBox createDescriptionBlock(ItemReport item) {
        Label title = new Label("Description");
        title.setStyle("-fx-text-fill: #4A1212; -fx-font-weight: bold; -fx-font-size: 13;");

        Label description = new Label(safe(item.getDescription()));
        description.setWrapText(true);
        description.setMinHeight(80);
        description.setStyle("-fx-background-color: #F7F7F9; -fx-background-radius: 8; -fx-padding: 10; -fx-text-fill: #333333;");

        VBox block = new VBox(6, title, description);
        return block;
    }

    private void addDetailRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: #777777; -fx-font-weight: bold;");

        Label value = new Label(safe(valueText));
        value.setWrapText(true);
        value.setStyle("-fx-text-fill: #222222;");
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    private void handleDeleteItem(ItemReport item) {
        // CONFIRMATION DIALOG BEFORE DELETION
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Confirmation");
        confirmDialog.setHeaderText("Delete Report: " + item.getItemName());
        confirmDialog.setContentText("Are you sure you want to delete this report? This action cannot be undone.");
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                AppDataStore.deleteItemReport(item);
            }
        });
    }
}

