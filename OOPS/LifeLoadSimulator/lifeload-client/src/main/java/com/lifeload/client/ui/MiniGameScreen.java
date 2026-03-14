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

        root.getChildren().addAll(header, sp);
        showQuestion(stage, questionArea, 0);

        stage.setScene(new Scene(root, 720, 560));
        stage.show();

        Platform.runLater(() -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Quiz Instructions");
            instruction.setHeaderText("Test Your Financial Literacy");
            instruction.setContentText("Answer 10 randomized questions.\n\n" +
                    "• Reward: $100 per correct answer.\n" +
                    "• Penalty: Each wrong answer REDUCES your final payout.\n" +
                    "• Bonus: High scores grant a Knowledge boost!");
            instruction.showAndWait();
        });
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

    private void showQuizResult(Stage stage, VBox area) {
        area.getChildren().clear();
        double reward = Math.max(0, score * 100.0);
        Label resultTitle = new Label(score >= 8 ? "🏆 EXCELLENT!" : score >= 5 ? "👍 GOOD EFFORT!" : "📚 KEEP LEARNING!");
        resultTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        resultTitle.setStyle("-fx-text-fill: #d29922;");
        Label scoreDisplay = new Label(score + " / " + sessionQuestions.length);
        scoreDisplay.setFont(Font.font("Consolas", 22));
        scoreDisplay.setStyle("-fx-text-fill: #cdd9e5;");
        Label rewardLbl = new Label("💰 Reward Earned: $" + (int)reward);
        rewardLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 20px; -fx-font-weight: bold;");
        Button closeBtn = new Button("COLLECT REWARD");
        closeBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 14 40;");
        closeBtn.setOnAction(e -> {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "BUDGET_QUIZ");
            req.put("score", (int)(score / 2));
            com.lifeload.client.service.GameService.playMiniGame(req);
            stage.close();
            if (onComplete != null) onComplete.run();
        });
        area.setAlignment(Pos.CENTER);
        area.setSpacing(20);
        area.getChildren().addAll(resultTitle, scoreDisplay, rewardLbl, closeBtn);
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
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        
        KeyFrame spawnFrame = new KeyFrame(Duration.millis(800), e -> {
            boolean isTask = new Random().nextDouble() > 0.3;
            javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(25);
            c.setFill(isTask ? javafx.scene.paint.Color.web("#3fb950") : javafx.scene.paint.Color.web("#f85149"));
            c.setCenterX(50 + new Random().nextInt(500));
            c.setCenterY(50 + new Random().nextInt(300));
            c.setCursor(javafx.scene.Cursor.HAND);
            c.setOnMouseClicked(ev -> {
                if (isTask) gameScore[0] += 10; else gameScore[0] -= 15;
                liveScoreLbl.setText("Score: " + gameScore[0]);
                gamePane.getChildren().remove(c);
            });
            gamePane.getChildren().add(c);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.millis(1500), c);
            ft.setFromValue(1.0); ft.setToValue(0.0);
            ft.setOnFinished(ev -> gamePane.getChildren().remove(c));
            ft.play();
        });
        
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeSeconds[0]--;
            timerLbl.setText("Time: " + timeSeconds[0] + "s");
            if (timeSeconds[0] <= 0) { gameLoop.stop(); showFocusResult(stage, gameScore[0], onComplete); }
        }));
        timer.setCycleCount(20);
        gameLoop.getKeyFrames().add(spawnFrame);
        stage.setScene(new Scene(root, 650, 550));
        stage.show();
        gameLoop.play();
        timer.play();

        Platform.runLater(() -> {
            Alert instruction = new Alert(Alert.AlertType.INFORMATION);
            instruction.setTitle("Deep Work Instructions");
            instruction.setHeaderText("⚡ Train Your Focus");
            instruction.setContentText("A fast-paced clicking challenge!\n\n" +
                    "• Click GREEN circles (+10 pts) as they appear.\n" +
                    "• Avoid RED circles (-15 pts).\n" +
                    "• Reward: $10 per point and Motivation bonus.");
            instruction.showAndWait();
        });
    }

    private void showFocusResult(Stage stage, int finalScore, Runnable onComplete) {
        VBox res = new VBox(20); res.setAlignment(Pos.CENTER); res.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");
        Label title = new Label("DEEP WORK SESSION COMPLETE");
        title.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label scoreLbl = new Label("Final Focus Score: " + finalScore);
        scoreLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button collect = new Button("COLLECT DATA & REWARDS");
        collect.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        collect.setOnAction(e -> {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "PRODUCTIVITY_FLOW");
            req.put("score", Math.max(0, finalScore));
            com.lifeload.client.service.GameService.playMiniGame(req);
            stage.close();
            if (onComplete != null) onComplete.run();
        });
        res.getChildren().addAll(title, scoreLbl, collect);
        stage.getScene().setRoot(res);
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
            startBtn.setDisable(true);
            sequence.clear();
            level[0] = 1;
            playNextLevel(sequence, level, userStep, inputAllowed, levelLbl, stage, onComplete);
        });

        root.getChildren().addAll(title, instruction, levelLbl, matrixGrid, startBtn);
        stage.setScene(new Scene(root, 650, 750));
        stage.show();

        Platform.runLater(() -> {
            Alert instr = new Alert(Alert.AlertType.INFORMATION);
            instr.setTitle("Memory Matrix Instructions");
            instr.setHeaderText("🧠 Evolving Intelligence Test");
            instr.setContentText("Memorize the sequence of flashing tiles.\n\n" +
                    "• L1-5: 3x3 Grid\n" +
                    "• L6-10: 4x4 Grid\n" +
                    "• L11-15: 5x5 Grid\n" +
                    "• Rewards INCREASE significantly as you level up!");
            instr.showAndWait();
        });
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
        VBox res = new VBox(20); res.setAlignment(Pos.CENTER); res.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");
        Label title = new Label("BRAIN TRAINING COMPLETE");
        title.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label scoreLbl = new Label("Mastery Level: " + finalLevel);
        scoreLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        Button collect = new Button("SYNC DATA & COLLECT REWARDS");
        collect.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        collect.setOnAction(e -> {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "MEMORY_MATRIX");
            req.put("score", finalLevel);
            com.lifeload.client.service.GameService.playMiniGame(req);
            stage.close();
            if (onComplete != null) onComplete.run();
        });
        res.getChildren().addAll(title, scoreLbl, collect);
        stage.getScene().setRoot(res);
    }

    // ─── TYPING HUSTLE ────────────────────────────────────────────────────────
    private static final String[] TYPING_WORDS = {
        "budget", "invest", "portfolio", "dividend", "equity", "liability", "asset", 
        "capital", "interest", "mortgage", "inflation", "revenue", "profit", "margin",
        "market", "bull", "bear", "stock", "bond", "crypto", "savings", "tax", "audit"
    };

    public void showTypingHustle(Stage owner, Runnable onComplete) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle("⌨️ Typing Hustle");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");

        Label title = new Label("⌨️ DATA ENTRY HUSTLE");
        title.setStyle("-fx-text-fill: #ffcc00; -fx-font-size: 26px; -fx-font-weight: bold;");
        Label instruction = new Label("Type correctly to earn. Mistakes deduct score!");
        instruction.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 16px;");
        Label timerLbl = new Label("Time: 30s");
        timerLbl.setStyle("-fx-text-fill: #f85149; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label scoreLbl = new Label("Score: 0");
        scoreLbl.setStyle("-fx-text-fill: #3fb950; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox stats = new HBox(50, timerLbl, scoreLbl);
        stats.setAlignment(Pos.CENTER);
        Label targetWordLbl = new Label("PRESS START");
        targetWordLbl.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold; -fx-background-color: #21262d; -fx-padding: 10 30; -fx-border-color: #30363d; -fx-border-width: 2;");

        TextField inputField = new TextField();
        inputField.setDisable(true);
        inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #161b22; -fx-text-fill: #cdd9e5; -fx-alignment: center;");
        inputField.setMaxWidth(300);

        Button startBtn = new Button("START HUSTLE");
        startBtn.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30;");

        final int[] time = {30};
        final int[] typeScore = {0};
        final String[] currentWord = {""};

        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            time[0]--;
            timerLbl.setText("Time: " + time[0] + "s");
            if (time[0] <= 0) { inputField.setDisable(true); showTypingResult(stage, typeScore[0], onComplete); }
        }));
        timer.setCycleCount(30);

        startBtn.setOnAction(e -> {
            startBtn.setVisible(false); inputField.setDisable(false); inputField.requestFocus();
            time[0] = 30; typeScore[0] = 0;
            scoreLbl.setText("Score: 0"); timerLbl.setText("Time: 30s");
            currentWord[0] = TYPING_WORDS[new Random().nextInt(TYPING_WORDS.length)];
            targetWordLbl.setText(currentWord[0]);
            timer.playFromStart();
        });

        inputField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > oldV.length()) {
                String target = currentWord[0].toLowerCase();
                String input = newV.trim().toLowerCase();
                if (!target.startsWith(input)) {
                    typeScore[0] = Math.max(-50, typeScore[0] - 1);
                    scoreLbl.setText("Score: " + typeScore[0]);
                    inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #4d1f1f; -fx-text-fill: #f85149; -fx-alignment: center;");
                } else {
                    inputField.setStyle("-fx-font-size: 24px; -fx-background-color: #161b22; -fx-text-fill: #cdd9e5; -fx-alignment: center;");
                }
            }
            if (newV.trim().equalsIgnoreCase(currentWord[0])) {
                typeScore[0] += 5;
                scoreLbl.setText("Score: " + typeScore[0]);
                inputField.setText("");
                currentWord[0] = TYPING_WORDS[new Random().nextInt(TYPING_WORDS.length)];
                targetWordLbl.setText(currentWord[0]);
            }
        });

        root.getChildren().addAll(title, instruction, stats, targetWordLbl, inputField, startBtn);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();

        Platform.runLater(() -> {
            Alert instr = new Alert(Alert.AlertType.INFORMATION);
            instr.setTitle("Typing Hustle Instructions");
            instr.setHeaderText("⌨️ Speed and Accuracy");
            instr.setContentText("Type the financial words as fast as you can!\n\n" +
                    "• Word: +5 pts.\n" +
                    "• Mistake: -1 pt penalty.\n" +
                    "• Reward: $20 per word and Reputation boost.");
            instr.showAndWait();
        });
    }

    private void showTypingResult(Stage stage, int score, Runnable onComplete) {
        VBox res = new VBox(20); res.setAlignment(Pos.CENTER); res.setStyle("-fx-background-color: #0d1117; -fx-padding: 40;");
        Label title = new Label("SHIFT OVER");
        title.setStyle("-fx-text-fill: #ffcc00; -fx-font-size: 28px; -fx-font-weight: bold;");
        Label scoreLbl = new Label("Final Score: " + score);
        scoreLbl.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        Button collect = new Button("COLLECT PAYCHECK");
        collect.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        collect.setOnAction(e -> {
            java.util.Map<String, Object> req = new java.util.HashMap<>();
            req.put("type", "TYPING_HUSTLE");
            req.put("score", score);
            com.lifeload.client.service.GameService.playMiniGame(req);
            stage.close();
            if (onComplete != null) onComplete.run();
        });
        res.getChildren().addAll(title, scoreLbl, collect);
        stage.getScene().setRoot(res);
    }
}
