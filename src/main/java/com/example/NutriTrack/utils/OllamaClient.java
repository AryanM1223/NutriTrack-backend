package com.example.NutriTrack.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OllamaClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static String getApiKeyFromEnv() {
        // First try system environment variables (for production/Render)
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        
        // Fallback to .env file (for local development)
        try {
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                Map<String, String> envVars = new HashMap<>();
                Files.lines(envPath).forEach(line -> {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        envVars.put(parts[0].trim(), parts[1].trim());
                    }
                });
                return envVars.get("GROQ_API_KEY");
            }
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }
        
        return null;
    }

    public static String askModel(String prompt) {
        try {
            String apiKey = getApiKeyFromEnv();
            if (apiKey == null || apiKey.isEmpty()) {
                return "{\"error\": \"❌ GROQ_API_KEY missing from environment variables\"}";
            }

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Add timeout settings to prevent hanging
            conn.setConnectTimeout(10000); // 10 seconds
            conn.setReadTimeout(30000);    // 30 seconds

            // Build JSON request using ObjectMapper instead of manual string concatenation
            ObjectNode requestJson = objectMapper.createObjectNode();
            requestJson.put("model", "llama3-8b-8192");
            requestJson.put("temperature", 0.3);
            
            ArrayNode messages = objectMapper.createArrayNode();
            
            // System message
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful nutritionist that replies with only JSON.");
            messages.add(systemMessage);
            
            // User message
            ObjectNode userMessage = objectMapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", prompt); // ObjectMapper handles proper escaping
            messages.add(userMessage);
            
            requestJson.set("messages", messages);

            // Convert to JSON string
            String requestBody = objectMapper.writeValueAsString(requestJson);
            
            // Debug: Print the request body (remove in production)
            System.out.println("=== REQUEST BODY ===");
            System.out.println(requestBody);
            System.out.println("=== END REQUEST BODY ===");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                // Get error response body for debugging
                String errorResponse = "";
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        Scanner errorScanner = new Scanner(errorStream, "utf-8");
                        StringBuilder errorBuilder = new StringBuilder();
                        while (errorScanner.hasNextLine()) {
                            errorBuilder.append(errorScanner.nextLine());
                        }
                        errorResponse = errorBuilder.toString();
                        errorScanner.close();
                    }
                }
                
                System.err.println("API Error Response: " + errorResponse);
                return "{\"error\": \"⚠️ Groq API returned status code: " + status + ". Details: " + errorResponse + "\"}";
            }

            Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
            StringBuilder responseBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                responseBuilder.append(scanner.nextLine());
            }
            scanner.close();

            String responseJson = responseBuilder.toString();
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode content = root.at("/choices/0/message/content");

            return content.isMissingNode()
                    ? "{\"error\": \"⚠️ No valid 'content' in Groq response\"}"
                    : content.asText().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"⚠️ Exception occurred: " + e.getMessage() + "\"}";
        }
    }
}
