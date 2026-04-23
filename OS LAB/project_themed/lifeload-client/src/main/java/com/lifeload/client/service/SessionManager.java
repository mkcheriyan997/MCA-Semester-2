package com.lifeload.client.service;

public class SessionManager {
    private static String jwtToken;
    private static String username;
    
    public static void setToken(String token) {
        jwtToken = token;
    }
    
    public static String getToken() {
        return jwtToken;
    }
    
    public static void setUsername(String user) {
        username = user;
    }
    
    public static String getUsername() {
        return username;
    }
    
    public static void clearSession() {
        jwtToken = null;
        username = null;
    }
    
    public static boolean isLoggedIn() {
        return jwtToken != null && !jwtToken.isEmpty();
    }
}
