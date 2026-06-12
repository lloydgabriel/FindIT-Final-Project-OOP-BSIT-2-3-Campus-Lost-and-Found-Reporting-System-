module com.example.findit {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive java.sql;

    opens com.example.findit to javafx.fxml;
    exports com.example.findit;

    opens com.example.findit.controllers to javafx.fxml;
    exports com.example.findit.controllers;

    opens com.example.findit.controllers.admin to javafx.fxml;
    exports com.example.findit.controllers.admin;

    opens com.example.findit.controllers.user to javafx.fxml;
    exports com.example.findit.controllers.user;
}