package com.example.NutriTrack.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OllamaClient {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String askModel(String prompt) {
        try {
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000); // 30 seconds
            conn.setReadTimeout(60000);    // 60 seconds

            // Escape the prompt properly for JSON
            String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
            String jsonInputString = String.format("{\"model\": \"phi3:mini\", \"prompt\": \"%s\", \"stream\": false}", escapedPrompt);

            System.out.println("=== DEBUG: Sending request to Ollama ===");
            System.out.println("URL: " + url);
            System.out.println("Request: " + jsonInputString);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            System.out.println("=== DEBUG: Ollama Response Code: " + responseCode + " ===");

            if (responseCode != 200) {
                return "{\"error\": \"⚠️ Ollama server returned error code: " + responseCode + "\"}";
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse the Ollama response wrapper to extract the actual response content
            String rawResponse = response.toString();
            System.out.println("=== DEBUG: Raw Ollama API Response ===\n" + rawResponse + "\n=== End Raw API Response ===");
            
            if (rawResponse.trim().isEmpty()) {
                return "{\"error\": \"⚠️ Empty response from Ollama server\"}";
            }
            
            try {
                JsonNode ollamaResponse = objectMapper.readTree(rawResponse);
                if (ollamaResponse.has("response")) {
                    String actualResponse = ollamaResponse.get("response").asText();
                    System.out.println("=== DEBUG: Extracted Response Content ===\n" + actualResponse + "\n=== End Response Content ===");
                    return actualResponse;
                } else if (ollamaResponse.has("error")) {
                    return "{\"error\": \"⚠️ Ollama error: " + ollamaResponse.get("error").asText() + "\"}";
                } else {
                    return "{\"error\": \"⚠️ No response field in Ollama output\"}";
                }
            } catch (Exception parseException) {
                System.err.println("Failed to parse Ollama wrapper JSON: " + parseException.getMessage());
                System.err.println("Raw response was: " + rawResponse);
                return "{\"error\": \"⚠️ Failed to parse Ollama response wrapper: " + parseException.getMessage() + "\"}";
            }

        } catch (java.net.ConnectException e) {
            System.err.println("Connection refused - is Ollama running on localhost:11434?");
            return "{\"error\": \"⚠️ Cannot connect to Ollama - make sure it's running on localhost:11434\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"⚠️ Failed to connect to Ollama: " + e.getMessage() + "\"}";
        }
    }
}