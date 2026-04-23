package com.lifeload.client.ui;

import com.lifeload.client.service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterScreen {

    private Stage stage;
    private Runnable onBack;

    public RegisterScreen(Stage stage, Runnable onBack) {
        this.stage = stage;
        this.onBack = onBack;
    }

    public Scene createScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f4f7f6, #e0e5ec); -fx-padding: 50;");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        Label title = new Label("Create Profile");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2c3e50;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setPrefHeight(45);
        userField.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #495057;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #495057;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(45);
        passField.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #495057;");

        Label msgLbl = new Label();
        msgLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");

        Button regBtn = new Button("Register");
        regBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 6; -fx-cursor: hand;");
        regBtn.setPrefWidth(Double.MAX_VALUE);
        regBtn.setPrefHeight(45);
        regBtn.setOnAction(e -> {
            boolean ok = AuthService.register(userField.getText(), emailField.getText(), passField.getText());
            if (ok) {
                msgLbl.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
                msgLbl.setText("Success! Returning to login...");
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) {}
                    javafx.application.Platform.runLater(onBack);
                }).start();
            } else {
                msgLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
                msgLbl.setText("Registration failed. Username/email taken.");
            }
        });

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        backLink.setOnAction(e -> onBack.run());

        card.getChildren().addAll(title, userField, emailField, passField, msgLbl, regBtn, backLink);
        root.getChildren().add(card);
        return new Scene(root, 1000, 700);
    }
}
