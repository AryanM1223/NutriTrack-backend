package com.example.NutriTrack.Controllers;

import com.example.NutriTrack.Services.FoodRepo;
import com.example.NutriTrack.Services.FoodCacheService;
import com.example.NutriTrack.Services.UserRepo;
import com.example.model.FoodModel;
import com.example.model.UserModel;
import com.example.NutriTrack.utils.GroqClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

// quantity text mein drop down daal do basic quantities ka to make it modular 
@RestController
@RequestMapping("/food")
public class FoodLogController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private FoodCacheService foodCacheService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> addFood(@RequestBody FoodModel foodModel, @RequestParam int userId) {
        Optional<UserModel> existingUserOptional = userRepo.findById(userId);
        if (!existingUserOptional.isPresent()) {
            return ResponseEntity.badRequest().body("❌ User not found");
        }

        String foodName = foodModel.getFoodItem();
        String quantityText = foodModel.getQuantityText();
        String nutritionJson;

        // 1. Check cache first
        String cachedData = foodCacheService.getFoodData(foodName, quantityText);

        if (cachedData != null) {
            System.out.println("=== DEBUG: Cache HIT for '" + foodName + " (" + quantityText + ")' ===");
            nutritionJson = cachedData;
        } else {
            System.out.println("=== DEBUG: Cache MISS for '" + foodName + " (" + quantityText + ")'. Querying LLM. ===");
            // 2. Cache miss: Query the LLM with an improved prompt
            String prompt = "You are a precise nutritionist specializing in Indian cuisine. Your task is to calculate the nutritional values for the given food item and quantity.\n\n"
                    + "Food Item: \"" + foodName + "\"\n"
                    + "Quantity: \"" + quantityText + "\"\n\n"
                    + "INSTRUCTIONS:\n"
                    + "1. Use a reliable, standard Indian food composition database for your calculations.\n"
                    + "2. If a generic quantity like '1 serving' or '1 bowl' is given, use these standard conversions: 1 serving = 200g, 1 bowl = 150g, 1 cup = 240ml. Otherwise, use the specified quantity.\n"
                    + "3. Calculate the total calories, protein (in grams), and fiber (in grams).\n"
                    + "4. Round all nutritional values to the nearest whole number.\n"
                    + "5. Do NOT provide any explanation, preamble, or text outside of the JSON object.\n"
                    + "6. If you cannot determine the nutritional value for the given food, return a JSON object with all values set to 0.\n\n"
                    + "Respond ONLY with a JSON object in the following format (do not add ```json markdown):\n"
                    + "{\n"
                    + "  \"calories\": <calculated_calories>,\n"
                    + "  \"protein\": <calculated_protein_in_grams>,\n"
                    + "  \"fiber\": <calculated_fiber_in_grams>\n"
                    + "}";

            String modelResponse = GroqClient.askModel(prompt);

            if (modelResponse == null || modelResponse.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Empty response from Groq. Make sure the API is reachable.");
            }

            if (modelResponse.contains("\"error\"")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error from LLM service: " + modelResponse);
            }

            try {
                nutritionJson = extractJsonFromResponse(modelResponse);
                // 3. Store the successful response in the cache
                foodCacheService.setFoodData(foodName, quantityText, nutritionJson);
                System.out.println("=== DEBUG: Stored in cache: " + nutritionJson + " ===");
            } catch (Exception e) {
                System.err.println("Raw response: " + modelResponse);
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to parse LLM response: " + e.getMessage() + ". Raw response: " + modelResponse);
            }
        }

        // 4. Parse the JSON (from cache or LLM) and save the food log
        try {
            System.out.println("=== DEBUG: Parsing JSON ===\n" + nutritionJson + "\n=== End Parsing JSON ===");
            JsonNode jsonNode = objectMapper.readTree(nutritionJson);

            double calories = jsonNode.has("calories") ? jsonNode.get("calories").asDouble(0) : 0;
            double protein = jsonNode.has("protein") ? jsonNode.get("protein").asDouble(0) : 0;
            double fiber = jsonNode.has("fiber") ? jsonNode.get("fiber").asDouble(0) : 0;

            foodModel.setCalories(calories);
            foodModel.setTotalProtein(protein);
            foodModel.setTotalFiber(fiber);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to parse nutritional data: " + e.getMessage() + ". Data: " + nutritionJson);
        }

        foodModel.setUser(existingUserOptional.get());
        FoodModel savedFood = foodRepo.save(foodModel);
        return ResponseEntity.ok(savedFood);
    }

    private String extractJsonFromResponse(String modelResponse) {

        modelResponse = modelResponse.trim();

        int start = modelResponse.indexOf('{');
        int end = modelResponse.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            String jsonString = modelResponse.substring(start, end + 1);

            jsonString = jsonString.replaceAll("\\r|\\n|\\t", " ").replaceAll("\\s+", " ").trim();

            return jsonString;
        } else {

            if (modelResponse.startsWith("{") && modelResponse.endsWith("}")) {
                return modelResponse;
            }
            throw new IllegalArgumentException("No valid JSON object found in model response: " + modelResponse);
        }
    }

    @GetMapping("/getfood/{id}")
    public ResponseEntity<ArrayList<FoodModel>> getFood(@PathVariable int id) {
        Optional<UserModel> exisOptional = userRepo.findById(id);
        if (!exisOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ArrayList<FoodModel> foodlogs = foodRepo.findByUser(exisOptional.get());
        return ResponseEntity.ok(foodlogs);
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getDailySummary(@RequestParam int userId, @RequestParam String date) {
        Optional<UserModel> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("❌ User not found");
        }

        LocalDate localDate = LocalDate.parse(date);
        List<FoodModel> foodList = foodRepo.findByUserAndDate(userOptional.get(), localDate);

        double totalCalories = 0, totalProtein = 0, totalFiber = 0;
        for (FoodModel food : foodList) {
            totalCalories += food.getCalories();
            totalProtein += food.getTotalProtein();
            totalFiber += food.getTotalFiber();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("date", localDate);
        response.put("totalCalories", totalCalories);
        response.put("totalProtein", totalProtein);
        response.put("totalFiber", totalFiber);
        response.put("totalItems", foodList.size());

        return ResponseEntity.ok(response);
    }

    @RestController
    public class HealthController {

        @GetMapping("/health")
        public ResponseEntity<String> testGroq() {
            try {
                String response = GroqClient.askModel("What is 2+2? Respond only with a number.");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Groq API test failed: " + e.getMessage());
            }
        }
    }

}
