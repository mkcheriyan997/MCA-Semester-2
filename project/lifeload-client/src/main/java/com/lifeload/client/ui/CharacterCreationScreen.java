package com.lifeload.client.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.lifeload.client.service.GameService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class CharacterCreationScreen {

    private Stage stage;

    public CharacterCreationScreen(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 40;");

        Label title = new Label("INITIALIZE_NEW_LIFE");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #00ffcc;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Player Designation...");
        nameField.setMaxWidth(400);
        nameField.setStyle("-fx-font-size: 18px; -fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #0f3460; -fx-border-width: 2;");

        Label instruction = new Label("Select Starting Trait:");
        instruction.setStyle("-fx-text-fill: #e94560; -fx-font-size: 18px;");

        ComboBox<String> traitBox = new ComboBox<>();
        traitBox.getItems().addAll(
            "Genius (+Knowledge growth)",
            "Social (+Relationships growth)",
            "Ambitious (+Career growth)",
            "Calm (-Stress build-up)"
        );
        traitBox.getSelectionModel().selectFirst();
        traitBox.setStyle("-fx-font-size: 16px; -fx-background-color: #16213e;");

        Button startBtn = new Button("BOOT_SEQUENCE");
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px; -fx-padding: 10 30;");
        startBtn.setOnAction(e -> {
            String name = nameField.getText().isEmpty() ? "Subject_XYZ" : nameField.getText();
            JsonNode newGame = GameService.startGame(name);
            if (newGame != null && newGame.has("profile")) {
                stage.setScene(new DashboardScreen(stage, newGame).createScene());
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to start game.").show();
            }
        });

        root.getChildren().addAll(title, nameField, instruction, traitBox, startBtn);
        return new Scene(root, 1000, 700);
    }
}
