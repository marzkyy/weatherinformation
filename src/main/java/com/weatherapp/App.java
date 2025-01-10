package com.weatherapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    // Variables to track the initial offset of the window
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
        Parent root = fxmlLoader.load();

        // Get the controller
        MainController controller = fxmlLoader.getController();

        // Set the main window in the controller
        controller.setMainWindow(primaryStage);

        // Get the WebView from the controller
        WebView forecastWebView = controller.forecastWebView;

        // Enable drag functionality on the root, excluding the WebView
        root.setOnMousePressed(event -> {
            // Only trigger drag if the mouse is not on the WebView
            if (event.getTarget() != forecastWebView) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(event -> {
            // Only trigger drag if the mouse is not on the WebView
            if (event.getTarget() != forecastWebView) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        // Configure and show the stage
        primaryStage.initStyle(StageStyle.UNDECORATED); // Remove title bar
        primaryStage.setWidth(400); // Set fixed width
        primaryStage.setHeight(510); // Set fixed height
        primaryStage.setTitle("Weather Information App");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
