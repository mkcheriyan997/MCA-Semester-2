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
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #121212; -fx-padding: 40;");
        
        Label title = new Label("LIFELOAD SIMULATOR");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #00e5ff;");
        
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(300);
        userField.setStyle("-fx-font-size: 16px; -fx-background-color: #2c2f33; -fx-text-fill: white;");
        
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(300);
        passField.setStyle("-fx-font-size: 16px; -fx-background-color: #2c2f33; -fx-text-fill: white;");
        
        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill: #ff3366;");
        
        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle("-fx-background-color: #00e5ff; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");
        loginBtn.setPrefWidth(300);
        loginBtn.setOnAction(e -> {
            boolean ok = AuthService.login(userField.getText(), passField.getText());
            if (ok) {
                onSuccess.run();
            } else {
                errorLbl.setText("Login failed. Check credentials.");
            }
        });
        
        Hyperlink regLink = new Hyperlink("Create an Account");
        regLink.setStyle("-fx-text-fill: #b9bbbe;");
        regLink.setOnAction(e -> stage.setScene(new RegisterScreen(stage, () -> stage.setScene(createScene())).createScene()));
        
        root.getChildren().addAll(title, userField, passField, errorLbl, loginBtn, regLink);
        return new Scene(root, 1000, 700);
    }
}
