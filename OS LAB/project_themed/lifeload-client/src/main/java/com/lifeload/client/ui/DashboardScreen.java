package com.lifeload.client.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeload.client.service.GameService;
import com.lifeload.client.service.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardScreen {

    private Stage stage;
    private JsonNode gameData;
    private VBox statsPanel;
    private FlowPane actionPanel;
    private VBox rivalPanel;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public DashboardScreen(Stage stage, JsonNode initialData) {
        this.stage = stage;
        this.gameData = initialData;
        this.statsPanel = new VBox(12);
        this.actionPanel = new FlowPane(20, 20);
        this.rivalPanel = new VBox(8);
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f7f6;");
        root.setPadding(new Insets(18));

        // ── TOP BAR ───────────────────────────────────────────────────────────
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        Label title = new Label("LifeLoad Control Center");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button timelineBtn = createTopBtn("📅 Timeline", "#ffffff", "#3498db");
        timelineBtn.setOnAction(e -> new TimelineScreen().show(stage));

        Button miniGameBtn = createTopBtn("🎮 Mini-Games", "#ffffff", "#f39c12");
        miniGameBtn.setOnAction(e -> showMiniGameMenu());

        Button rewardBtn = createTopBtn("🎁 Daily Reward", "#ffffff", "#2ecc71");
        rewardBtn.setOnAction(e -> claimDailyReward());

        Button marketBtn = createTopBtn("📊 Market", "#ffffff", "#9b59b6");
        marketBtn.setOnAction(e -> new MarketScreen().show(stage, () -> updateUI(GameService.loadGame())));

        Button lbBtn = createTopBtn("🏆 Leaderboard", "#ffffff", "#f1c40f");
        lbBtn.setOnAction(e -> new LeaderboardScreen().show(stage));

        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-border-color: #e74c3c; -fx-border-radius: 6; -fx-font-weight: bold; -fx-padding: 7 14; -fx-cursor: hand;");
        exitBtn.setOnAction(e -> Platform.exit());

        topBar.getChildren().addAll(title, spacer, timelineBtn, miniGameBtn, rewardBtn, marketBtn, lbBtn, exitBtn);
        root.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 16, 0));

        // ── LEFT PANEL: STATS ─────────────────────────────────────────────────
        statsPanel.setPrefWidth(320);
        statsPanel.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        ScrollPane statsScroll = new ScrollPane(statsPanel);
        statsScroll.setFitToWidth(true);
        statsScroll.setPrefWidth(340);
        statsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        root.setLeft(statsScroll);

        // ── CENTER PANEL: ACTIONS ─────────────────────────────────────────────
        actionPanel.setPadding(new Insets(16));
        actionPanel.setAlignment(Pos.TOP_LEFT);
        ScrollPane actionScroll = new ScrollPane(actionPanel);
        actionScroll.setFitToWidth(true);
        actionScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        root.setCenter(actionScroll);

        // ── RIGHT PANEL: RIVALS ───────────────────────────────────────────────
        rivalPanel.setPrefWidth(240);
        rivalPanel.setPadding(new Insets(16));
        rivalPanel.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        Label rivalTitle = new Label("⚔️ Rivals");
        rivalTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        rivalTitle.setStyle("-fx-text-fill: #34495e;");
        rivalPanel.getChildren().add(rivalTitle);
        rivalPanel.getChildren().add(new Separator());

        ScrollPane rivalScroll = new ScrollPane(rivalPanel);
        rivalScroll.setPrefWidth(255);
        rivalScroll.setFitToWidth(true);
        rivalScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        root.setRight(rivalScroll);

        updateUI(gameData);
        loadRivals();

        return new Scene(root, 1280, 820);
    }

    // ── DAILY REWARD ──────────────────────────────────────────────────────────
    private void claimDailyReward() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/game/daily-reward"))
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode r = mapper.readTree(resp.body());

            String msg = "Streak: " + r.get("streak").asInt() + " day(s)\n";
            if (r.get("claimed").asBoolean()) {
                msg += "✅ " + r.get("bonus").asText();
                new Alert(Alert.AlertType.INFORMATION, "🎁 DAILY REWARD CLAIMED!\n\n" + msg).show();
                updateUI(GameService.loadGame());
            } else {
                msg += r.get("message").asText();
                new Alert(Alert.AlertType.INFORMATION, msg).show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not claim reward: " + e.getMessage()).show();
        }
    }

    // ── MINI-GAME MENU ────────────────────────────────────────────────────────
    private void showMiniGameMenu() {
        Alert menu = new Alert(Alert.AlertType.CONFIRMATION);
        menu.setTitle("Mini-Games");
        menu.setHeaderText("🎮  Choose a Mini-Game");
        menu.setContentText("Select which mini-game to play:");
        ButtonType quizBtn = new ButtonType("💡 Budget Quiz");
        ButtonType focusBtn = new ButtonType("⚡ Deep Work Focus");
        ButtonType memoryBtn = new ButtonType("🧠 Memory Matrix");
        ButtonType typeBtn = new ButtonType("⌨️ Typing Hustle");
        ButtonType crisisBtn = new ButtonType("🚨 Crisis Management");
        ButtonType ladderBtn = new ButtonType("👔 Corporate Ladder");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        menu.getButtonTypes().setAll(quizBtn, focusBtn, memoryBtn, typeBtn, crisisBtn, ladderBtn, cancelBtn);
        menu.showAndWait().ifPresent(choice -> {
            MiniGameScreen mg = new MiniGameScreen();
            if (choice == quizBtn) {
                mg.showBudgetQuiz(stage, () -> updateUI(GameService.loadGame()));
            } else if (choice == focusBtn) {
                mg.showProductivityFlow(stage, () -> updateUI(GameService.loadGame()));
            } else if (choice == memoryBtn) {
                mg.showMemoryMatrix(stage, () -> updateUI(GameService.loadGame()));
            } else if (choice == typeBtn) {
                mg.showTypingHustle(stage, () -> updateUI(GameService.loadGame()));
            } else if (choice == crisisBtn) {
                mg.showCrisisManagement(stage, () -> updateUI(GameService.loadGame()));
            } else if (choice == ladderBtn) {
                mg.showCorporateLadder(stage, () -> updateUI(GameService.loadGame()));
            }
        });
    }

    // ── LOAD RIVALS ───────────────────────────────────────────────────────────
    private void loadRivals() {
        new Thread(() -> {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8081/api/game/rivals"))
                        .header("Authorization", "Bearer " + SessionManager.getToken())
                        .GET().build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) {
                    JsonNode rivals = mapper.readTree(resp.body());
                    Platform.runLater(() -> buildRivalPanel(rivals));
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void buildRivalPanel(JsonNode rivals) {
        // Keep the title + separator, clear the rest
        while (rivalPanel.getChildren().size() > 2) {
            rivalPanel.getChildren().remove(2);
        }
        if (rivals.isEmpty()) {
            Label noRivals = new Label("No rivals yet.\nStart a game!");
            noRivals.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");
            rivalPanel.getChildren().add(noRivals);
            return;
        }
        for (JsonNode rival : rivals) {
            String type = rival.get("type").asText();
            String icon = switch (type) {
                case "STARTUP_FOUNDER" -> "🚀";
                case "INVESTOR"        -> "📊";
                default                -> "👔";
            };
            VBox card = new VBox(5);
            card.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 10; -fx-border-color: #ff3366; -fx-border-width: 0 0 2 0;");
            Label name = new Label(icon + " " + rival.get("name").asText());
            name.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
            Label typeLbl = new Label(type.replace("_", " "));
            typeLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 11px;");
            Label wealthLbl = new Label("💰 $" + String.format("%,.0f", rival.get("wealth").asDouble()));
            wealthLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 13px;");
            Label lvlLbl = new Label("Level " + rival.get("level").asInt());
            lvlLbl.setStyle("-fx-text-fill: #d29922; -fx-font-size: 12px;");
            card.getChildren().addAll(name, typeLbl, wealthLbl, lvlLbl);
            rivalPanel.getChildren().add(card);
        }
    }

    // ── UPDATE UI FROM GAME STATE ─────────────────────────────────────────────
    public void updateUI(JsonNode data) {
        if (data == null || !data.has("profile")) return;
        this.gameData = data;

        JsonNode profile = data.get("profile");
        JsonNode stats = data.get("stats");

        String status = profile.get("status").asText();
        if ("FAILED".equals(status) || "WON".equals(status)) {
            showEndingScreen(profile, stats, status);
            return;
        }

        buildStatsPanel(profile, stats);
        buildActionPanel();

        // Check for pending events that need a decision
        if (data.has("pendingEvent")) {
            showEventPopup(data.get("pendingEvent"));
        }
    }

    private void showEventPopup(JsonNode event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Random Event: " + event.get("title").asText());
            alert.setHeaderText(event.get("title").asText());
            alert.setContentText(event.get("description").asText());

            List<ButtonType> buttons = new ArrayList<>();
            JsonNode options = event.get("options");
            for (int i = 0; i < options.size(); i++) {
                buttons.add(new ButtonType(options.get(i).get("label").asText()));
            }

            alert.getButtonTypes().setAll(buttons);
            alert.showAndWait().ifPresent(type -> {
                for (int i = 0; i < buttons.size(); i++) {
                    if (type == buttons.get(i)) {
                        handleEventChoice(event.get("id").asLong(), i);
                        break;
                    }
                }
            });
        });
    }

    private void handleEventChoice(Long eventId, int optionIndex) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("eventId", eventId);
            req.put("optionIndex", optionIndex);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/game/event/choice"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                updateUI(mapper.readTree(resp.body()));
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to process choice: " + resp.body()).show();
                updateUI(GameService.loadGame());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEndingScreen(JsonNode profile, JsonNode stats, String status) {
        actionPanel.getChildren().clear();
        statsPanel.getChildren().clear();

        boolean won = "WON".equals(status);
        String bgColor = won ? "#0d2b17" : "#2d0a0a";
        String accentColor = won ? "#3fb950" : "#f85149";
        String icon = won ? "🏆" : "💀";

        VBox endScreen = new VBox(20);
        endScreen.setAlignment(Pos.CENTER);
        endScreen.setPadding(new Insets(60));
        endScreen.setStyle("-fx-background-color: " + bgColor + ";");

        Label endIcon = new Label(icon);
        endIcon.setFont(Font.font(80));

        Label endTitle = new Label(won ? "LIFE COMPLETE!" : "GAME OVER");
        endTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 42));
        endTitle.setStyle("-fx-text-fill: " + accentColor + ";");

        String reason = profile.has("endReason") && !profile.get("endReason").isNull()
                ? profile.get("endReason").asText() : "";
        Label reasonLbl = new Label(reason);
        reasonLbl.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 18px;");
        reasonLbl.setWrapText(true);

        Label ageLbl = new Label("Age Reached: " + profile.get("age").asInt());
        ageLbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 16px;");

        VBox finalStats = new VBox(8);
        finalStats.setAlignment(Pos.CENTER);
        finalStats.setStyle("-fx-background-color: #161b22; -fx-padding: 20; -fx-border-color: " + accentColor + "; -fx-border-radius: 6;");
        finalStats.getChildren().addAll(
            statSummaryLine("💰 Final Wealth", "$" + String.format("%,.0f", stats.get("money").asDouble()), "#3fb950"),
            statSummaryLine("❤️ Health", stats.get("health").asInt() + "/100", "#58a6ff"),
            statSummaryLine("😊 Happiness", stats.get("happiness").asInt() + "/100", "#d29922"),
            statSummaryLine("🤝 Relationships", stats.get("relationships").asInt() + "/100", "#f78166"),
            statSummaryLine("📚 Knowledge", stats.get("knowledge").asInt() + " pts", "#a855f7")
        );

        Button timelineBtn2 = new Button("📅 View Your Life Timeline");
        timelineBtn2.setStyle("-fx-background-color: #21262d; -fx-text-fill: #58a6ff; -fx-font-size: 15px; -fx-padding: 12 30;");
        timelineBtn2.setOnAction(e -> new TimelineScreen().show(stage));

        Button newGameBtn = new Button("▶  Start a New Life");
        newGameBtn.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: #000; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 14 40;");
        newGameBtn.setOnAction(e -> stage.setScene(new CharacterCreationScreen(stage).createScene()));

        HBox buttons = new HBox(20, timelineBtn2, newGameBtn);
        buttons.setAlignment(Pos.CENTER);

        endScreen.getChildren().addAll(endIcon, endTitle, reasonLbl, ageLbl, new Separator(), finalStats, buttons);
        actionPanel.getChildren().add(endScreen);
    }

    private Label statSummaryLine(String label, String value, String color) {
        Label lbl = new Label(label + ":  " + value);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px;");
        return lbl;
    }

    // ── STATS PANEL ───────────────────────────────────────────────────────────
    private void buildStatsPanel(JsonNode profile, JsonNode stats) {
        statsPanel.getChildren().clear();

        Label nameLbl = new Label(profile.get("playerName").asText());
        nameLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label ageLbl = new Label("Age " + profile.get("age").asInt() + "  |  Week " + profile.get("currentWeek").asInt());
        ageLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label moneyLbl = new Label("💰 $" + String.format("%,.0f", stats.get("money").asDouble()));
        moneyLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        statsPanel.getChildren().addAll(nameLbl, ageLbl, new Separator(), moneyLbl, new Separator());
        statsPanel.getChildren().add(createStatBar("❤️ Health",     stats.get("health").asDouble(),     "#2ecc71"));
        statsPanel.getChildren().add(createStatBar("⚡ Energy",      stats.get("energy").asDouble(),     "#f1c40f"));
        statsPanel.getChildren().add(createStatBar("😊 Happiness",   stats.get("happiness").asDouble(),  "#3498db"));
        statsPanel.getChildren().add(createStatBar("🔥 Stress",      stats.get("stress").asDouble(),     "#e74c3c"));
        statsPanel.getChildren().add(new Separator());
        statsPanel.getChildren().add(createMinimalStat("📚 Knowledge",    stats.get("knowledge").asInt()));
        statsPanel.getChildren().add(createMinimalStat("🤝 Relationships", stats.get("relationships").asInt()));
        statsPanel.getChildren().add(createMinimalStat("⭐ Reputation",    stats.get("reputation").asInt()));
        statsPanel.getChildren().add(createMinimalStat("💪 Confidence",    stats.get("confidence").asInt()));
        statsPanel.getChildren().add(createMinimalStat("🚀 Motivation",    stats.get("motivation").asInt()));
    }

    private VBox createStatBar(String label, double val, String color) {
        VBox box = new VBox(6);
        Label lbl = new Label(label + "  [" + (int)val + "]");
        lbl.setStyle("-fx-text-fill: #34495e; -fx-font-size: 14px; -fx-font-weight: bold;");
        ProgressBar pb = new ProgressBar(val / 100.0);
        pb.setPrefWidth(290);
        pb.setPrefHeight(12);
        pb.setStyle("-fx-accent: " + color + "; -fx-control-inner-background: #ecf0f1; -fx-background-radius: 6;");
        box.getChildren().addAll(lbl, pb);
        return box;
    }

    private Label createMinimalStat(String label, int val) {
        Label lbl = new Label(label + ":  " + val);
        lbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-font-weight: bold;");
        return lbl;
    }

    // ── ACTION PANEL ──────────────────────────────────────────────────────────
    private static boolean instructionsShown = false;

    private void buildActionPanel() {
        actionPanel.getChildren().clear();
        actionPanel.getChildren().addAll(
            createActionCard("💼 Go to Work",         "Cost: 20 Energy, ~15 Stress.\nGain: Money (based on Know), +1 Rep.", "work", "#00ffcc"),
            createActionCard("📚 Read & Study",       "Cost: $50, 15 Energy, ~5 Stress.\nGain: +10 Knowledge, +3 Motivation.", "study", "#a855f7"),
            createActionCard("🏋️ Go to Gym",          "Cost: $20, 10 Energy.\nGain: +15 Health, -15 Stress, +5 Conf.", "gym", "#ffcc00"),
            createActionCard("🤝 Hang out with Friends","Cost: $100, 15 Energy.\nGain: +10 Rel, +20 Happy, -10 Stress.", "socialize", "#ff3366"),
            createActionCard("😴 Sleep & Rest",       "Cost: Time.\nGain: +40 Energy, -20 Stress, +5 Health/Happy.", "rest", "#3498db"),
            createActionCard("🧘 Meditate & Relax",   "Cost: Time.\nGain: -30 Stress, +15 Energy, +10 Happy/Motiv.", "meditate", "#58a6ff"),
            createActionCard("🌐 Networking Event",   "Cost: $150.\nGain: +8 Rep, +5 Rel, +5 Motivation.", "network", "#d29922"),
            createActionCard("💻 Freelance Project",  "Cost: 25 Energy, ~10 Stress.\nGain: High Pay (based on Know), +2 Know.", "freelance", "#3fb950")
        );

        if (gameData != null && gameData.has("stats") && gameData.get("stats").get("money").asDouble() < 100) {
            actionPanel.getChildren().addAll(
                createActionCard("🧹 Odd Jobs", "Takes 20 energy, 10 stress. Earns $250 (~$100 net).", "odd_jobs", "#ffaa00"),
                createActionCard("🔥 Desperate Hustle", "Takes 40 energy, 30 stress. Earns $450 (~$300 net).", "hustle", "#ff3333")
            );
        }

        if (!instructionsShown) {
            instructionsShown = true;
            Platform.runLater(() -> {
                Alert instructionAlert = new Alert(Alert.AlertType.INFORMATION);
                instructionAlert.setTitle("Welcome to LifeLoad!");
                instructionAlert.setHeaderText("How to Play");
                instructionAlert.setContentText("Manage your core stats by picking Actions. " +
                        "Every action requires Energy and impacts your other attributes.\n\n" +
                        "⚠️ Watch your Money and Stress! Falling into debt or maxing out your stress will severely penalize your health.\n" +
                        "Use the Top Bar to visit the Market, play Mini Games, or claim Daily Rewards.");
                instructionAlert.showAndWait();
            });
        }
    }

    private VBox createActionCard(String titleText, String desc, String actionId, String accentColor) {
        VBox card = new VBox(12);
        card.setPrefSize(235, 175);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2); -fx-padding: 16;");

        Label titleLbl = new Label(titleText);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        titleLbl.setWrapText(true);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        descLbl.setWrapText(true);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btn = new Button("Execute Action");
        btn.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 0;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            JsonNode result = GameService.performAction(actionId);
            if (result != null) {
                if (result.isTextual()) {
                    new Alert(Alert.AlertType.WARNING, result.asText()).show();
                    // Always refresh UI after a warning to show updated stats (like Stress from Insufficient Funds)
                    updateUI(GameService.loadGame());
                } else {
                    updateUI(result);
                    loadRivals(); // Refresh rivals after each action
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Action failed. Check energy/money.").show();
                updateUI(GameService.loadGame());
            }
        });

        card.getChildren().addAll(titleLbl, descLbl, spacer, btn);
        return card;
    }

    private Button createTopBtn(String text, String textColor, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-font-size: 13px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        return btn;
    }
}
