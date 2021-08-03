package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    public void start(Stage primaryStage) throws Exception {

        AnchorPane rootLayout = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(rootLayout,1000,600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("MySQL监控工具");
        primaryStage.setResizable(false);
//        primaryStage.getIcons().add();

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
