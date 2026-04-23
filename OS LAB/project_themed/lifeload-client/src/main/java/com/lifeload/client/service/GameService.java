package com.lifeload.client.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GameService {
    private static final String API_URL = "http://localhost:8081/api/game";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static HttpRequest.Builder authBuilder(String endpoint) throws Exception {
        return HttpRequest.newBuilder()
                .uri(new URI(API_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + SessionManager.getToken());
    }

    public static JsonNode loadGame() {
        try {
            HttpRequest request = authBuilder("/load").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return mapper.readTree(response.body());
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode startGame(String playerName) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("playerName", playerName);
            HttpRequest request = authBuilder("/start")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return mapper.readTree(response.body());
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode performAction(String actionId) {
        try {
            Map<String, String> req = new HashMap<>();
            req.put("actionId", actionId);
            HttpRequest request = authBuilder("/action")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(req)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 400) {
                return mapper.readTree(response.body());
            }
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode getTimeline() {
        try {
            HttpRequest request = authBuilder("/timeline").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return mapper.readTree(response.body());
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode getRivals() {
        try {
            HttpRequest request = authBuilder("/rivals").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return mapper.readTree(response.body());
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode claimDailyReward() {
        try {
            HttpRequest request = authBuilder("/daily-reward").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return mapper.readTree(response.body());
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    public static JsonNode playMiniGame(Map<String, Object> reqData) {
        try {
            HttpRequest request = authBuilder("/minigame")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(reqData)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 400) {
                return mapper.readTree(response.body());
            }
            return null;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
}
