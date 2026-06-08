module com.example.findit {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.findit to javafx.fxml;
    exports com.example.findit;
}