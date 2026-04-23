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
    private String[][] sessionQuestions;

    // ─── BUDGET QUIZ ─────────────────────────────────────────────────────────
    public void showBudgetQuiz(Stage owner, Runnable onComplete) {
        this.onComplete = onComplete;
        this.score = 0;
        this.currentQuestion = 0;

        List<String[]> pool = new java.util.ArrayList<>(java.util.Arrays.asList(QuizData.ALL_QUESTIONS));
        java.util.Collections.shuffle(pool);
        sessionQuestions = pool.subList(0, 10).toArray(new String[0][0]);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("💡 Budget Mastery Quiz");

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #0d1117;");

        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");
        Label title = new Label("💡 FINANCIAL LITERACY CHALLENGE");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #d29922;");
        scoreLbl = new Label("Score: 0/" + sessionQuestions.length);
        scoreLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, scoreLbl);

        VBox questionArea = new VBox(20);
        questionArea.setPadding(new Insets(40, 40, 40, 40));
        ScrollPane sp = new ScrollPane(questionArea);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117; -fx-border-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        // Show a welcome/start screen inside the question area
        VBox welcomeBox = new VBox(20);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(60, 40, 40, 40));
        Label welcomeTitle = new Label("💡 FINANCIAL LITERACY CHALLENGE");
        welcomeTitle.setStyle("-fx-text-fill: #d29922; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label welcomeDesc = new Label("Test your financial knowledge across 10 questions.\nEarn money and knowledge for correct answers!");
        welcomeDesc.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 15px;");
        welcomeDesc.setWrapText(true);
        welcomeDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        Button startQuizBtn = new Button("▶  START QUIZ");
        startQuizBtn.setStyle("-fx-background-color: #d29922; -fx-text-fill: #0d1117; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 40; -fx-background-radius: 6; -fx-cursor: hand;");
        startQuizBtn.setOnAction(ev -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Quiz Instructions");
            instruction.setHeaderText("Test Your Financial Literacy");
            instruction.setContentText("Answer 10 randomized questions.\n\n" +
                    "✅ Reward: Earn $100 and +Knowledge per correct answer.\n" +
                    "❌ Risk: Each wrong answer reduces your final payout and costs you -$50!");
            instruction.showAndWait();
            showQuestion(stage, questionArea, 0);
        });
        welcomeBox.getChildren().addAll(welcomeTitle, welcomeDesc, startQuizBtn);
        questionArea.getChildren().add(welcomeBox);

        root.getChildren().addAll(header, sp);
        stage.setScene(new Scene(root, 720, 560));
        stage.show();
    }

    private void showQuestion(Stage stage, VBox area, int index) {
        area.getChildren().clear();
        if (index >= sessionQuestions.length) {
            showQuizResult(stage, area);
            return;
        }

        String[] q = sessionQuestions[index];
        Label questionNum = new Label("Question " + (index + 1) + " of " + sessionQuestions.length);
        questionNum.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 13px;");

        ProgressBar progress = new ProgressBar((double) index / sessionQuestions.length);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setStyle("-fx-accent: #d29922;");

        Label questionLbl = new Label(q[0]);
        questionLbl.setStyle("-fx-text-fill: #cdd9e5; -fx-font-size: 18px; -fx-font-weight: bold;");
        questionLbl.setWrapText(true);

        VBox optionsBox = new VBox(10);
        class Option {
            String text; boolean isCorrect;
            Option(String t, boolean c) { this.text = t; this.isCorrect = c; }
        }
        List<Option> opts = new java.util.ArrayList<>();
        opts.add(new Option(q[1], true));
        opts.add(new Option(q[2], false));
        opts.add(new Option(q[3], false));
        opts.add(new Option(q[4], false));
        java.util.Collections.shuffle(opts);

        for (int i = 0; i < opts.size(); i++) {
            Option opt = opts.get(i);
            Button btn = new Button((char)('A' + i) + ".  " + opt.text);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #cdd9e5; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #30363d; -fx-alignment: CENTER_LEFT;");
            
            btn.setOnAction(e -> {
                optionsBox.getChildren().forEach(c -> c.setDisable(true));
                if (opt.isCorrect) {
                    btn.setStyle("-fx-background-color: #1a4731; -fx-text-fill: #3fb950; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #3fb950; -fx-alignment: CENTER_LEFT;");
                    score++;
                } else {
                    btn.setStyle("-fx-background-color: #4d1f1f; -fx-text-fill: #f85149; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #f85149; -fx-alignment: CENTER_LEFT;");
                    score = Math.max(-5, score - 1); 
                    for (javafx.scene.Node node : optionsBox.getChildren()) {
                        Button b = (Button) node;
                        if (b.getText().contains(q[1])) {
                             b.setStyle("-fx-background-color: #1a4731; -fx-text-fill: #3fb950; -fx-font-size: 15px; -fx-padding: 14 20; -fx-border-color: #3fb950; -fx-alignment: CENTER_LEFT;");
                        }
                    }
                }
                scoreLbl.setText("Score: " + score + "/" + sessionQuestions.length);
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

    private void showMiniGameResult(Stage stage, String type, java.util.Map<String, Object> data, String titleText, String scoreDetail, Runnable onComplete) {
        VBox res = new VBox(20);
        res.setAlignment(Pos.CENTER);
        res.setStyle("-fx-background-color: #f4f7f6; -fx-padding: 40; -fx-border-color: #34495e; -fx-border-width: 2;");
        
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 28px; -fx-font-weight: bold;");
        
        Label detail = new Label(scoreDetail);
        detail.setStyle("-fx-text-fill: #34495e; -fx-font-size: 18px;");
        
        Button collect = new Button("Collect Rewards & Finish");
        collect.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 35; -fx-background-radius: 6; -fx-cursor: hand;");
        
        collect.setOnAction(e -> {
            collect.setDisable(true);
            java.util.Map<String, Object> req = new java.util.HashMap<>(data);
            req.put("type", type);
            
            new Thread(() -> {
                com.fasterxml.jackson.databind.JsonNode result = com.lifeload.client.service.GameService.playMiniGame(req);
                Platform.runLater(() -> {
                    if (result != null) {
                        stage.close();
                        if (onComplete != null) onComplete.run();
                    } else {
                        collect.setDisable(false);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Sync Error");
                        alert.setHeaderText("Server Connection Failed");
                        alert.setContentText("Could not sync your results. Please check your connection and try again.");
                        alert.showAndWait();
                    }
                });
            }).start();
        });
        
        res.getChildren().addAll(title, detail, collect);
        stage.getScene().setRoot(res);
    }

    private void showQuizResult(Stage stage, VBox area) {
        double reward = Math.max(0, score * 100.0);
        String title = score >= 8 ? "🏆 EXCELLENT!" : score >= 5 ? "👍 GOOD EFFORT!" : "📚 KEEP LEARNING!";
        String detail = "Score: " + score + " / " + sessionQuestions.length + "\nReward: $" + (int)reward;
        
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", score);
        data.put("mistakes", sessionQuestions.length - score);
        
        showMiniGameResult(stage, "BUDGET_QUIZ", data, title, detail, onComplete);
    }

    // ─── DEEP WORK FOCUS ─────────────────────────────────────────────────────
    public void showProductivityFlow(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("⚡ Deep Work Focus Challenge");

        Pane gamePane = new Pane();
        gamePane.setPrefSize(600, 400);
        gamePane.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 2;");

        VBox root = new VBox(15, new Label("⚡ FOCUS FLOW: CLICK THE TASKS!"), new Label("Avoid the Red Distractions. Green = +10, Red = -15"), gamePane);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #161b22;");
        for(javafx.scene.Node n : root.getChildren()) if(n instanceof Label) n.setStyle("-fx-text-fill: #cdd9e5; -fx-font-weight: bold;");

        Label timerLbl = new Label("Time: 20s");
        Label liveScoreLbl = new Label("Score: 0");
        HBox stats = new HBox(40, timerLbl, liveScoreLbl);
        stats.setAlignment(Pos.CENTER);
        root.getChildren().add(0, stats);

        final int[] timeSeconds = {20};
        final int[] gameScore = {0};
        final int[] mistakes = {0};
        
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);

        // Background penalty to prevent spam clicking
        gamePane.setOnMouseClicked(e -> {
            if (e.getTarget() == gamePane) {
                gameScore[0] = Math.max(0, gameScore[0] - 5);
                mistakes[0]++;
                liveScoreLbl.setText("Score: " + gameScore[0] + " [MISCLICK -5]");
                liveScoreLbl.setStyle("-fx-text-fill: #ffaa00;");
            }
        });
        
        KeyFrame spawnFrame = new KeyFrame(Duration.millis(800), e -> {
            Random rnd = new Random();
            // Higher chance of distractions (red) as time goes on
            double redChance = 0.3 + ( (20 - timeSeconds[0]) * 0.02 ); // Starts at 30%, ends at 70%
            boolean isTask = rnd.nextDouble() > redChance;
            
            double radius = 25;
            javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(radius);
            c.setFill(isTask ? javafx.scene.paint.Color.web("#3fb950") : javafx.scene.paint.Color.web("#f85149"));
            
            c.setCenterX(100 + rnd.nextInt(400));
            c.setCenterY(100 + rnd.nextInt(200));
            c.setCursor(javafx.scene.Cursor.HAND);
            
            // "Glitch" Mechanic: Green circles might turn red right before they disappear
            boolean willGlitch = isTask && rnd.nextDouble() < ( (20 - timeSeconds[0]) * 0.03 ); // Up to 60% glitch chance at end
            
            final boolean[] isCurrentlyTask = {isTask};

            c.setOnMouseClicked(ev -> {
                ev.consume(); // Prevent background click penalty
                int points = isCurrentlyTask[0] ? 10 : -30; // Harsher penalty for red
                if (!isCurrentlyTask[0]) mistakes[0]++;
                gameScore[0] += points;
                liveScoreLbl.setText("Score: " + gameScore[0]);
                liveScoreLbl.setStyle("-fx-text-fill: " + (points > 0 ? "#3fb950" : "#f85149") + ";");
                gamePane.getChildren().remove(c);
            });
            
            gamePane.getChildren().add(c);
            
            // Movement/Drift Mechanic: Circles drift in a random direction
            double dx = (rnd.nextDouble() - 0.5) * 2.5; 
            double dy = (rnd.nextDouble() - 0.5) * 2.5;
            
            Timeline drift = new Timeline(new KeyFrame(Duration.millis(20), ev -> {
                c.setCenterX(c.getCenterX() + dx);
                c.setCenterY(c.getCenterY() + dy);
            }));
            drift.setCycleCount(Timeline.INDEFINITE);
            drift.play();

            // Stabilized fade speed (not getting too fast)
            double fadeMs = 1500; 
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.millis(fadeMs), c);
            ft.setFromValue(1.0); ft.setToValue(0.0);
            
            if (willGlitch) {
                // Glitch to red at 70% of its lifetime
                Timeline glitchTimer = new Timeline(new KeyFrame(Duration.millis(fadeMs * 0.7), ev -> {
                    if (gamePane.getChildren().contains(c)) {
                        c.setFill(javafx.scene.paint.Color.web("#f85149")); // Turn red
                        isCurrentlyTask[0] = false;
                        c.setStroke(javafx.scene.paint.Color.WHITE); // Flash white to alert glitch
                        c.setStrokeWidth(3);
                    }
                }));
                glitchTimer.play();
            }

            ft.setOnFinished(ev -> {
                drift.stop();
                gamePane.getChildren().remove(c);
            });
            ft.play();
        });
        
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds[0]--;
            timerLbl.setText("Time: " + timeSeconds[0] + "s");
            
            // Speed up the spawn frequency (but not the fade speed)
            gameLoop.setRate(1.0 + (20 - timeSeconds[0]) * 0.1); 
            
            if (timeSeconds[0] <= 0) { 
                gameLoop.stop(); 
                showFocusResult(stage, gameScore[0], mistakes[0], onComplete); 
            }
        }));
        timer.setCycleCount(20);
        gameLoop.getKeyFrames().add(spawnFrame);
        stage.setScene(new Scene(root, 650, 550));
        stage.show();

        Platform.runLater(() -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Deep Work Instructions");
            instruction.setHeaderText("⚡ Train Your Focus");
            instruction.setContentText("A fast-paced clicking challenge!\n\n" +
                    "• Click GREEN circles (+10 pts) as they appear.\n" +
                    "• Avoid RED circles and misclicks.\n\n" +
                    "✅ Reward: Earn $10 per point and Motivation bonus.\n" +
                    "❌ Risk: Every mistake (Red Circle or background misclick) increases Stress and costs you Money!");
            instruction.showAndWait();
            
            // Start the game after user clicks OK
            gameLoop.play();
            timer.play();
        });
    }

    private void showFocusResult(Stage stage, int finalScore, int mistakes, Runnable onComplete) {
        String detail = "Final Focus Score: " + finalScore + " | Mistakes: " + mistakes;
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", Math.max(0, finalScore));
        data.put("mistakes", mistakes);
        
        showMiniGameResult(stage, "PRODUCTIVITY_FLOW", data, "⚡ SESSION COMPLETE", detail, onComplete);
    }

    // ─── MEMORY MATRIX ───────────────────────────────────────────────────────
    private GridPane matrixGrid;
    private Button[] matrixButtons;
    private int currentGridSize = 3;

    public void showMemoryMatrix(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("🧠 Memory Matrix");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0d1117; -fx-padding: 30;");

        Label title = new Label("🧠 EVOLVING MEMORY MATRIX");
        title.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label instruction = new Label("Grid grows every 5 levels. Speed scales slowly.");
        instruction.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 16px;");
        Label levelLbl = new Label("Level: 1");
        levelLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        matrixGrid = new GridPane();
        matrixGrid.setAlignment(Pos.CENTER);
        matrixGrid.setHgap(8); matrixGrid.setVgap(8);

        Button startBtn = new Button("START CHALLENGE");
        startBtn.setStyle("-fx-background-color: #a855f7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        
        List<Integer> sequence = new java.util.ArrayList<>();
        final int[] userStep = {0};
        final int[] level = {1};
        final boolean[] inputAllowed = {false};

        setupGrid(3);

        startBtn.setOnAction(e -> {
            // Show instructions first, then start when OK is clicked
            Alert instr = new Alert(Alert.AlertType.INFORMATION);
            instr.setTitle("Memory Matrix Instructions");
            instr.setHeaderText("🧠 Evolving Intelligence Test");
            instr.setContentText("Memorize the sequence of flashing tiles.\n\n" +
                    "• L1-5: 3x3 Grid\n" +
                    "• L6-10: 4x4 Grid\n" +
                    "• L11-15: 5x5 Grid\n\n" +
                    "✅ Reward: Earn Money and Knowledge for each level you master.\n" +
                    "❌ Risk: You pay $20 per failed level and suffer an Energy drain!");
            instr.showAndWait();
            // Start game after OK
            startBtn.setDisable(true);
            sequence.clear();
            level[0] = 1;
            playNextLevel(sequence, level, userStep, inputAllowed, levelLbl, stage, onComplete);
        });

        root.getChildren().addAll(title, instruction, levelLbl, matrixGrid, startBtn);
        stage.setScene(new Scene(root, 650, 750));
        stage.show();
    }

    private void setupGrid(int size) {
        currentGridSize = size;
        matrixGrid.getChildren().clear();
        matrixButtons = new Button[size * size];
        double btnSize = size <= 4 ? 70 : size == 5 ? 55 : 45;
        for (int i = 0; i < size * size; i++) {
            Button btn = new Button();
            btn.setPrefSize(btnSize, btnSize);
            btn.setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; -fx-border-width: 2;");
            matrixButtons[i] = btn;
            matrixGrid.add(btn, i % size, i / size);
        }
    }

    private void playNextLevel(List<Integer> sequence, int[] level, int[] userStep, boolean[] inputAllowed, Label levelLbl, Stage stage, Runnable onComplete) {
        int neededSize = (level[0] > 15) ? 6 : (level[0] > 10) ? 5 : (level[0] > 5) ? 4 : 3;
        if (neededSize != currentGridSize) setupGrid(neededSize);

        sequence.clear();
        int steps = level[0] + 2;
        levelLbl.setText("Level: " + level[0] + " (" + steps + " Steps on " + neededSize + "x" + neededSize + ")");
        Random rnd = new Random();
        for(int i=0; i<steps; i++) sequence.add(rnd.nextInt(neededSize * neededSize));
        
        userStep[0] = 0; inputAllowed[0] = false;
        double pauseMs = Math.max(350, 800 - (level[0] * 30));

        Timeline timeline = new Timeline();
        for (int i = 0; i < sequence.size(); i++) {
            final int index = sequence.get(i);
            KeyFrame flashOn = new KeyFrame(Duration.millis((i * pauseMs) + 600), e -> {
                matrixButtons[index].setStyle("-fx-background-color: #3fb950; -fx-border-color: #30363d; -fx-border-width: 2;");
            });
            KeyFrame flashOff = new KeyFrame(Duration.millis((i * pauseMs) + pauseMs + 400), e -> {
                matrixButtons[index].setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; -fx-border-width: 2;");
            });
            timeline.getKeyFrames().addAll(flashOn, flashOff);
        }
        
        timeline.setOnFinished(e -> {
            inputAllowed[0] = true;
            for (int i = 0; i < matrixButtons.length; i++) {
                final int btnIdx = i;
                matrixButtons[i].setOnAction(ev -> {
                    if (!inputAllowed[0]) return;
                    matrixButtons[btnIdx].setStyle("-fx-background-color: #a855f7;");
                    Timeline f = new Timeline(new KeyFrame(Duration.millis(150), ex -> matrixButtons[btnIdx].setStyle("-fx-background-color: #21262d; -fx-border-color: #30363d; -fx-border-width: 2;")));
                    f.play();

                    if (sequence.get(userStep[0]) == btnIdx) {
                        userStep[0]++;
                        if (userStep[0] == sequence.size()) {
                            inputAllowed[0] = false; level[0]++;
                            Timeline p = new Timeline(new KeyFrame(Duration.millis(1000), ex -> playNextLevel(sequence, level, userStep, inputAllowed, levelLbl, stage, onComplete)));
                            p.play();
                        }
                    } else {
                        inputAllowed[0] = false;
                        Timeline t = new Timeline(new KeyFrame(Duration.millis(1200), ex -> showMatrixResult(stage, level[0] - 1, onComplete)));
                        t.play();
                    }
                });
            }
        });
        timeline.play();
    }

    private void showMatrixResult(Stage stage, int finalLevel, Runnable onComplete) {
        String detail = "Mastery Level: " + finalLevel;
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", finalLevel);
        
        showMiniGameResult(stage, "MEMORY_MATRIX", data, "🧠 BRAIN TRAINING COMPLETE", detail, onComplete);
    }

    // ─── TYPING HUSTLE ────────────────────────────────────────────────────────
    public void showTypingHustle(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("⌨️ Typing Hustle");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f7f6; -fx-padding: 40;");

        Label title = new Label("⌨️ DATA ENTRY HUSTLE");
        title.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 26px; -fx-font-weight: bold;");
        Label instruction = new Label("Insane difficulty! Time drains faster. Mistakes are lethal.");
        instruction.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label timerLbl = new Label("Time: 15.0s");
        timerLbl.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label scoreLbl = new Label("Score: 0");
        scoreLbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox stats = new HBox(50, timerLbl, scoreLbl);
        stats.setAlignment(Pos.CENTER);
        Label targetWordLbl = new Label("PRESS START");
        targetWordLbl.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 32px; -fx-font-weight: bold; -fx-background-color: white; -fx-padding: 15 40; -fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-background-radius: 8; -fx-border-radius: 8;");
        targetWordLbl.setWrapText(true);
        targetWordLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        targetWordLbl.setMaxWidth(500);

        TextField inputField = new TextField();
        inputField.setDisable(true);
        inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-alignment: center; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");
        inputField.setMaxWidth(350);

        Button startBtn = new Button("START HUSTLE");
        startBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 12 35; -fx-background-radius: 6; -fx-cursor: hand;");

        final double[] time = {15.0};
        final int[] typeScore = {0};
        final int[] mistakes = {0};
        final String[] currentWord = {""};
        final boolean[] gameOver = {false}; // Guard flag to prevent double-trigger

        Runnable pickNextWord = () -> {
            int totalWords = TypingData.WORDS.length;
            int baseIndex = Math.min(totalWords - 100, (typeScore[0] * 5)); 
            baseIndex = Math.max(0, baseIndex);
            
            int randOffset = new Random().nextInt(100);
            currentWord[0] = TypingData.WORDS[baseIndex + randOffset];
            targetWordLbl.setText(currentWord[0]);
        };

        final Timeline[] timer = new Timeline[1];
        timer[0] = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double drainRate = 0.016 + (typeScore[0] * 0.0015);
            time[0] -= drainRate;
            timerLbl.setText(String.format("Time: %.1fs", time[0]));
            if (time[0] <= 0 && !gameOver[0]) {
                gameOver[0] = true;
                timer[0].stop();
                inputField.setDisable(true);
                // Use Platform.runLater so any pending textProperty listener
                // Platform.runLater calls (e.g. clearing the field) finish first
                Platform.runLater(() -> showTypingResult(stage, typeScore[0], mistakes[0], onComplete));
            }
        }));
        timer[0].setCycleCount(Timeline.INDEFINITE);

        startBtn.setOnAction(e -> {
            // Show instructions first, then start when OK is clicked
            Alert instr = new Alert(Alert.AlertType.WARNING);
            instr.setTitle("Typing Hustle: HARDCORE");
            instr.setHeaderText("🔥 Welcome to Hardcore Mode");
            instr.setContentText("This is not a drill.\n\n" +
                    "• The clock drains FASTER the higher you score.\n" +
                    "• Time rewards SHRINK as you progress.\n\n" +
                    "✅ Reward: High payout and Reputation bonus for every word typed.\n" +
                    "❌ Risk: Every typo costs you 2.5 seconds, lowers Reputation, and costs $20!");
            instr.showAndWait();
            // Start game after OK
            gameOver[0] = false;
            startBtn.setVisible(false); inputField.setDisable(false); inputField.requestFocus();
            time[0] = 15.0; typeScore[0] = 0; mistakes[0] = 0;
            scoreLbl.setText("Score: 0"); timerLbl.setText("Time: 15.0s");
            pickNextWord.run();
            timer[0].playFromStart();
        });

        inputField.textProperty().addListener((obs, oldV, newV) -> {
            if (gameOver[0]) return; // Ignore all changes after game ends
            if (newV.length() > oldV.length()) {
                String target = currentWord[0].toLowerCase();
                String input = newV.trim().toLowerCase();
                if (!target.startsWith(input)) {
                    mistakes[0]++;
                    time[0] = Math.max(0, time[0] - 2.5);
                    timerLbl.setText(String.format("Time: %.1fs", time[0]));
                    inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #fce4e4; -fx-text-fill: #c0392b; -fx-alignment: center; -fx-background-radius: 8; -fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 8;");
                    // Clear SYNCHRONOUSLY — Platform.runLater would allow extra keystrokes
                    // before the clear, each counting as another mistake ($20 penalty each).
                    inputField.setText("");
                    // Reset style so field looks clean for next word attempt
                    inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-alignment: center; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");
                } else {
                    inputField.setStyle("-fx-font-size: 24px; -fx-background-color: white; -fx-text-fill: #2c3e50; -fx-alignment: center; -fx-background-radius: 8; -fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 8;");
                }
            }
            if (newV.trim().equalsIgnoreCase(currentWord[0])) {
                typeScore[0] += 5;
                double timeBonus = Math.max(0.5, 2.0 - (typeScore[0] * 0.02));
                time[0] += timeBonus;
                scoreLbl.setText("Score: " + typeScore[0]);
                inputField.setText("");
                inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-alignment: center; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");
                pickNextWord.run();
            }
        });

        root.getChildren().addAll(title, instruction, stats, targetWordLbl, inputField, startBtn);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private void showTypingResult(Stage stage, int score, int mistakes, Runnable onComplete) {
        String detail = "Words: " + score + " | Typos: " + mistakes;
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", score);
        data.put("mistakes", mistakes);
        
        showMiniGameResult(stage, "TYPING_HUSTLE", data, "⌨️ SHIFT OVER", detail, onComplete);
    }

    // ─── CRISIS MANAGEMENT ───────────────────────────────────────────────────
    public void showCrisisManagement(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("🚨 Crisis Management");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f7f6; -fx-padding: 30;");

        Label title = new Label("🚨 CRISIS MANAGEMENT");
        title.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 26px; -fx-font-weight: bold;");
        Label instruction = new Label("Servers are failing! Click the RED servers to fix them before they crash.");
        instruction.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label scoreLbl = new Label("Score: 0");
        scoreLbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label strikeLbl = new Label("Strikes: 0 / 3");
        strikeLbl.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 20px; -fx-font-weight: bold;");
        HBox topStats = new HBox(50, scoreLbl, strikeLbl);
        topStats.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15); grid.setVgap(15);

        class ServerNode {
            Button btn;
            ProgressBar pb;
            boolean isFailing = false;
            double progress = 0;
            ServerNode() {
                btn = new Button("SERVER OK");
                btn.setPrefSize(100, 80);
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                pb = new ProgressBar(0);
                pb.setPrefWidth(100);
                pb.setVisible(false);
            }
        }
        
        ServerNode[] servers = new ServerNode[9];
        for (int i = 0; i < 9; i++) {
            servers[i] = new ServerNode();
            VBox box = new VBox(5, servers[i].btn, servers[i].pb);
            box.setAlignment(Pos.CENTER);
            grid.add(box, i % 3, i / 3);
        }

        final int[] score = {0};
        final int[] strikes = {0};
        final boolean[] isPlaying = {false};
        final Timeline[] gameLoop = new Timeline[1];

        Button startBtn = new Button("START SHIFT");
        startBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 30;");

        startBtn.setOnAction(e -> {
            // Show instructions first, then start when OK is clicked
            Alert instr = new Alert(Alert.AlertType.INFORMATION);
            instr.setTitle("Crisis Management");
            instr.setHeaderText("🚨 Prevent the Servers from Crashing!");
            instr.setContentText("Rules and Regulations:\n\n" +
                    "• Servers will randomly turn RED and a progress bar will fill.\n" +
                    "• Click the RED servers before the bar reaches 100% to fix them.\n" +
                    "• The game progressively speeds up as your score increases.\n" +
                    "• You get 3 strikes (crashes). On the 3rd strike, the game ends.\n\n" +
                    "✅ Reward: You will gain Money and Confidence based on your performance.\n" +
                    "❌ Risk: You will LOSE Money and Reputation for every server that crashes!");
            instr.showAndWait();
            // Start game after OK
            startBtn.setVisible(false);
            isPlaying[0] = true;
            
            gameLoop[0] = new Timeline(new KeyFrame(Duration.millis(50), ev -> {
                if (!isPlaying[0]) return;
                
                // Randomly start failing a server based on score
                double spawnChance = 0.02 + (score[0] * 0.002);
                if (new Random().nextDouble() < spawnChance) {
                    int idx = new Random().nextInt(9);
                    if (!servers[idx].isFailing && !servers[idx].btn.getText().equals("CRASHED!")) {
                        servers[idx].isFailing = true;
                        servers[idx].progress = 0;
                        servers[idx].pb.setVisible(true);
                        servers[idx].btn.setText("FAILING!");
                        servers[idx].btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                    }
                }
                
                // Update failing servers
                for (int i=0; i<9; i++) {
                    if (servers[i].isFailing) {
                        // Fill rate increases with score
                        double fillRate = 0.015 + (score[0] * 0.001);
                        servers[i].progress += fillRate;
                        servers[i].pb.setProgress(servers[i].progress);
                        
                        if (servers[i].progress >= 1.0) {
                            servers[i].isFailing = false;
                            servers[i].pb.setVisible(false);
                            servers[i].btn.setText("CRASHED!");
                            servers[i].btn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
                            
                            strikes[0]++;
                            strikeLbl.setText("Strikes: " + strikes[0] + " / 3");
                            
                            if (strikes[0] >= 3) {
                                isPlaying[0] = false;
                                gameLoop[0].stop();
                                showCrisisResult(stage, score[0], strikes[0], onComplete);
                            }
                        }
                    }
                }
            }));
            gameLoop[0].setCycleCount(Timeline.INDEFINITE);
            gameLoop[0].play();
        });

        for (int i = 0; i < 9; i++) {
            final int idx = i;
            servers[idx].btn.setOnAction(ev -> {
                if (isPlaying[0] && servers[idx].isFailing) {
                    servers[idx].isFailing = false;
                    servers[idx].pb.setVisible(false);
                    servers[idx].btn.setText("SERVER OK");
                    servers[idx].btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                    score[0]++;
                    scoreLbl.setText("Score: " + score[0]);
                }
            });
        }

        root.getChildren().addAll(title, instruction, topStats, grid, startBtn);
        stage.setScene(new Scene(root, 550, 600));
        stage.show();
    }

    private void showCrisisResult(Stage stage, int score, int strikes, Runnable onComplete) {
        String detail = "Servers Saved: " + score + " | Crashed: " + strikes;
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", score);
        data.put("strikes", strikes);
        
        showMiniGameResult(stage, "CRISIS_MANAGEMENT", data, "🚨 SHIFT OVER", detail, onComplete);
    }

    // ─── CORPORATE LADDER ────────────────────────────────────────────────────
    public void showCorporateLadder(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("👔 Corporate Ladder");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f7f6; -fx-padding: 30;");

        Label title = new Label("👔 CORPORATE LADDER");
        title.setStyle("-fx-text-fill: #2980b9; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label scoreLbl = new Label("Promotion Level: 1");
        scoreLbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox gameArea = new VBox(10);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPrefHeight(200);

        Button startBtn = new Button("ENTER OFFICE");
        startBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-padding: 10 30;");

        final int[] level = {1};
        final boolean[] isPlaying = {false};
        
        // Target sequence size grows with level
        final List<String> targetSequence = new java.util.ArrayList<>();
        final List<String> playerSequence = new java.util.ArrayList<>();

        String[] tasks = {"EMAILS", "MEETING", "REPORT", "CALL"};
        String[] colors = {"#3498db", "#e67e22", "#9b59b6", "#16a085"};

        Runnable generateSequence = () -> {
            targetSequence.clear();
            playerSequence.clear();
            Random rnd = new Random();
            int sequenceLength = 2 + level[0]; // Gets harder each level
            
            FlowPane sequenceBox = new FlowPane(10, 10);
            sequenceBox.setAlignment(Pos.CENTER);
            
            for (int i = 0; i < sequenceLength; i++) {
                int taskIdx = rnd.nextInt(tasks.length);
                targetSequence.add(tasks[taskIdx]);
                
                Label tLbl = new Label(tasks[taskIdx]);
                tLbl.setStyle("-fx-background-color: " + colors[taskIdx] + "; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-weight: bold; -fx-background-radius: 4;");
                tLbl.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                sequenceBox.getChildren().add(tLbl);
            }
            
            gameArea.getChildren().clear();
            
            Label memLbl = new Label("MEMORIZE THIS SCHEDULE:");
            memLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            gameArea.getChildren().addAll(memLbl, sequenceBox);
            
            // Hide after a short time
            Timeline hideTimer = new Timeline(new KeyFrame(Duration.millis(Math.max(1000, 3000 - (level[0] * 200))), e -> {
                gameArea.getChildren().clear();
                
                Label actLbl = new Label("EXECUTE SCHEDULE:");
                actLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #c0392b;");
                
                FlowPane buttons = new FlowPane(10, 10);
                buttons.setAlignment(Pos.CENTER);
                
                for (int i = 0; i < tasks.length; i++) {
                    String taskName = tasks[i];
                    Button btn = new Button(taskName);
                    btn.setStyle("-fx-background-color: " + colors[i] + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 25; -fx-font-size: 14px;");
                    
                    btn.setOnAction(ev -> {
                        playerSequence.add(taskName);
                        
                        // Check logic
                        int currentIndex = playerSequence.size() - 1;
                        if (!playerSequence.get(currentIndex).equals(targetSequence.get(currentIndex))) {
                            // FAILED
                            showLadderResult(stage, level[0] - 1, true, onComplete);
                        } else if (playerSequence.size() == targetSequence.size()) {
                            // SUCCESS, next level
                            level[0]++;
                            scoreLbl.setText("Promotion Level: " + level[0]);
                            
                            Label succ = new Label("PROMOTED!");
                            succ.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 20px;");
                            gameArea.getChildren().clear();
                            gameArea.getChildren().add(succ);
                            
                            // Ask to continue or cash out
                            Button contBtn = new Button("Push for Next Promotion");
                            contBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                            
                            Button cashBtn = new Button("Cash Out & Leave");
                            cashBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                            
                            contBtn.setOnAction(ev2 -> {
                                // Start next round automatically after a short delay so the player can't spam it
                                gameArea.getChildren().clear();
                                Timeline t = new Timeline(new KeyFrame(Duration.millis(500), ev3 -> {}));
                                t.setOnFinished(ev3 -> {
                                    // Actually need to trigger the next sequence generation here
                                });
                                t.play();
                            });
                            
                            cashBtn.setOnAction(ev2 -> showLadderResult(stage, level[0] - 1, false, onComplete));
                            
                            FlowPane opts = new FlowPane(15, 15, contBtn, cashBtn);
                            opts.setAlignment(Pos.CENTER);
                            gameArea.getChildren().add(opts);
                            
                            // Re-bind continue button to loop back
                            contBtn.setOnAction(ev2 -> {
                                // Have to use a nested call or a loop logic. To keep it simple, just run generateSequence again.
                                // But since generateSequence is the current lambda, we can't easily self-reference cleanly in Java without an array hack or method.
                            });
                        }
                    });
                    buttons.getChildren().add(btn);
                }
                gameArea.getChildren().addAll(actLbl, buttons);
            }));
            hideTimer.play();
        };
        
        // Hack to allow self-reference in lambda
        final Runnable[] runGame = new Runnable[1];
        runGame[0] = () -> {
            targetSequence.clear();
            playerSequence.clear();
            Random rnd = new Random();
            int sequenceLength = 2 + level[0]; 
            
            FlowPane sequenceBox = new FlowPane(10, 10);
            sequenceBox.setAlignment(Pos.CENTER);
            
            for (int i = 0; i < sequenceLength; i++) {
                int taskIdx = rnd.nextInt(tasks.length);
                targetSequence.add(tasks[taskIdx]);
                
                Label tLbl = new Label(tasks[taskIdx]);
                tLbl.setStyle("-fx-background-color: " + colors[taskIdx] + "; -fx-text-fill: white; -fx-padding: 8 12; -fx-font-weight: bold; -fx-background-radius: 4;");
                tLbl.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
                sequenceBox.getChildren().add(tLbl);
            }
            
            gameArea.getChildren().clear();
            
            Label memLbl = new Label("MEMORIZE THIS SCHEDULE:");
            memLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            gameArea.getChildren().addAll(memLbl, sequenceBox);
            
            Timeline hideTimer = new Timeline(new KeyFrame(Duration.millis(Math.max(800, 3000 - (level[0] * 300))), e -> {
                gameArea.getChildren().clear();
                
                Label actLbl = new Label("EXECUTE SCHEDULE:");
                actLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #c0392b;");
                
                FlowPane buttons = new FlowPane(10, 10);
                buttons.setAlignment(Pos.CENTER);
                
                for (int i = 0; i < tasks.length; i++) {
                    String taskName = tasks[i];
                    Button btn = new Button(taskName);
                    btn.setStyle("-fx-background-color: " + colors[i] + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 15 25; -fx-font-size: 14px;");
                    
                    btn.setOnAction(ev -> {
                        playerSequence.add(taskName);
                        int currentIndex = playerSequence.size() - 1;
                        
                        if (!playerSequence.get(currentIndex).equals(targetSequence.get(currentIndex))) {
                            showLadderResult(stage, level[0] - 1, true, onComplete);
                        } else if (playerSequence.size() == targetSequence.size()) {
                            level[0]++;
                            scoreLbl.setText("Promotion Level: " + level[0]);
                            
                            Label succ = new Label("PROMOTED!");
                            succ.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 20px;");
                            gameArea.getChildren().clear();
                            gameArea.getChildren().add(succ);
                            
                            Button contBtn = new Button("Next Promotion");
                            contBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                            contBtn.setOnAction(ev2 -> runGame[0].run());
                            
                            Button cashBtn = new Button("Cash Out");
                            cashBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                            cashBtn.setOnAction(ev2 -> showLadderResult(stage, level[0] - 1, false, onComplete));
                            
                            FlowPane opts = new FlowPane(15, 15, contBtn, cashBtn);
                            opts.setAlignment(Pos.CENTER);
                            gameArea.getChildren().add(opts);
                        }
                    });
                    buttons.getChildren().add(btn);
                }
                gameArea.getChildren().addAll(actLbl, buttons);
            }));
            hideTimer.play();
        };

        startBtn.setOnAction(e -> {
            // Show instructions first, then start when OK is clicked
            Alert instr = new Alert(Alert.AlertType.INFORMATION);
            instr.setTitle("Corporate Ladder");
            instr.setHeaderText("👔 Climb the Corporate Ladder!");
            instr.setContentText("Rules and Regulations:\n\n" +
                    "• Memorize the sequence of office tasks.\n" +
                    "• The sequence gets LONGER and vanishes FASTER every promotion.\n" +
                    "• After each level, you can choose to Cash Out or risk going for the next promotion.\n\n" +
                    "✅ Reward: High payouts and Reputation boosts for cashing out safely.\n" +
                    "❌ Risk: If you mess up the sequence, you get FIRED! You lose your earned cash and suffer a massive Stress penalty.");
            instr.showAndWait();
            // Start game after OK
            startBtn.setVisible(false);
            runGame[0].run();
        });

        root.getChildren().addAll(title, scoreLbl, gameArea, startBtn);
        stage.setScene(new Scene(root, 550, 450));
        stage.show();
    }

    private void showLadderResult(Stage stage, int level, boolean failed, Runnable onComplete) {
        String title = failed ? "💀 FIRED!" : "👔 RETIRED SUCCESSFULLY";
        String detail = "Highest Level Reached: " + level;
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("score", level);
        data.put("failed", failed);
        
        showMiniGameResult(stage, "CORPORATE_LADDER", data, title, detail, onComplete);
    }
}
