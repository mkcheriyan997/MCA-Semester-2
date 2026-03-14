package com.lifeload.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final String API_URL = "http://localhost:8080/api/auth";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean login(String username, String password) {
        try {
            Map<String, String> creds = new HashMap<>();
            creds.put("username", username);
            creds.put("password", password);
            
            String reqBody = mapper.writeValueAsString(creds);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/signin"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode resJson = mapper.readTree(response.body());
                if (resJson.has("token")) {
                    SessionManager.setToken(resJson.get("token").asText());
                    SessionManager.setUsername(username);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(String username, String email, String password) {
        try {
            Map<String, String> creds = new HashMap<>();
            creds.put("username", username);
            creds.put("email", email);
            creds.put("password", password);
            
            String reqBody = mapper.writeValueAsString(creds);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
