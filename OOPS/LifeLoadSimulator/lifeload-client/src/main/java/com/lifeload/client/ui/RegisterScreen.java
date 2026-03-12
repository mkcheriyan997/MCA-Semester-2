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
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #121212; -fx-padding: 40;");

        Label title = new Label("CREATE PROFILE");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setStyle("-fx-text-fill: #00e5ff;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(300);
        userField.setStyle("-fx-font-size: 16px; -fx-background-color: #2c2f33; -fx-text-fill: white;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);
        emailField.setStyle("-fx-font-size: 16px; -fx-background-color: #2c2f33; -fx-text-fill: white;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(300);
        passField.setStyle("-fx-font-size: 16px; -fx-background-color: #2c2f33; -fx-text-fill: white;");

        Label msgLbl = new Label();
        msgLbl.setStyle("-fx-text-fill: #ff3366;");

        Button regBtn = new Button("REGISTER");
        regBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");
        regBtn.setPrefWidth(300);
        regBtn.setOnAction(e -> {
            boolean ok = AuthService.register(userField.getText(), emailField.getText(), passField.getText());
            if (ok) {
                msgLbl.setStyle("-fx-text-fill: #00e5ff;");
                msgLbl.setText("Success! Returning to login...");
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) {}
                    javafx.application.Platform.runLater(onBack);
                }).start();
            } else {
                msgLbl.setStyle("-fx-text-fill: #ff3366;");
                msgLbl.setText("Registration failed. Username/email taken.");
            }
        });

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle("-fx-text-fill: #b9bbbe;");
        backLink.setOnAction(e -> onBack.run());

        root.getChildren().addAll(title, userField, emailField, passField, msgLbl, regBtn, backLink);
        return new Scene(root, 1000, 700);
    }
}
