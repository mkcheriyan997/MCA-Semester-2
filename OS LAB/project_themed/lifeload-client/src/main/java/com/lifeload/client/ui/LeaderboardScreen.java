package com.lifeload.client.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeload.client.service.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LeaderboardScreen {

    private static final String API_URL = "http://localhost:8081/api/leaderboard";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Global Leaderboard");

        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #121212;");

        Label title = new Label("TOP PLAYERS (WEALTH)");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #00ffcc;");

        ListView<String> list = new ListView<>();
        list.setStyle("-fx-background-color: #1e1e24; -fx-control-inner-background: #1e1e24; -fx-text-fill: white; -fx-font-family: Consolas;");
        list.setPrefHeight(400);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/wealth"))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode arr = mapper.readTree(response.body());
                int rank = 1;
                for (JsonNode n : arr) {
                    list.getItems().add(
                        String.format("#%d | %-20s | $%,.2f | Score: %.0f", 
                            rank++, 
                            n.get("playerName").asText(), 
                            n.get("finalWealth").asDouble(),
                            n.get("balanceScore").asDouble())
                    );
                }
            }
        } catch (Exception e) {
            list.getItems().add("Failed to load leaderboard.");
        }

        Button close = new Button("CLOSE");
        close.setStyle("-fx-background-color: #ff3366; -fx-text-fill: white; -fx-font-weight: bold;");
        close.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, list, close);
        stage.setScene(new Scene(root, 600, 600));
        stage.show();
    }
}
