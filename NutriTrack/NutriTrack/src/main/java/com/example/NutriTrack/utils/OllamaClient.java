package com.example.NutriTrack.utils;
import io.github.cdimascio.dotenv.Dotenv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OllamaClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("GROQ_API_KEY");    
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public static String askModel(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Construct JSON request
            String requestBody = "{\n" +
                    "  \"model\": \"mixtral-8x7b-32768\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"You are a helpful nutritionist that replies with only JSON.\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}\n" +
                    "  ],\n" +
                    "  \"temperature\": 0.3\n" +
                    "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                return "{\"error\": \"⚠️ Groq API returned status code: " + status + "\"}";
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

            if (!content.isMissingNode()) {
                return content.asText().trim();
            } else {
                return "{\"error\": \"⚠️ No valid 'content' in Groq response\"}";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"⚠️ Exception occurred: " + e.getMessage() + "\"}";
        }
    }
}
