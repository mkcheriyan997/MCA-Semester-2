package com.lifeload.client.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TypingData {
    public static String[] WORDS;
    
    static {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                TypingData.class.getResourceAsStream("/typing_words.txt"), StandardCharsets.UTF_8))) {
            List<String> list = reader.lines().collect(Collectors.toList());
            WORDS = list.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            WORDS = new String[]{"budget", "invest", "portfolio", "dividend", "equity", "liability", "asset", "capital"};
        }
    }
}