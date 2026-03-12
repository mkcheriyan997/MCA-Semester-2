package com.lifeload.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.lifeload.client.service.GameService;
import com.lifeload.client.ui.CharacterCreationScreen;
import com.lifeload.client.ui.DashboardScreen;
import com.lifeload.client.ui.LoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LifeLoadApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LifeLoad: Life Management Simulator");

        Runnable onLoginSuccess = () -> {
            JsonNode gameData = GameService.loadGame();
            if (gameData != null && gameData.has("profile")) {
                primaryStage.setScene(new DashboardScreen(primaryStage, gameData).createScene());
            } else {
                primaryStage.setScene(new CharacterCreationScreen(primaryStage).createScene());
            }
        };

        Scene loginScene = new LoginScreen(primaryStage, onLoginSuccess).createScene();
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
