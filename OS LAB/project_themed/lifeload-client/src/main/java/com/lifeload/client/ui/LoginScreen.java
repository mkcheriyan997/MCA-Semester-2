package com.lifeload.client.ui;

import com.lifeload.client.service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScreen {
    
    private Stage stage;
    private Runnable onSuccess;
    
    public LoginScreen(Stage stage, Runnable onSuccess) {
        this.stage = stage;
        this.onSuccess = onSuccess;
    }
    
    public Scene createScene() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f4f7f6, #e0e5ec); -fx-padding: 50;");
        
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: white; -fx-padding: 40; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        Label title = new Label("LifeLoad Simulator");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setPrefHeight(45);
        userField.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #495057;");
        
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(45);
        passField.setStyle("-fx-font-size: 14px; -fx-background-color: #f8f9fa; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #495057;");
        
        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
        
        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 6; -fx-cursor: hand;");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(45);
        loginBtn.setOnAction(e -> {
            boolean ok = AuthService.login(userField.getText(), passField.getText());
            if (ok) {
                onSuccess.run();
            } else {
                errorLbl.setText("Login failed. Check credentials.");
            }
        });
        
        Hyperlink regLink = new Hyperlink("Create an Account");
        regLink.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        regLink.setOnAction(e -> stage.setScene(new RegisterScreen(stage, () -> stage.setScene(createScene())).createScene()));
        
        card.getChildren().addAll(title, userField, passField, errorLbl, loginBtn, regLink);
        root.getChildren().add(card);
        return new Scene(root, 1000, 700);
    }
}
