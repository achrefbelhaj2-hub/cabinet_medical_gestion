package com.cabinet.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/cabinet/view/login.fxml"));
            
            Scene scene = new Scene(root, 1000, 700);
            primaryStage.setTitle("Cabinet Médical");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de login.fxml");
            System.err.println("Chemin cherché: /com/cabinet/view/login.fxml");
            System.err.println("Vérifiez que le fichier existe dans src/main/resources/");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}