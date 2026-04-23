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
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f4f7f6, #e0e5ec); -fx-padding: 40;");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450);
        card.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        Label title = new Label("Initialize New Life");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #2c3e50;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Player Name...");
        nameField.setMaxWidth(400);
        nameField.setPrefHeight(45);
        nameField.setStyle("-fx-font-size: 16px; -fx-background-color: #f8f9fa; -fx-text-fill: #495057; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label instruction = new Label("Select Starting Trait:");
        instruction.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");

        ComboBox<String> traitBox = new ComboBox<>();
        traitBox.getItems().addAll(
            "Genius (+Knowledge growth)",
            "Social (+Relationships growth)",
            "Ambitious (+Career growth)",
            "Calm (-Stress build-up)"
        );
        traitBox.getSelectionModel().selectFirst();
        traitBox.setPrefHeight(40);
        traitBox.setMaxWidth(400);
        traitBox.setStyle("-fx-font-size: 15px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6;");

        Button startBtn = new Button("Start Journey");
        startBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 12 30; -fx-background-radius: 6; -fx-cursor: hand;");
        startBtn.setMaxWidth(400);
        startBtn.setOnAction(e -> {
            String name = nameField.getText().isEmpty() ? "Subject_XYZ" : nameField.getText();
            JsonNode newGame = GameService.startGame(name);
            if (newGame != null && newGame.has("profile")) {
                stage.setScene(new DashboardScreen(stage, newGame).createScene());
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to start game.").show();
            }
        });

        card.getChildren().addAll(title, nameField, instruction, traitBox, startBtn);
        root.getChildren().add(card);
        return new Scene(root, 1000, 700);
    }
}
