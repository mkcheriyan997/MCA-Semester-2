package com.lifeload.client.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeload.client.service.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TimelineScreen {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Life Timeline");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d1117;");

        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");
        Label title = new Label("📅  LIFE TIMELINE");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #58a6ff;");
        header.getChildren().add(title);

        // Timeline scroll
        VBox timelineBox = new VBox(0);
        timelineBox.setPadding(new Insets(20, 30, 20, 30));

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/game/timeline"))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonNode events = mapper.readTree(resp.body());
                if (events.isEmpty()) {
                    Label empty = new Label("No events yet. Start playing to build your timeline!");
                    empty.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 16px;");
                    timelineBox.getChildren().add(empty);
                } else {
                    for (JsonNode event : events) {
                        timelineBox.getChildren().add(createTimelineEntry(event));
                    }
                }
            }
        } catch (Exception e) {
            Label err = new Label("Could not load timeline: " + e.getMessage());
            err.setStyle("-fx-text-fill: #f85149;");
            timelineBox.getChildren().add(err);
        }

        ScrollPane scroll = new ScrollPane(timelineBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Close button
        Button closeBtn = new Button("CLOSE");
        closeBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #cdd9e5; -fx-font-weight: bold; -fx-padding: 10 30;");
        closeBtn.setOnAction(e -> stage.close());
        HBox footer = new HBox(closeBtn);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(15, 30, 15, 30));
        footer.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 1 0 0 0;");

        root.getChildren().addAll(header, scroll, footer);
        stage.setScene(new Scene(root, 700, 600));
        stage.show();
    }

    private HBox createTimelineEntry(JsonNode event) {
        String type = event.has("type") ? event.get("type").asText() : "MILESTONE";
        String color = switch (type) {
            case "CAREER", "START" -> "#58a6ff";
            case "MONEY"           -> "#3fb950";
            case "HEALTH"          -> "#f85149";
            case "RELATIONSHIP"    -> "#f78166";
            case "ACHIEVEMENT","WON" -> "#d29922";
            case "CRISIS","FAILED" -> "#f85149";
            default                -> "#8b949e";
        };
        String icon = switch (type) {
            case "CAREER"       -> "💼";
            case "MONEY"        -> "💰";
            case "HEALTH"       -> "❤️";
            case "RELATIONSHIP" -> "🤝";
            case "ACHIEVEMENT","WON" -> "🏆";
            case "CRISIS","FAILED"   -> "⚠️";
            case "START"        -> "🚀";
            default             -> "📌";
        };

        HBox entry = new HBox(15);
        entry.setAlignment(Pos.TOP_LEFT);
        entry.setPadding(new Insets(0, 0, 0, 0));

        // Left column: dot + vertical line
        VBox lineCol = new VBox();
        lineCol.setAlignment(Pos.TOP_CENTER);
        lineCol.setPrefWidth(40);
        Circle dot = new Circle(8);
        dot.setFill(Color.web(color));
        Region line = new Region();
        line.setPrefWidth(2);
        line.setPrefHeight(50);
        line.setStyle("-fx-background-color: #30363d;");
        lineCol.getChildren().addAll(dot, line);

        // Right column: content
        VBox content = new VBox(4);
        content.setPadding(new Insets(0, 0, 20, 0));

        HBox ageTag = new HBox(8);
        ageTag.setAlignment(Pos.CENTER_LEFT);
        Label ageLbl = new Label("Age " + event.get("age").asInt());
        ageLbl.setStyle("-fx-background-color: " + color + "22; -fx-text-fill: " + color + "; -fx-padding: 3 10; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label weekLbl = new Label("Week " + event.get("week").asInt());
        weekLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;");
        ageTag.getChildren().addAll(ageLbl, weekLbl);

        Label desc = new Label(icon + " " + event.get("description").asText());
        desc.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 14px;");
        desc.setWrapText(true);

        content.getChildren().addAll(ageTag, desc);
        entry.getChildren().addAll(lineCol, content);
        HBox.setHgrow(content, Priority.ALWAYS);
        return entry;
    }
}
