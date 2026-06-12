package com.example.findit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ProjectApplication.class.getResource("views/user/MainPortal.fxml"));
        
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Campus Lost and Found System"); 
        stage.setScene(scene);
        stage.show();
    }
}
