package com.lifeload.client.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeload.client.service.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MarketScreen {

    private static final String API_URL = "http://localhost:8080/api/economy";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private VBox portfolioBox;

    public void show(Stage owner, Runnable onInvestSuccess) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Stock Market & Investments");

        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0d1117;");

        Label title = new Label("GLOBAL ECONOMY MODULE");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #a855f7;");

        Label stateLbl = new Label("Current Market State: FETCHING...");
        stateLbl.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 18px;");

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/market"))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                JsonNode node = mapper.readTree(res.body());
                stateLbl.setText("Current Market State: " + node.get("state").asText());
            }
        } catch (Exception e) {}

        HBox investBox = new HBox(15);
        investBox.setAlignment(Pos.CENTER);
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("STOCK", "STARTUP", "REAL_ESTATE");
        typeBox.getSelectionModel().selectFirst();
        
        TextField amountField = new TextField();
        amountField.setPromptText("Amount to Invest");
        
        Button investBtn = new Button("EXECUTE TRADE");
        investBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold;");
        investBtn.setOnAction(e -> {
            try {
                Map<String, Object> reqData = new HashMap<>();
                reqData.put("type", typeBox.getValue());
                reqData.put("name", typeBox.getValue() + " Index");
                reqData.put("amount", Double.parseDouble(amountField.getText()));

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(API_URL + "/invest"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + SessionManager.getToken())
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(reqData)))
                        .build();
                        
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    new Alert(Alert.AlertType.INFORMATION, "Trade Executed successfully!").show();
                    loadPortfolio(onInvestSuccess); // Reload the UI
                    if (onInvestSuccess != null) onInvestSuccess.run();
                } else {
                    new Alert(Alert.AlertType.ERROR, response.body()).show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid input").show();
            }
        });
        
        investBox.getChildren().addAll(typeBox, amountField, investBtn);

        Button close = new Button("CLOSE TERMINAL");
        close.setStyle("-fx-background-color: #da3633; -fx-text-fill: white; -fx-font-weight: bold;");
        close.setOnAction(e -> stage.close());

        portfolioBox = new VBox(10);
        portfolioBox.setAlignment(Pos.CENTER);
        
        ScrollPane scroll = new ScrollPane(portfolioBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.getChildren().addAll(title, stateLbl, new Separator(), new Label("Make an Investment:"), investBox, new Separator(), new Label("Your Portfolio:"), scroll, close);
        
        loadPortfolio(onInvestSuccess);
        
        stage.setScene(new Scene(root, 650, 600));
        stage.show();
    }

    private void loadPortfolio(Runnable onInvestSuccess) {
        portfolioBox.getChildren().clear();
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/portfolio"))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .GET()
                    .build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                JsonNode items = mapper.readTree(res.body());
                if (items.isEmpty()) {
                    Label empty = new Label("No active investments.");
                    empty.setStyle("-fx-text-fill: #8b949e;");
                    portfolioBox.getChildren().add(empty);
                    return;
                }
                for (JsonNode item : items) {
                    String id = item.get("id").asText();
                    String name = item.get("name").asText();
                    double initialAmt = item.get("initialAmount").asDouble();
                    double currentAmt = item.get("currentAmount").asDouble();

                    HBox row = new HBox(15);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-background-color: #161b22; -fx-padding: 10; -fx-border-color: #30363d; -fx-border-radius: 5;");

                    Label nameLbl = new Label("📈 " + name);
                    nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    nameLbl.setPrefWidth(150);

                    Label buyLbl = new Label("Bought: $" + String.format("%.2f", initialAmt));
                    buyLbl.setStyle("-fx-text-fill: #8b949e;");
                    buyLbl.setPrefWidth(120);

                    String color = currentAmt >= initialAmt ? "#3fb950" : "#f85149";
                    Label curLbl = new Label("Value: $" + String.format("%.2f", currentAmt));
                    curLbl.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                    curLbl.setPrefWidth(120);

                    Button sellBtn = new Button("SELL");
                    sellBtn.setStyle("-fx-background-color: #da3633; -fx-text-fill: white;");
                    sellBtn.setOnAction(e -> sellInvestment(id, onInvestSuccess));

                    Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
                    row.getChildren().addAll(nameLbl, buyLbl, curLbl, spacer, sellBtn);
                    portfolioBox.getChildren().add(row);
                }
            }
        } catch (Exception e) {}
    }

    private void sellInvestment(String id, Runnable onInvestSuccess) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/sell/" + id))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                new Alert(Alert.AlertType.INFORMATION, "Investment Sold!").show();
                loadPortfolio(onInvestSuccess); // Refresh the list
                if (onInvestSuccess != null) onInvestSuccess.run(); // Update dashboard money
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to sell: " + response.body()).show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Network error selling investment.").show();
        }
    }
}
