package com.lifeload.client.ui;

import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class MiniGameScreen {

    private int score = 0;
    private int currentQuestion = 0;
    private Label scoreLbl;
    private Runnable onComplete;

    // ─── BUDGET QUIZ DATA ──────────────────────────────────────────────────────
    private static final String[][] QUIZ_QUESTIONS = {
        {"You earn $3000/month. Rent costs $1200. What % of income is rent?", "40%", "35%", "50%", "30%", "0"},
        {"Which investment type has the highest risk and reward?", "Stocks", "Savings Account", "Bonds", "Real Estate", "0"},
        {"Compound interest means...", "Earning interest on interest", "Flat rate interest", "Tax-free savings", "Government bonds", "0"},
        {"Emergency fund should cover how many months of expenses?", "3-6 months", "1 month", "12 months", "No need", "0"},
        {"Diversification in investing means...", "Spread across asset types", "Put all in one stock", "Only buy gold", "Avoid stocks", "0"},
        {"What is a bull market?", "Rising stock prices", "Falling stock prices", "Stable prices", "High inflation", "0"},
        {"Inflation of 5% means $100 today is worth __ in 1 year?", "$95 in buying power", "$105 more", "$100 same", "$90", "0"},
    };

    public void showBudgetQuiz(Stage owner, Runnable onComplete) {
        this.onComplete = onComplete;
        this.score = 0;
        this.currentQuestion = 0;

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("💡 Budget Mastery Quiz");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d1117;");

        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");
        Label title = new Label("💡 FINANCIAL LITERACY CHALLENGE");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #d29922;");
        scoreLbl = new Label("Score: 0/" + QUIZ_QUESTIONS.length);
        scoreLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, scoreLbl);

        VBox questionArea = new VBox(20);
        questionArea.setPadding(new Insets(40, 40, 40, 40));

        ScrollPane sp = new ScrollPane(questionArea);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117; -fx-border-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        root.getChildren().addAll(header, sp);

        showQuestion(stage, questionArea, 0);

        stage.setScene(new Scene(root, 720, 560));
        stage.show();

        Platform.runLater(() -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Quiz Instructions");
            instruction.setHeaderText("Test Your Financial Literacy");
            instruction.setContentText("Answer the following 7 questions. A perfect score grants a $500 reward and +10 Knowledge. Good luck!");
            instruction.showAndWait();
        });
    }

    private void showQuestion(Stage stage, VBox area, int index) {
        area.getChildren().clear();
        if (index >= QUIZ_QUESTIONS.length) {
            showQuizResult(stage, area);
            return;
        }

        String[] q = QUIZ_QUESTIONS[index];
        Label questionNum = new Label("Question " + (index + 1) + " of " + QUIZ_QUESTIONS.length);
        questionNum.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");

        ProgressBar progress = new ProgressBar((double) index / QUIZ_QUESTIONS.length);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setStyle("-fx-accent: #d29922;");

        Label questionLbl = new Label(q[0]);
        questionLbl.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 18px; -fx-font-weight: bold;");
        questionLbl.setWrapText(true);

        VBox optionsBox = new VBox(10);
        String[] options = {q[1], q[2], q[3], q[4]};
        int correctIdx = Integer.parseInt(q[5]);

        for (int i = 0; i < options.length; i++) {
            final int optIdx = i;
            Button btn = new Button((char)('A' + i) + ".  " + options[i]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #cdd9e5; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #30363d; -fx-alignment: CENTER_LEFT;");
            btn.setOnAction(e -> {
                // Disable all buttons
                optionsBox.getChildren().forEach(c -> c.setDisable(true));
                boolean correct = (optIdx == correctIdx);
                if (correct) {
                    btn.setStyle("-fx-background-color: #1a4731; -fx-text-fill: #3fb950; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #3fb950; -fx-alignment: CENTER_LEFT;");
                    score++;
                } else {
                    btn.setStyle("-fx-background-color: #4d1f1f; -fx-text-fill: #f85149; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #f85149; -fx-alignment: CENTER_LEFT;");
                    // Highlight correct
                    Button correctBtn = (Button) optionsBox.getChildren().get(correctIdx);
                    correctBtn.setStyle("-fx-background-color: #1a4731; -fx-text-fill: #3fb950; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #3fb950; -fx-alignment: CENTER_LEFT;");
                }
                scoreLbl.setText("Score: " + score + "/" + QUIZ_QUESTIONS.length);
                // Auto-advance after 1.5s
                Timeline t = new Timeline(new KeyFrame(Duration.millis(1500), ev -> {
                    currentQuestion++;
                    showQuestion(stage, area, currentQuestion);
                }));
                t.play();
            });
            optionsBox.getChildren().add(btn);
        }

        area.getChildren().addAll(questionNum, progress, questionLbl, optionsBox);
    }

    private void showQuizResult(Stage stage, VBox area) {
        area.getChildren().clear();
        String[] grades = {"F", "F", "D", "C", "B", "B+", "A", "A+"};
        String grade = grades[Math.min(score, grades.length - 1)];
        double reward = score * 500.0;

        Label resultTitle = new Label(score >= 5 ? "🏆 EXCELLENT!" : score >= 3 ? "👍 GOOD EFFORT!" : "📚 KEEP LEARNING!");
        resultTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        resultTitle.setStyle("-fx-text-fill: #d29922;");

        Label scoreDisplay = new Label(score + " / " + QUIZ_QUESTIONS.length + "  Grade: " + grade);
        scoreDisplay.setFont(Font.font("Consolas", 22));
        scoreDisplay.setStyle("-fx-text-fill: #cdd9e5;");

        Label rewardLbl = new Label("💰 Reward Earned: $" + (int)reward);
        rewardLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button closeBtn = new Button("COLLECT REWARD");
        closeBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 14 40;");
        closeBtn.setOnAction(e -> {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "BUDGET_QUIZ");
            req.put("score", score);
            com.lifeload.client.service.GameService.playMiniGame(req);
            stage.close();
            if (onComplete != null) onComplete.run();
        });

        area.setAlignment(Pos.CENTER);
        area.setSpacing(20);
        area.getChildren().addAll(resultTitle, scoreDisplay, rewardLbl, closeBtn);
    }

    // ─── INVESTMENT SIMULATOR ──────────────────────────────────────────────────
    public void showInvestmentSimulator(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("📈 Investment Risk Simulator");

        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #0d1117;");
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("📈 INVESTMENT RISK SIMULATOR");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #58a6ff;");

        Label subtitle = new Label("Choose your risk level. The market is unpredictable...");
        subtitle.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 15px;");

        Label amountLabel = new Label("Investment Amount: $1,000");
        amountLabel.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 16px;");

        Slider riskSlider = new Slider(0, 100, 50);
        riskSlider.setShowTickLabels(true);
        riskSlider.setShowTickMarks(true);
        riskSlider.setMajorTickUnit(25);
        riskSlider.setStyle("-fx-control-inner-background: #21262d;");

        Label[] riskLabels = {new Label("LOW RISK"), new Label("BALANCED"), new Label("HIGH RISK")};
        HBox riskRow = new HBox();
        riskRow.setAlignment(Pos.CENTER);
        Region r1 = new Region(); HBox.setHgrow(r1, Priority.ALWAYS);
        Region r2 = new Region(); HBox.setHgrow(r2, Priority.ALWAYS);
        for (Label l : riskLabels) { l.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12px;"); }
        riskRow.getChildren().addAll(riskLabels[0], r1, riskLabels[1], r2, riskLabels[2]);

        Label riskDesc = new Label("Balanced: moderate risk, moderate return");
        riskDesc.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px;");
        riskSlider.valueProperty().addListener((obs, old, val) -> {
            double r = val.doubleValue();
            if (r < 30)       riskDesc.setText("Conservative: low risk, ~5-10% return");
            else if (r < 70)  riskDesc.setText("Balanced: moderate risk, ~10-30% return");
            else               riskDesc.setText("⚠️ Aggressive: high risk — can lose everything or 3x returns!");
        });

        Button spinBtn = new Button("🎲  EXECUTE INVESTMENT");
        spinBtn.setStyle("-fx-background-color: #1f6feb; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 14 40;");

        Label resultLbl = new Label("");
        resultLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        resultLbl.setWrapText(true);
        resultLbl.setTextAlignment(TextAlignment.CENTER);

        spinBtn.setOnAction(e -> {
            double riskVal = riskSlider.getValue();
            String riskLevel = riskVal > 70 ? "HIGH" : "LOW";

            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "INVESTMENT");
            req.put("risk", riskLevel);
            req.put("amount", 1000.0);

            com.fasterxml.jackson.databind.JsonNode res = com.lifeload.client.service.GameService.playMiniGame(req);
            
            if (res != null) {
                if (res.has("message")) {
                    resultLbl.setText(res.get("message").asText());
                    if (res.get("message").asText().toLowerCase().contains("lost") || res.get("message").asText().toLowerCase().contains("crashed")) {
                        resultLbl.setStyle("-fx-text-fill: #f85149; -fx-font-size: 18px; -fx-font-weight: bold;");
                    } else {
                        resultLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 18px; -fx-font-weight: bold;");
                    }
                } else {
                    resultLbl.setText("Investment executed. Check your stats.");
                    resultLbl.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 18px; -fx-font-weight: bold;");
                }
            } else {
                resultLbl.setText("Investment failed. Check money/connection.");
                resultLbl.setStyle("-fx-text-fill: #f85149; -fx-font-size: 18px; -fx-font-weight: bold;");
            }

            spinBtn.setDisable(true); // only once per open
            
            if (onComplete != null) {
                Button closeBtn = new Button("CLOSE");
                closeBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #cdd9e5; -fx-font-weight: bold; -fx-padding: 10 30;");
                closeBtn.setOnAction(ev -> { stage.close(); onComplete.run(); });
                if (!root.getChildren().contains(closeBtn)) root.getChildren().add(closeBtn);
            }
        });

        root.getChildren().addAll(title, subtitle, amountLabel, riskSlider, riskRow, riskDesc, spinBtn, resultLbl);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();

        Platform.runLater(() -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Simulator Instructions");
            instruction.setHeaderText("Risk vs. Reward");
            instruction.setContentText("Allocate $1,000 into the market. Use the slider to set your risk tolerance.\n\n" +
                    "• Low Risk: Safe, small steady returns.\n" +
                    "• High Risk: Volatile. You might triple your money, or lose it all!");
            instruction.showAndWait();
        });
    }
}
